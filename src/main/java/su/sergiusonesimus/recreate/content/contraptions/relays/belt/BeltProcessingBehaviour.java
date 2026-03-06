package su.sergiusonesimus.recreate.content.contraptions.relays.belt;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.content.contraptions.relays.belt.transport.TransportedItemStack;
import su.sergiusonesimus.recreate.foundation.tileentity.SmartTileEntity;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.BehaviourType;

/**
 * Behaviour for TileEntities which can process items on belts or depots beneath
 * them. Currently only supports placement location 2 spaces above the belt
 * block. Example use: Mechanical Press
 */
public class BeltProcessingBehaviour extends TileEntityBehaviour {

    public static BehaviourType<BeltProcessingBehaviour> TYPE = new BehaviourType<>();

    public static enum ProcessingResult {
        PASS,
        HOLD,
        REMOVE;
    }

    private ProcessingCallback onItemEnter;
    private ProcessingCallback continueProcessing;

    public BeltProcessingBehaviour(SmartTileEntity te) {
        super(te);
        onItemEnter = (s, i) -> ProcessingResult.PASS;
        continueProcessing = (s, i) -> ProcessingResult.PASS;
    }

    public BeltProcessingBehaviour whenItemEnters(ProcessingCallback callback) {
        onItemEnter = callback;
        return this;
    }

    public BeltProcessingBehaviour whileItemHeld(ProcessingCallback callback) {
        continueProcessing = callback;
        return this;
    }

    public static boolean isBlocked(World world, ChunkCoordinates processingSpace) {
        return isBlocked(world, processingSpace.posX, processingSpace.posY, processingSpace.posZ);
    }

    public static boolean isBlocked(World world, int processingSpaceX, int processingSpaceY, int processingSpaceZ) {
        Block block = world.getBlock(processingSpaceX, processingSpaceY + 1, processingSpaceZ);
        // TODO
        // if (AbstractFunnelBlock.isFunnel(block))
        // return false;
        List<AxisAlignedBB> collisions = new ArrayList<AxisAlignedBB>();
        block.addCollisionBoxesToList(
            world,
            processingSpaceX,
            processingSpaceY + 1,
            processingSpaceZ,
            block.getCollisionBoundingBoxFromPool(world, processingSpaceX, processingSpaceY + 1, processingSpaceZ),
            collisions,
            null);
        return !collisions.isEmpty();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public ProcessingResult handleReceivedItem(TransportedItemStack stack,
        TransportedItemStackHandlerBehaviour inventory) {
        return onItemEnter.apply(stack, inventory);
    }

    public ProcessingResult handleHeldItem(TransportedItemStack stack, TransportedItemStackHandlerBehaviour inventory) {
        return continueProcessing.apply(stack, inventory);
    }

    @FunctionalInterface
    public interface ProcessingCallback {

        public ProcessingResult apply(TransportedItemStack stack, TransportedItemStackHandlerBehaviour inventory);
    }

}
