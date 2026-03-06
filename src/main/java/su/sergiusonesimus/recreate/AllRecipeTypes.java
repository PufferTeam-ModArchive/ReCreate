package su.sergiusonesimus.recreate;

import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.content.contraptions.fan.SplashingRecipes;
import su.sergiusonesimus.recreate.content.contraptions.processing.EmptyingRecipes;
import su.sergiusonesimus.recreate.content.contraptions.processing.IProcessingRecipeProvider;
import su.sergiusonesimus.recreate.content.contraptions.processing.ProcessingRecipe;

public enum AllRecipeTypes {

    // CONVERSION(ConversionRecipe::new),
    // CRUSHING(CrushingRecipe::new),
    // CUTTING(CuttingRecipe::new),
    // MILLING(MillingRecipe::new),
    // BASIN(BasinRecipe::new),
    // MIXING(MixingRecipe::new),
    // COMPACTING(CompactingRecipe::new),
    // PRESSING(PressingRecipe::new),
    // SANDPAPER_POLISHING(SandPaperPolishingRecipe::new),
    SPLASHING(new SplashingRecipes()),
    // DEPLOYING(DeployerApplicationRecipe::new),
    // FILLING(FillingRecipe::new),
    EMPTYING(new EmptyingRecipes()),
    //
    // MECHANICAL_CRAFTING(MechanicalCraftingRecipe.Serializer::new),
    // SEQUENCED_ASSEMBLY(SequencedAssemblyRecipeSerializer::new),
    //
    // TOOLBOX_DYEING(() -> new SimpleRecipeSerializer<>(ToolboxDyeingRecipe::new), RecipeType.CRAFTING);

    ;

    private IProcessingRecipeProvider<?> recipeProvider;

    AllRecipeTypes(IProcessingRecipeProvider<?> provider) {
        recipeProvider = provider;
    }

    @SuppressWarnings("unchecked")
    public <I extends IInventory, T extends ProcessingRecipe<I>> Optional<T> find(I inv, World world) {
        return (Optional<T>) recipeProvider.getRecipeFor(inv, world);
    }

}
