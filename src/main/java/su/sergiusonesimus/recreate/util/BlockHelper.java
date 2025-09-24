package su.sergiusonesimus.recreate.util;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockHelper {

    public static void breakBlock(World worldIn, int x, int y, int z, boolean shouldDrop) {
        Block block = worldIn.getBlock(x, y, z);
        if (shouldDrop) block.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
        worldIn.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (worldIn.getBlockMetadata(x, y, z) << 12));
        worldIn.setBlockToAir(x, y, z);
    }

}
