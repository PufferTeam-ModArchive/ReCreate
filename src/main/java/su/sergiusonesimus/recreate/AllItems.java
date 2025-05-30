package su.sergiusonesimus.recreate;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import su.sergiusonesimus.recreate.content.contraptions.goggles.GogglesItem;
import su.sergiusonesimus.recreate.content.contraptions.wrench.WrenchItem;
import su.sergiusonesimus.recreate.foundation.item.CreateCreativeTab;

public class AllItems {

    public static ItemArmor.ArmorMaterial ARMOR_GOGGLES = EnumHelper
        .addArmorMaterial("GOGGLES", 0, new int[] { 0, 0, 0, 0 }, 0);

    public static Item goggles;
    public static Item wrench;

    public static final CreateCreativeTab BASE_CREATIVE_TAB = new CreateCreativeTab("base");

    public static void registerItems() {
        int gogglesRenderID = ReCreate.proxy.registerArmorRenderID("recreate/goggles_");

        goggles = new GogglesItem(ARMOR_GOGGLES, gogglesRenderID).setUnlocalizedName("goggles")
            .setMaxStackSize(1);
        wrench = new WrenchItem().setUnlocalizedName("wrench")
            .setMaxStackSize(1);

        registerItem(goggles);
        registerItem(wrench);
    }

    private static void registerItem(Item item) {
        GameRegistry.registerItem(item, item.getUnlocalizedName(), ReCreate.ID);
    }

}
