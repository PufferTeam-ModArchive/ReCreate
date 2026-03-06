package su.sergiusonesimus.recreate.content.contraptions.processing;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class EmptyingRecipe extends ProcessingRecipe<IInventory> {

    public EmptyingRecipe(String id) {
        super("emptying", id);
    }

    @Override
    public boolean matches(IInventory inv, World world) {
        ItemStack stackToCheck = inv.getStackInSlot(0);
        if (stackToCheck == null) return false;
        return ingredients.size() > 0 ? ingredients.get(0)
            .isItemEqual(stackToCheck)
            : OreDictionary.getOres(oreDictIngredients.get(0))
                .contains(stackToCheck);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 1;
    }

    public FluidStack getResultingFluid() {
        if (fluidResults.isEmpty())
            throw new IllegalStateException("Emptying Recipe: " + id.toString() + " has no fluid output!");
        return fluidResults.get(0);
    }

}
