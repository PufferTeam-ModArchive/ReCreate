package su.sergiusonesimus.recreate.foundation.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class ShadowRenderHelper {

    private static final ResourceLocation SHADOW_LAYER = new ResourceLocation("textures/misc/shadow.png");

    public static void renderShadow(double x, double y, double z, float opacity, float radius) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderManager.instance.renderEngine.bindTexture(SHADOW_LAYER);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        opacity /= 2.0F;
        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, opacity);

        shadowVertex(tessellator, x - radius, y, z - radius, 0, 0);
        shadowVertex(tessellator, x - radius, y, z + radius, 0, 1);
        shadowVertex(tessellator, x + radius, y, z + radius, 1, 1);
        shadowVertex(tessellator, x + radius, y, z - radius, 1, 0);

        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void renderShadow(World world, double x, double y, double z, float opacity, float radius) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderManager.instance.renderEngine.bindTexture(SHADOW_LAYER);

        int i = MathHelper.floor_double(x - radius);
        int j = MathHelper.floor_double(x + radius);
        int k = MathHelper.floor_double(y - radius);
        int l = MathHelper.floor_double(y);
        int i1 = MathHelper.floor_double(z - radius);
        int j1 = MathHelper.floor_double(z + radius);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        for (int bX = i; bX <= j; ++bX) {
            for (int bY = k; bY <= l; ++bY) {
                for (int bZ = i1; bZ <= j1; ++bZ) {
                    renderBlockShadow(world, bX, bY, bZ, x, y, z, radius, opacity);
                }
            }
        }

        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
    }

    private static void renderBlockShadow(World world, int bx, int by, int bz, double x, double y, double z,
        float radius, float opacity) {
        Block block = world.getBlock(bx, by - 1, bz);

        if (block.getRenderType() != -1 && world.getBlockLightValue(bx, by, bz) > 3) {
            if (block.renderAsNormalBlock()) {
                AxisAlignedBB aabb = block.getSelectedBoundingBoxFromPool(world, bx, by - 1, bz);

                if (aabb != null) {
                    float f = (float) (((double) opacity - (y - (double) by) / 2.0D) * 0.5D
                        * (double) world.getLightBrightness(bx, by, bz));

                    if (f >= 0.0F) {
                        if (f > 1.0F) f = 1.0F;

                        double minX = aabb.minX - x;
                        double maxX = aabb.maxX - x;
                        double minY = aabb.minY - y + 0.015625D;
                        double minZ = aabb.minZ - z;
                        double maxZ = aabb.maxZ - z;

                        float uMin = (float) (-minX / 2.0F / radius + 0.5F);
                        float uMax = (float) (-maxX / 2.0F / radius + 0.5F);
                        float vMin = (float) (-minZ / 2.0F / radius + 0.5F);
                        float vMax = (float) (-maxZ / 2.0F / radius + 0.5F);

                        Tessellator tessellator = Tessellator.instance;
                        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, f);
                        tessellator.addVertexWithUV(x + minX, y + minY, z + minZ, uMin, vMin);
                        tessellator.addVertexWithUV(x + minX, y + minY, z + maxZ, uMin, vMax);
                        tessellator.addVertexWithUV(x + maxX, y + minY, z + maxZ, uMax, vMax);
                        tessellator.addVertexWithUV(x + maxX, y + minY, z + minZ, uMax, vMin);
                    }
                }
            }
        }
    }

    private static void shadowVertex(Tessellator tessellator, double x, double y, double z, double u, double v) {
        tessellator.addVertexWithUV(x, y + 0.015625D, z, u, v);
    }
}
