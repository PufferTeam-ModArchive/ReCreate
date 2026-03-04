package su.sergiusonesimus.recreate.content.contraptions.fan;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import su.sergiusonesimus.recreate.content.contraptions.processing.InWorldProcessing.SplashingWrapper;
import su.sergiusonesimus.recreate.content.contraptions.processing.ProcessingRecipe;

@ParametersAreNonnullByDefault
public class SplashingRecipe extends ProcessingRecipe<SplashingWrapper> {

    public SplashingRecipe(String id) {
        super("splashing", id);
    }

    @Override
    public boolean matches(SplashingWrapper inv, World worldIn) {
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
        return 12;
    }

}
