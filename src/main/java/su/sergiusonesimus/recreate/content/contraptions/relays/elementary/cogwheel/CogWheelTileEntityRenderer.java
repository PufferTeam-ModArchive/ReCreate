package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel;

import org.lwjgl.opengl.GL11;

import net.minecraft.tileentity.TileEntity;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntityRenderer;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftModel;
import su.sergiusonesimus.recreate.foundation.utility.Color;
import su.sergiusonesimus.recreate.util.Direction.Axis;

public class CogWheelTileEntityRenderer extends KineticTileEntityRenderer {

    private final ShaftModel shaft = new ShaftModel();
    private final CogWheelModel cogwheel = new CogWheelModel();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z,
			float partialTicks) {
		CogWheelBlock block = (CogWheelBlock)tileEntity.getBlockType();
		Axis axis = block.getAxis(tileEntity.getBlockMetadata());
		float angle = getAngleForTe((KineticTileEntity) tileEntity, tileEntity.xCoord, tileEntity.yCoord,
				tileEntity.zCoord, axis);
		shaft.setAxis(axis);
		shaft.setRotation(angle);
		if(!block.isLarge) {
			cogwheel.setAxis(axis);
			cogwheel.setRotation(angle);
		} else {
			
		}
		Color color = getColor((KineticTileEntity) tileEntity);
		
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
        GL11.glColor4f(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), color.getAlphaAsFloat());
        
        shaft.render();
		if(!block.isLarge) {
	        cogwheel.render();
		} else {
			
		}
        
        GL11.glPopMatrix();
	}

}
