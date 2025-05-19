package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.content.contraptions.base.IRotate;
import su.sergiusonesimus.recreate.content.contraptions.base.RotatedPillarKineticBlock;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.AbstractShaftBlock;
import su.sergiusonesimus.recreate.foundation.block.ITE;
import su.sergiusonesimus.recreate.foundation.utility.Iterate;
import su.sergiusonesimus.recreate.util.Direction;
import su.sergiusonesimus.recreate.util.Direction.Axis;

@ParametersAreNonnullByDefault
public class CogWheelBlock extends AbstractShaftBlock implements ITE<CogWheelTileEntity>, ICogWheel {

	boolean isLarge;

	protected CogWheelBlock(Material materialIn, boolean large) {
		super(materialIn);
		this.setHardness(2.0F);
		this.setResistance(5.0F);
		this.setStepSound(soundTypeWood);
		this.setBlockTextureName("planks_spruce");
		isLarge = large;
	}

	public static CogWheelBlock small(Material materialIn) {
		return new CogWheelBlock(materialIn, false);
	}

	public static CogWheelBlock large(Material materialIn) {
		return new CogWheelBlock(materialIn, true);
	}

	@Override
	public boolean isLargeCog() {
		return isLarge;
	}

	@Override
	public boolean isSmallCog() {
		return !isLarge;
	}

	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
    	if(worldIn == null) return;
    	int meta = worldIn.getBlockMetadata(x, y, z);
    	double d = 1d / 16d;
    	double minThickness = 6 * d;
    	double maxThickness = 1D - 6 * d;
    	double minDiameter = isSmallCog()? 2 * d : 0D;
    	double maxDiameter = isSmallCog()? 1d - 2 * d : 1D;
    	switch(meta) {
    		default:
    		case 0:
    			minX = minZ = minDiameter;
    			maxX = maxZ = maxDiameter;
    			minY = minThickness;
    			maxY = maxThickness;
    			break;
    		case 1:
    			minY = minZ = minDiameter;
    			maxY = maxZ = maxDiameter;
    			minX = minThickness;
    			maxX = maxThickness;
    			break;
    		case 2:
    			minX = minY = minDiameter;
    			maxX = maxY = maxDiameter;
    			minZ = minThickness;
    			maxZ = maxThickness;
    			break;
    	}
    }

	@Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider)
    {
    	int meta = worldIn.getBlockMetadata(x, y, z);
    	double shaftMinX = 0;
    	double shaftMinY = 0;
    	double shaftMinZ = 0;
    	double shaftMaxX = 1;
    	double shaftMaxY = 1;
    	double shaftMaxZ = 1;
    	double cogMinX = 0;
    	double cogMinY = 0;
    	double cogMinZ = 0;
    	double cogMaxX = 1;
    	double cogMaxY = 1;
    	double cogMaxZ = 1;
    	double d = 1d / 16d;
    	double minThickness = 6 * d;
    	double maxThickness = 1D - 6 * d;
    	double minDiameter = isSmallCog()? 2 * d : 0D;
    	double maxDiameter = isSmallCog()? 1d - 2 * d : 1D;
    	switch(meta) {
    		default:
    		case 0:
    			shaftMinX = shaftMinZ = 6 * d;
    			shaftMaxX = shaftMaxZ = 1 - 6 * d;
    			cogMinX = cogMinZ = minDiameter;
    			cogMaxX = cogMaxZ = maxDiameter;
    			cogMinY = minThickness;
    			cogMaxY = maxThickness;
    			break;
    		case 1:
    			shaftMinY = shaftMinZ = 6 * d;
    			shaftMaxY = shaftMaxZ = 1 - 6 * d;
    			cogMinY = cogMinZ = minDiameter;
    			cogMaxY = cogMaxZ = maxDiameter;
    			cogMinX = minThickness;
    			cogMaxX = maxThickness;
    			break;
    		case 2:
    			shaftMinX = shaftMinY = 6 * d;
    			shaftMaxX = shaftMaxY = 1 - 6 * d;
    			cogMinX = cogMinY = minDiameter;
    			cogMaxX = cogMaxY = maxDiameter;
    			cogMinZ = minThickness;
    			cogMaxZ = maxThickness;
    			break;
    	}
    	AxisAlignedBB shaft = AxisAlignedBB.getBoundingBox(x + shaftMinX, y + shaftMinY, z + shaftMinZ, x + shaftMaxX, y + shaftMaxY, z + shaftMaxZ);
    	AxisAlignedBB cog = AxisAlignedBB.getBoundingBox(x + cogMinX, y + cogMinY, z + cogMinZ, x + cogMaxX, y + cogMaxY, z + cogMaxZ);

        if (shaft != null && mask.intersectsWith(shaft)) list.add(shaft);
        if (cog != null && mask.intersectsWith(cog)) list.add(cog);
    }

    @Override
    public int getRenderType() {
        return ReCreate.proxy.getCogWheelBlockRenderID();
    }

	@Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
		CogWheelBlock block = (CogWheelBlock) worldIn.getBlock(x, y, z);
		int meta = worldIn.getBlockMetadata(x, y, z);
		return isValidCogwheelPosition(ICogWheel.isLargeCog(block), worldIn, x, y, z, block.getAxis(meta));
	}

	@Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player,
    		int side, float subX, float subY, float subZ) {
		if (player.isSneaking() || player.isPlayerSleeping() || player.isRiding())
			return false;

		//TODO
//		ItemStack heldItem = player.getHeldItem();
//		EncasedCogwheelBlock[] encasedBlocks = isLarge
//			? new EncasedCogwheelBlock[] { AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL.get(),
//				AllBlocks.BRASS_ENCASED_LARGE_COGWHEEL.get() }
//			: new EncasedCogwheelBlock[] { AllBlocks.ANDESITE_ENCASED_COGWHEEL.get(),
//				AllBlocks.BRASS_ENCASED_COGWHEEL.get() };
//
//		for (EncasedCogwheelBlock encasedCog : encasedBlocks) {
//			if (!encasedCog.getCasing()
//				.isIn(heldItem))
//				continue;
//
//			if (world.isRemote)
//				return true;
//
//			BlockState encasedState = encasedCog.defaultBlockState()
//				.setValue(AXIS, state.getValue(AXIS));
//
//			for (Direction d : Iterate.directionsInAxis(state.getValue(AXIS))) {
//				BlockState adjacentState = world.getBlockState(pos.relative(d));
//				if (!(adjacentState.getBlock() instanceof IRotate))
//					continue;
//				IRotate def = (IRotate) adjacentState.getBlock();
//				if (!def.hasShaftTowards(world, pos.relative(d), adjacentState, d.getOpposite()))
//					continue;
//				encasedState =
//					encasedState.cycle(d.getAxisDirection() == AxisDirection.POSITIVE ? EncasedCogwheelBlock.TOP_SHAFT
//						: EncasedCogwheelBlock.BOTTOM_SHAFT);
//			}
//			
//			KineticTileEntity.switchToBlockState(world, pos, encasedState);
//			return true;
//		}

		return false;
	}

	public static boolean isValidCogwheelPosition(boolean large, IBlockAccess worldIn, int x, int y, int z, Axis cogAxis) {
		for (Direction facing : Iterate.directions) {
			if (facing.getAxis() == cogAxis)
				continue;

			ChunkCoordinates facingNormal = facing.getNormal();
			Block block = worldIn.getBlock(x + facingNormal.posX, y + facingNormal.posY, z + facingNormal.posZ);
			int meta = worldIn.getBlockMetadata(x + facingNormal.posX, y + facingNormal.posY, z + facingNormal.posZ);
			if (block instanceof RotatedPillarKineticBlock
					&& facing.getAxis() == ((RotatedPillarKineticBlock)block).getAxis(meta))
				continue;

			if (ICogWheel.isLargeCog(block) || large && ICogWheel.isSmallCog(block))
				return false;
		}
		return true;
	}

	@Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta)
    {
		int localMeta = this.getMetaFromDirection(Direction.from3DDataValue(side));
		worldIn.setBlockMetadataWithNotify(x, y, z, localMeta, 24);
		return localMeta;
    }

	@Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		Axis preferredAxis = getAxisForPlacement(worldIn, x, y, z);
		int meta = worldIn.getBlockMetadata(x, y, z);
		if(preferredAxis != null && (placer == null || !placer.isSneaking())) {
			meta = this.getMetaFromAxis(preferredAxis);
		}
		worldIn.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

	protected Axis getAxisForPlacement(World worldIn, int x, int y, int z) {
		//TODO
//		Block blockBelow = worldIn.getBlock(x, y - 1, z);
//		int metaBelow = worldIn.getBlockMetadata(x, y - 1, z);
//
//		if (AllBlocks.ROTATION_SPEED_CONTROLLER.has(blockBelow) && isLargeCog())
//			return blockBelow.getValue(SpeedControllerBlock.HORIZONTAL_AXIS) == Axis.X ? Axis.Z : Axis.X;

		MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
		Direction sideNormal = Direction.from3DDataValue(mop.sideHit);
		ChunkCoordinates normalAgainst = sideNormal.getOpposite().getNormal();

		Block blockAgainst = worldIn.getBlock(x + normalAgainst.posX, y + normalAgainst.posY, z + normalAgainst.posZ);
		int metaAgainst = worldIn.getBlockMetadata(x + normalAgainst.posX, y + normalAgainst.posY, z + normalAgainst.posZ);
		if (ICogWheel.isSmallCog(blockAgainst))
			return ((IRotate) blockAgainst).getAxis(metaAgainst);

		Axis preferredAxis = getPreferredAxis(worldIn, x, y, z);
		return preferredAxis != null ? preferredAxis
			: sideNormal.getAxis();
	}

	@Override
	public float getParticleTargetRadius() {
		return isLargeCog() ? 1.125f : .65f;
	}

	@Override
	public float getParticleInitialRadius() {
		return isLargeCog() ? 1f : .75f;
	}

	@Override
	public boolean isDedicatedCogWheel() {
		return true;
	}

	@Override
	public Class<CogWheelTileEntity> getTileEntityClass() {
		return CogWheelTileEntity.class;
	}
}
