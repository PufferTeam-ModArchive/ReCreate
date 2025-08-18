package su.sergiusonesimus.recreate.content.contraptions.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.metaworlds.util.Rotation;
import su.sergiusonesimus.recreate.foundation.utility.Iterate;

public abstract class RotatedPillarKineticBlock extends KineticBlock {

    public RotatedPillarKineticBlock(Material materialIn) {
        super(materialIn);
    }

    @Override
    public Axis getAxis(int meta) {
        switch (meta) {
            default:
            case 0:
                return Axis.Y;
            case 1:
                return Axis.X;
            case 2:
                return Axis.Z;
        }
    }

    public int getMetaFromAxis(Axis axis) {
        switch (axis) {
            default:
            case Y:
                return 0;
            case X:
                return 1;
            case Z:
                return 2;
        }
    }

    @Override
    public int getMetaFromDirection(Direction direction) {
        return this.getMetaFromAxis(direction.getAxis());
    }

    @Override
    public int rotate(World world, int x, int y, int z, Rotation rot) {
        int meta = world.getBlockMetadata(x, y, z);
        switch (rot) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (this.getAxis(meta)) {
                    case X:
                        meta = 2;
                        break;
                    case Z:
                        meta = 1;
                        break;
                    default:
                        return meta;
                }
                world.setBlockMetadataWithNotify(x, y, z, meta, 2);
                return meta;
            default:
                return meta;
        }
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ,
        int meta) {
        int localMeta = this.getMetaFromDirection(Direction.from3DDataValue(side));
        worldIn.setBlockMetadataWithNotify(x, y, z, localMeta, 24);
        return localMeta;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        Axis preferredAxis = getPreferredAxis(worldIn, x, y, z);
        int meta = worldIn.getBlockMetadata(x, y, z);
        if (preferredAxis != null && (placer == null || !placer.isSneaking())) {
            meta = this.getMetaFromAxis(preferredAxis);
        }
        worldIn.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    public static Axis getPreferredAxis(World world, int x, int y, int z) {
        Axis prefferedAxis = null;
        for (Direction side : Iterate.directions) {
            ChunkCoordinates normal = side.getNormal();
            int neighbourX = x + normal.posX;
            int neighbourY = y + normal.posY;
            int neighbourZ = z + normal.posZ;
            Block neighbour = world.getBlock(neighbourX, neighbourY, neighbourZ);
            if (neighbour instanceof IRotate) {
                if (((IRotate) neighbour)
                    .hasShaftTowards(world, neighbourX, neighbourY, neighbourZ, side.getOpposite()))
                    if (prefferedAxis != null && prefferedAxis != side.getAxis()) {
                        prefferedAxis = null;
                        break;
                    } else {
                        prefferedAxis = side.getAxis();
                    }
            }
        }
        return prefferedAxis;
    }
}
