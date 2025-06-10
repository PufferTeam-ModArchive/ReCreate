package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import net.minecraft.util.ChunkCoordinates;

import su.sergiusonesimus.recreate.foundation.gui.AllIcons;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue.INamedIconOptions;
import su.sergiusonesimus.recreate.foundation.utility.Lang;

public interface IControlContraption {

    public boolean isAttachedTo(Contraption contraption);

    public void attach(Contraption contraption);

    public void onStall();

    public boolean isValid();

    public ChunkCoordinates getPosition();

    public int getPositionX();

    public int getPositionY();

    public int getPositionZ();

    static enum MovementMode implements INamedIconOptions {

        MOVE_PLACE(AllIcons.I_MOVE_PLACE),
        MOVE_PLACE_RETURNED(AllIcons.I_MOVE_PLACE_RETURNED),
        MOVE_NEVER_PLACE(AllIcons.I_MOVE_NEVER_PLACE),

        ;

        private String translationKey;
        private AllIcons icon;

        private MovementMode(AllIcons icon) {
            this.icon = icon;
            translationKey = "contraptions.movement_mode." + Lang.asId(name());
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }

    }

    static enum RotationMode implements INamedIconOptions {

        ROTATE_PLACE(AllIcons.I_ROTATE_PLACE),
        ROTATE_PLACE_RETURNED(AllIcons.I_ROTATE_PLACE_RETURNED),
        ROTATE_NEVER_PLACE(AllIcons.I_ROTATE_NEVER_PLACE),

        ;

        private String translationKey;
        private AllIcons icon;

        private RotationMode(AllIcons icon) {
            this.icon = icon;
            translationKey = "contraptions.movement_mode." + Lang.asId(name());
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }

    }

}
