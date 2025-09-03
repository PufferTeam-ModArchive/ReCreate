package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel;

import net.minecraft.client.model.ModelRenderer;

import su.sergiusonesimus.recreate.AllModelTextures;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.AbstractShaftModel;

public class LargeCogWheelModel extends AbstractShaftModel {

    public ModelRenderer hub1;
    public ModelRenderer hub2;
    public ModelRenderer[] hubParts = new ModelRenderer[4];
    public ModelRenderer disk;
    public ModelRenderer[] cogs = new ModelRenderer[8];

    public LargeCogWheelModel() {
        super(AllModelTextures.LARGE_COGWHEEL, 80, 48);

        int hubOffsetU = 24;
        int hubOffsetV = 0;
        int hubDiameter = 14;
        int hubThickness = 2;
        float rotationPointX = 0F;
        float rotationPointY = 0F;
        float rotationPointZ = 0F;
        hub1 = new ModelRenderer(this, hubOffsetU, hubOffsetV).setTextureSize(textureWidth, textureHeight);
        hub1.addBox(-hubDiameter / 2F, 0.5F, -hubDiameter / 2F, hubDiameter, hubThickness, hubDiameter, 0F);
        hub1.setRotationPoint(rotationPointX, rotationPointY, rotationPointZ);
        core.addChild(hub1);

        hub2 = new ModelRenderer(this, hubOffsetU, hubOffsetV).setTextureSize(textureWidth, textureHeight);
        hub2.addBox(-hubDiameter / 2F, -2.5F, -hubDiameter / 2F, hubDiameter, hubThickness, hubDiameter, 0F);
        hub2.setRotationPoint(rotationPointX, rotationPointY, rotationPointZ);
        core.addChild(hub2);

        for (int i = 0; i < hubParts.length; i++) {
            hubParts[i] = new ModelRenderer(this, 66, 16).setTextureSize(textureWidth, textureHeight);
            hubParts[i].addBox(-2.5F, -7F, -9F, 5, 14, 2, 0F);
            hubParts[i].setRotationPoint(0F, 0F, 0F);
            hubParts[i].rotateAngleX = (float) (Math.PI * i / 2F);
            hubParts[i].rotateAngleZ = (float) (Math.PI / 2F);
            core.addChild(hubParts[i]);
        }

        disk = new ModelRenderer(this, 0, 25).setTextureSize(textureWidth, textureHeight);
        disk.addBox(-10F, -1F, -10F, 20, 2, 20, 0F);
        disk.setRotationPoint(0F, 0F, 0F);
        core.addChild(disk);

        int cogWidth = 3;
        int cogHeight = 30;
        for (int i = 0; i < cogs.length; i++) {
            cogs[i] = new ModelRenderer(this, 0, 0).setTextureSize(textureWidth, textureHeight);
            cogs[i].addBox(-cogWidth / 2F, -cogHeight / 2F, -cogWidth / 2F, cogWidth, cogHeight, cogWidth, 0F);
            cogs[i].setRotationPoint(0F, 0F, 0F);
            cogs[i].rotateAngleX = (float) (Math.PI / 2F);
            cogs[i].rotateAngleY = (float) (Math.PI * i / 8F);
            core.addChild(cogs[i]);
        }
    }

}
