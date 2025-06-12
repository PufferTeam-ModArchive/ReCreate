package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.bearing;

import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import su.sergiusonesimus.recreate.AllModelTextures;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntityRenderer;
import su.sergiusonesimus.recreate.foundation.utility.Color;
import su.sergiusonesimus.recreate.util.Direction;

public class BearingTileEntityRenderer extends KineticTileEntityRenderer {

    BearingModel model = new BearingModel();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        Direction direction = ((BearingBlock) tileEntity.getBlockType()).getDirection(tileEntity.getBlockMetadata());
        model.setFace(direction);
        MechanicalBearingTileEntity mbte = (MechanicalBearingTileEntity) tileEntity;
        float topAngle = mbte.movedContraption == null ? mbte.getInterpolatedAngle(partialTicks - 1f)
            : (float) mbte.movedContraption.getAngle();
        model.setRotations(
            getAngleForTe(mbte, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, direction.getAxis()),
            topAngle / 180f * (float) Math.PI);
        Color color = getColor(mbte);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        GL11.glColor4f(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), color.getAlphaAsFloat());

        model.render(AllModelTextures.MECHANICAL_BEARING);

        GL11.glPopMatrix();
    }

}
