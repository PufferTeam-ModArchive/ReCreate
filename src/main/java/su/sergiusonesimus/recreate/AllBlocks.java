package su.sergiusonesimus.recreate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

import cpw.mods.fml.common.registry.GameRegistry;
import su.sergiusonesimus.recreate.content.contraptions.components.motor.CreativeMotorBlock;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel.CogWheelBlock;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel.CogWheelItemBlock;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftBlock;

public class AllBlocks {

    public static Block shaft;
    public static Block creativeMotor;
    public static Block cogwheel;
    public static Block largeCogwheel;

    public static void registerBlocks() {
        shaft = new ShaftBlock(Material.rock).setBlockName("shaft");
        creativeMotor = new CreativeMotorBlock(Material.piston).setBlockName("creative_motor");
        cogwheel = CogWheelBlock.small(Material.wood)
            .setBlockName("cogwheel");
        largeCogwheel = CogWheelBlock.large(Material.wood)
            .setBlockName("large_cogwheel");

        registerMyBlock(shaft);
        registerMyBlock(creativeMotor);
        registerMyBlock(cogwheel, CogWheelItemBlock.class);
        registerMyBlock(largeCogwheel, CogWheelItemBlock.class);
    }

    private static void registerMyBlock(Block block, Class<? extends ItemBlock> pickup, BlockSlab singleSlab,
        BlockSlab doubleSlab, boolean isDouble) {
        GameRegistry.registerBlock(block, pickup, block.getUnlocalizedName(), singleSlab, doubleSlab, isDouble);

    }

    private static void registerMyBlock(Block block, Class<? extends ItemBlock> pickup, Block blockAgain,
        String[] names) {
        GameRegistry.registerBlock(block, pickup, block.getUnlocalizedName(), blockAgain, names);
    }

    private static void registerMyBlock(Block block, Class<? extends ItemBlock> pickup) {
        GameRegistry.registerBlock(block, pickup, block.getUnlocalizedName());
    }

    private static void registerMyBlock(Block block) {
        GameRegistry.registerBlock(block, ItemBlock.class, block.getUnlocalizedName());
    }

}
