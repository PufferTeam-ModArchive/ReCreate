package su.sergiusonesimus.recreate.foundation.config;

import net.minecraftforge.common.config.Configuration;

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

    public static void init(String category, Configuration config) {
        bulkPressing = config.getBoolean("bulkPressing", category, false, Comments.bulkPressing);
        bulkCutting = config.getBoolean("bulkCutting", category, false, Comments.bulkCutting);
        allowShapelessInMixer = config
            .getBoolean("allowShapelessInMixer", category, true, Comments.allowShapelessInMixer);
        allowShapedSquareInPress = config
            .getBoolean("allowShapedSquareInPress", category, true, Comments.allowShapedSquareInPress);
        allowRegularCraftingInCrafter = config
            .getBoolean("allowRegularCraftingInCrafter", category, true, Comments.allowRegularCraftingInCrafter);
        allowBiggerFireworksInCrafter = config
            .getBoolean("allowBiggerFireworksInCrafter", category, false, Comments.allowBiggerFireworksInCrafter);
        allowStonecuttingOnSaw = config
            .getBoolean("allowStonecuttingOnSaw", category, true, Comments.allowStonecuttingOnSaw);
        allowWoodcuttingOnSaw = config
            .getBoolean("allowWoodcuttingOnSaw", category, true, Comments.allowWoodcuttingOnSaw);
        allowCastingBySpout = config.getBoolean("allowCastingBySpout", category, true, Comments.allowCastingBySpout);
        lightSourceCountForRefinedRadiance = config
            .getInt("lightSourceCountForRefinedRadiance", category, 10, 1, Integer.MAX_VALUE, Comments.refinedRadiance);
        enableRefinedRadianceRecipe = config
            .getBoolean("enableRefinedRadianceRecipe", category, true, Comments.refinedRadianceRecipe);
        enableShadowSteelRecipe = config
            .getBoolean("enableShadowSteelRecipe", category, true, Comments.shadowSteelRecipe);
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
