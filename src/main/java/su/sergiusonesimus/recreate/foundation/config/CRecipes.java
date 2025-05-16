package su.sergiusonesimus.recreate.foundation.config;

import net.minecraftforge.common.config.Configuration;

import su.sergiusonesimus.recreate.foundation.config.CServer.Categories;

public class CRecipes {

    public static boolean bulkPressing;
    public static boolean bulkCutting;
    public static boolean allowShapelessInMixer;
    public static boolean allowShapedSquareInPress;
    public static boolean allowRegularCraftingInCrafter;
    public static boolean allowBiggerFireworksInCrafter;
    public static boolean allowStonecuttingOnSaw;
    public static boolean allowWoodcuttingOnSaw;
    public static boolean allowCastingBySpout;
    public static int lightSourceCountForRefinedRadiance;
    public static boolean enableRefinedRadianceRecipe;
    public static boolean enableShadowSteelRecipe;

    public static void init(Configuration config) {
        bulkPressing = config.getBoolean("bulkPressing", Categories.recipes, false, Comments.bulkPressing);
        bulkCutting = config.getBoolean("bulkCutting", Categories.recipes, false, Comments.bulkCutting);
        allowShapelessInMixer = config
            .getBoolean("allowShapelessInMixer", Categories.recipes, true, Comments.allowShapelessInMixer);
        allowShapedSquareInPress = config
            .getBoolean("allowShapedSquareInPress", Categories.recipes, true, Comments.allowShapedSquareInPress);
        allowRegularCraftingInCrafter = config.getBoolean(
            "allowRegularCraftingInCrafter",
            Categories.recipes,
            true,
            Comments.allowRegularCraftingInCrafter);
        allowBiggerFireworksInCrafter = config.getBoolean(
            "allowBiggerFireworksInCrafter",
            Categories.recipes,
            false,
            Comments.allowBiggerFireworksInCrafter);
        allowStonecuttingOnSaw = config
            .getBoolean("allowStonecuttingOnSaw", Categories.recipes, true, Comments.allowStonecuttingOnSaw);
        allowWoodcuttingOnSaw = config
            .getBoolean("allowWoodcuttingOnSaw", Categories.recipes, true, Comments.allowWoodcuttingOnSaw);
        allowCastingBySpout = config
            .getBoolean("allowCastingBySpout", Categories.recipes, true, Comments.allowCastingBySpout);
        lightSourceCountForRefinedRadiance = config.getInt(
            "lightSourceCountForRefinedRadiance",
            Categories.recipes,
            10,
            1,
            Integer.MAX_VALUE,
            Comments.refinedRadiance);
        enableRefinedRadianceRecipe = config
            .getBoolean("enableRefinedRadianceRecipe", Categories.recipes, true, Comments.refinedRadianceRecipe);
        enableShadowSteelRecipe = config
            .getBoolean("enableShadowSteelRecipe", Categories.recipes, true, Comments.shadowSteelRecipe);
    }

    private static class Comments {

        static String bulkPressing = "Allow the Mechanical Press to process entire stacks at a time.";
        static String bulkCutting = "Allow the Mechanical Saw to process entire stacks at a time.";
        static String allowShapelessInMixer = "Allow allows any shapeless crafting recipes to be processed by a Mechanical Mixer + Basin.";
        static String allowShapedSquareInPress = "Allow any single-ingredient 2x2 or 3x3 crafting recipes to be processed by a Mechanical Press + Basin.";
        static String allowRegularCraftingInCrafter = "Allow any standard crafting recipes to be processed by Mechanical Crafters.";
        static String allowBiggerFireworksInCrafter = "Allow Firework Rockets with more than 9 ingredients to be crafted using Mechanical Crafters.";
        static String allowStonecuttingOnSaw = "Allow any stonecutting recipes to be processed by a Mechanical Saw.";
        static String allowWoodcuttingOnSaw = "Allow any Druidcraft woodcutter recipes to be processed by a Mechanical Saw.";
        static String allowCastingBySpout = "Allow Spouts to interact with Casting Tables and Basins from Tinkers' Construct.";
        static String refinedRadiance = "The amount of Light sources destroyed before Chromatic Compound turns into Refined Radiance.";
        static String refinedRadianceRecipe = "Allow the standard in-world Refined Radiance recipes.";
        static String shadowSteelRecipe = "Allow the standard in-world Shadow Steel recipe.";
    }

}
