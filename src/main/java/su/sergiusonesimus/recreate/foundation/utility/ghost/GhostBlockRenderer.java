package su.sergiusonesimus.recreate.foundation.utility.ghost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import su.sergiusonesimus.recreate.zmixin.interfaces.IMixinRenderBlocks;

public abstract class GhostBlockRenderer {

    private static final GhostBlockRenderer transparent = new TransparentGhostBlockRenderer();

    public static GhostBlockRenderer transparent() {
        return transparent;
    }

    private static final GhostBlockRenderer standard = new DefaultGhostBlockRenderer();

    public static GhostBlockRenderer standard() {
        return standard;
    }

    public abstract void render(GhostBlockParams params);

    private static class DefaultGhostBlockRenderer extends GhostBlockRenderer {

        public void render(GhostBlockParams params) {
            GL11.glDisable(GL11.GL_LIGHTING);

            RenderBlocks renderer = Minecraft.getMinecraft().renderGlobal.renderBlocksRg;
            renderer.renderBlockByRenderType(params.block, params.posX, params.posY, params.posZ);

            GL11.glEnable(GL11.GL_LIGHTING);
        }

    }

    private static class TransparentGhostBlockRenderer extends GhostBlockRenderer {

        public void render(GhostBlockParams params) {
            float alpha = params.alphaSupplier.get();
            GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
            GL11.glPushMatrix();

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            GL11.glColorMask(true, true, true, false);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();

            RenderBlocks renderer = Minecraft.getMinecraft().renderGlobal.renderBlocksRg;
            ((IMixinRenderBlocks) renderer).setOverrideAlpha(alpha);

            renderer.renderBlockByRenderType(params.block, params.posX, params.posY, params.posZ);

            ((IMixinRenderBlocks) renderer).clearOverrideAlpha();

            tessellator.draw();

            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glColorMask(true, true, true, true);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }

    }

}
