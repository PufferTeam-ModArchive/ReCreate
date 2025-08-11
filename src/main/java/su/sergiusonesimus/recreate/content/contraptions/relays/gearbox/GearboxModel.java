package su.sergiusonesimus.recreate.content.contraptions.relays.gearbox;

import net.minecraft.client.model.ModelRenderer;
import su.sergiusonesimus.recreate.AllModelTextures;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.AbstractShaftModel;
import su.sergiusonesimus.recreate.content.contraptions.relays.encased.EncasedShaftModel;

public class GearboxModel extends AbstractShaftModel {
    ModelRenderer[] sides = new ModelRenderer[4];
    ModelRenderer bearing;
    ModelRenderer bearing2;
    ModelRenderer bearing3;
    ModelRenderer bearing4;

    public GearboxModel(AllModelTextures texture) {
        super(texture, 64, 64);

        bearing = new ModelRenderer(this, 0, 18).setTextureSize(textureWidth, textureHeight);
        bearing.addBox(-6.0F, -15.0F + 8F, -6.0F, 12, 1, 12, 0.0F);
        bearing.setRotationPoint(0, 0, 0);
        core.addChild(bearing);

        bearing2 = new ModelRenderer(this, 0, 18).setTextureSize(textureWidth, textureHeight);
        bearing2.addBox(-6.0F, -2.0F + 8F, -6.0F, 12, 1, 12);
        bearing2.setRotationPoint(0, 0, 0);
        core.addChild(bearing2);

        bearing3 = new ModelRenderer(this, 11, 30).setTextureSize(textureWidth, textureHeight);
        bearing3.addBox(-6.0F, -14.0F + 8F, -7.0F, 12, 12, 1);
        bearing3.setRotationPoint(0, 0, 0);
        core.addChild(bearing3);

        bearing4 = new ModelRenderer(this, 11, 30).setTextureSize(textureWidth, textureHeight);
        bearing4.addBox(-6.0F, -14.0F + 8F, 6.0F, 12, 12, 1);
        bearing4.setRotationPoint(0, 0, 0);
        core.addChild(bearing4);

    }
}
