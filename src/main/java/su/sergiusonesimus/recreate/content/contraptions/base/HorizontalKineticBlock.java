package su.sergiusonesimus.recreate.content.contraptions.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.foundation.utility.Iterate;
import su.sergiusonesimus.metaworlds.util.Rotation;

public abstract class HorizontalKineticBlock extends KineticBlock {

    public HorizontalKineticBlock(Material materialIn) {
        super(materialIn);
    }

    public Direction getPreferredHorizontalFacing(World world, int x, int y, int z) {
        Direction prefferedSide = null;
        for (Direction side : Iterate.horizontalDirections) {
            ChunkCoordinates sideNormal = side.getNormal();
            Block block = world.getBlock(x + sideNormal.posX, y + sideNormal.posY, z + sideNormal.posZ);
            if (block instanceof IRotate) {
                if (((IRotate) block).hasShaftTowards(
                    world,
                    x + sideNormal.posX,
                    y + sideNormal.posY,
                    z + sideNormal.posZ,
                    side.getOpposite())) if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
                        prefferedSide = null;
                        break;
                    } else {
                        prefferedSide = side;
                    }
            }
        }
        return prefferedSide;
    }

    @Override
    public Axis getAxis(int meta) {
        return getDirection(meta).getAxis();
    }

    @Override
    public Direction getDirection(int meta) {
        return Direction.from2DDataValue(meta);
    }

    @Override
    public int getMetaFromDirection(Direction direction) {
        if (direction.ordinal() < 2) return 0;
        return direction.ordinal() - 2;
    }

    @Override
    public int rotate(World world, int x, int y, int z, Rotation rot) {
        Direction dir = this.getDirection(world.getBlockMetadata(x, y, z));
        rot.rotate(dir);
        int newMeta = this.getMetaFromDirection(dir);
        world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
        return newMeta;
    }

}
