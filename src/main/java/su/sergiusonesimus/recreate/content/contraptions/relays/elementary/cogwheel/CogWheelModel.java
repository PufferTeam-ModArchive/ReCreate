package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel;

import net.minecraft.client.model.ModelRenderer;

import su.sergiusonesimus.recreate.AllModelTextures;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.AbstractShaftModel;

public class CogWheelModel extends AbstractShaftModel {

    ModelRenderer hub;
    ModelRenderer disk;
    ModelRenderer cog1;
    ModelRenderer cog2;
    ModelRenderer cog3;
    ModelRenderer cog4;

    public CogWheelModel() {
        super(AllModelTextures.COGWHEEL, 48, 32);

        hub = new ModelRenderer(this, 16, 0).setTextureSize(textureWidth, textureHeight);
        hub.addBox(-4F, -2F, -4F, 8, 4, 8, 0F);
        hub.setRotationPoint(0F, 0F, 0F);
        core.addChild(hub);

        disk = new ModelRenderer(this, 0, 17).setTextureSize(textureWidth, textureHeight);
        disk.addBox(-6F, -1F, -6F, 12, 2, 12, 0F);
        disk.setRotationPoint(0F, 0F, 0F);
        core.addChild(disk);

        int cogOffsetU = 0;
        int cogOffsetV = 1;
        int cogWidth = 3;
        int cogHeight = 18;
        float cogRPX = 0F;
        float cogRPY = 0F;
        float cogRPZ = 0F;
        float rotX = (float) (Math.PI / 2F);
        cog1 = new ModelRenderer(this, cogOffsetU, cogOffsetV).setTextureSize(textureWidth, textureHeight);
        cog1.addBox(-cogWidth / 2F, -cogHeight / 2F, -cogWidth / 2F, cogWidth, cogHeight, cogWidth, 0F);
        cog1.setRotationPoint(cogRPX, cogRPY, cogRPZ);
        cog1.rotateAngleX = rotX;
        core.addChild(cog1);

        cog2 = new ModelRenderer(this, cogOffsetU, cogOffsetV).setTextureSize(textureWidth, textureHeight);
        cog2.addBox(-cogWidth / 2F, -cogHeight / 2F, -cogWidth / 2F, cogWidth, cogHeight, cogWidth, 0F);
        cog2.setRotationPoint(cogRPX, cogRPY, cogRPZ);
        cog2.rotateAngleX = rotX;
        cog2.rotateAngleY = (float) (Math.PI / 4F);
        core.addChild(cog2);

        cog3 = new ModelRenderer(this, cogOffsetU, cogOffsetV).setTextureSize(textureWidth, textureHeight);
        cog3.addBox(-cogWidth / 2F, -cogHeight / 2F, -cogWidth / 2F, cogWidth, cogHeight, cogWidth, 0F);
        cog3.setRotationPoint(cogRPX, cogRPY, cogRPZ);
        cog3.rotateAngleX = rotX;
        cog3.rotateAngleY = (float) (Math.PI / 2F);
        core.addChild(cog3);

        cog4 = new ModelRenderer(this, cogOffsetU, cogOffsetV).setTextureSize(textureWidth, textureHeight);
        cog4.addBox(-cogWidth / 2F, -cogHeight / 2F, -cogWidth / 2F, cogWidth, cogHeight, cogWidth, 0F);
        cog4.setRotationPoint(cogRPX, cogRPY, cogRPZ);
        cog4.rotateAngleX = rotX;
        cog4.rotateAngleY = (float) (Math.PI * 3F / 4F);
        core.addChild(cog4);
    }

}
