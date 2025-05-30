package su.sergiusonesimus.recreate.content.contraptions.wrench;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

public class WrenchRenderItem implements IItemRenderer {

    private WrenchModel model = new WrenchModel();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return helper != ItemRendererHelper.BLOCK_3D;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        switch (type) {
            case ENTITY:
                GL11.glScaled(0.5D, 0.5D, 0.5D);
                GL11.glTranslated(0, 0.4D, 0);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glScaled(1.3D, 1.3D, 1.3D);
                GL11.glTranslated(0.5D, 0.75D, 0.5D);
                GL11.glRotated(75, 0, 1D, 0);
                break;
            case EQUIPPED:
                GL11.glTranslated(0.5D, 1D, 0.5D);
                GL11.glRotated(90, 0, 1D, 0);
                break;
            case INVENTORY:
                GL11.glRotated(-90, 0, 1D, 0);
                GL11.glRotated(30, 0, 0, 1D);
                GL11.glRotated(-15, 1D, 0, 0);
                GL11.glScaled(0.9D, 0.9D, 0.9D);
                break;
            default:
                break;
        }

        model.render();

        GL11.glPopMatrix();
    }

}
