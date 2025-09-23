package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston;

import org.lwjgl.opengl.GL11;

import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntityRenderer;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftModel;
import su.sergiusonesimus.recreate.foundation.utility.Color;

public class MechanicalPistonTileEntityRenderer extends KineticTileEntityRenderer {

    ShaftModel shaft = new ShaftModel();

    @Override
    protected void renderSafe(KineticTileEntity tileEntity, double x, double y, double z, float partialTicks) {
        MechanicalPistonBlock block = (MechanicalPistonBlock) tileEntity.getBlockType();
        Axis axis = block.getAxis(tileEntity.getBlockMetadata());
        shaft.setAxis(axis);
        MechanicalPistonTileEntity mpte = (MechanicalPistonTileEntity) tileEntity;
        shaft.setRotation(getAngleForTe(mpte, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, axis));
        Color color = getColor(mpte);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GL11.glColor4f(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), color.getAlphaAsFloat());

        shaft.render(this);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();
    }

}
