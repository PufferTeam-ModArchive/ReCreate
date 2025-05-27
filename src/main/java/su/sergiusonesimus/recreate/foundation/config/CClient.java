package su.sergiusonesimus.recreate.foundation.config;

import net.minecraftforge.common.config.Configuration;

import su.sergiusonesimus.recreate.foundation.config.ui.ConfigAnnotations;

public class CClient {

    public static void init(Configuration config) {
        // no group
        tooltips = config.getBoolean("enableTooltips", Categories.client, true, Comments.tooltips);
        enableOverstressedTooltip = config
            .getBoolean("enableOverstressedTooltip", Categories.client, true, Comments.enableOverstressedTooltip);
        explainRenderErrors = config
            .getBoolean("explainRenderErrors", Categories.client, true, Comments.explainRenderErrors);
        fanParticleDensity = config
            .getFloat("fanParticleDensity", Categories.client, .5f, 0, 1, Comments.fanParticleDensity);
        filterItemRenderDistance = config.getFloat(
            "filterItemRenderDistance",
            Categories.client,
            10f,
            1,
            Float.MAX_VALUE,
            Comments.filterItemRenderDistance);
        rainbowDebug = config.getBoolean("enableRainbowDebug", Categories.client, true, Comments.rainbowDebug);
        experimentalRendering = config
            .getBoolean("experimentalRendering", Categories.client, true, Comments.experimentalRendering);
        maxContraptionLightVolume = config.getInt(
            "maximumContraptionLightVolume",
            Categories.client,
            16384,
            0,
            Integer.MAX_VALUE,
            Comments.maxContraptionLightVolume);
        mainMenuConfigButtonRow = config
            .getInt("mainMenuConfigButtonRow", Categories.client, 2, 0, 4, Comments.mainMenuConfigButtonRow);
        mainMenuConfigButtonOffsetX = config.getInt(
            "mainMenuConfigButtonOffsetX",
            Categories.client,
            -4,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            Comments.mainMenuConfigButtonOffsetX);
        ingameMenuConfigButtonRow = config
            .getInt("ingameMenuConfigButtonRow", Categories.client, 3, 0, 5, Comments.ingameMenuConfigButtonRow);
        ingameMenuConfigButtonOffsetX = config.getInt(
            "ingameMenuConfigButtonOffsetX",
            Categories.client,
            -4,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            Comments.ingameMenuConfigButtonOffsetX);
        ignoreFabulousWarning = config
            .getBoolean("ignoreFabulousWarning", Categories.client, false, Comments.ignoreFabulousWarning);

        // overlay group
        overlayOffsetX = config.getInt(
            "overlayOffsetX",
            Categories.overlay,
            30,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            Comments.overlayOffset);
        overlayOffsetY = config.getInt(
            "overlayOffsetY",
            Categories.overlay,
            -16,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            Comments.overlayOffset);
        overlayCustomColor = config
            .getBoolean("customColorsOverlay", Categories.overlay, false, Comments.overlayCustomColor);
        overlayBackgroundColor = config.getInt(
            "customBackgroundOverlay",
            Categories.overlay,
            0xf0_100010,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            Comments.overlayBackgroundColor);
        overlayBorderColorTop = config.getInt(
            "customBorderTopOverlay",
            Categories.overlay,
            0x50_5000ff,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            Comments.overlayBorderColorTop);
        overlayBorderColorBot = config.getInt(
            "customBorderBotOverlay",
            Categories.overlay,
            0x50_28007f,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            Comments.overlayBorderColorBot);

        // placement assist group
        String indicatorString = config
            .getString("indicatorType", Categories.placementAssist, "TEXTURE", Comments.placementIndicator);
        switch (indicatorString) {
            default:
            case "TEXTURE":
                placementIndicator = PlacementIndicatorSetting.TEXTURE;
                break;
            case "TRIANGLE":
                placementIndicator = PlacementIndicatorSetting.TRIANGLE;
                break;
            case "NONE":
                placementIndicator = PlacementIndicatorSetting.NONE;
                break;
        }
        indicatorScale = config
            .getFloat("indicatorScale", Categories.placementAssist, 1.0f, 0, Float.MAX_VALUE, Comments.indicatorScale);

        // ponder group
        comfyReading = config.getBoolean("comfyReading", Categories.ponder, false, Comments.comfyReading);
        editingMode = config.getBoolean("editingMode", Categories.ponder, false, Comments.editingMode);

        // sound group
        enableAmbientSounds = config
            .getBoolean("enableAmbientSounds", Categories.sound, true, Comments.enableAmbientSounds);
        ambientVolumeCap = config.getFloat("ambientVolumeCap", Categories.sound, .1f, 0, 1, Comments.ambientVolumeCap);
    }

    // no group
    public static boolean tooltips;
    public static boolean enableOverstressedTooltip;
    public static boolean explainRenderErrors;
    public static float fanParticleDensity;
    public static float filterItemRenderDistance;
    public static boolean rainbowDebug;
    public static boolean experimentalRendering;
    public static int maxContraptionLightVolume;
    public static int mainMenuConfigButtonRow;
    public static int mainMenuConfigButtonOffsetX;
    public static int ingameMenuConfigButtonRow;
    public static int ingameMenuConfigButtonOffsetX;
    public static boolean ignoreFabulousWarning;

    // overlay group
    public static int overlayOffsetX;
    public static int overlayOffsetY;
    public static boolean overlayCustomColor;
    public static int overlayBackgroundColor;
    public static int overlayBorderColorTop;
    public static int overlayBorderColorBot;

    // placement assist group
    public static PlacementIndicatorSetting placementIndicator;
    public static float indicatorScale;

    // ponder group
    public static boolean comfyReading;
    public static boolean editingMode;

    // sound group
    public static boolean enableAmbientSounds;
    public static float ambientVolumeCap;

    public enum PlacementIndicatorSetting {
        TEXTURE,
        TRIANGLE,
        NONE
    }

    private static class Categories {

        static String client = "client";
        static String overlay = "goggleOverlay";
        static String placementAssist = "placementAssist";
        static String ponder = "ponder";
        static String sound = "sound";
    }

    private static class Comments {

        static String client = "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!";
        static String tooltips = "Show item descriptions on Shift and controls on Ctrl.";
        static String enableOverstressedTooltip = "Display a tooltip when looking at overstressed components.";
        static String explainRenderErrors = "Log a stack-trace when rendering issues happen within a moving contraption.";
        static String fanParticleDensity = "Higher density means more spawned particles.";
        static String filterItemRenderDistance = "[in Blocks]\n"
            + "Maximum Distance to the player at which items in Blocks' filter slots will be displayed";
        static String rainbowDebug = "Show colourful debug information while the F3-Menu is open.";
        static String experimentalRendering = "Use modern OpenGL features to drastically increase performance.";
        static String maxContraptionLightVolume = "The maximum amount of blocks for which to try and calculate dynamic contraption lighting. Decrease if large contraption cause too much lag";
        static String mainMenuConfigButtonRow = "Choose the menu row that the Create config button appears on in the main menu\n"
            + "Set to 0 to disable the button altogether";
        static String mainMenuConfigButtonOffsetX = "Offset the Create config button in the main menu by this many pixels on the X axis\n"
            + "The sign (-/+) of this value determines what side of the row the button appears on (left/right)";
        static String ingameMenuConfigButtonRow = "Choose the menu row that the Create config button appears on in the in-game menu\n"
            + "Set to 0 to disable the button altogether";
        static String ingameMenuConfigButtonOffsetX = "Offset the Create config button in the in-game menu by this many pixels on the X axis\n"
            + "The sign (-/+) of this value determines what side of the row the button appears on (left/right)";
        static String ignoreFabulousWarning = "Setting this to true will prevent Create from sending you a warning when playing with Fabulous graphics enabled";
        static String overlay = "Settings for the Goggle Overlay";
        static String overlayOffset = "Offset the overlay from goggle- and hover- information by this many pixels on the respective axis; Use /create overlay";
        static String overlayCustomColor = "Enable this to use your custom colors for the Goggle- and Hover- Overlay";
        static String overlayBackgroundColor = "The custom background color to use for the Goggle- and Hover- Overlays + if enabled\n"
            + "[in Hex: #AaRrGgBb]\n"
            + ConfigAnnotations.IntDisplay.HEX.asComment();
        static String overlayBorderColorTop = "The custom top color of the border gradient to use for the Goggle- and Hover- Overlays + if enabled\n"
            + "[in Hex: #AaRrGgBb]\n"
            + ConfigAnnotations.IntDisplay.HEX.asComment();
        static String overlayBorderColorBot = "The custom bot color of the border gradient to use for the Goggle- and Hover- Overlays + if enabled\n"
            + "[in Hex: #AaRrGgBb]\n"
            + ConfigAnnotations.IntDisplay.HEX.asComment();
        static String placementAssist = "Settings for the Placement Assist";
        static String placementIndicator = "What indicator should be used when showing where the assisted placement ends up relative to your crosshair\n"
            + "[TEXTURE|TRIANGLE|NONE]\n"
            + "Choose 'NONE' to disable the Indicator altogether";
        static String indicatorScale = "Change the size of the Indicator by this multiplier";
        static String ponder = "Ponder settings";
        static String comfyReading = "Slow down a ponder scene whenever there is text on screen.";
        static String editingMode = "Show additional info in the ponder view and reload scene scripts more frequently.";
        static String sound = "Sound settings";
        static String enableAmbientSounds = "Make cogs rumble and machines clatter.";
        static String ambientVolumeCap = "Maximum volume modifier of Ambient noise";
    }

}
