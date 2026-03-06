package su.sergiusonesimus.recreate.content.contraptions.processing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ProcessingRecipes<I extends IInventory, R extends ProcessingRecipe<I>>
    implements IProcessingRecipeProvider<R> {

    /** The list of processing recipes. */
    protected final List<R> recipesList = new ArrayList<R>();
    protected final Class<I> inventoryClass;

    public ProcessingRecipes(Class<I> inventoryClass) {
        this.inventoryClass = inventoryClass;
    }

    protected void registerRecipe(R recipe) {
        recipe.validate();
        this.recipesList.add(recipe);
    }

    public List<R> getRecipesList() {
        return this.recipesList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<R> getRecipeFor(IInventory inv, World world) {
        if (inv.getClass()
            .isAssignableFrom(inventoryClass)) return Optional.empty();
        Iterator<R> iterator = this.recipesList.iterator();
        R recipe;

        do {
            if (!iterator.hasNext()) return Optional.empty();
            recipe = iterator.next();
        } while (!recipe.matches((I) inv, world));

        return Optional.of(recipe);
    }

    protected ItemStack stackFromObject(Object object) {
        if (object instanceof ItemStack stack) return stack;
        else if (object instanceof Item item) return new ItemStack(item);
        else if (object instanceof Block block) return new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE);
        return null;
    }

    protected List<ProcessingOutput> outputFromObjects(Object... objects) {
        List<ProcessingOutput> result = new ArrayList<ProcessingOutput>();

        int i = 0;
        float chance;
        while (i < objects.length) {
            ItemStack stack = stackFromObject(objects[i]);
            if (stack == null) return null;
            if (i + 1 < objects.length && objects[i + 1] instanceof Float f) {
                chance = f;
                i++;
            } else chance = 1F;
            result.add(new ProcessingOutput(stack, chance));
            i++;
        }

        return result;
    }
}
