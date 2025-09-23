package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston.MechanicalPistonBlock.PistonState;
import su.sergiusonesimus.recreate.foundation.block.WrenchableDirectionalBlock;

public class MechanicalPistonHeadBlock extends WrenchableDirectionalBlock {

    public MechanicalPistonHeadBlock(Material material) {
        super(material);
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public int getRenderType() {
        return ReCreate.proxy.getMechanicalPistonHeadBlockRenderID();
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        Direction sideDir = getDirection(side);
        Direction blockDir = getDirection(meta);
        if (sideDir == blockDir) {
            return (isSticky(meta) ? Blocks.sticky_piston : Blocks.piston).getPistonExtensionTexture();
        } else if (sideDir == blockDir.getOpposite()) {
            return Blocks.piston.getPistonExtensionTexture();
        }
        return MechanicalPistonBlock.pistonSide;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getMobilityFlag() {
        return 0;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return new ItemStack(AllBlocks.piston_extension_pole);
    }

    @Override
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player) {
        Direction direction = getDirection(meta);
        ChunkCoordinates pistonHead = new ChunkCoordinates(x, y, z);
        ChunkCoordinates pistonBase = null;
        boolean dropBlocks = player == null || !player.capabilities.isCreativeMode;

        ChunkCoordinates normal = direction.getOpposite()
            .getNormal();
        for (int offset = 1; offset < MechanicalPistonBlock.maxAllowedPistonPoles(); offset++) {
            int currentX = x + normal.posX * offset;
            int currentY = y + normal.posY * offset;
            int currentZ = z + normal.posZ * offset;
            Block block = worldIn.getBlock(currentX, currentY, currentZ);
            int currentMeta = worldIn.getBlockMetadata(currentX, currentY, currentZ);

            if (MechanicalPistonBlock.isExtensionPole(block)
                && direction.getAxis() == ((PistonExtensionPoleBlock) block).getDirection(currentMeta)
                    .getAxis())
                continue;

            if (MechanicalPistonBlock.isPiston(block)
                && ((MechanicalPistonBlock) block).getDirection(currentMeta) == direction)
                pistonBase = new ChunkCoordinates(currentX, currentY, currentZ);

            break;
        }

        if (pistonHead != null && pistonBase != null) {
            int startX;
            int startY;
            int startZ;
            int endX;
            int endY;
            int endZ;
            if (pistonBase.posX < pistonHead.posX) {
                startX = pistonBase.posX;
                endX = pistonHead.posX;
            } else {
                startX = pistonHead.posX;
                endX = pistonBase.posX;
            }
            if (pistonBase.posY < pistonHead.posY) {
                startY = pistonBase.posY;
                endY = pistonHead.posY;
            } else {
                startY = pistonHead.posY;
                endY = pistonBase.posY;
            }
            if (pistonBase.posZ < pistonHead.posZ) {
                startZ = pistonBase.posZ;
                endZ = pistonHead.posZ;
            } else {
                startZ = pistonHead.posZ;
                endZ = pistonBase.posZ;
            }

            for (int tempX = startX; tempX <= endX; tempX++) {
                for (int tempY = startY; tempY <= endY; tempY++) {
                    for (int tempZ = startZ; tempZ <= endZ; tempZ++) {
                        if (tempX != x && tempY != y && tempZ != z) {
                            if (dropBlocks) worldIn.getBlock(tempX, tempY, tempZ)
                                .dropBlockAsItem(
                                    worldIn,
                                    tempX,
                                    tempY,
                                    tempZ,
                                    worldIn.getBlockMetadata(tempX, tempY, tempZ),
                                    0);
                            worldIn.setBlockToAir(tempX, tempY, tempZ);
                        }
                    }
                }
            }

            MechanicalPistonTileEntity te = ((MechanicalPistonTileEntity) worldIn
                .getTileEntity(pistonBase.posX, pistonBase.posY, pistonBase.posZ));
            te.state = PistonState.RETRACTED;
            te.notifyUpdate();
        }

        super.onBlockHarvested(worldIn, x, y, z, meta, player);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list,
        Entity collider) {
        if (mask.intersectsWith(AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D))) {
            list.add(AllBlocks.piston_extension_pole.getCollisionBoundingBoxFromPool(worldIn, x, y, z));
            double minX = 0.0D;
            double minY = 0.0D;
            double minZ = 0.0D;
            double maxX = 1.0D;
            double maxY = 1.0D;
            double maxZ = 1.0D;
            double topHeight = 4.0D / 16.0D;
            switch (this.getDirection(worldIn.getBlockMetadata(x, y, z))) {
                case UP:
                    minY = 1.0D - topHeight;
                    break;
                case DOWN:
                    maxY = topHeight;
                    break;
                case EAST:
                    minX = 1.0D - topHeight;
                    break;
                case WEST:
                    maxX = topHeight;
                    break;
                case SOUTH:
                    minZ = 1.0D - topHeight;
                    break;
                case NORTH:
                    maxZ = topHeight;
                    break;
            }
            list.add(AxisAlignedBB.getBoundingBox(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ));
        }
    }

    public boolean isSticky(int meta) {
        return meta % 12 >= 6;
    }

    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return getDirection(world.getBlockMetadata(x, y, z)) == Direction.fromForgeDirection(side);
    }
}
