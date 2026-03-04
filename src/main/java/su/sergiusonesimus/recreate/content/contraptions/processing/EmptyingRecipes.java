package su.sergiusonesimus.recreate.content.contraptions.processing;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class EmptyingRecipes extends ProcessingRecipes<IInventory, EmptyingRecipe> {

    public EmptyingRecipes() {
        super(IInventory.class);

        // potion/water bottles are handled internally

        addRecipe(
            "water_bucket",
            new ItemStack(Items.water_bucket),
            new FluidStack(FluidRegistry.WATER, 1000),
            new ItemStack(Items.bucket));
        addRecipe(
            "lava_bucket",
            new ItemStack(Items.lava_bucket),
            new FluidStack(FluidRegistry.LAVA, 1000),
            new ItemStack(Items.bucket));

        // TODO
        // HONEY_BOTTLE = create("honey_bottle", b -> b
        // .require(Items.HONEY_BOTTLE)
        // .output(AllFluids.HONEY.get(), 250)
        // .output(Items.GLASS_BOTTLE)),
        //
        // BUILDERS_TEA = create("builders_tea", b -> b
        // .require(AllItems.BUILDERS_TEA.get())
        // .output(AllFluids.TEA.get(), 250)
        // .output(Items.GLASS_BOTTLE))
    }

    public void addRecipe(Block input, FluidStack outputFluid, ItemStack output) {
        addRecipe(input.getLocalizedName(), new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), outputFluid, output);
    }

    public void addRecipe(String name, Block input, FluidStack outputFluid, ItemStack output) {
        addRecipe(name, new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), outputFluid, output);
    }

    public void addRecipe(ItemStack input, FluidStack outputFluid, ItemStack output) {
        addRecipe(input.getDisplayName(), input, outputFluid, output);
    }

    public void addRecipe(String name, ItemStack input, FluidStack outputFluid, ItemStack output) {
        EmptyingRecipe recipe = new EmptyingRecipe(name);

        recipe.addIngredient(input);
        recipe.addFluidResult(outputFluid);
        recipe.addResult(new ProcessingOutput(output, 1));

        this.registerRecipe(recipe);
    }

    public void addRecipe(String input, FluidStack outputFluid, ItemStack output) {
        addRecipe(input, input, outputFluid, output);
    }

    public void addRecipe(String name, String input, FluidStack outputFluid, ItemStack output) {
        EmptyingRecipe recipe = new EmptyingRecipe(name);

        recipe.addIngredient(input);
        recipe.addFluidResult(outputFluid);
        recipe.addResult(new ProcessingOutput(output, 1));

        this.registerRecipe(recipe);
    }

}
