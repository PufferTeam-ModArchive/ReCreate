package su.sergiusonesimus.recreate.foundation.config;

import net.minecraftforge.common.config.Configuration;

public class CCommon {

    public static void init(Configuration config) {
        config.setCategoryComment(Categories.worldgen, Comments.worldGen);

        // worldgen
        disableWorldGen = config.getBoolean("disableWorldGen", Categories.worldgen, false, Comments.disableWorldGen);
    }

    private static class Categories {

        static String worldgen = "worldgen";
    }

    private static class Comments {

        static String worldGen = "Modify Create's impact on your terrain";
        static String disableWorldGen = "Prevents all worldgen added by Create from taking effect";
    }

    public static boolean disableWorldGen;

}
