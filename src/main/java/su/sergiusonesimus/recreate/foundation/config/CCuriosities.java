package su.sergiusonesimus.recreate.foundation.config;

import net.minecraftforge.common.config.Configuration;

public class CCuriosities {

    public static int maxSymmetryWandRange;
    public static int placementAssistRange;
    public static int toolboxRange;
    public static int airInBacktank;
    public static int enchantedBacktankCapacity;

    public static int maxExtendoGripActions;
    public static int maxPotatoCannonShots;

    // public int zapperUndoLogLength = i(10, 0, "zapperUndoLogLength", Comments.zapperUndoLogLength); NYI

    public static void init(String category, Configuration config) {
        maxSymmetryWandRange = config
            .getInt("maxSymmetryWandRange", category, 50, 10, Integer.MAX_VALUE, Comments.symmetryRange);
        placementAssistRange = config
            .getInt("placementAssistRange", category, 12, 3, Integer.MAX_VALUE, Comments.placementRange);
        toolboxRange = config.getInt("toolboxRange", category, 10, 1, Integer.MAX_VALUE, Comments.toolboxRange);
        airInBacktank = config.getInt("airInBacktank", category, 900, 1, Integer.MAX_VALUE, Comments.maxAirInBacktank);
        enchantedBacktankCapacity = config.getInt(
            "enchantedBacktankCapacity",
            category,
            300,
            1,
            Integer.MAX_VALUE,
            Comments.enchantedBacktankCapacity);

        maxExtendoGripActions = config
            .getInt("maxExtendoGripActions", category, 1000, 0, Integer.MAX_VALUE, Comments.maxExtendoGripActions);
        maxPotatoCannonShots = config
            .getInt("maxPotatoCannonShots", category, 200, 0, Integer.MAX_VALUE, Comments.maxPotatoCannonShots);
    }

    private static class Comments {

        static String symmetryRange = "The Maximum Distance to an active mirror for the symmetry wand to trigger.";
        static String maxAirInBacktank = "The Maximum volume of Air that can be stored in a backtank = Seconds of underwater breathing";
        static String enchantedBacktankCapacity = "The volume of Air added by each level of the backtanks Capacity Enchantment";
        static String placementRange = "The Maximum Distance a Block placed by Create's placement assist will have to its interaction point.";
        static String toolboxRange = "The Maximum Distance at which a Toolbox can interact with Players' Inventories.";
        static String maxExtendoGripActions = "Amount of free Extendo Grip actions provided by one filled Copper Backtank. Set to 0 makes Extendo Grips unbreakable";
        static String maxPotatoCannonShots = "Amount of free Potato Cannon shots provided by one filled Copper Backtank. Set to 0 makes Potato Cannons unbreakable";
        // static String zapperUndoLogLength = "The maximum amount of operations a blockzapper can remember for undoing.
        // (0 to disable undo)";
    }

}
