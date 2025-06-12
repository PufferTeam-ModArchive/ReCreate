package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import su.sergiusonesimus.metaworlds.api.SubWorld;
import su.sergiusonesimus.metaworlds.api.SubWorldTypeManager;
import su.sergiusonesimus.metaworlds.api.SubWorldTypeManager.SubWorldInfoProvider;
import su.sergiusonesimus.metaworlds.world.SubWorldInfoHolder;
import su.sergiusonesimus.recreate.zmixin.interfaces.IMixinWorldReCreate;

public class AllSubWorldTypes {

    public static final String SUBWORLD_TYPE_CONTRAPTION_BEARING = "bearing_contraption";

    public static void register() {
        ContraptionWorldInfoProvider contraptionProvider = new ContraptionWorldInfoProvider();
        SubWorldTypeManager.registerSubWorldType(SUBWORLD_TYPE_CONTRAPTION_BEARING, contraptionProvider);
    }

    public static class ContraptionWorldInfoProvider extends SubWorldInfoProvider {

        public World create(World parentWorld, int id) {
            return ((IMixinWorldReCreate) parentWorld).createContraptionWorld(id, null);
        }

        public IMessage getCreatePacket(SubWorld sourceWorld) {
            return new ContraptionWorldCreatePacket((ContraptionWorld) sourceWorld);
        }

        public SubWorldInfoHolder fromSubworld(SubWorld sourceWorld) {
            return new ContraptionWorldInfoHolder(sourceWorld);
        }

        public SubWorldInfoHolder fromNBT(NBTTagCompound sourceNBT) {
            return new ContraptionWorldInfoHolder(sourceNBT);
        }

    }

}
