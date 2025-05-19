package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft;

import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntityRenderer;
import su.sergiusonesimus.recreate.foundation.utility.Color;
import su.sergiusonesimus.recreate.util.Direction.Axis;

public class ShaftTileEntityRenderer extends KineticTileEntityRenderer {

    private final ShaftModel model = new ShaftModel();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        Axis axis = ((ShaftBlock) tileEntity.getBlockType()).getAxis(tileEntity.getBlockMetadata());
        model.setAxis(axis);
        model.setRotation(
            getAngleForTe(
                (KineticTileEntity) tileEntity,
                tileEntity.xCoord,
                tileEntity.yCoord,
                tileEntity.zCoord,
                axis));
        Color color = getColor((KineticTileEntity) tileEntity);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GL11.glColor4f(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), color.getAlphaAsFloat());

        model.render();

        GL11.glPopMatrix();
    }

}
