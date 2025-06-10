package su.sergiusonesimus.recreate.content.contraptions.wrench;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import su.sergiusonesimus.recreate.AllModelTextures;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue.ScrollValueHandler;
import su.sergiusonesimus.recreate.util.AnimationTickHolder;

public class WrenchModel extends ModelBase {

    AllModelTextures texture;
    protected int textureWidth;
    protected int textureHeight;

    protected ModelRenderer wrench;
    protected ModelRenderer handle1;
    protected ModelRenderer handle2;
    protected ModelRenderer axle;
    protected ModelRenderer clamp1;
    protected ModelRenderer clamp2;
    protected ModelRenderer gearCaseTop;
    protected ModelRenderer gearCase;
    protected ModelRenderer cog;
    protected ModelRenderer[] cogs;

    public WrenchModel() {
        texture = AllModelTextures.WRENCH;
        textureWidth = 32;
        textureHeight = 32;

        wrench = new ModelRenderer(this, 0, 0).setTextureSize(textureWidth, textureHeight);

        handle1 = new ModelRenderer(this, 0, 0).setTextureSize(textureWidth, textureHeight);
        handle1.addBox(-1F, -6F, -1F, 2, 12, 2);
        handle1.setRotationPoint(0, -10, 0.5F);
        wrench.addChild(handle1);

        handle2 = new ModelRenderer(this, 20, 0).setTextureSize(textureWidth, textureHeight);
        handle2.addBox(-3.8F, 1F, -1F, 4, 12, 2);
        handle2.setRotationPoint(0, 0, 0);
        handle2.rotateAngleY = (float) (Math.PI / 2);
        wrench.addChild(handle2);

        axle = new ModelRenderer(this, 8, 0).setTextureSize(textureWidth, textureHeight);
        axle.addBox(-1F, -7F, -1F, 2, 14, 2);
        axle.setRotationPoint(0, 1, -1);
        axle.rotateAngleY = (float) (Math.PI / 4);
        wrench.addChild(axle);

        clamp1 = new ModelRenderer(this, 20, 22).setTextureSize(textureWidth, textureHeight);
        clamp1.addBox(-2F, -4F, -1F, 4, 8, 2);
        clamp1.setRotationPoint(0F, 13F, -1F);
        clamp1.rotateAngleX = (float) (Math.PI / 2);
        wrench.addChild(clamp1);

        clamp2 = new ModelRenderer(this, 20, 22).setTextureSize(textureWidth, textureHeight);
        clamp2.addBox(-2F, -3F, -1F, 4, 6, 2);
        clamp2.setRotationPoint(0, 9F, -2F);
        clamp2.rotateAngleX = (float) (-Math.PI / 2);
        clamp2.rotateAngleY = (float) Math.PI;
        wrench.addChild(clamp2);

        gearCaseTop = new ModelRenderer(this, 8, 22).setTextureSize(textureWidth, textureHeight);
        gearCaseTop.addBox(-2F, -4F, -1F, 4, 8, 2);
        gearCaseTop.setRotationPoint(0, 1, 0);
        gearCaseTop.rotateAngleX = (float) (Math.PI / 2);
        wrench.addChild(gearCaseTop);

        gearCase = new ModelRenderer(this, 8, 16).setTextureSize(textureWidth, textureHeight);
        gearCase.addBox(-2F, -1F, -2F, 4, 2, 4);
        gearCase.setRotationPoint(0, -3, 0);
        gearCase.rotateAngleY = (float) (Math.PI / 4);
        wrench.addChild(gearCase);

        cog = new ModelRenderer(this, 0, 0).setTextureSize(textureWidth, textureHeight);
        cogs = new ModelRenderer[4];
        for (int i = 0; i < cogs.length; i++) {
            cogs[i] = new ModelRenderer(this, 0, 14).setTextureSize(textureWidth, textureHeight);
            cogs[i].addBox(-1, -4, -1, 2, 8, 2);
            cogs[i].setRotationPoint(0, 0, 0);
            cogs[i].rotateAngleX = (float) (Math.PI / 2);
            cogs[i].rotateAngleY = (float) (Math.PI / 4) * i;
            cog.addChild(cogs[i]);
        }
        cog.setRotationPoint(0, -1, -1);
        wrench.addChild(cog);
        wrench.rotateAngleY = (float) (-Math.PI / 180 * 105);
    }

    public void render() {
        texture.bind();
        cog.rotateAngleY = -ScrollValueHandler.getScroll(AnimationTickHolder.getPartialTicks()) / 180F
            * (float) Math.PI;
        wrench.render(0.0625f);
    }

}
