package su.sergiusonesimus.recreate.content.contraptions.components.waterwheel;

import net.minecraft.client.model.ModelRenderer;

import su.sergiusonesimus.recreate.AllModelTextures;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.AbstractShaftModel;

public class WaterWheelModel extends AbstractShaftModel {

    public ModelRenderer[] outerSegments = new ModelRenderer[16];
    public ModelRenderer[] innerSegments = new ModelRenderer[8];
    public ModelRenderer[] connectors = new ModelRenderer[4];
    public ModelRenderer[] rims = new ModelRenderer[2];
    public ModelRenderer axisCoat;

    public WaterWheelModel() {
        super(AllModelTextures.WATER_WHEEL, 80, 48);

        int sizeX = 18;
        int sizeY = 8;
        int sizeZ = 1;
        float originX = -sizeX / 2F;
        float originY = -16F;
        float originZ = -6F;
        for (int i = 0; i < outerSegments.length; i++) {
            outerSegments[i] = new ModelRenderer(this, 0, 0).setTextureSize(textureWidth, textureHeight);
            outerSegments[i].addBox(originX, originY, originZ, sizeX, sizeY, sizeZ, 0F);
            outerSegments[i].setRotationPoint(0F, 0F, 0F);
            outerSegments[i].rotateAngleZ = (float) (Math.PI / 2F);
            outerSegments[i].rotateAngleX = (float) (2 * Math.PI * i) / outerSegments.length;
            core.addChild(outerSegments[i]);
        }

        sizeX = 17;
        sizeY = 8;
        sizeZ = 2;
        originX = -sizeX / 2F;
        originY = -sizeY / 2F;
        originZ = -10F;
        for (int i = 0; i < innerSegments.length; i++) {
            innerSegments[i] = new ModelRenderer(this, 0, 9).setTextureSize(textureWidth, textureHeight);
            innerSegments[i].addBox(originX, originY, originZ, sizeX, sizeY, sizeZ, 0F);
            innerSegments[i].setRotationPoint(0F, 0F, 0F);
            innerSegments[i].rotateAngleZ = (float) (Math.PI / 2F);
            innerSegments[i].rotateAngleX = (float) (2 * Math.PI * i) / innerSegments.length;
            core.addChild(innerSegments[i]);
        }

        sizeX = 18;
        sizeY = 12;
        sizeZ = 3;
        originX = -sizeX / 2F;
        originY = -sizeY / 2F;
        originZ = -sizeZ / 2F;
        for (int i = 0; i < connectors.length; i++) {
            connectors[i] = new ModelRenderer(this, 38, 0).setTextureSize(textureWidth, textureHeight);
            connectors[i].addBox(originX, originY, originZ, sizeX, sizeY, sizeZ, 0F);
            connectors[i].setRotationPoint(0F, 0F, 0F);
            connectors[i].rotateAngleY = (float) (Math.PI * (i + 0.5F)) / connectors.length;
            core.addChild(connectors[i]);
        }

        sizeX = 26;
        sizeY = 26;
        sizeZ = 0;
        originX = -sizeX / 2F;
        originY = -sizeY / 2F;
        originZ = 8.67F;
        for (int i = 0; i < rims.length; i++) {
            rims[i] = new ModelRenderer(this, 0, 19).setTextureSize(textureWidth, textureHeight);
            rims[i].addBox(originX, originY, originZ, sizeX, sizeY, sizeZ, 0F);
            rims[i].setRotationPoint(0F, 0F, 0F);
            rims[i].rotateAngleX = (float) (Math.PI * (i + 0.5F));
            core.addChild(rims[i]);
        }

        sizeX = 6;
        sizeY = 14;
        sizeZ = 6;
        originX = -sizeX / 2F;
        originY = -sizeY / 2F;
        originZ = -sizeZ / 2F;
        axisCoat = new ModelRenderer(this, 52, 15).setTextureSize(textureWidth, textureHeight);
        axisCoat.addBox(originX, originY, originZ, sizeX, sizeY, sizeZ, 0F);
        axisCoat.setRotationPoint(0F, 0F, 0F);
        core.addChild(axisCoat);

    }
}
