package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel;

import net.minecraft.client.model.ModelRenderer;

import su.sergiusonesimus.recreate.AllModelTextures;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.AbstractShaftModel;

public class CogWheelModel extends AbstractShaftModel {

    public ModelRenderer hub;
    public ModelRenderer disk;
    public ModelRenderer[] cogs = new ModelRenderer[4];

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

        int cogWidth = 3;
        int cogHeight = 18;
        for (int i = 0; i < cogs.length; i++) {
            cogs[i] = new ModelRenderer(this, 0, 1).setTextureSize(textureWidth, textureHeight);
            cogs[i].addBox(-cogWidth / 2F, -cogHeight / 2F, -cogWidth / 2F, cogWidth, cogHeight, cogWidth, 0F);
            cogs[i].setRotationPoint(0F, 0F, 0F);
            cogs[i].rotateAngleX = (float) (Math.PI / 2F);
            cogs[i].rotateAngleY = (float) (Math.PI * i / 4F);
            core.addChild(cogs[i]);
        }
    }

}
