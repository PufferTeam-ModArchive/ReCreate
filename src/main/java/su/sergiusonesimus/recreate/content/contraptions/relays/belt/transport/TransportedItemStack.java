package su.sergiusonesimus.recreate.content.contraptions.relays.belt.transport;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.content.contraptions.processing.InWorldProcessing;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.BeltHelper;

public class TransportedItemStack implements Comparable<TransportedItemStack> {

    private static Random R = new Random();

    public ItemStack stack;
    public float beltPosition;
    public float sideOffset;
    public int angle;
    public int insertedAt;
    public Direction insertedFrom;
    public boolean locked;
    public boolean lockedExternally;

    public float prevBeltPosition;
    public float prevSideOffset;

    public InWorldProcessing.Type processedBy;
    public int processingTime;

    public TransportedItemStack(ItemStack stack) {
        this.stack = stack;
        boolean centered = BeltHelper.isItemUpright(stack);
        angle = centered ? 180 : R.nextInt(360);
        sideOffset = prevSideOffset = getTargetSideOffset();
        insertedFrom = Direction.UP;
    }

    public float getTargetSideOffset() {
        return (angle - 180) / (360 * 3f);
    }

    @Override
    public int compareTo(TransportedItemStack o) {
        return beltPosition < o.beltPosition ? 1 : beltPosition > o.beltPosition ? -1 : 0;
    }

    public TransportedItemStack getSimilar() {
        TransportedItemStack copy = new TransportedItemStack(stack.copy());
        copy.beltPosition = beltPosition;
        copy.insertedAt = insertedAt;
        copy.insertedFrom = insertedFrom;
        copy.prevBeltPosition = prevBeltPosition;
        copy.prevSideOffset = prevSideOffset;
        copy.processedBy = processedBy;
        copy.processingTime = processingTime;
        return copy;
    }

    public TransportedItemStack copy() {
        TransportedItemStack copy = getSimilar();
        copy.angle = angle;
        copy.sideOffset = sideOffset;
        return copy;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound item = new NBTTagCompound();
        stack.writeToNBT(item);
        nbt.setTag("Item", item);
        nbt.setFloat("Pos", beltPosition);
        nbt.setFloat("PrevPos", prevBeltPosition);
        nbt.setFloat("Offset", sideOffset);
        nbt.setFloat("PrevOffset", prevSideOffset);
        nbt.setInteger("InSegment", insertedAt);
        nbt.setInteger("Angle", angle);
        nbt.setInteger("InDirection", insertedFrom.get3DDataValue());
        if (locked) nbt.setBoolean("Locked", locked);
        if (lockedExternally) nbt.setBoolean("LockedExternally", lockedExternally);
        return nbt;
    }

    public static TransportedItemStack read(NBTTagCompound nbt) {
        TransportedItemStack stack = new TransportedItemStack(
            ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Item")));
        stack.beltPosition = nbt.getFloat("Pos");
        stack.prevBeltPosition = nbt.getFloat("PrevPos");
        stack.sideOffset = nbt.getFloat("Offset");
        stack.prevSideOffset = nbt.getFloat("PrevOffset");
        stack.insertedAt = nbt.getInteger("InSegment");
        stack.angle = nbt.getInteger("Angle");
        stack.insertedFrom = Direction.from3DDataValue(nbt.getInteger("InDirection"));
        stack.locked = nbt.getBoolean("Locked");
        stack.lockedExternally = nbt.getBoolean("LockedExternally");
        return stack;
    }

}
