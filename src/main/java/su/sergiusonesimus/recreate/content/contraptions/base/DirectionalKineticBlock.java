package su.sergiusonesimus.recreate.content.contraptions.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Rotation;
import su.sergiusonesimus.recreate.foundation.utility.Iterate;

public abstract class DirectionalKineticBlock extends KineticBlock {

    public DirectionalKineticBlock(Material materialIn) {
        super(materialIn);
    }

    @Override
    public Direction getDirection(int meta) {
        return Direction.from3DDataValue(meta);
    }

    @Override
    public int getMetaFromDirection(Direction direction) {
        return direction.get3DDataValue();
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ,
        int meta) {
        Direction preferredFacing = getPreferredFacing(worldIn, x, y, z);
        int localMeta = preferredFacing == null ? 0 : this.getMetaFromDirection(preferredFacing.getOpposite());
        worldIn.setBlockMetadataWithNotify(x, y, z, localMeta, 2 | 4);
        return localMeta;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        Direction preferredFacing = getPreferredFacing(worldIn, x, y, z);
        int meta = worldIn.getBlockMetadata(x, y, z);
        if (preferredFacing == null || (placer != null && placer.isSneaking())) {
            Direction nearestLookingDirection = Direction.getNearestLookingDirection(placer);
            meta = this.getMetaFromDirection(
                placer != null && placer.isSneaking() ? nearestLookingDirection
                    : nearestLookingDirection.getOpposite());
        }
        worldIn.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    public Direction getPreferredFacing(World world, int x, int y, int z) {
        Direction prefferedSide = null;
        for (Direction side : Iterate.directions) {
            ChunkCoordinates normal = side.getNormal();
            int neighbourX = x + normal.posX;
            int neighbourY = y + normal.posY;
            int neighbourZ = z + normal.posZ;
            Block neighbour = world.getBlock(neighbourX, neighbourY, neighbourZ);
            if (neighbour instanceof IRotate) {
                if (((IRotate) neighbour)
                    .hasShaftTowards(world, neighbourX, neighbourY, neighbourZ, side.getOpposite()))
                    if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
                        prefferedSide = null;
                        break;
                    } else {
                        prefferedSide = side;
                    }
            }
        }
        return prefferedSide;
    }

    public int rotate(World world, int x, int y, int z, Rotation rot) {
        int meta = world.getBlockMetadata(x, y, z);
        int newMeta = this.getMetaFromDirection(rot.rotate(this.getDirection(meta)));
        if (newMeta != meta) world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        return newMeta;
    }

}
