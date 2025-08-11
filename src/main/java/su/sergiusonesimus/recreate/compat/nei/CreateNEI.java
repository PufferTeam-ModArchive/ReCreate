package su.sergiusonesimus.recreate.compat.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import net.minecraft.item.ItemStack;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.Tags;

public class CreateNEI implements IConfigureNEI {
    @Override
    public void loadConfig() {
    }

    @Override
    public String getName() {
        return "Create NEI Plugin";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
