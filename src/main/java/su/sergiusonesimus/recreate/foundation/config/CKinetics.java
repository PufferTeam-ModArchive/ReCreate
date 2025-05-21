package su.sergiusonesimus.recreate.foundation.config;

import net.minecraftforge.common.config.Configuration;

import su.sergiusonesimus.recreate.foundation.config.ui.ConfigAnnotations;

public class CKinetics {

    public static boolean disableStress;
    public static int maxBeltLength;
    public static int crushingDamage;
    public static int maxMotorSpeed;
    public static int waterWheelBaseSpeed;
    public static int waterWheelFlowSpeed;
    public static int furnaceEngineSpeed;
    public static int maxRotationSpeed;
    public static DeployerAggroSetting ignoreDeployerAttacks;
    public static int kineticValidationFrequency;
    public static float crankHungerMultiplier;
    public static int minimumWindmillSails;
    public static int windmillSailsPerRPM;
    public static int maxEjectorDistance;
    public static int ejectorScanInterval;

    // Encased Fan
    public static int fanPushDistance;
    public static int fanPullDistance;
    public static int fanBlockCheckRate;
    public static int fanRotationArgmax;
    public static Integer generatingFanSpeed;
    public static int inWorldProcessingTime;

    // Moving Contraptions
    public static int maxBlocksMoved;
    public static int maxChassisRange;
    public static int maxPistonPoles;
    public static int maxRopeLength;
    public static int maxCartCouplingLength;
    public static ContraptionMovementSetting spawnerMovement;
    public static ContraptionMovementSetting obsidianMovement;
    public static boolean moveItemsToStorage;
    public static boolean harvestPartiallyGrown;
    public static boolean harvesterReplants;

    // stress
    // TODO
    // public static CStress stressValues = nested(1, CStress::new, Comments.stress);

    // Stats
    public static float mediumSpeed;
    public static float fastSpeed;
    public static float mediumStressImpact;
    public static float highStressImpact;
    public static float mediumCapacity;
    public static float highCapacity;

    public static void init(String category, Configuration config) {
        // kinetics
        disableStress = config.getBoolean("disableStress", category, false, Comments.disableStress);

        maxBeltLength = config.getInt("maxBeltLength", category, 20, 5, Integer.MAX_VALUE, Comments.maxBeltLength);

        crushingDamage = config.getInt("crushingDamage", category, 4, 0, Integer.MAX_VALUE, Comments.crushingDamage);

        maxMotorSpeed = config.getInt(
            "maxMotorSpeed",
            category,
            256,
            64,
            Integer.MAX_VALUE,
            Comments.rpm + "\n" + Comments.maxMotorSpeed + "\n" + ConfigAnnotations.RequiresRestart.BOTH.asComment());

        waterWheelBaseSpeed = config.getInt(
            "waterWheelBaseSpeed",
            category,
            4,
            1,
            Integer.MAX_VALUE,
            Comments.rpm + "\n" + Comments.waterWheelBaseSpeed);

        waterWheelFlowSpeed = config.getInt(
            "waterWheelFlowSpeed",
            category,
            4,
            1,
            Integer.MAX_VALUE,
            Comments.rpm + "\n" + Comments.waterWheelFlowSpeed);

        furnaceEngineSpeed = config.getInt(
            "furnaceEngineSpeed",
            category,
            16,
            1,
            Integer.MAX_VALUE,
            Comments.rpm + "\n" + Comments.furnaceEngineSpeed);

        maxRotationSpeed = config.getInt(
            "maxRotationSpeed",
            category,
            256,
            64,
            Integer.MAX_VALUE,
            Comments.rpm + "\n" + Comments.maxRotationSpeed);

        String ignoreDeployerString = config
            .getString("ignoreDeployerAttacks", category, "CREEPERS", Comments.ignoreDeployerAttacks);
        switch (ignoreDeployerString) {
            case "ALL":
                ignoreDeployerAttacks = DeployerAggroSetting.ALL;
                break;
            default:
            case "CREEPERS":
                ignoreDeployerAttacks = DeployerAggroSetting.CREEPERS;
                break;
            case "NONE":
                ignoreDeployerAttacks = DeployerAggroSetting.NONE;
                break;
        }

        kineticValidationFrequency = config.getInt(
            "kineticValidationFrequency",
            category,
            60,
            5,
            Integer.MAX_VALUE,
            Comments.kineticValidationFrequency);

        crankHungerMultiplier = config
            .getFloat("crankHungerMultiplier", category, 0.01f, 0.0f, 1.0f, Comments.crankHungerMultiplier);

        minimumWindmillSails = config
            .getInt("minimumWindmillSails", category, 8, 0, Integer.MAX_VALUE, Comments.minimumWindmillSails);

        windmillSailsPerRPM = config
            .getInt("windmillSailsPerRPM", category, 8, 1, Integer.MAX_VALUE, Comments.windmillSailsPerRPM);

        maxEjectorDistance = config
            .getInt("maxEjectorDistance", category, 32, 0, Integer.MAX_VALUE, Comments.maxEjectorDistance);

        ejectorScanInterval = config
            .getInt("ejectorScanInterval", category, 120, 10, Integer.MAX_VALUE, Comments.ejectorScanInterval);

        // Encased Fan
        config.addCustomCategoryComment(Categories.encasedFan, "Encased Fan");

        fanPushDistance = config
            .getInt("fanPushDistance", Categories.encasedFan, 20, 5, Integer.MAX_VALUE, Comments.fanPushDistance);

        fanPullDistance = config
            .getInt("fanPullDistance", Categories.encasedFan, 20, 5, Integer.MAX_VALUE, Comments.fanPullDistance);

        fanBlockCheckRate = config
            .getInt("fanBlockCheckRate", Categories.encasedFan, 30, 10, Integer.MAX_VALUE, Comments.fanBlockCheckRate);

        fanRotationArgmax = config.getInt(
            "fanRotationArgmax",
            Categories.encasedFan,
            256,
            64,
            Integer.MAX_VALUE,
            Comments.rpm + "\n" + Comments.fanRotationArgmax);

        generatingFanSpeed = config.getInt(
            "generatingFanSpeed",
            Categories.encasedFan,
            4,
            0,
            Integer.MAX_VALUE,
            Comments.rpm + "\n" + Comments.generatingFanSpeed);

        inWorldProcessingTime = config.getInt(
            "inWorldProcessingTime",
            Categories.encasedFan,
            150,
            0,
            Integer.MAX_VALUE,
            Comments.inWorldProcessingTime);

        // Moving Contraptions
        maxBlocksMoved = config
            .getInt("maxBlocksMoved", Categories.contraptions, 2048, 1, Integer.MAX_VALUE, Comments.maxBlocksMoved);

        maxChassisRange = config
            .getInt("maxChassisRange", Categories.contraptions, 16, 1, Integer.MAX_VALUE, Comments.maxChassisRange);

        maxPistonPoles = config
            .getInt("maxPistonPoles", Categories.contraptions, 64, 1, Integer.MAX_VALUE, Comments.maxPistonPoles);

        maxRopeLength = config
            .getInt("maxRopeLength", Categories.contraptions, 128, 1, Integer.MAX_VALUE, Comments.maxRopeLength);

        maxCartCouplingLength = config.getInt(
            "maxCartCouplingLength",
            Categories.contraptions,
            32,
            1,
            Integer.MAX_VALUE,
            Comments.maxCartCouplingLength);

        String movableSpawnersString = config
            .getString("movableSpawners", Categories.contraptions, "NO_PICKUP", Comments.spawnerMovement);
        switch (movableSpawnersString) {
            case "MOVABLE":
                spawnerMovement = ContraptionMovementSetting.MOVABLE;
                break;
            default:
            case "NO_PICKUP":
                spawnerMovement = ContraptionMovementSetting.NO_PICKUP;
                break;
            case "UNMOVABLE":
                spawnerMovement = ContraptionMovementSetting.UNMOVABLE;
                break;
        }

        String movableObsidianString = config
            .getString("movableObsidian", Categories.contraptions, "UNMOVABLE", Comments.obsidianMovement);
        switch (movableSpawnersString) {
            case "MOVABLE":
                obsidianMovement = ContraptionMovementSetting.MOVABLE;
                break;
            case "NO_PICKUP":
                obsidianMovement = ContraptionMovementSetting.NO_PICKUP;
                break;
            default:
            case "UNMOVABLE":
                obsidianMovement = ContraptionMovementSetting.UNMOVABLE;
                break;
        }

        moveItemsToStorage = config
            .getBoolean("moveItemsToStorage", Categories.contraptions, true, Comments.moveItemsToStorage);

        harvestPartiallyGrown = config
            .getBoolean("harvestPartiallyGrown", Categories.contraptions, false, Comments.harvestPartiallyGrown);

        harvesterReplants = config
            .getBoolean("harvesterReplants", Categories.contraptions, true, Comments.harvesterReplants);

        // Stress
        // TODO

        // Stats
        mediumSpeed = config.getFloat(
            "mediumSpeed",
            Categories.stats,
            30.0f,
            0.0f,
            4096.0f,
            Comments.rpm + "\n" + Comments.mediumSpeed);

        fastSpeed = config
            .getFloat("fastSpeed", Categories.stats, 100.0f, 0.0f, 65535.0f, Comments.rpm + "\n" + Comments.fastSpeed);

        mediumStressImpact = config.getFloat(
            "mediumStressImpact",
            Categories.stats,
            4.0f,
            0.0f,
            4096.0f,
            Comments.su + "\n" + Comments.mediumStressImpact);

        highStressImpact = config.getFloat(
            "highStressImpact",
            Categories.stats,
            8.0f,
            0.0f,
            65535.0f,
            Comments.su + "\n" + Comments.highStressImpact);

        mediumCapacity = config.getFloat(
            "mediumCapacity",
            Categories.stats,
            128.0f,
            0.0f,
            4096.0f,
            Comments.su + "\n" + Comments.mediumCapacity);

        highCapacity = config.getFloat(
            "highCapacity",
            Categories.stats,
            512.0f,
            0.0f,
            65535.0f,
            Comments.su + "\n" + Comments.highCapacity);
    }

    private static class Categories {

        static String encasedFan = "encasedFan";
        static String contraptions = "contraptions";
        static String stats = "stats";
    }

    private static class Comments {

        static String maxBeltLength = "Maximum length in blocks of mechanical belts.";
        static String crushingDamage = "Damage dealt by active Crushing Wheels.";
        static String maxMotorSpeed = "Maximum allowed speed of a configurable motor.";
        static String maxRotationSpeed = "Maximum allowed rotation speed for any Kinetic Tile.";
        static String fanPushDistance = "Maximum distance in blocks Fans can push entities.";
        static String fanPullDistance = "Maximum distance in blocks from where Fans can pull entities.";
        static String fanBlockCheckRate = "Game ticks between Fans checking for anything blocking their air flow.";
        static String fanRotationArgmax = "Rotation speed at which the maximum stats of fans are reached.";
        static String generatingFanSpeed = "Rotation speed generated by a vertical fan above fire.";
        static String inWorldProcessingTime = "Game ticks required for a Fan-based processing recipe to take effect.";
        static String crankHungerMultiplier = "multiplier used for calculating exhaustion from speed when a crank is turned.";
        static String maxBlocksMoved = "Maximum amount of blocks in a structure movable by Pistons, Bearings or other means.";
        static String maxChassisRange = "Maximum value of a chassis attachment range.";
        static String maxPistonPoles = "Maximum amount of extension poles behind a Mechanical Piston.";
        static String maxRopeLength = "Max length of rope available off a Rope Pulley.";
        static String maxCartCouplingLength = "Maximum allowed distance of two coupled minecarts.";
        static String moveItemsToStorage = "Whether items mined or harvested by contraptions should be placed in their mounted storage.";
        static String harvestPartiallyGrown = "Whether harvesters should break crops that aren't fully grown.";
        static String harvesterReplants = "Whether harvesters should replant crops after harvesting.";
        static String stats = "Configure speed/capacity levels for requirements and indicators.";
        static String rpm = "[in Revolutions per Minute]";
        static String su = "[in Stress Units]";
        static String mediumSpeed = "Minimum speed of rotation to be considered 'medium'";
        static String fastSpeed = "Minimum speed of rotation to be considered 'fast'";
        static String mediumStressImpact = "Minimum stress impact to be considered 'medium'";
        static String highStressImpact = "Minimum stress impact to be considered 'high'";
        static String mediumCapacity = "Minimum added Capacity by sources to be considered 'medium'";
        static String highCapacity = "Minimum added Capacity by sources to be considered 'high'";
        static String stress = "Fine tune the kinetic stats of individual components";
        static String ignoreDeployerAttacks = "Select what mobs should ignore Deployers when attacked by them.\n"
            + "[ALL|CREEPERS|NONE]";
        static String waterWheelBaseSpeed = "Added rotation speed by a water wheel when at least one flow is present.";
        static String waterWheelFlowSpeed = "Rotation speed gained by a water wheel for each side with running fluids. (halved if not against blades)";
        static String furnaceEngineSpeed = "Base rotation speed for the furnace engine generator";
        static String disableStress = "Disable the Stress mechanic altogether.";
        static String kineticValidationFrequency = "Game ticks between Kinetic Blocks checking whether their source is still valid.";
        static String minimumWindmillSails = "Amount of sail-type blocks required for a windmill to assemble successfully.";
        static String windmillSailsPerRPM = "Number of sail-type blocks required to increase windmill speed by 1RPM.";
        static String maxEjectorDistance = "Max Distance in blocks a Weighted Ejector can throw";
        static String ejectorScanInterval = "Time in ticks until the next item launched by an ejector scans blocks for potential collisions";
        static String spawnerMovement = "Configure how Spawner blocks can be moved by contraptions.\n"
            + "[MOVABLE|NO_PICKUP|UNMOVABLE]";
        // static String amethystMovement = "Configure how Budding Amethyst can be moved by contraptions.";
        static String obsidianMovement = "Configure how Obsidian blocks can be moved by contraptions.\n"
            + "[MOVABLE|NO_PICKUP|UNMOVABLE]";
    }

    public enum DeployerAggroSetting {
        ALL,
        CREEPERS,
        NONE
    }

}
