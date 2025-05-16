package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import su.sergiusonesimus.recreate.AllModelTextures;
import su.sergiusonesimus.recreate.util.Direction.Axis;
import su.sergiusonesimus.recreate.util.Direction.AxisDirection;

public class ShaftModel extends ModelBase {

    ModelRenderer shaft;
    Axis axis;
    
    public ShaftModel() {
    	shaft = new ModelRenderer(this, 0, 0).setTextureSize(32, 32);
    	shaft.addBox(-2F, -8F, -2F, 4, 16, 4, 0F);
    	shaft.setRotationPoint(0F, 0F, 0F);
    	axis = Axis.Y;
    	setRotation(0);
    }
    
    public ShaftModel(AxisDirection direction) {
    	shaft = new ModelRenderer(this, 0, 0).setTextureSize(32, 32);
    	shaft.addBox(-2F, direction == AxisDirection.POSITIVE? -8F : 0, -2F, 4, 8, 4, 0F);
    	shaft.setRotationPoint(0F, 0F, 0F);
    	axis = Axis.Y;
    	setRotation(0);
    }
    
    public ShaftModel setAxis(Axis axis) {
    	this.axis = axis;
    	switch(this.axis) {
		case X:
			shaft.rotateAngleX = 0;
			shaft.rotateAngleY = 0;
			shaft.rotateAngleZ = (float) (- Math.PI / 2);
			break;
		default:
		case Y:
			shaft.rotateAngleX = 0;
			shaft.rotateAngleY = 0;
			shaft.rotateAngleZ = 0;
			break;
		case Z:
			shaft.rotateAngleX = (float) (- Math.PI / 2);
			shaft.rotateAngleY = 0;
			shaft.rotateAngleZ = 0;
			break;
    	}
    	return this;
    }
    
    public ShaftModel setRotation(float angle) {
    	switch(axis) {
    		default:
    		case X:
    		case Y:
    			shaft.rotateAngleY = angle;
    			break;
    		case Z:
    			shaft.rotateAngleZ = angle;
    			break;
    	}
    	return this;
    }
    
    public void render() {
        AllModelTextures.SHAFT.bind();
        //GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
    	shaft.render(0.0625F);
    }

}
