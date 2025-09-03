package su.sergiusonesimus.recreate.content.contraptions.relays.elementary;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.AllModelTextures;

public abstract class AbstractShaftModel extends ModelBase {

    AllModelTextures texture;
    protected int textureWidth;
    protected int textureHeight;
    protected ModelRenderer core;
    protected Axis axis;

    public AbstractShaftModel(AllModelTextures texture, int textureWidth, int textureHeight) {
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        core = new ModelRenderer(this).setTextureSize(textureWidth, textureHeight);
        this.axis = Axis.Y;
        setRotation(0);
    }

    public AbstractShaftModel setAxis(Axis axis) {
        this.axis = axis;
        switch (this.axis) {
            case X:
                core.rotateAngleX = 0;
                core.rotateAngleY = 0;
                core.rotateAngleZ = (float) (-Math.PI / 2);
                break;
            default:
            case Y:
                core.rotateAngleX = 0;
                core.rotateAngleY = 0;
                core.rotateAngleZ = 0;
                break;
            case Z:
                core.rotateAngleX = (float) (-Math.PI / 2);
                core.rotateAngleY = 0;
                core.rotateAngleZ = 0;
                break;
        }
        return this;
    }

    public AbstractShaftModel setRotation(float angle) {
        switch (axis) {
            default:
            case X:
            case Y:
                core.rotateAngleY = angle;
                break;
            case Z:
                core.rotateAngleZ = angle;
                break;
        }
        return this;
    }

    public void render() {
        texture.bind();
        core.render(0.0625f);
    }

    public void render(TileEntitySpecialRenderer renderer) {
        renderer.bindTexture(texture.getLocation());
        core.render(0.0625f);
    }

}
