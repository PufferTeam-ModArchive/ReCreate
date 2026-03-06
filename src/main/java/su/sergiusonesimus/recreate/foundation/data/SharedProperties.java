package su.sergiusonesimus.recreate.foundation.data;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class SharedProperties {

    public static Material beltMaterial = new Material(MapColor.grayColor);

    public static Block stone() {
        return Blocks.stone;
    }

    public static Block softMetal() {
        return Blocks.gold_block;
    }

    // TODO
    // public static Block copperMetal() {
    // return Blocks.COPPER_BLOCK;
    // }

    public static Block wooden() {
        // Originally was stripped spruce
        return Blocks.planks;
    }
}
