package su.sergiusonesimus.recreate.foundation.utility.ghost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;

import org.lwjgl.opengl.GL11;

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
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1, 1, 1, params.alphaSupplier.get());
            GL11.glDisable(GL11.GL_LIGHTING);

            GL11.glDepthMask(false);

            RenderBlocks renderer = Minecraft.getMinecraft().renderGlobal.renderBlocksRg;
            renderer.renderBlockByRenderType(params.block, params.posX, params.posY, params.posZ);

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
        }

    }

}
