package su.sergiusonesimus.recreate.content.contraptions.processing;

import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

public interface IProcessingRecipeProvider<T extends ProcessingRecipe<?>> {

    Optional<T> getRecipeFor(IInventory inv, World world);

}
