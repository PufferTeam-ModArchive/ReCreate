package su.sergiusonesimus.recreate.content.contraptions.relays.encased;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import su.sergiusonesimus.recreate.content.contraptions.RotationPropagator;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;

import java.util.Random;

public class AbstractRedstoneShaftBlock extends AbstractEncasedShaftBlock {
    private final boolean field_150171_a;

    public AbstractRedstoneShaftBlock(Material materialIn, boolean p_i45421_1_) {
        super(materialIn);

        setTickRandomly(true);
        this.field_150171_a = p_i45421_1_;
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        super.onBlockAdded(worldIn, x, y, z);

        if (!worldIn.isRemote)
        {
            if (this.field_150171_a && !worldIn.isBlockIndirectlyGettingPowered(x, y, z))
            {
                worldIn.scheduleBlockUpdate(x, y, z, this, 4);
            }
            else if (!this.field_150171_a && worldIn.isBlockIndirectlyGettingPowered(x, y, z))
            {
                worldIn.setBlock(x, y, z, this.getLitBlock(), worldIn.getBlockMetadata(x, y, z), 2);
                detachKinetics(worldIn, x, y, z, true);
                this.updateTileEntity(worldIn, x, y, z);
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);

        if (!worldIn.isRemote)
        {
            if (this.field_150171_a && !worldIn.isBlockIndirectlyGettingPowered(x, y, z))
            {
                worldIn.scheduleBlockUpdate(x, y, z, this, 4);
            }
            else if (!this.field_150171_a && worldIn.isBlockIndirectlyGettingPowered(x, y, z))
            {
                worldIn.setBlock(x, y, z, this.getLitBlock(), worldIn.getBlockMetadata(x, y, z), 2);
                detachKinetics(worldIn, x, y, z, true);
                this.updateTileEntity(worldIn, x, y, z);
            }
        }
    }

    public void detachKinetics(World worldIn, int x, int y, int z, boolean reAttachNextTick) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te == null || !(te instanceof KineticTileEntity))
            return;
        RotationPropagator.handleRemoved(worldIn, x, y, z, (KineticTileEntity) te);

        // Re-attach next tick
        if (reAttachNextTick)
            worldIn.scheduleBlockUpdate(x, y, z, this, 4);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        super.updateTick(worldIn, x, y, z, random);

        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te == null || !(te instanceof KineticTileEntity kte))
            return;
        RotationPropagator.handleAdded(worldIn, x, y, z, kte);

        if (!worldIn.isRemote && this.field_150171_a && !worldIn.isBlockIndirectlyGettingPowered(x, y, z))
        {
            worldIn.setBlock(x, y, z, this.getUnlitBlock(), worldIn.getBlockMetadata(x, y, z), 2);
            this.updateTileEntity(worldIn, x, y, z);
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        return new ItemStack(getUnlitBlock(), 1, 0);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
        if(this != getUnlitBlock()) {
            return getUnlitBlock().getItemDropped(0, random, fortune);
        }
        return super.getItemDropped(0, random, fortune);
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z)
    {
        return Item.getItemFromBlock(getUnlitBlock());
    }

    public Block getUnlitBlock() {
        return Blocks.redstone_lamp;
    }

    public Block getLitBlock() {
        return Blocks.redstone_lamp;
    }

    public boolean isPowered() {
        return this.field_150171_a;
    }
 }
