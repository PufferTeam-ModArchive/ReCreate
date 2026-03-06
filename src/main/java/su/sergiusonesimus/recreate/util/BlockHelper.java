package su.sergiusonesimus.recreate.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;

public class BlockHelper {

    // TODO Not needed?
    // public static BlockState setZeroAge(BlockState blockState) {
    // if (blockState.hasProperty(BlockStateProperties.AGE_1))
    // return blockState.setValue(BlockStateProperties.AGE_1, 0);
    // if (blockState.hasProperty(BlockStateProperties.AGE_2))
    // return blockState.setValue(BlockStateProperties.AGE_2, 0);
    // if (blockState.hasProperty(BlockStateProperties.AGE_3))
    // return blockState.setValue(BlockStateProperties.AGE_3, 0);
    // if (blockState.hasProperty(BlockStateProperties.AGE_5))
    // return blockState.setValue(BlockStateProperties.AGE_5, 0);
    // if (blockState.hasProperty(BlockStateProperties.AGE_7))
    // return blockState.setValue(BlockStateProperties.AGE_7, 0);
    // if (blockState.hasProperty(BlockStateProperties.AGE_15))
    // return blockState.setValue(BlockStateProperties.AGE_15, 0);
    // if (blockState.hasProperty(BlockStateProperties.AGE_25))
    // return blockState.setValue(BlockStateProperties.AGE_25, 0);
    // if (blockState.hasProperty(BlockStateProperties.LEVEL_HONEY))
    // return blockState.setValue(BlockStateProperties.LEVEL_HONEY, 0);
    // if (blockState.hasProperty(BlockStateProperties.HATCH))
    // return blockState.setValue(BlockStateProperties.HATCH, 0);
    // if (blockState.hasProperty(BlockStateProperties.STAGE))
    // return blockState.setValue(BlockStateProperties.STAGE, 0);
    // if (blockState.hasProperty(BlockStateProperties.LEVEL_CAULDRON))
    // return blockState.setValue(BlockStateProperties.LEVEL_CAULDRON, 0);
    // if (blockState.hasProperty(BlockStateProperties.LEVEL_COMPOSTER))
    // return blockState.setValue(BlockStateProperties.LEVEL_COMPOSTER, 0);
    // if (blockState.hasProperty(BlockStateProperties.EXTENDED))
    // return blockState.setValue(BlockStateProperties.EXTENDED, false);
    // return blockState;
    // }

    public static int findAndRemoveInInventory(Block block, EntityPlayer player, int amount) {
        return findAndRemoveInInventory(block, 0, player, amount);
    }

    public static int findAndRemoveInInventory(Block block, int meta, EntityPlayer player, int amount) {
        int amountFound = 0;
        Item required = getRequiredItem(block, meta).getItem();

        boolean needsTwo = block instanceof BlockSlab slab && slab.isOpaqueCube();

        if (needsTwo) amount *= 2;

        // TODO
        // if (block.hasProperty(BlockStateProperties.EGGS))
        // amount *= block.getValue(BlockStateProperties.EGGS);
        //
        // if (block.hasProperty(BlockStateProperties.PICKLES))
        // amount *= block.getValue(BlockStateProperties.PICKLES);

        {
            // Try held Item first
            int preferredSlot = player.inventory.currentItem;
            ItemStack itemstack = player.inventory.getStackInSlot(preferredSlot);
            int count = itemstack.stackSize;
            if (itemstack.getItem() == required && count > 0) {
                int taken = Math.min(count, amount - amountFound);
                player.inventory
                    .setInventorySlotContents(preferredSlot, new ItemStack(itemstack.getItem(), count - taken));
                amountFound += taken;
            }
        }

        // Search inventory
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            if (amountFound == amount) break;

            ItemStack itemstack = player.inventory.getStackInSlot(i);
            int count = itemstack.stackSize;
            if (itemstack.getItem() == required && count > 0) {
                int taken = Math.min(count, amount - amountFound);
                player.inventory.setInventorySlotContents(i, new ItemStack(itemstack.getItem(), count - taken));
                amountFound += taken;
            }
        }

        if (needsTwo) {
            // Give back 1 if uneven amount was removed
            if (amountFound % 2 != 0) player.inventory.addItemStackToInventory(new ItemStack(required));
            amountFound /= 2;
        }

        return amountFound;
    }

    public static ItemStack getRequiredItem(Block block) {
        return getRequiredItem(block, 0);
    }

    public static ItemStack getRequiredItem(Block block, int meta) {
        if (block == Blocks.farmland /* TODO || item == Items.DIRT_PATH */) return new ItemStack(Blocks.dirt);
        return new ItemStack(block, 1, meta);
    }

    public static void destroyBlock(World world, int x, int y, int z, float effectChance) {
        destroyBlock(
            world,
            x,
            y,
            z,
            effectChance,
            stack -> {
                world.getBlock(x, y, z)
                    .dropBlockAsItem(world, x, y, z, stack);
            });
    }

    public static void destroyBlock(World world, int x, int y, int z, float effectChance,
        Consumer<ItemStack> droppedItemCallback) {
        destroyBlockAs(world, x, y, z, null, null, effectChance, droppedItemCallback);
    }

    public static void destroyBlockAs(World world, int x, int y, int z, @Nullable EntityPlayer player,
        ItemStack usedTool, float effectChance, Consumer<ItemStack> droppedItemCallback) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (world.rand.nextFloat() < effectChance) world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block));
        if (player != null) {
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, world, block, meta, player);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) return;

            if (event.getExpToDrop() > 0 && world instanceof WorldServer)
                block.dropXpOnBlockBreak(world, x, y, z, event.getExpToDrop());

            if (usedTool != null) usedTool.func_150999_a(world, block, x, y, z, player);
            player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
        }

        if (world instanceof WorldServer && world.getGameRules()
            .getGameRuleBooleanValue("doTileDrops")
            && !world.restoringBlockSnapshots
            && (player == null || !player.capabilities.isCreativeMode)) {
            for (ItemStack itemStack : block
                .getDrops(world, x, y, z, meta, EnchantmentHelper.getFortuneModifier(player)))
                droppedItemCallback.accept(itemStack);
            // TODO May have to delete or rewrite this
            // block.spawnAfterBreak((WorldServer) world, x, y, z, ItemStack.EMPTY);
        }

        world.setBlockToAir(x, y, z);
    }

    public static boolean isSolidWall(IBlockAccess reader, int fromX, int fromY, int fromZ, Direction toDirection) {
        ChunkCoordinates normal = toDirection.getNormal();
        return hasBlockSolidSide(
            reader.getBlock(fromX + normal.posX, fromY + normal.posY, fromZ + normal.posZ),
            reader,
            fromX + normal.posX,
            fromY + normal.posY,
            fromZ + normal.posZ,
            toDirection.getOpposite());
    }

    public static boolean noCollisionInSpace(World reader, int x, int y, int z) {
        Block block = reader.getBlock(x, y, z);
        List<AxisAlignedBB> colliders = new ArrayList<AxisAlignedBB>();
        block.addCollisionBoxesToList(
            reader,
            x,
            y,
            z,
            block.getCollisionBoundingBoxFromPool(reader, x, y, z),
            colliders,
            null);
        return colliders.isEmpty();
    }

    private static void placeRailWithoutUpdate(World world, Block block, int meta, int targetX, int targetY,
        int targetZ) {
        int i = targetX & 15;
        int j = targetY;
        int k = targetZ & 15;
        Chunk chunk = world.getChunkFromBlockCoords(targetX, targetZ);
        ExtendedBlockStorage chunksection = chunk.getBlockStorageArray()[j >> 4];
        if (chunksection == null) {
            chunksection = new ExtendedBlockStorage(j >> 4 << 4, !world.provider.hasNoSky);
            chunk.getBlockStorageArray()[j >> 4] = chunksection;
        }
        j &= 15;
        Block oldBlock = chunksection.getBlockByExtId(i, j, k);
        chunksection.func_150818_a(i, j, k, block);
        chunksection.setExtBlockMetadata(i, j, k, meta);
        chunk.setChunkModified();
        world.markAndNotifyBlock(targetX, targetY, targetZ, chunk, oldBlock, block, 2);

        world.setBlock(targetX, targetY, targetZ, block, meta, 2);
        world.notifyBlockOfNeighborChange(targetX, targetY, targetZ, world.getBlock(targetX, targetY - 1, targetZ));
    }

    public static void placeSchematicBlock(World world, Block block, int meta, int targetX, int targetY, int targetZ,
        ItemStack stack, @Nullable NBTTagCompound data) {
        // TODO Most likely not needed
        // // Piston
        // if (state.hasProperty(BlockStateProperties.EXTENDED))
        // state = state.setValue(BlockStateProperties.EXTENDED, Boolean.FALSE);

        if (block == AllBlocks.belt) {
            world.setBlock(targetX, targetY, targetZ, block, meta, 2);
            return;
        } /*
           * TODO else if (state.getBlock() == Blocks.COMPOSTER)
           * state = Blocks.COMPOSTER.defaultBlockState();
           */
        else if (/* TODOstate.getBlock() != Blocks.SEA_PICKLE && */ block instanceof IPlantable) {
            block = ((IPlantable) block).getPlant(world, targetX, targetY, targetZ);
            meta = ((IPlantable) block).getPlantMetadata(world, targetX, targetY, targetZ);
        }

        if (world.provider.isHellWorld && block.getMaterial() == Material.water) {
            world.playSoundEffect(
                (double) ((float) targetX + 0.5F),
                (double) ((float) targetY + 0.5F),
                (double) ((float) targetZ + 0.5F),
                "random.fizz",
                0.5F,
                2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

            for (int l = 0; l < 8; ++l) {
                world.spawnParticle(
                    "largesmoke",
                    (double) targetX + Math.random(),
                    (double) targetY + Math.random(),
                    (double) targetZ + Math.random(),
                    0.0D,
                    0.0D,
                    0.0D);
            }
            block.dropBlockAsItem(world, targetX, targetY, targetZ, 0, 0);
            return;
        }

        if (block instanceof BlockRailBase) {
            placeRailWithoutUpdate(world, block, meta, targetX, targetY, targetZ);
        } else {
            world.setBlock(targetX, targetY, targetZ, block, meta, 2);
        }

        if (data != null) {
            TileEntity tile = world.getTileEntity(targetX, targetY, targetZ);
            if (tile != null) {
                data.setInteger("x", targetX);
                data.setInteger("y", targetY);
                data.setInteger("z", targetZ);
                if (tile instanceof KineticTileEntity kte) kte.warnOfMovement();
                tile.readFromNBT(data);
            }
        }

        try {
            block.onBlockPlacedBy(world, targetX, targetY, targetZ, null, stack);
        } catch (Exception e) {}
    }

    public static double getBounceMultiplier(Block block) {
        // TODO
        // if (block instanceof SlimeBlock)
        // return 0.8D;
        if (block instanceof BlockBed /* TODO || block instanceof SeatBlock */) return 0.66 * 0.8D;
        return 0;
    }

    public static boolean hasBlockSolidSide(Block block, IBlockAccess world, int x, int y, int z, Direction dir) {
        return !block.isLeaves(world, x, y, z) && block.isSideSolid(world, x, y, z, dir.toForgeDirection());
    }

    public static boolean extinguishFire(World world, @Nullable EntityPlayer player, int x, int y, int z,
        Direction dir) {
        ChunkCoordinates normal = dir.getNormal();
        if (world.getBlock(x + normal.posX, y + normal.posY, z + normal.posZ) == Blocks.fire) {
            world.playAuxSFXAtEntity(player, 1009, x, y, z, 0);
            world.setBlockToAir(x, y, z);
            return true;
        } else {
            return false;
        }
    }

    // TODO
    // public static BlockState copyProperties(BlockState fromState, BlockState toState) {
    // for (Property<?> property : fromState.getProperties()) {
    // toState = copyProperty(property, fromState, toState);
    // }
    // return toState;
    // }
    //
    // public static <T extends Comparable<T>> BlockState copyProperty(Property<T> property, BlockState fromState,
    // BlockState toState) {
    // if (fromState.hasProperty(property) && toState.hasProperty(property)) {
    // return toState.setValue(property, fromState.getValue(property));
    // }
    // return toState;
    // }

    public static void breakBlock(World worldIn, int x, int y, int z, boolean shouldDrop) {
        Block block = worldIn.getBlock(x, y, z);
        if (shouldDrop) block.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
        worldIn.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (worldIn.getBlockMetadata(x, y, z) << 12));
        worldIn.setBlockToAir(x, y, z);
    }

}
