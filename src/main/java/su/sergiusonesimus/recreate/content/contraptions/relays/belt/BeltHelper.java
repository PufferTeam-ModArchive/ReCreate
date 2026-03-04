package su.sergiusonesimus.recreate.content.contraptions.relays.belt;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidContainerItem;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.AllTags.AllItemTags;
import su.sergiusonesimus.recreate.util.VecHelper;

public class BeltHelper {

    public static boolean isItemUpright(ItemStack stack) {
        return stack.getItem() instanceof IFluidContainerItem || AllItemTags.UPRIGHT_ON_BELT.matches(stack);
    }

    public static BeltTileEntity getSegmentTE(World world, ChunkCoordinates pos) {
        return getSegmentTE(world, pos.posX, pos.posY, pos.posZ);
    }

    public static BeltTileEntity getSegmentTE(World world, int posX, int posY, int posZ) {
        if (!world.doChunksNearChunkExist(posX, posY, posZ, 0)) return null;
        TileEntity tileEntity = world.getTileEntity(posX, posY, posZ);
        if (!(tileEntity instanceof BeltTileEntity)) return null;
        return (BeltTileEntity) tileEntity;
    }

    public static BeltTileEntity getControllerTE(World world, ChunkCoordinates pos) {
        return getControllerTE(world, pos.posX, pos.posY, pos.posZ);
    }

    public static BeltTileEntity getControllerTE(World world, int posX, int posY, int posZ) {
        BeltTileEntity segment = getSegmentTE(world, posX, posY, posZ);
        if (segment == null) return null;
        ChunkCoordinates controllerPos = segment.getController();
        if (controllerPos == null) return null;
        return getSegmentTE(world, controllerPos);
    }

    public static BeltTileEntity getBeltForOffset(BeltTileEntity controller, float offset) {
        return getBeltAtSegment(controller, (int) Math.floor(offset));
    }

    public static BeltTileEntity getBeltAtSegment(BeltTileEntity controller, int segment) {
        ChunkCoordinates pos = getPositionForOffset(controller, segment);
        TileEntity te = controller.getWorld()
            .getTileEntity(pos.posX, pos.posY, pos.posZ);
        if (te == null || !(te instanceof BeltTileEntity bte)) return null;
        return bte;
    }

    public static ChunkCoordinates getPositionForOffset(BeltTileEntity controller, int offset) {
        ChunkCoordinates pos = new ChunkCoordinates(controller.xCoord, controller.yCoord, controller.zCoord);
        ChunkCoordinates vec = controller.getBeltFacing()
            .getNormal();
        BeltSlope slope = controller.slopeType;
        int verticality = slope == BeltSlope.DOWNWARD ? -1 : slope == BeltSlope.UPWARD ? 1 : 0;

        pos.posX += offset * vec.posX;
        pos.posY += MathHelper.clamp_int(offset, 0, controller.beltLength - 1) * verticality;
        pos.posZ += offset * vec.posZ;
        return pos;
    }

    public static Vec3 getVectorForOffset(BeltTileEntity controller, float offset) {
        BeltSlope slope = controller.slopeType;
        int verticality = slope == BeltSlope.DOWNWARD ? -1 : slope == BeltSlope.UPWARD ? 1 : 0;
        float verticalMovement = verticality;
        if (offset < .5) verticalMovement = 0;
        verticalMovement = verticalMovement * (Math.min(offset, controller.beltLength - .5f) - .5f);
        Vec3 vec = VecHelper.getCenterOf(controller.xCoord, controller.yCoord, controller.zCoord);
        ChunkCoordinates normal = controller.getBeltFacing()
            .getNormal();
        float multiplier = offset - .5f;
        Vec3 horizontalMovement = Vec3
            .createVectorHelper(normal.posX * multiplier, normal.posY * multiplier, normal.posZ * multiplier);

        if (slope == BeltSlope.VERTICAL) horizontalMovement = VecHelper.ZERO;

        vec = vec.addVector(horizontalMovement.xCoord, horizontalMovement.yCoord, horizontalMovement.zCoord)
            .addVector(0, verticalMovement, 0);
        return vec;
    }

    public static Vec3 getBeltVector(World world, int x, int y, int z) {
        if (!(world.getTileEntity(x, y, z) instanceof BeltTileEntity bte)) return null;
        BeltBlock block = (BeltBlock) bte.getBlockType();
        int meta = bte.getBlockMetadata();
        BeltSlope slope = bte.slopeType;
        int verticality = slope == BeltSlope.DOWNWARD ? -1 : slope == BeltSlope.UPWARD ? 1 : 0;
        Direction facing = block.getDirection(meta);
        ChunkCoordinates normal = facing.getNormal();
        Vec3 horizontalMovement = Vec3.createVectorHelper(normal.posX, normal.posY, normal.posZ);
        if (slope == BeltSlope.VERTICAL) return Vec3.createVectorHelper(
            0,
            facing.getAxisDirection()
                .getStep(),
            0);
        return Vec3.createVectorHelper(0, verticality, 0)
            .addVector(horizontalMovement.xCoord, horizontalMovement.yCoord, horizontalMovement.zCoord);
    }

}
