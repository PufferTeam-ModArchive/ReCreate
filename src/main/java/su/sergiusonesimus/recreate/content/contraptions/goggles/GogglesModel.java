package su.sergiusonesimus.recreate.content.contraptions.goggles;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

public class GogglesModel extends ModelBiped {

    public ModelRenderer goggles;
    public ModelRenderer frame1;
    public ModelRenderer frame2;
    public ModelRenderer frame3;
    public ModelRenderer frame4;
    public ModelRenderer frame5;
    public ModelRenderer frame6;
    public ModelRenderer frame7;
    public ModelRenderer glass;

    public GogglesModel() {
        super(0.1F);
        final int textureWidth = 64;
        final int textureHeight = 32;

        goggles = new ModelRenderer(this, 0, 0).setTextureSize(textureWidth, textureHeight);

        frame1 = new ModelRenderer(this, 24, 0).setTextureSize(textureWidth, textureHeight);
        frame1.addBox(-4F, 0, 0, 1, 1, 1);
        goggles.addChild(frame1);

        frame2 = new ModelRenderer(this, 28, 0).setTextureSize(textureWidth, textureHeight);
        frame2.addBox(3F, 0, 0, 1, 1, 1);
        goggles.addChild(frame2);

        frame3 = new ModelRenderer(this, 0, 0).setTextureSize(textureWidth, textureHeight);
        frame3.addBox(-3F, -1F, 0, 2, 1, 1);
        goggles.addChild(frame3);

        frame4 = new ModelRenderer(this, 0, 2).setTextureSize(textureWidth, textureHeight);
        frame4.addBox(1F, -1F, 0, 2, 1, 1);
        goggles.addChild(frame4);

        frame5 = new ModelRenderer(this, 0, 4).setTextureSize(textureWidth, textureHeight);
        frame5.addBox(-1F, 0, 0, 2, 1, 1);
        goggles.addChild(frame5);

        frame6 = new ModelRenderer(this, 0, 6).setTextureSize(textureWidth, textureHeight);
        frame6.addBox(-3F, 1F, 0, 2, 1, 1);
        goggles.addChild(frame6);

        frame7 = new ModelRenderer(this, 0, 6).setTextureSize(textureWidth, textureHeight);
        frame7.addBox(1F, 1F, 0, 2, 1, 1);
        goggles.addChild(frame7);

        glass = new ModelRenderer(this, 24, 2).setTextureSize(textureWidth, textureHeight);
        glass.addBox(-3F, 0, 0.5F, 6, 1, 0);
        goggles.addChild(glass);

        goggles.setRotationPoint(0F, 0F, 0F);
        goggles.offsetZ = -5F / 16F;
        goggles.offsetY = -7.5F / 16F;

        bipedHead.addChild(goggles);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (par1Entity != null) {
            this.isSneak = par1Entity.isSneaking();
        }

        if (par1Entity != null && par1Entity instanceof EntityLivingBase) {
            this.heldItemRight = ((EntityLivingBase) par1Entity).getHeldItem() != null ? 1 : 0;
        }

        super.render(par1Entity, par2, par3, par4, par5, par6, par7);

        GL11.glPopAttrib();
    }
}
