package su.sergiusonesimus.recreate.content.contraptions.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import su.sergiusonesimus.recreate.foundation.utility.Iterate;
import su.sergiusonesimus.recreate.util.Direction;
import su.sergiusonesimus.recreate.util.Rotation;
import su.sergiusonesimus.recreate.util.Direction.Axis;

public abstract class HorizontalAxisKineticBlock extends KineticBlock {

	public HorizontalAxisKineticBlock(Material materialIn) {
		super(materialIn);
	}

	@Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		Axis preferredAxis = getPreferredHorizontalAxis(worldIn, x, y, z);
		int meta = worldIn.getBlockMetadata(x, y, z);
		if(preferredAxis != null) {
			meta = this.getMetaFromAxis(preferredAxis);
		} else {
			meta = this.getMetaFromDirection(Direction.getNearestLookingDirection(placer));
		}
		worldIn.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

	public static Axis getPreferredHorizontalAxis(World world, int x, int y, int z) {
		Direction prefferedSide = null;
		for (Direction side : Iterate.horizontalDirections) {
			ChunkCoordinates normal = side.getNormal();
			int neighbourX = x + normal.posX;
			int neighbourY = y + normal.posY;
			int neighbourZ = z + normal.posZ;
			Block neighbour = world.getBlock(neighbourX, neighbourY, neighbourZ);
			if (neighbour instanceof IRotate) {
				if (((IRotate) neighbour).hasShaftTowards(world, neighbourX, neighbourY, neighbourZ, side.getOpposite()))
					if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
						prefferedSide = null;
						break;
					} else {
						prefferedSide = side;
					}
			}
		}
		return prefferedSide == null ? null : prefferedSide.getAxis();
	}

	@Override
	public Axis getAxis(int meta) {
		return meta == 1? Axis.Z : Axis.X;
	}

    public int getMetaFromAxis(Axis axis) {
    	switch(axis) {
    		default:
    		case X:
    			return 0;
    		case Z:
    			return 1;
    	}
    }

	@Override
    public int getMetaFromDirection(Direction direction) {
		return this.getMetaFromAxis(direction.getAxis());
    }

	@Override
	public boolean hasShaftTowards(IBlockAccess world, int x, int y, int z, Direction face) {
		return face.getAxis() == getAxis(world.getBlockMetadata(x, y, z));
	}

	@Override
	public int rotate(World world, int x, int y, int z, Rotation rot) {
		int meta = world.getBlockMetadata(x, y, z);
		switch (rot) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:
			switch (this.getAxis(meta)) {
			case X:
				meta = 1;
				break;
			case Z:
				meta = 0;
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

}
