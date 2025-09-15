package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.bearing;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.foundation.gui.AllIcons;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue.INamedIconOptions;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue.ScrollOptionBehaviour;
import su.sergiusonesimus.recreate.foundation.utility.Lang;

public class WindmillBearingTileEntity extends MechanicalBearingTileEntity {

    protected ScrollOptionBehaviour<RotationDirection> movementDirection;
    protected float lastGeneratedSpeed;

    @Override
    public void updateGeneratedRotation() {
        super.updateGeneratedRotation();
        lastGeneratedSpeed = getGeneratedSpeed();
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        boolean cancelAssembly = assembleNextTick;
        super.onSpeedChanged(prevSpeed);
        assembleNextTick = cancelAssembly;
    }

    @SuppressWarnings("static-access")
    @Override
    public float getGeneratedSpeed() {
        if (!running) return 0;
        if (movedContraption == null) return lastGeneratedSpeed;
        int sails = movedContraption.getSailBlocks() / AllConfigs.SERVER.kinetics.windmillSailsPerRPM;
        return MathHelper.clamp_int(sails, 1, 16) * getAngleSpeedDirection();
    }

    @Override
    protected boolean isWindmill() {
        return true;
    }

    protected float getAngleSpeedDirection() {
        RotationDirection rotationDirection = RotationDirection.values()[movementDirection.getValue()];
        return (rotationDirection == RotationDirection.CLOCKWISE ? 1 : -1);
    }

    @Override
    public void write(NBTTagCompound compound, boolean clientPacket) {
        compound.setFloat("LastGenerated", lastGeneratedSpeed);
        super.write(compound, clientPacket);
    }

    @Override
    protected void fromTag(NBTTagCompound compound, boolean clientPacket) {
        if (!wasMoved) lastGeneratedSpeed = compound.getFloat("LastGenerated");
        super.fromTag(compound, clientPacket);
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.remove(movementMode);
        movementDirection = new ScrollOptionBehaviour<>(
            RotationDirection.class,
            Lang.translate("contraptions.windmill.rotation_direction"),
            this,
            getMovementModeSlot());
        movementDirection.requiresWrench();
        movementDirection.withCallback($ -> onDirectionChanged());
        behaviours.add(movementDirection);
    }

    private void onDirectionChanged() {
        if (!running) return;
        if (!worldObj.isRemote) updateGeneratedRotation();
    }

    @Override
    public boolean isWoodenTop() {
        return true;
    }

    static enum RotationDirection implements INamedIconOptions {

        CLOCKWISE(AllIcons.I_REFRESH),
        COUNTER_CLOCKWISE(AllIcons.I_ROTATE_CCW),

        ;

        private String translationKey;
        private AllIcons icon;

        private RotationDirection(AllIcons icon) {
            this.icon = icon;
            translationKey = "generic." + Lang.asId(name());
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
