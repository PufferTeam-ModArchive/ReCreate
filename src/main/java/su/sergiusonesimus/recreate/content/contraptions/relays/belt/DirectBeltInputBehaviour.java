package su.sergiusonesimus.recreate.content.contraptions.relays.belt;

import java.util.function.Supplier;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.transport.TransportedItemStack;
import su.sergiusonesimus.recreate.foundation.tileentity.SmartTileEntity;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.BehaviourType;
import su.sergiusonesimus.recreate.util.ItemHandlerHelper;

/**
 * Behaviour for TileEntities to which belts can transfer items directly in a
 * backup-friendly manner. Example uses: Basin, Saw, Depot
 */
public class DirectBeltInputBehaviour extends TileEntityBehaviour {

    public static BehaviourType<DirectBeltInputBehaviour> TYPE = new BehaviourType<>();

    private InsertionCallback tryInsert;
    private AvailabilityPredicate canInsert;
    private Supplier<Boolean> supportsBeltFunnels;

    public DirectBeltInputBehaviour(SmartTileEntity te) {
        super(te);
        tryInsert = this::defaultInsertionCallback;
        canInsert = d -> true;
        supportsBeltFunnels = () -> false;
    }

    public DirectBeltInputBehaviour allowingBeltFunnelsWhen(Supplier<Boolean> pred) {
        supportsBeltFunnels = pred;
        return this;
    }

    public DirectBeltInputBehaviour allowingBeltFunnels() {
        supportsBeltFunnels = () -> true;
        return this;
    }

    public DirectBeltInputBehaviour onlyInsertWhen(AvailabilityPredicate pred) {
        canInsert = pred;
        return this;
    }

    public DirectBeltInputBehaviour setInsertionHandler(InsertionCallback callback) {
        tryInsert = callback;
        return this;
    }

    private ItemStack defaultInsertionCallback(TransportedItemStack inserted, Direction side, boolean simulate) {
        if (!(tileEntity instanceof IInventory inventory)) return inserted.stack;
        return ItemHandlerHelper.insertItem(inventory, inserted.stack.copy(), simulate);
    }

    public boolean canInsertFromSide(Direction side) {
        return canInsert.test(side);
    }

    public ItemStack handleInsertion(ItemStack stack, Direction side, boolean simulate) {
        return handleInsertion(new TransportedItemStack(stack), side, simulate);
    }

    public ItemStack handleInsertion(TransportedItemStack stack, Direction side, boolean simulate) {
        return tryInsert.apply(stack, side, simulate);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @FunctionalInterface
    public interface InsertionCallback {

        public ItemStack apply(TransportedItemStack stack, Direction side, boolean simulate);
    }

    @FunctionalInterface
    public interface AvailabilityPredicate {

        public boolean test(Direction side);
    }

    // TODO
    // @Nullable
    // public ItemStack tryExportingToBeltFunnel(ItemStack stack, @Nullable Direction side, boolean simulate) {
    // int funnelX = getPosX();
    // int funnelY = getPosY() + 1;
    // int funnelZ = getPosZ();
    // World world = getWorld();
    // Block block = world.getBlock(funnelX, funnelY, funnelZ);
    // if (!(block instanceof BeltFunnelBlock funnel)) return null;
    // if (funnelState.getValue(BeltFunnelBlock.SHAPE) != Shape.PULLING) return null;
    // if (side != null && FunnelBlock.getFunnelFacing(funnelState) != side) return null;
    // TileEntity te = world.getTileEntity(funnelX, funnelY, funnelZ);
    // if (!(te instanceof FunnelTileEntity fte)) return null;
    // if (funnelState.getValue(BeltFunnelBlock.POWERED)) return stack;
    // ItemStack insert = FunnelBlock.tryInsert(world, funnelPos, stack, simulate);
    // if (insert.getCount() != stack.getCount() && !simulate) ((FunnelTileEntity) te).flap(true);
    // return insert;
    // }

    public boolean canSupportBeltFunnels() {
        return supportsBeltFunnels.get();
    }

}
