package su.sergiusonesimus.recreate.zmixin.interfaces;

import net.minecraft.world.World;

import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.Contraption;

public interface IMixinWorldReCreate {

    public abstract World createContraptionWorld(Contraption contraption);

    public abstract World createContraptionWorld(int newSubWorldID, Contraption contraption);

}
