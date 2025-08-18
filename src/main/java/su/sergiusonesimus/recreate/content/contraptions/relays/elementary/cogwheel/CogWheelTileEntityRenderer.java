package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel;

import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntityRenderer;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftModel;
import su.sergiusonesimus.recreate.foundation.utility.Color;

public class CogWheelTileEntityRenderer extends KineticTileEntityRenderer {

    private final ShaftModel shaft = new ShaftModel();
    private final CogWheelModel cogwheel = new CogWheelModel();
    private final LargeCogWheelModel largeCogwheel = new LargeCogWheelModel();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        CogWheelBlock block = (CogWheelBlock) tileEntity.getBlockType();
        Axis axis = block.getAxis(tileEntity.getBlockMetadata());
        float angle = getAngleForTe(
            (KineticTileEntity) tileEntity,
            tileEntity.xCoord,
            tileEntity.yCoord,
            tileEntity.zCoord,
            axis);
        shaft.setAxis(axis);
        shaft.setRotation(angle);
        if (!block.isLarge) {
            cogwheel.setAxis(axis);
            cogwheel.setRotation(angle);
        } else {
            largeCogwheel.setAxis(axis);
            if (!shouldOffset(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, axis)) angle -= Math.PI / 16F;
            largeCogwheel.setRotation(angle);
        }
        Color color = getColor((KineticTileEntity) tileEntity);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GL11.glColor4f(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), color.getAlphaAsFloat());

        shaft.render();
        if (!block.isLarge) {
            cogwheel.render();
        } else {
            largeCogwheel.render();
        }

        GL11.glPopMatrix();
    }

}
