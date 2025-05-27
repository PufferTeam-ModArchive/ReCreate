package su.sergiusonesimus.recreate.content.contraptions.components.motor;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class CreativeMotorItemBlock extends ItemBlock {

    public CreativeMotorItemBlock(Block block) {
        super(block);
    }

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.epic;
    }

}
