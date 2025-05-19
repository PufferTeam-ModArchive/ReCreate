package su.sergiusonesimus.recreate.content.contraptions.wrench;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IWrenchableWithBracket extends IWrenchable {

    public Optional<ItemStack> removeBracket(IBlockAccess world, int x, int y, int z, boolean inOnReplacedContext);

    @Override
    default boolean onWrenched(World world, int x, int y, int z, int face, EntityPlayer player) {
        if (tryRemoveBracket(world, x, y, z, face, player)) return true;
        return IWrenchable.super.onWrenched(world, x, y, z, face, player);
    }

    default boolean tryRemoveBracket(World world, int x, int y, int z, int face, EntityPlayer player) {
        Optional<ItemStack> bracket = removeBracket(world, x, y, z, false);
        if (bracket.isPresent()) {
            if (!world.isRemote) {
                if (!player.capabilities.isCreativeMode) player.inventory.addItemStackToInventory(bracket.get());
                // TODO
                // if (AllBlocks.FLUID_PIPE.has(blockState)) {
                // Axis preferred = FluidPropagator.getStraightPipeAxis(blockState);
                // Direction preferredDirection =
                // preferred == null ? Direction.UP : Direction.get(AxisDirection.POSITIVE, preferred);
                // BlockState updated = AllBlocks.FLUID_PIPE.get()
                // .updateBlockState(blockState, preferredDirection, null, world, pos);
                // if (updated != blockState)
                // world.setBlockAndUpdate(pos, updated);
                // }
            }
            return true;
        }
        return false;
    }

}
