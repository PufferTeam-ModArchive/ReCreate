package su.sergiusonesimus.recreate.foundation.config;

import net.minecraftforge.common.config.Configuration;

public class CFluids {

    public static int fluidTankCapacity;
    public static int fluidTankMaxHeight;
    public static int mechanicalPumpRange;

    public static int hosePulleyBlockThreshold;
    public static boolean fillInfinite;
    public static int hosePulleyRange;

    public static boolean placeFluidSourceBlocks;

    public static void init(String category, Configuration config) {
        fluidTankCapacity = config.getInt(
            "fluidTankCapacity",
            category,
            8,
            1,
            Integer.MAX_VALUE,
            Comments.buckets + "\n" + Comments.fluidTankCapacity);
        fluidTankMaxHeight = config.getInt(
            "fluidTankMaxHeight",
            category,
            32,
            1,
            Integer.MAX_VALUE,
            Comments.blocks + "\n" + Comments.fluidTankMaxHeight);
        mechanicalPumpRange = config.getInt(
            "mechanicalPumpRange",
            category,
            16,
            1,
            Integer.MAX_VALUE,
            Comments.blocks + "\n" + Comments.mechanicalPumpRange);

        hosePulleyBlockThreshold = config.getInt(
            "hosePulleyBlockThreshold",
            category,
            10000,
            -1,
            Integer.MAX_VALUE,
            Comments.blocks + "\n" + Comments.toDisable + "\n" + Comments.hosePulleyBlockThreshold);
        fillInfinite = config.getBoolean("fillInfinite", category, false, Comments.fillInfinite);
        hosePulleyRange = config.getInt(
            "hosePulleyRange",
            category,
            128,
            1,
            Integer.MAX_VALUE,
            Comments.blocks + "\n" + Comments.hosePulleyRange);

        placeFluidSourceBlocks = config
            .getBoolean("placeFluidSourceBlocks", category, true, Comments.placeFluidSourceBlocks);
    }

    private static class Comments {

        static String blocks = "[in Blocks]";
        static String buckets = "[in Buckets]";
        static String fluidTankCapacity = "The amount of liquid a tank can hold per block.";
        static String fluidTankMaxHeight = "The maximum height a fluid tank can reach.";
        static String mechanicalPumpRange = "The maximum distance a mechanical pump can push or pull liquids on either side.";

        static String hosePulleyRange = "The maximum distance a hose pulley can draw fluid blocks from.";
        static String toDisable = "[-1 to disable this behaviour]";
        static String hosePulleyBlockThreshold = "The minimum amount of fluid blocks the hose pulley needs to find before deeming it an infinite source.";
        static String fillInfinite = "Whether hose pulleys should continue filling up above-threshold sources";
        static String placeFluidSourceBlocks = "Whether open-ended pipes and hose pulleys should be allowed to place fluid sources";
    }

}
