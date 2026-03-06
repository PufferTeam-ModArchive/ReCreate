package su.sergiusonesimus.recreate.content.contraptions.fan;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import su.sergiusonesimus.recreate.content.contraptions.processing.InWorldProcessing.SplashingWrapper;
import su.sergiusonesimus.recreate.content.contraptions.processing.ProcessingOutput;
import su.sergiusonesimus.recreate.content.contraptions.processing.ProcessingRecipes;

public class SplashingRecipes extends ProcessingRecipes<SplashingWrapper, SplashingRecipe> {

    public SplashingRecipes() {
        super(SplashingWrapper.class);

        addRecipe("wool", Blocks.wool, new ItemStack(Blocks.wool, 1, 15));
        addRecipe("stained glass", Blocks.stained_glass, new ItemStack(Blocks.glass));
        addRecipe("stained glass pane", Blocks.stained_glass_pane, new ItemStack(Blocks.glass_pane));

        // TODO
        // GRAVEL = create(() -> Blocks.GRAVEL, b -> b.output(.25f, Items.FLINT)
        // .output(.125f, Items.IRON_NUGGET)),
        addRecipe(Blocks.soul_sand, Items.quartz, .125f, Items.gold_nugget, .02f);
        // TODO
        // RED_SAND = create(() -> Blocks.RED_SAND, b -> b.output(.125f, Items.GOLD_NUGGET, 3)
        // .output(.05f, Items.DEAD_BUSH)),
        addRecipe(Blocks.sand, Items.clay_ball, .25f);

        // TODO
        // CRUSHED_COPPER = crushedOre(AllItems.CRUSHED_COPPER, AllItems.COPPER_NUGGET::get, () -> Items.CLAY_BALL,
        // .5f),
        // CRUSHED_ZINC = crushedOre(AllItems.CRUSHED_ZINC, AllItems.ZINC_NUGGET::get, () -> Items.GUNPOWDER, .25f),
        // CRUSHED_GOLD = crushedOre(AllItems.CRUSHED_GOLD, () -> Items.GOLD_NUGGET, () -> Items.QUARTZ, .5f),
        // CRUSHED_IRON = crushedOre(AllItems.CRUSHED_IRON, () -> Items.IRON_NUGGET, () -> Items.REDSTONE, .125f),
        //
        // CRUSHED_OSMIUM = moddedCrushedOre(AllItems.CRUSHED_OSMIUM, "osmium", MEK),
        // CRUSHED_PLATINUM = moddedCrushedOre(AllItems.CRUSHED_PLATINUM, "platinum", SM),
        // CRUSHED_SILVER = moddedCrushedOre(AllItems.CRUSHED_SILVER, "silver", TH, MW, IE, SM, INF),
        // CRUSHED_TIN = moddedCrushedOre(AllItems.CRUSHED_TIN, "tin", TH, MEK, MW, SM),
        // CRUSHED_LEAD = moddedCrushedOre(AllItems.CRUSHED_LEAD, "lead", MEK, TH, MW, IE, SM, EID),
        // CRUSHED_QUICKSILVER = moddedCrushedOre(AllItems.CRUSHED_QUICKSILVER, "quicksilver", MW),
        // CRUSHED_BAUXITE = moddedCrushedOre(AllItems.CRUSHED_BAUXITE, "aluminum", IE, SM),
        // CRUSHED_URANIUM = moddedCrushedOre(AllItems.CRUSHED_URANIUM, "uranium", MEK, IE, SM),
        // CRUSHED_NICKEL = moddedCrushedOre(AllItems.CRUSHED_NICKEL, "nickel", TH, IE, SM),

        addRecipe(Blocks.ice, Blocks.packed_ice);
        // TODO
        // MAGMA_BLOCK = convert(Blocks.MAGMA_BLOCK, Blocks.OBSIDIAN),
        //
        // WHITE_CONCRETE = convert(Blocks.WHITE_CONCRETE_POWDER, Blocks.WHITE_CONCRETE),
        // ORANGE_CONCRETE = convert(Blocks.ORANGE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE),
        // MAGENTA_CONCRETE = convert(Blocks.MAGENTA_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE),
        // LIGHT_BLUE_CONCRETE = convert(Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE),
        // LIME_CONCRETE = convert(Blocks.LIME_CONCRETE_POWDER, Blocks.LIME_CONCRETE),
        // YELLOW_CONCRETE = convert(Blocks.YELLOW_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE),
        // PINK_CONCRETE = convert(Blocks.PINK_CONCRETE_POWDER, Blocks.PINK_CONCRETE),
        // LIGHT_GRAY_CONCRETE = convert(Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE),
        // GRAY_CONCRETE = convert(Blocks.GRAY_CONCRETE_POWDER, Blocks.GRAY_CONCRETE),
        // PURPLE_CONCRETE = convert(Blocks.PURPLE_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE),
        // GREEN_CONCRETE = convert(Blocks.GREEN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE),
        // BROWN_CONCRETE = convert(Blocks.BROWN_CONCRETE_POWDER, Blocks.BROWN_CONCRETE),
        // RED_CONCRETE = convert(Blocks.RED_CONCRETE_POWDER, Blocks.RED_CONCRETE),
        // BLUE_CONCRETE = convert(Blocks.BLUE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE),
        // CYAN_CONCRETE = convert(Blocks.CYAN_CONCRETE_POWDER, Blocks.CYAN_CONCRETE),
        // BLACK_CONCRETE = convert(Blocks.BLACK_CONCRETE_POWDER, Blocks.BLACK_CONCRETE),
        //
        // FLOUR = create(AllItems.WHEAT_FLOUR::get, b -> b.output(AllItems.DOUGH.get()))
    }

    public void addRecipe(Block input, ItemStack output) {
        addRecipe(input.getLocalizedName(), new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), output);
    }

    public void addRecipe(String name, Block input, ItemStack output) {
        addRecipe(name, new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), output);
    }

    public void addRecipe(ItemStack input, ItemStack output) {
        addRecipe(input.getDisplayName(), input, output);
    }

    public void addRecipe(String name, ItemStack input, ItemStack output) {
        SplashingRecipe recipe = new SplashingRecipe(name);

        recipe.addIngredient(input);
        recipe.addResult(new ProcessingOutput(output, 1));

        this.registerRecipe(recipe);
    }

    public void addRecipe(String input, ItemStack output) {
        addRecipe(input, input, output);
    }

    public void addRecipe(String name, String input, ItemStack output) {
        SplashingRecipe recipe = new SplashingRecipe(name);

        recipe.addIngredient(input);
        recipe.addResult(new ProcessingOutput(output, 1));

        this.registerRecipe(recipe);
    }

    public void addRecipe(Object input, Object... output) {
        ItemStack inputStack = this.stackFromObject(input);
        if (inputStack == null) return;

        addRecipe(inputStack.getDisplayName(), input, output);
    }

    public void addRecipe(String name, Object input, Object... output) {
        SplashingRecipe recipe = new SplashingRecipe(name);

        ItemStack inputStack = this.stackFromObject(input);
        if (inputStack == null) return;
        recipe.addIngredient(inputStack);
        List<ProcessingOutput> outputs = this.outputFromObjects(output);
        if (outputs == null) return;
        recipe.addResult(outputs);

        this.registerRecipe(recipe);
    }

}
