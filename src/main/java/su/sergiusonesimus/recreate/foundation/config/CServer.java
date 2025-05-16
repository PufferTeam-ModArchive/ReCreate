package su.sergiusonesimus.recreate.foundation.config;

import net.minecraftforge.common.config.Configuration;

public class CServer {

    public static CRecipes recipes;
    public static CKinetics kinetics;
    // public static CFluids fluids;
    // public static CLogistics logistics;
    // public static CSchematics schematics;
    // public static CCuriosities curiosities;

    public static void init(Configuration config) {
        // infrastructure
        config.setCategoryComment(Categories.infrastructure, Comments.infrastructure);

        tickrateSyncTimer = config.getInt(
            "tickrateSyncTimer",
            Categories.infrastructure,
            20,
            5,
            Integer.MAX_VALUE,
            Comments.tickrateSyncTimer);

        // recipes
        config.setCategoryComment(Categories.recipes, Comments.recipes);
        CRecipes.init(config);

        // kinetics
        config.setCategoryComment(Categories.kinetics, Comments.kinetics);
        CKinetics.init(config);

        // fluids
        config.setCategoryComment(Categories.fluids, Comments.fluids);

        // logistics
        config.setCategoryComment(Categories.logistics, Comments.logistics);

        // schematics
        config.setCategoryComment(Categories.schematics, Comments.schematics);

        // curiosities
        config.setCategoryComment(Categories.curiosities, Comments.curiosities);
    }

    public static int tickrateSyncTimer;

    public static class Categories {

        static String infrastructure = "infrastructure";
        static String recipes = "recipes";
        static String kinetics = "kinetics";
        static String fluids = "fluids";
        static String logistics = "logistics";
        static String schematics = "schematics";
        static String curiosities = "curiosities";
    }

    private static class Comments {

        static String recipes = "Packmakers' control panel for internal recipe compat";
        static String schematics = "Everything related to Schematic tools";
        static String kinetics = "Parameters and abilities of Create's kinetic mechanisms";
        static String fluids = "Create's liquid manipulation tools";
        static String logistics = "Tweaks for logistical components";
        static String curiosities = "Gadgets and other Shenanigans added by Create";
        static String infrastructure = "The Backbone of Create";
        static String tickrateSyncTimer = "The amount of time a server waits before sending out tickrate synchronization packets.\n"
            + "These packets help animations to be more accurate when tps is below 20.";
    }

}
