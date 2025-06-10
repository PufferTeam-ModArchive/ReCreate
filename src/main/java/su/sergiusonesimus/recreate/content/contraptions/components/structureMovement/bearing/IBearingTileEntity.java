package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.bearing;

import net.minecraft.block.Block;

import su.sergiusonesimus.recreate.content.contraptions.base.IRotate;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.DirectionalExtenderScrollOptionSlot;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.IControlContraption;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.ValueBoxTransform;
import su.sergiusonesimus.recreate.util.Direction.Axis;

public interface IBearingTileEntity extends IControlContraption {

    float getInterpolatedAngle(float partialTicks);

    boolean isWoodenTop();

    default ValueBoxTransform getMovementModeSlot() {
        return new DirectionalExtenderScrollOptionSlot((state, d) -> {
            Axis axis = d.getAxis();
            Block block = state.getFirst();
            int meta = state.getSecond();
            Axis bearingAxis = ((IRotate) block).getAxis(meta);
            return bearingAxis != axis;
        });
    }

    void setAngle(float forcedAngle);

}
