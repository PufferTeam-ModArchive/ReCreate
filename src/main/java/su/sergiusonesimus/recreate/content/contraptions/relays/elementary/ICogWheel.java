package su.sergiusonesimus.recreate.content.contraptions.relays.elementary;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import su.sergiusonesimus.recreate.content.contraptions.base.IRotate;

public interface ICogWheel extends IRotate {

    static boolean isSmallCog(Block block) {
        return block instanceof ICogWheel && ((ICogWheel) block).isSmallCog();
    }

    static boolean isLargeCog(Block block) {
        return block instanceof ICogWheel && ((ICogWheel) block).isLargeCog();
    }

    static boolean isDedicatedCogWheel(Block block) {
        return block instanceof ICogWheel && ((ICogWheel) block).isDedicatedCogWheel();
    }

    static boolean isDedicatedCogItem(ItemStack test) {
        Item item = test.getItem();
        if (!(item instanceof ItemBlock)) return false;
        return isDedicatedCogWheel(((ItemBlock) item).field_150939_a);
    }

    static boolean isSmallCogItem(ItemStack test) {
        Item item = test.getItem();
        if (!(item instanceof ItemBlock)) return false;
        return isSmallCog(((ItemBlock) item).field_150939_a);
    }

    static boolean isLargeCogItem(ItemStack test) {
        Item item = test.getItem();
        if (!(item instanceof ItemBlock)) return false;
        return isLargeCog(((ItemBlock) item).field_150939_a);
    }

    default boolean isLargeCog() {
        return false;
    }

    default boolean isSmallCog() {
        return !isLargeCog();
    }

    default boolean isDedicatedCogWheel() {
        return false;
    }
}
