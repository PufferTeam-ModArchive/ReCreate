package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.bearing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.foundation.block.WrenchableDirectionalBlock;
import su.sergiusonesimus.recreate.foundation.utility.Iterate;
import su.sergiusonesimus.recreate.foundation.utility.Pair;
import su.sergiusonesimus.recreate.foundation.utility.placement.IPlacementHelper;
import su.sergiusonesimus.recreate.foundation.utility.placement.PlacementHelpers;
import su.sergiusonesimus.recreate.foundation.utility.placement.PlacementOffset;

public class SailBlock extends WrenchableDirectionalBlock {

    public static SailBlock frame() {
        return new SailBlock(true, null);
    }

    public static SailBlock withCanvas(Integer color) {
        return new SailBlock(false, color);
    }

    public static IIcon[] coloredSails = new IIcon[ItemDye.field_150921_b.length];
    public static IIcon frameFront;
    public static IIcon frameSide;

    private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    protected final boolean frame;
    protected final Integer color;

    protected SailBlock(boolean frame, Integer color) {
        super(Material.cloth);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypeWood);
        this.frame = frame;
        this.color = color;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public int getRenderType() {
        return ReCreate.proxy.getSailBlockRenderID();
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        IIcon defaultIcon = this.getIcon(side, world.getBlockMetadata(x, y, z));
        if (world.getBlock(x, y, z) instanceof SailBlock) return defaultIcon;
        else {
            MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
            if (mop == null || mop.typeOfHit != MovingObjectType.BLOCK
                || !(world.getBlock(mop.blockX, mop.blockY, mop.blockZ) instanceof SailBlock)) return defaultIcon;
            return this.getIcon(side, world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ));
        }

    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        Direction blockDir = getDirection(meta);
        Direction sideDir = Direction.from3DDataValue(side);
        if (sideDir == blockDir) {
            if (frame) return frameFront;
            else return coloredSails[color];
        } else {
            if (sideDir == blockDir.getOpposite()) return frameFront;
            else return frameSide;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list,
        Entity collider) {
        AxisAlignedBB testBB = getBoundingBox(worldIn, x, y, z);

        if (mask.intersectsWith(testBB)) {
            double pixel = 1.0D / 16D;
            switch (getDirection(worldIn.getBlockMetadata(x, y, z))) {
                case UP:
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 1.0D - 4 * pixel,
                            z + 0.0D,
                            x + 3 * pixel,
                            y + 1.0D,
                            z + 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 1.0D - 4 * pixel,
                            z + 1.0D - 3 * pixel,
                            x + 3 * pixel,
                            y + 1.0D,
                            z + 1.0D));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 1.0D - 4 * pixel,
                            z + 0.0D,
                            x + 1.0D,
                            y + 1.0D,
                            z + 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 1.0D - 4 * pixel,
                            z + 1.0D - 3 * pixel,
                            x + 1.0D,
                            y + 1.0D,
                            z + 1.0D));

                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 1.0D - 3 * pixel,
                            z + 3 * pixel,
                            x + 2 * pixel,
                            y + 1.0D,
                            z + 1.0D - 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 2 * pixel,
                            y + 1.0D - 3 * pixel,
                            z + 3 * pixel,
                            x + 1.0D,
                            y + 1.0D,
                            z + 1.0D - 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 3 * pixel,
                            y + 1.0D - 3 * pixel,
                            z + 0.0D,
                            x + 1.0D - 3 * pixel,
                            y + 1.0D,
                            z + 2 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 3 * pixel,
                            y + 1.0D - 3 * pixel,
                            z + 1.0D - 2 * pixel,
                            x + 1.0D - 3 * pixel,
                            y + 1.0D,
                            z + 1.0D));
                    if (!frame) list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 2 * pixel,
                            y + 1.0D - pixel,
                            z + 2 * pixel,
                            x + 1.0D - 2 * pixel,
                            y + 1.0D,
                            z + 1.0D - 2 * pixel));
                    break;
                case DOWN:
                    list.add(
                        AxisAlignedBB
                            .getBoundingBox(x + 0.0D, y + 0.0D, z + 0.0D, x + 3 * pixel, y + 4 * pixel, z + 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 0.0D,
                            z + 1.0D - 3 * pixel,
                            x + 3 * pixel,
                            y + 4 * pixel,
                            z + 1.0D));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 0.0D,
                            z + 0.0D,
                            x + 1.0D,
                            y + 4 * pixel,
                            z + 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 0.0D,
                            z + 1.0D - 3 * pixel,
                            x + 1.0D,
                            y + 4 * pixel,
                            z + 1.0D));

                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 0.0D,
                            z + 3 * pixel,
                            x + 2 * pixel,
                            y + 3 * pixel,
                            z + 1.0D - 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 2 * pixel,
                            y + 0.0D,
                            z + 3 * pixel,
                            x + 1.0D,
                            y + 3 * pixel,
                            z + 1.0D - 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 3 * pixel,
                            y + 0.0D,
                            z + 0.0D,
                            x + 1.0D - 3 * pixel,
                            y + 3 * pixel,
                            z + 2 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 3 * pixel,
                            y + 0.0D,
                            z + 1.0D - 2 * pixel,
                            x + 1.0D - 3 * pixel,
                            y + 3 * pixel,
                            z + 1.0D));
                    if (!frame) list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 2 * pixel,
                            y + 0.0D,
                            z + 2 * pixel,
                            x + 1.0D - 2 * pixel,
                            y + pixel,
                            z + 1.0D - 2 * pixel));
                    break;
                case EAST:
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 4 * pixel,
                            y + 0.0D,
                            z + 0.0D,
                            x + 1.0D,
                            y + 3 * pixel,
                            z + 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 4 * pixel,
                            y + 0.0D,
                            z + 1.0D - 3 * pixel,
                            x + 1.0D,
                            y + 3 * pixel,
                            z + 1.0D));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 4 * pixel,
                            y + 1.0D - 3 * pixel,
                            z + 0.0D,
                            x + 1.0D,
                            y + 1.0D,
                            z + 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 4 * pixel,
                            y + 1.0D - 3 * pixel,
                            z + 1.0D - 3 * pixel,
                            x + 1.0D,
                            y + 1.0D,
                            z + 1.0D));

                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 0.0D,
                            z + 3 * pixel,
                            x + 1.0D,
                            y + 2 * pixel,
                            z + 1.0D - 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 1.0D - 2 * pixel,
                            z + 3 * pixel,
                            x + 1.0D,
                            y + 1.0D,
                            z + 1.0D - 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 3 * pixel,
                            z + 0.0D,
                            x + 1.0D,
                            y + 1.0D - 3 * pixel,
                            z + 2 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 3 * pixel,
                            z + 1.0D - 2 * pixel,
                            x + 1.0D,
                            y + 1.0D - 3 * pixel,
                            z + 1.0D));
                    if (!frame) list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - pixel,
                            y + 2 * pixel,
                            z + 2 * pixel,
                            x + 1.0D,
                            y + 1.0D - 2 * pixel,
                            z + 1.0D - 2 * pixel));
                    break;
                case WEST:
                    list.add(
                        AxisAlignedBB
                            .getBoundingBox(x + 0.0D, y + 0.0D, z + 0.0D, x + 4 * pixel, y + 3 * pixel, z + 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 0.0D,
                            z + 1.0D - 3 * pixel,
                            x + 4 * pixel,
                            y + 3 * pixel,
                            z + 1.0D));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 1.0D - 3 * pixel,
                            z + 0.0D,
                            x + 4 * pixel,
                            y + 1.0D,
                            z + 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 1.0D - 3 * pixel,
                            z + 1.0D - 3 * pixel,
                            x + 4 * pixel,
                            y + 1.0D,
                            z + 1.0D));

                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 0.0D,
                            z + 3 * pixel,
                            x + 3 * pixel,
                            y + 2 * pixel,
                            z + 1.0D - 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 1.0D - 2 * pixel,
                            z + 3 * pixel,
                            x + 3 * pixel,
                            y + 1.0D,
                            z + 1.0D - 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 3 * pixel,
                            z + 0.0D,
                            x + 3 * pixel,
                            y + 1.0D - 3 * pixel,
                            z + 2 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 3 * pixel,
                            z + 1.0D - 2 * pixel,
                            x + 3 * pixel,
                            y + 1.0D - 3 * pixel,
                            z + 1.0D));
                    if (!frame) list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 2 * pixel,
                            z + 2 * pixel,
                            x + pixel,
                            y + 1.0D - 2 * pixel,
                            z + 1.0D - 2 * pixel));
                    break;
                case SOUTH:
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 0.0D,
                            z + 1.0D - 4 * pixel,
                            x + 3 * pixel,
                            y + 3 * pixel,
                            z + 1.0D));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 1.0D - 3 * pixel,
                            z + 1.0D - 4 * pixel,
                            x + 3 * pixel,
                            y + 1.0D,
                            z + 1.0D));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 0.0D,
                            z + 1.0D - 4 * pixel,
                            x + 1.0D,
                            y + 3 * pixel,
                            z + 1.0D));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 1.0D - 3 * pixel,
                            z + 1.0D - 4 * pixel,
                            x + 1.0D,
                            y + 1.0D,
                            z + 1.0D));

                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 3 * pixel,
                            z + 1.0D - 3 * pixel,
                            x + 2 * pixel,
                            y + 1.0D - 3 * pixel,
                            z + 1.0D));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 2 * pixel,
                            y + 3 * pixel,
                            z + 1.0D - 3 * pixel,
                            x + 1.0D,
                            y + 1.0D - 3 * pixel,
                            z + 1.0D));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 3 * pixel,
                            y + 0.0D,
                            z + 1.0D - 3 * pixel,
                            x + 1.0D - 3 * pixel,
                            y + 2 * pixel,
                            z + 1.0D));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 3 * pixel,
                            y + 1.0D - 2 * pixel,
                            z + 1.0D - 3 * pixel,
                            x + 1.0D - 3 * pixel,
                            y + 1.0D,
                            z + 1.0D));
                    if (!frame) list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 2 * pixel,
                            y + 2 * pixel,
                            z + 1.0D - pixel,
                            x + 1.0D - 2 * pixel,
                            y + 1.0D - 2 * pixel,
                            z + 1.0D));
                    break;
                case NORTH:
                    list.add(
                        AxisAlignedBB
                            .getBoundingBox(x + 0.0D, y + 0.0D, z + 0.0D, x + 3 * pixel, y + 3 * pixel, z + 4 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 1.0D - 3 * pixel,
                            z + 0.0D,
                            x + 3 * pixel,
                            y + 1.0D,
                            z + 4 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 0.0D,
                            z + 0.0D,
                            x + 1.0D,
                            y + 3 * pixel,
                            z + 4 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 3 * pixel,
                            y + 1.0D - 3 * pixel,
                            z + 0.0D,
                            x + 1.0D,
                            y + 1.0D,
                            z + 4 * pixel));

                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 0.0D,
                            y + 3 * pixel,
                            z + 0.0D,
                            x + 2 * pixel,
                            y + 1.0D - 3 * pixel,
                            z + 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 1.0D - 2 * pixel,
                            y + 3 * pixel,
                            z + 0.0D,
                            x + 1.0D,
                            y + 1.0D - 3 * pixel,
                            z + 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 3 * pixel,
                            y + 0.0D,
                            z + 0.0D,
                            x + 1.0D - 3 * pixel,
                            y + 2 * pixel,
                            z + 3 * pixel));
                    list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 3 * pixel,
                            y + 1.0D - 2 * pixel,
                            z + 0.0D,
                            x + 1.0D - 3 * pixel,
                            y + 1.0D,
                            z + 3 * pixel));
                    if (!frame) list.add(
                        AxisAlignedBB.getBoundingBox(
                            x + 2 * pixel,
                            y + 2 * pixel,
                            z + 0.0D,
                            x + 1.0D - 2 * pixel,
                            y + 1.0D - 2 * pixel,
                            z + pixel));
                    break;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return getBoundingBox(worldIn, x, y, z);
    }

    public AxisAlignedBB getBoundingBox(IBlockAccess worldIn, int x, int y, int z) {
        Direction direction = getDirection(worldIn.getBlockMetadata(x, y, z));
        double pixel = 1.0D / 16D;
        double minX = 0.0D;
        double minY = 0.0D;
        double minZ = 0.0D;
        double maxX = 1.0D;
        double maxY = 1.0D;
        double maxZ = 1.0D;

        switch (direction) {
            case UP:
                minY = 1.0D - 4 * pixel;
                break;
            case DOWN:
                maxY = 4 * pixel;
                break;
            case EAST:
                minX = 1.0D - 4 * pixel;
                break;
            case WEST:
                maxX = 4 * pixel;
                break;
            case SOUTH:
                minZ = 1.0D - 4 * pixel;
                break;
            case NORTH:
                maxZ = 4 * pixel;
                break;
        }

        return AxisAlignedBB.getBoundingBox(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem == null) return false;
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        MovingObjectPosition ray = new MovingObjectPosition(
            x,
            y,
            z,
            side,
            Vec3.createVectorHelper(subX, subY, subZ),
            true);

        IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);
        if (placementHelper.matchesItem(heldItem))
            return placementHelper.getOffset(player, world, block, meta, x, y, z, ray)
                .placeInWorld(world, (ItemBlock) heldItem.getItem(), player, ray);

        if (heldItem.getItem() instanceof ItemShears) {
            if (!world.isRemote) applyDye(world, x, y, z, null);
            return true;
        }

        if (frame) return false;

        if (heldItem.getItem() instanceof ItemDye dye) {
            if (!world.isRemote) applyDye(world, x, y, z, heldItem.getItemDamage());
            return true;
        }

        return false;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        Direction direction = getDirection(worldIn.getBlockMetadata(x, y, z));
        double pixel = 1.0D / 16D;
        double minX = 0.0D;
        double minY = 0.0D;
        double minZ = 0.0D;
        double maxX = 1.0D;
        double maxY = 1.0D;
        double maxZ = 1.0D;

        switch (direction) {
            case UP:
                minY = 1.0D - 4 * pixel;
                break;
            case DOWN:
                maxY = 4 * pixel;
                break;
            case EAST:
                minX = 1.0D - 4 * pixel;
                break;
            case WEST:
                maxX = 4 * pixel;
                break;
            case SOUTH:
                minZ = 1.0D - 4 * pixel;
                break;
            case NORTH:
                maxZ = 4 * pixel;
                break;
        }

        setBlockBounds((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        frameFront = reg.registerIcon(ReCreate.ID + ":sail/frame_front");
        frameSide = reg.registerIcon(ReCreate.ID + ":sail/frame_side");
        for (int i = 0; i < ItemDye.field_150921_b.length; i++) {
            coloredSails[i] = reg.registerIcon(ReCreate.ID + ":sail/canvas_" + ItemDye.field_150921_b[i]);
        }
    }

    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return !frame && getDirection(world.getBlockMetadata(x, y, z)) == Direction.fromForgeDirection(side);
    }

    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
        return this.applyDye(world, x, y, z, colour);
    }

    protected boolean applyDye(World world, int x, int y, int z, Integer color) {
        SailBlock block = (SailBlock) world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        Axis blockAxis = block.getAxis(meta);
        Block newBlock = (color == null ? AllBlocks.sail_frame : AllBlocks.sails[color]);

        // Dye the block itself
        if (block != newBlock) {
            world.setBlock(x, y, z, newBlock, meta, 2);
            return true;
        }

        // Dye all adjacent
        for (Direction d : Iterate.directions) {
            if (d.getAxis() == blockAxis) continue;
            ChunkCoordinates normal = d.getNormal();
            int adjacentX = x + normal.posX;
            int adjacentY = y + normal.posY;
            int adjacentZ = z + normal.posZ;
            Block adjacentBlock = world.getBlock(adjacentX, adjacentY, adjacentZ);
            if (!(adjacentBlock instanceof SailBlock sail) || sail.frame) continue;
            if (adjacentBlock == newBlock) continue;
            world.setBlock(adjacentX, adjacentY, adjacentZ, newBlock, meta, 2);
            return true;
        }

        // Dye all the things
        List<ChunkCoordinates> frontier = new ArrayList<ChunkCoordinates>();
        frontier.add(new ChunkCoordinates(x, y, z));
        Set<ChunkCoordinates> visited = new HashSet<ChunkCoordinates>();
        int timeout = 100;
        boolean result = false;
        while (!frontier.isEmpty()) {
            if (timeout-- < 0) break;

            ChunkCoordinates currentPos = frontier.remove(0);
            visited.add(currentPos);

            for (Direction d : Iterate.directions) {
                if (d.getAxis() == blockAxis) continue;
                ChunkCoordinates normal = d.getNormal();
                int adjacentX = currentPos.posX + normal.posX;
                int adjacentY = currentPos.posY + normal.posY;
                int adjacentZ = currentPos.posZ + normal.posZ;
                ChunkCoordinates adjacentPos = new ChunkCoordinates(adjacentX, adjacentY, adjacentZ);
                if (visited.contains(adjacentPos)) continue;
                Block adjacentBlock = world.getBlock(adjacentX, adjacentY, adjacentZ);
                if (!(adjacentBlock instanceof SailBlock sail) || sail.frame && color != null) continue;
                if (adjacentBlock != newBlock) {
                    world.setBlock(adjacentX, adjacentY, adjacentZ, newBlock, meta, 2);
                    result = true;
                }
                frontier.add(adjacentPos);
                visited.add(adjacentPos);
            }
        }

        return result;
    }

    @Override
    public void onFallenUpon(World worldIn, int x, int y, int z, Entity entityIn, float fallDistance) {
        if (!frame && this.getDirection(worldIn.getBlockMetadata(x, y, z))
            .getAxis() == Axis.Y) {
            entityIn.fallDistance = 0;
            if (!entityIn.isSneaking()) this.bounce(entityIn);
        }
    }

    private void bounce(Entity entity) {
        if (entity.motionY < 0.0D) {
            double bounceFactor = entity instanceof EntityLivingBase ? 1.0D : 0.8D;
            entity.motionY = -entity.motionY * (double) 0.26F * bounceFactor;
            entity.velocityChanged = true;
        }

    }

    public boolean isFrame() {
        return frame;
    }

    public Integer getColor() {
        return color;
    }

    public MapColor getMapColor(int meta) {
        return frame ? MapColor.woodColor : MapColor.getMapColorForBlockColored(color);
    }

    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
        MovingObjectPosition result = super.collisionRayTrace(worldIn, x, y, z, startVec, endVec);
        if (result != null && frame) {
            double frame = 3.0D / 16.0D;
            int counter = 0
                + ((result.hitVec.xCoord % 1.0D) > frame && (result.hitVec.xCoord % 1.0D) < (1 - frame) ? 1 : 0)
                + ((result.hitVec.yCoord % 1.0D) > frame && (result.hitVec.yCoord % 1.0D) < (1 - frame) ? 1 : 0)
                + ((result.hitVec.zCoord % 1.0D) > frame && (result.hitVec.zCoord % 1.0D) < (1 - frame) ? 1 : 0);
            if (counter >= 2) return null;
        }
        return result;
    }

    private static class PlacementHelper implements IPlacementHelper {

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return i -> i != null && i.getItem() instanceof ItemBlock ib
                && Block.getBlockFromItem(ib) instanceof SailBlock;
        }

        @Override
        public Predicate<Pair<Block, Integer>> getBlockPredicate() {
            return s -> s != null && s.getFirst() instanceof SailBlock;
        }

        @Override
        public PlacementOffset getOffset(EntityPlayer player, World world, Block block, int meta, int x, int y, int z,
            MovingObjectPosition ray) {
            List<Direction> directions = IPlacementHelper
                .orderedByDistanceExceptAxis(x, y, z, ray.hitVec, ((SailBlock) block).getAxis(meta), dir -> {
                    ChunkCoordinates normal = dir.getNormal();
                    return world.getBlock(x + normal.posX, y + normal.posY, z + normal.posZ)
                        .getMaterial()
                        .isReplaceable();
                });

            if (directions.isEmpty()) return PlacementOffset.fail();
            else {
                ChunkCoordinates offset = directions.get(0)
                    .getNormal();
                offset.posX += x;
                offset.posY += y;
                offset.posZ += z;
                return PlacementOffset.success(offset, s -> {
                    s.setSecond(meta);
                    return s;
                });
            }
        }
    }
}
