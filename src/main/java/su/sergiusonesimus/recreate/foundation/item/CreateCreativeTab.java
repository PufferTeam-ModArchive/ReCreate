package su.sergiusonesimus.recreate.foundation.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.ReCreate;

public class CreateCreativeTab extends CreativeTabs {

    public CreateCreativeTab(String label) {
        super(ReCreate.ID + "." + label);
    }

    /**
     * Get the ItemStack that will be rendered to the tab.
     */
    @Override
    public Item getTabIconItem() {
        return Item.getItemFromBlock(AllBlocks.cogwheel);
    }
}
