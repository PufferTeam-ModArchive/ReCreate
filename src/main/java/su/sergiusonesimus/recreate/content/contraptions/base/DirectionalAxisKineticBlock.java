package su.sergiusonesimus.recreate.content.contraptions.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.metaworlds.util.Rotation;
import su.sergiusonesimus.recreate.foundation.utility.DirectionHelper;
import su.sergiusonesimus.recreate.foundation.utility.Iterate;

public abstract class DirectionalAxisKineticBlock extends DirectionalKineticBlock implements IAxisAlongFirstCoordinate {

    public DirectionalAxisKineticBlock(Material material) {
        super(material);
    }

    protected Direction getFacingForPlacement(EntityLivingBase placer) {
        Direction facing = Direction.getNearestLookingDirection(placer)
            .getOpposite();
        if (placer.isSneaking()) facing = facing.getOpposite();
        return facing;
    }

    protected boolean getAxisAlignmentForPlacement(EntityLivingBase placer) {
        Vec3 lookVec = placer.getLookVec();
        return Direction.getNearest(lookVec.xCoord, 0, lookVec.zCoord)
            .getAxis() == Axis.X;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        Direction facing = getFacingForPlacement(placer);
        boolean alongFirst = false;
        Axis faceAxis = facing.getAxis();

        if (faceAxis.isHorizontal()) {
            alongFirst = faceAxis == Axis.Z;
            Direction positivePerpendicular = DirectionHelper.getPositivePerpendicular(faceAxis);

            boolean shaftAbove = prefersConnectionTo(world, x, y, z, Direction.UP, true);
            boolean shaftBelow = prefersConnectionTo(world, x, y, z, Direction.DOWN, true);
            boolean preferLeft = prefersConnectionTo(world, x, y, z, positivePerpendicular, false);
            boolean preferRight = prefersConnectionTo(world, x, y, z, positivePerpendicular.getOpposite(), false);

            if (shaftAbove || shaftBelow || preferLeft || preferRight) alongFirst = faceAxis == Axis.X;
        }

        if (faceAxis.isVertical()) {
            alongFirst = getAxisAlignmentForPlacement(placer);
            Direction prefferedSide = null;

            for (Direction side : Iterate.horizontalDirections) {
                if (!prefersConnectionTo(world, x, y, z, side, true)
                    && !prefersConnectionTo(world, x, y, z, side.getClockWise(), false)) continue;
                if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
                    prefferedSide = null;
                    break;
                }
                prefferedSide = side;
            }

            if (prefferedSide != null) alongFirst = prefferedSide.getAxis() == Axis.X;
        }

        world.setBlockMetadataWithNotify(x, y, z, getMetadata(facing, alongFirst), 2);
    }

    protected boolean prefersConnectionTo(IBlockAccess world, int x, int y, int z, Direction facing,
        boolean shaftAxis) {
        if (!shaftAxis) return false;
        ChunkCoordinates normal = facing.getNormal();
        Block neighbourBlock = world.getBlock(x + normal.posX, y + normal.posY, z + normal.posZ);
        return neighbourBlock instanceof IRotate
            && ((IRotate) neighbourBlock).hasShaftTowards(world, x, y, z, facing.getOpposite());
    }

    @Override
    public Axis getAxis(int meta) {
        Axis pistonAxis = getDirection(meta).getAxis();
        boolean alongFirst = isAxisAlongFirstCoordinate(meta);

        if (pistonAxis == Axis.X) return alongFirst ? Axis.Y : Axis.Z;
        if (pistonAxis == Axis.Y) return alongFirst ? Axis.X : Axis.Z;
        if (pistonAxis == Axis.Z) return alongFirst ? Axis.X : Axis.Y;

        throw new IllegalStateException("Unknown axis??");
    }

    @Override
    public int rotate(World world, int x, int y, int z, Rotation rot) {
        int meta = world.getBlockMetadata(x, y, z);
        if (rot.ordinal() % 2 == 1) meta = cycleMetadata(meta);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        return super.rotate(world, x, y, z, rot);
    }

    @Override
    public boolean hasShaftTowards(IBlockAccess world, int x, int y, int z, Direction face) {
        return face.getAxis() == ((IRotate) world.getBlock(x, y, z)).getAxis(world.getBlockMetadata(x, y, z));
    }

}
