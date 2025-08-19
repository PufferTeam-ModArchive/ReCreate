package su.sergiusonesimus.recreate.content.contraptions.relays.encased;

import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.content.contraptions.RotationPropagator;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;
import su.sergiusonesimus.recreate.foundation.block.ITE;

@ParametersAreNonnullByDefault
public class GearshiftBlock extends AbstractEncasedShaftBlock implements ITE<SplitShaftTileEntity> {

    public final boolean isPowered;

    public GearshiftBlock(Material materialIn, boolean powered) {
        super(materialIn);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypeWood);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setBlockTextureName("planks_spruce");
        this.isPowered = powered;
        setTickRandomly(true);
    }

    protected GearshiftBlock getBlockPowered() {
        return AllBlocks.powered_gearshift;
    }

    protected GearshiftBlock getBlockUnpowered() {
        return AllBlocks.unpowered_gearshift;
    }

    public void onBlockAdded(World worldIn, int x, int y, int z) {
        if (worldIn.isBlockIndirectlyGettingPowered(x, y, z)) {
            worldIn.scheduleBlockUpdate(x, y, z, this, 1);
        }
        super.onBlockAdded(worldIn, x, y, z);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (worldIn.isRemote) return;

        boolean isNowPowered = worldIn.isBlockIndirectlyGettingPowered(x, y, z);
        if (this.isPowered != isNowPowered) {
            detachKinetics(worldIn, x, y, z, true);
            worldIn.setBlock(
                x,
                y,
                z,
                isNowPowered ? this.getBlockPowered() : this.getBlockUnpowered(),
                worldIn.getBlockMetadata(x, y, z),
                2);
        }
    }

    public void detachKinetics(World worldIn, int x, int y, int z, boolean reAttachNextTick) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te == null || !(te instanceof KineticTileEntity kte)) return;
        RotationPropagator.handleRemoved(worldIn, x, y, z, kte);

        // Re-attach next tick
        if (reAttachNextTick) worldIn.scheduleBlockUpdate(x, y, z, this, 4);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te == null || !(te instanceof KineticTileEntity kte)) return;
        RotationPropagator.handleAdded(worldIn, x, y, z, kte);
    }

    @Override
    public Class<? extends SplitShaftTileEntity> getTileEntityClass() {
        return GearshiftTileEntity.class;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return ReCreate.proxy.getGearshiftBlockRenderID();
    }

    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(AllBlocks.unpowered_gearshift);
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemFromBlock(AllBlocks.unpowered_gearshift);
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int meta) {
        return new ItemStack(AllBlocks.unpowered_gearshift);
    }
}
