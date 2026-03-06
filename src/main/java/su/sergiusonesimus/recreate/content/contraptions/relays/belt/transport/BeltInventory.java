package su.sergiusonesimus.recreate.content.contraptions.relays.belt.transport;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.BeltHelper;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.BeltProcessingBehaviour;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.BeltProcessingBehaviour.ProcessingResult;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.BeltSlope;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.BeltTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.DirectBeltInputBehaviour;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.TransportedItemStackHandlerBehaviour;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.TransportedItemStackHandlerBehaviour.TransportedResult;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.foundation.utility.ServerSpeedProvider;
import su.sergiusonesimus.recreate.util.BlockHelper;

public class BeltInventory {

    final BeltTileEntity belt;
    private final List<TransportedItemStack> items;
    final List<TransportedItemStack> toInsert;
    final List<TransportedItemStack> toRemove;
    boolean beltMovementPositive;
    final float SEGMENT_WINDOW = .75f;

    public BeltInventory(BeltTileEntity te) {
        this.belt = te;
        items = new LinkedList<>();
        toInsert = new LinkedList<>();
        toRemove = new LinkedList<>();
    }

    @SuppressWarnings("unused")
    public void tick() {

        // Added/Removed items from previous cycle
        if (!toInsert.isEmpty() || !toRemove.isEmpty()) {
            toInsert.forEach(this::insert);
            toInsert.clear();
            items.removeAll(toRemove);
            toRemove.clear();
            belt.markDirty();
            belt.sendData();
        }

        if (belt.getSpeed() == 0) return;

        // Reverse item collection if belt just reversed
        if (beltMovementPositive != belt.getDirectionAwareBeltMovementSpeed() > 0) {
            beltMovementPositive = !beltMovementPositive;
            Collections.reverse(items);
            belt.markDirty();
            belt.sendData();
        }

        // Assuming the first entry is furthest on the belt
        TransportedItemStack stackInFront = null;
        TransportedItemStack currentItem = null;
        Iterator<TransportedItemStack> iterator = items.iterator();

        // Useful stuff
        float beltSpeed = belt.getDirectionAwareBeltMovementSpeed();
        Direction movementFacing = belt.getMovementFacing();
        boolean horizontal = belt.slopeType == BeltSlope.HORIZONTAL;
        float spacing = 1;
        World world = belt.getWorld();
        boolean onClient = world.isRemote && !belt.isVirtual();

        // resolve ending only when items will reach it this tick
        Ending ending = Ending.UNRESOLVED;

        // Loop over items
        while (iterator.hasNext()) {
            stackInFront = currentItem;
            currentItem = iterator.next();
            currentItem.prevBeltPosition = currentItem.beltPosition;
            currentItem.prevSideOffset = currentItem.sideOffset;

            if (currentItem.stack.stackSize == 0) {
                iterator.remove();
                currentItem = null;
                continue;
            }

            float movement = beltSpeed;
            if (onClient) movement *= ServerSpeedProvider.get();

            // Don't move if held by processing (client)
            if (world.isRemote && currentItem.locked) continue;

            // Don't move if held by external components
            if (currentItem.lockedExternally) {
                currentItem.lockedExternally = false;
                continue;
            }

            // Don't move if other items are waiting in front
            boolean noMovement = false;
            float currentPos = currentItem.beltPosition;
            if (stackInFront != null) {
                float diff = stackInFront.beltPosition - currentPos;
                if (Math.abs(diff) <= spacing) noMovement = true;
                movement = beltMovementPositive ? Math.min(movement, diff - spacing)
                    : Math.max(movement, diff + spacing);
            }

            // Don't move beyond the edge
            float diffToEnd = beltMovementPositive ? belt.beltLength - currentPos : -currentPos;
            if (Math.abs(diffToEnd) < Math.abs(movement) + 1) {
                if (ending == Ending.UNRESOLVED) ending = resolveEnding();
                diffToEnd += beltMovementPositive ? -ending.margin : ending.margin;
            }
            float limitedMovement = beltMovementPositive ? Math.min(movement, diffToEnd)
                : Math.max(movement, diffToEnd);
            float nextOffset = currentItem.beltPosition + limitedMovement;

            // Belt item processing
            if (!onClient && horizontal) {
                ItemStack item = currentItem.stack;
                if (handleBeltProcessingAndCheckIfRemoved(currentItem, nextOffset, noMovement)) {
                    iterator.remove();
                    belt.sendData();
                    continue;
                }
                if (item != currentItem.stack) belt.sendData();
                if (currentItem.locked) continue;
            }

            if (noMovement) continue;

            // TODO
            // // Belt Tunnels
            // if (BeltTunnelInteractionHandler.flapTunnelsAndCheckIfStuck(this, currentItem, nextOffset))
            // continue;
            //
            // // Belt Funnels
            // if (BeltFunnelInteractionHandler.checkForFunnels(this, currentItem, nextOffset))
            // continue;
            //
            // // Horizontal Crushing Wheels
            // if (BeltCrusherInteractionHandler.checkForCrushers(this, currentItem, nextOffset))
            // continue;

            // Apply Movement
            currentItem.beltPosition += limitedMovement;
            currentItem.sideOffset += (currentItem.getTargetSideOffset() - currentItem.sideOffset)
                * Math.abs(limitedMovement)
                * 2f;
            currentPos = currentItem.beltPosition;

            // Movement successful
            if (limitedMovement == movement || onClient) continue;

            // End reached
            int lastOffset = beltMovementPositive ? belt.beltLength - 1 : 0;
            ChunkCoordinates nextPosition = BeltHelper
                .getPositionForOffset(belt, beltMovementPositive ? belt.beltLength : -1);

            if (ending == Ending.FUNNEL) continue;

            if (ending == Ending.INSERT) {
                DirectBeltInputBehaviour inputBehaviour = TileEntityBehaviour
                    .get(world, nextPosition.posX, nextPosition.posY, nextPosition.posZ, DirectBeltInputBehaviour.TYPE);
                if (inputBehaviour == null) continue;
                if (!inputBehaviour.canInsertFromSide(movementFacing)) continue;

                ItemStack remainder = inputBehaviour.handleInsertion(currentItem, movementFacing, false);
                if (remainder != null && remainder.getItem()
                    .equals(currentItem.stack.getItem())) continue;

                currentItem.stack = remainder;
                if (remainder == null || remainder.stackSize == 0) iterator.remove();

                // TODO
                // flapTunnel(this, lastOffset, movementFacing, false);
                belt.sendData();
                continue;
            }

            if (ending == Ending.BLOCKED) continue;

            if (ending == Ending.EJECT) {
                eject(currentItem);
                iterator.remove();
                // TODO
                // flapTunnel(this, lastOffset, movementFacing, false);
                belt.sendData();
                continue;
            }
        }
    }

    protected boolean handleBeltProcessingAndCheckIfRemoved(TransportedItemStack currentItem, float nextOffset,
        boolean noMovement) {
        int currentSegment = (int) currentItem.beltPosition;

        // Continue processing if held
        if (currentItem.locked) {
            BeltProcessingBehaviour processingBehaviour = getBeltProcessingAtSegment(currentSegment);
            TransportedItemStackHandlerBehaviour stackHandlerBehaviour = getTransportedItemStackHandlerAtSegment(
                currentSegment);

            if (stackHandlerBehaviour == null) return false;
            if (processingBehaviour == null) {
                currentItem.locked = false;
                belt.sendData();
                return false;
            }

            ProcessingResult result = processingBehaviour.handleHeldItem(currentItem, stackHandlerBehaviour);
            if (result == ProcessingResult.REMOVE) return true;
            if (result == ProcessingResult.HOLD) return false;

            currentItem.locked = false;
            belt.sendData();
            return false;
        }

        if (noMovement) return false;

        // See if any new belt processing catches the item
        if (currentItem.beltPosition > .5f || beltMovementPositive) {
            int firstUpcomingSegment = (int) (currentItem.beltPosition + (beltMovementPositive ? .5f : -.5f));
            int step = beltMovementPositive ? 1 : -1;

            for (int segment = firstUpcomingSegment; beltMovementPositive ? segment + .5f <= nextOffset
                : segment + .5f >= nextOffset; segment += step) {

                BeltProcessingBehaviour processingBehaviour = getBeltProcessingAtSegment(segment);
                TransportedItemStackHandlerBehaviour stackHandlerBehaviour = getTransportedItemStackHandlerAtSegment(
                    segment);

                if (processingBehaviour == null) continue;
                if (stackHandlerBehaviour == null) continue;
                if (BeltProcessingBehaviour.isBlocked(belt.getWorld(), BeltHelper.getPositionForOffset(belt, segment)))
                    continue;

                ProcessingResult result = processingBehaviour.handleReceivedItem(currentItem, stackHandlerBehaviour);
                if (result == ProcessingResult.REMOVE) return true;

                if (result == ProcessingResult.HOLD) {
                    currentItem.beltPosition = segment + .5f + (beltMovementPositive ? 1 / 512f : -1 / 512f);
                    currentItem.locked = true;
                    belt.sendData();
                    return false;
                }
            }
        }

        return false;
    }

    protected BeltProcessingBehaviour getBeltProcessingAtSegment(int segment) {
        ChunkCoordinates offsetPos = BeltHelper.getPositionForOffset(belt, segment);
        return TileEntityBehaviour
            .get(belt.getWorld(), offsetPos.posX, offsetPos.posY + 2, offsetPos.posZ, BeltProcessingBehaviour.TYPE);
    }

    protected TransportedItemStackHandlerBehaviour getTransportedItemStackHandlerAtSegment(int segment) {
        ChunkCoordinates offsetPos = BeltHelper.getPositionForOffset(belt, segment);
        return TileEntityBehaviour.get(
            belt.getWorld(),
            offsetPos.posX,
            offsetPos.posY,
            offsetPos.posZ,
            TransportedItemStackHandlerBehaviour.TYPE);
    }

    private enum Ending {

        UNRESOLVED(0),
        EJECT(0),
        INSERT(.25f),
        FUNNEL(.5f),
        BLOCKED(.45f);

        private float margin;

        Ending(float f) {
            this.margin = f;
        }
    }

    private Ending resolveEnding() {
        World world = belt.getWorld();
        ChunkCoordinates nextPosition = BeltHelper
            .getPositionForOffset(belt, beltMovementPositive ? belt.beltLength : -1);

        // if (AllBlocks.BRASS_BELT_FUNNEL.has(world.getBlockState(lastPosition.up())))
        // return Ending.FUNNEL;

        DirectBeltInputBehaviour inputBehaviour = TileEntityBehaviour
            .get(world, nextPosition.posX, nextPosition.posY, nextPosition.posZ, DirectBeltInputBehaviour.TYPE);
        if (inputBehaviour != null) return Ending.INSERT;

        if (BlockHelper.hasBlockSolidSide(
            world.getBlock(nextPosition.posX, nextPosition.posY, nextPosition.posZ),
            world,
            nextPosition.posX,
            nextPosition.posY,
            nextPosition.posZ,
            belt.getMovementFacing()
                .getOpposite()))
            return Ending.BLOCKED;

        return Ending.EJECT;
    }

    //

    public boolean canInsertAt(int segment) {
        return canInsertAtFromSide(segment, Direction.UP);
    }

    public boolean canInsertAtFromSide(int segment, Direction side) {
        float segmentPos = segment;
        if (belt.getMovementFacing() == side.getOpposite()) return false;
        if (belt.getMovementFacing() != side) segmentPos += .5f;
        else if (!beltMovementPositive) segmentPos += 1f;

        for (TransportedItemStack stack : items) if (isBlocking(segment, side, segmentPos, stack)) return false;
        for (TransportedItemStack stack : toInsert) if (isBlocking(segment, side, segmentPos, stack)) return false;

        return true;
    }

    private boolean isBlocking(int segment, Direction side, float segmentPos, TransportedItemStack stack) {
        float currentPos = stack.beltPosition;
        if (stack.insertedAt == segment && stack.insertedFrom == side
            && (beltMovementPositive ? currentPos <= segmentPos + 1 : currentPos >= segmentPos - 1)) return true;
        return false;
    }

    public void addItem(TransportedItemStack newStack) {
        toInsert.add(newStack);
    }

    private void insert(TransportedItemStack newStack) {
        if (items.isEmpty()) items.add(newStack);
        else {
            int index = 0;
            for (TransportedItemStack stack : items) {
                if (stack.compareTo(newStack) > 0 == beltMovementPositive) break;
                index++;
            }
            items.add(index, newStack);
        }
    }

    public TransportedItemStack getStackAtOffset(int offset) {
        float min = offset;
        float max = offset + 1;
        for (TransportedItemStack stack : items) {
            if (stack.beltPosition > max) continue;
            if (stack.beltPosition > min) return stack;
        }
        return null;
    }

    public void read(NBTTagCompound nbt) {
        items.clear();
        NBTTagList tags = nbt.getTagList("Items", 10);
        for (int i = 0; i < tags.tagCount(); i++) items.add(TransportedItemStack.read(tags.getCompoundTagAt(i)));
        beltMovementPositive = nbt.getBoolean("PositiveOrder");
    }

    public NBTTagCompound write() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList itemsNBT = new NBTTagList();
        items.forEach(stack -> itemsNBT.appendTag(stack.serializeNBT()));
        nbt.setTag("Items", itemsNBT);
        nbt.setBoolean("PositiveOrder", beltMovementPositive);
        return nbt;
    }

    public void eject(TransportedItemStack stack) {
        ItemStack ejected = stack.stack;
        Vec3 outPos = BeltHelper.getVectorForOffset(belt, stack.beltPosition);
        outPos.yCoord += 1D / 16D;
        float movementSpeed = Math.max(Math.abs(belt.getBeltMovementSpeed()), 1 / 8f);
        ChunkCoordinates chainDirection = belt.getBeltChainDirection();
        Vec3 outMotion = Vec3
            .createVectorHelper(
                chainDirection.posX * movementSpeed,
                chainDirection.posY * movementSpeed,
                chainDirection.posZ * movementSpeed)
            .addVector(0, 1 / 8f, 0);
        Vec3 temp = outMotion.normalize();
        outPos = outPos.addVector(temp.xCoord * 0.001, temp.yCoord * 0.001, temp.zCoord * 0.001);
        EntityItem entity = new EntityItem(
            belt.getWorld(),
            outPos.xCoord,
            outPos.yCoord + 6D / 16D,
            outPos.zCoord,
            ejected);
        entity.motionX = outMotion.xCoord;
        entity.motionY = outMotion.yCoord;
        entity.motionZ = outMotion.zCoord;
        entity.velocityChanged = true;
        entity.delayBeforeCanPickup = 10;
        belt.getWorld()
            .spawnEntityInWorld(entity);
    }

    public void ejectAll() {
        items.forEach(this::eject);
        items.clear();
    }

    public void applyToEachWithin(float position, float maxDistanceToPosition,
        Function<TransportedItemStack, TransportedResult> processFunction) {
        boolean dirty = false;
        for (TransportedItemStack transported : items) {
            ItemStack stackBefore = transported.stack.copy();
            if (Math.abs(position - transported.beltPosition) >= maxDistanceToPosition) continue;
            TransportedResult result = processFunction.apply(transported);
            if (result == null || result.didntChangeFrom(stackBefore)) continue;

            dirty = true;
            if (result.hasHeldOutput()) {
                TransportedItemStack held = result.getHeldOutput();
                held.beltPosition = ((int) position) + .5f - (beltMovementPositive ? 1 / 512f : -1 / 512f);
                toInsert.add(held);
            }
            toInsert.addAll(result.getOutputs());
            toRemove.add(transported);
        }
        if (dirty) {
            belt.markDirty();
            belt.sendData();
        }
    }

    public List<TransportedItemStack> getTransportedItems() {
        return items;
    }

}
