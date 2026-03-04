package su.sergiusonesimus.recreate.content.contraptions.processing;

import java.util.List;
import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import su.sergiusonesimus.recreate.AllRecipeTypes;
import su.sergiusonesimus.recreate.foundation.utility.Pair;

public class EmptyingByBasin {

    static IInventory wrapper = new InventoryBasic("", false, 1);

    public static boolean canItemBeEmptied(World world, ItemStack stack) {
        if (stack == null) return false;
        if (stack.getItem() instanceof ItemPotion) return true;

        wrapper.setInventorySlotContents(0, stack);
        if (AllRecipeTypes.EMPTYING.find(wrapper, world)
            .isPresent()) return true;

        if (!(stack.getItem() instanceof IFluidContainerItem tank)) return false;
        if (tank.getCapacity(stack) > 0) return true;
        return false;
    }

    public static Pair<FluidStack, ItemStack> emptyItem(World world, ItemStack stack, boolean simulate) {
        FluidStack resultingFluid = null;
        ItemStack resultingItem = null;

        // TODO if (stack.getItem() instanceof ItemPotion) return PotionFluidHandler.emptyPotion(stack, simulate);

        wrapper.setInventorySlotContents(0, stack);
        Optional<ProcessingRecipe<IInventory>> recipe = AllRecipeTypes.EMPTYING.find(wrapper, world);
        if (recipe.isPresent()) {
            EmptyingRecipe emptyingRecipe = (EmptyingRecipe) recipe.get();
            List<ItemStack> results = emptyingRecipe.rollResults();
            if (!simulate) stack.stackSize--;
            resultingItem = results.isEmpty() ? null : results.get(0);
            resultingFluid = emptyingRecipe.getResultingFluid();
            return Pair.of(resultingFluid, resultingItem);
        }

        ItemStack split = stack.copy();
        split.stackSize = 1;
        if (!(split.getItem() instanceof IFluidContainerItem tank)) return Pair.of(resultingFluid, resultingItem);
        resultingFluid = tank.drain(split, 1000, simulate);
        resultingItem = split.copy();
        if (!simulate) stack.stackSize--;

        return Pair.of(resultingFluid, resultingItem);
    }

}
