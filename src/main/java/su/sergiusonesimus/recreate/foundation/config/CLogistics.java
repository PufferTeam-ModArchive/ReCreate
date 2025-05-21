package su.sergiusonesimus.recreate.foundation.config;

import net.minecraftforge.common.config.Configuration;

public class CLogistics {

    public static int defaultExtractionLimit;
    public static int defaultExtractionTimer;
    public static int psiTimeout;
    public static int mechanicalArmRange;
    public static int linkRange;
    public static int vaultCapacity;

    public static void init(String category, Configuration config) {
        defaultExtractionLimit = config
            .getInt("defaultExtractionLimit", category, 64, 1, 64, Comments.defaultExtractionLimit);
        defaultExtractionTimer = config
            .getInt("defaultExtractionTimer", category, 8, 1, Integer.MAX_VALUE, Comments.defaultExtractionTimer);
        psiTimeout = config.getInt("psiTimeout", category, 20, 1, Integer.MAX_VALUE, Comments.psiTimeout);
        mechanicalArmRange = config
            .getInt("mechanicalArmRange", category, 5, 1, Integer.MAX_VALUE, Comments.mechanicalArmRange);
        linkRange = config.getInt("linkRange", category, 128, 1, Integer.MAX_VALUE, Comments.linkRange);
        vaultCapacity = config.getInt("vaultCapacity", category, 20, 1, Integer.MAX_VALUE, Comments.vaultCapacity);
    }

    private static class Comments {

        static String defaultExtractionLimit = "The maximum amount of items a funnel pulls at a time without an applied filter.";
        static String defaultExtractionTimer = "The amount of ticks a funnel waits between item transferrals, when it is not re-activated by redstone.";
        static String linkRange = "Maximum possible range in blocks of redstone link connections.";
        static String psiTimeout = "The amount of ticks a portable storage interface waits for transfers until letting contraptions move along.";
        static String mechanicalArmRange = "Maximum distance in blocks a Mechanical Arm can reach across.";
        static String vaultCapacity = "The total amount of stacks a vault can hold per block in size.";
    }

}
