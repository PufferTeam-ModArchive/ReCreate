package su.sergiusonesimus.recreate.foundation.gui;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.Framebuffer;
import su.sergiusonesimus.recreate.foundation.utility.Color;
import su.sergiusonesimus.recreate.foundation.utility.Couple;

public class UIRenderHelper {

	/**
	 * An FBO that has a stencil buffer for use wherever stencil are necessary. Forcing the main FBO to have a stencil
	 * buffer will cause GL error spam when using fabulous graphics.
	 */
	public static CustomFramebuffer framebuffer;

	public static void init() {
	    framebuffer = CustomFramebuffer.create();
	}

	public static void updateWindowSize() {
	    if (framebuffer != null) {
	        Minecraft mc = Minecraft.getMinecraft();
	        framebuffer.createFramebuffer(mc.displayWidth, mc.displayHeight);
	    }
	}

	public static void drawFramebuffer(float alpha) {
		framebuffer.renderWithAlpha(alpha);
	}

	public static void streak(float angle, int x, int y, int breadth, int length) {
		streak(angle, x, y, breadth, length, Theme.i(Theme.Key.STREAK));
	}
	// angle in degrees; 0° -> fading to the right
	// x and y specify the middle point of the starting edge
	// breadth is the total width of the streak

	public static void streak(float angle, int x, int y, int breadth, int length, int color) {
	    int a1 = 0xa0 << 24;
	    int a2 = 0x80 << 24;
	    int a3 = 0x10 << 24;
	    int a4 = 0x00 << 24;

	    color &= 0x00FFFFFF;
	    int c1 = a1 | color;
	    int c2 = a2 | color;
	    int c3 = a3 | color;
	    int c4 = a4 | color;

	    GL11.glPushMatrix();
	    GL11.glTranslatef(x, y, 0);
	    GL11.glRotatef(angle - 90, 0, 0, 1);

	    streak(breadth / 2, length, c1, c2, c3, c4);

	    GL11.glPopMatrix();
	}

	public static void streak(float angle, int x, int y, int breadth, int length, Color c) {
	    int c1 = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(c.getAlpha() * 0.625f)).getRGB();
	    int c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(c.getAlpha() * 0.5f)).getRGB();
	    int c3 = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(c.getAlpha() * 0.0625f)).getRGB();
	    int c4 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0).getRGB();

	    GL11.glPushMatrix();
	    GL11.glTranslatef(x, y, 0);
	    GL11.glRotatef(angle - 90, 0, 0, 1);

	    streak(breadth / 2, length, c1, c2, c3, c4);

	    GL11.glPopMatrix();
	}

	private static void streak(int width, int height, int c1, int c2, int c3, int c4) {
	    double split1 = 0.5;
	    double split2 = 0.75;
	    
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    
	    Tessellator tessellator = Tessellator.instance;
	    
	    drawGradientRect(tessellator, -width, 0, width, (int)(split1 * height), c1, c2);
	    drawGradientRect(tessellator, -width, (int)(split1 * height), width, (int)(split2 * height), c2, c3);
	    drawGradientRect(tessellator, -width, (int)(split2 * height), width, height, c3, c4);
	    
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glDisable(GL11.GL_BLEND);
	}

	private static void drawGradientRect(Tessellator tessellator, int left, int top, int right, int bottom, int color1, int color2) {
	    tessellator.startDrawingQuads();
	    
	    float a1 = (float)(color1 >> 24 & 255) / 255.0F;
	    float r1 = (float)(color1 >> 16 & 255) / 255.0F;
	    float g1 = (float)(color1 >> 8 & 255) / 255.0F;
	    float b1 = (float)(color1 & 255) / 255.0F;
	    
	    float a2 = (float)(color2 >> 24 & 255) / 255.0F;
	    float r2 = (float)(color2 >> 16 & 255) / 255.0F;
	    float g2 = (float)(color2 >> 8 & 255) / 255.0F;
	    float b2 = (float)(color2 & 255) / 255.0F;
	    
	    tessellator.setColorRGBA_F(r1, g1, b1, a1);
	    tessellator.addVertex(left, bottom, 0);
	    
	    tessellator.setColorRGBA_F(r1, g1, b1, a1);
	    tessellator.addVertex(right, bottom, 0);
	    
	    tessellator.setColorRGBA_F(r2, g2, b2, a2);
	    tessellator.addVertex(right, top, 0);
	    
	    tessellator.setColorRGBA_F(r2, g2, b2, a2);
	    tessellator.addVertex(left, top, 0);
	    
	    tessellator.draw();
	}

	/**
	 * @see #angledGradient(MatrixStack, float, int, int, int, int, int, Color, Color)
	 */
	public static void angledGradient(@Nonnull float angle, int x, int y, int breadth, int length, Couple<Color> c) {
		angledGradient(angle, x, y, 0, breadth, length, c);
	}

	/**
	 * @see #angledGradient(MatrixStack, float, int, int, int, int, int, Color, Color)
	 */
	public static void angledGradient(@Nonnull float angle, int x, int y, int z, int breadth, int length, Couple<Color> c) {
		angledGradient(angle, x, y, z, breadth, length, c.getFirst(), c.getSecond());
	}

	/**
	 * @see #angledGradient(MatrixStack, float, int, int, int, int, int, Color, Color)
	 */
	public static void angledGradient(@Nonnull float angle, int x, int y, int breadth, int length, Color color1, Color color2) {
		angledGradient(angle, x, y, 0, breadth, length, color1, color2);
	}

	/**
	 * x and y specify the middle point of the starting edge
	 *
	 * @param angle   the angle of the gradient in degrees; 0° means from left to right
	 * @param color1  the color at the starting edge
	 * @param color2  the color at the ending edge
	 * @param breadth the total width of the gradient
	 */
	public static void angledGradient(float angle, int x, int y, int z, int breadth, int length, Color color1, Color color2) {
	    GL11.glPushMatrix();
	    
	    GL11.glTranslatef(x, y, z);
	    GL11.glRotatef(angle - 90, 0, 0, 1);
	    
	    int w = breadth / 2;
	    
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    
	    Tessellator tessellator = Tessellator.instance;
	    tessellator.startDrawingQuads();
	    
	    tessellator.setColorRGBA_I(color1.getRGB(), color1.getAlpha());
	    tessellator.addVertex(-w, 0, z);
	    
	    tessellator.setColorRGBA_I(color1.getRGB(), color1.getAlpha());
	    tessellator.addVertex(w, 0, z);
	    
	    tessellator.setColorRGBA_I(color2.getRGB(), color2.getAlpha());
	    tessellator.addVertex(w, length, z);
	    
	    tessellator.setColorRGBA_I(color2.getRGB(), color2.getAlpha());
	    tessellator.addVertex(-w, length, z);
	    
	    tessellator.draw();
	    
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glDisable(GL11.GL_BLEND);
	    
	    GL11.glPopMatrix();
	}

	public static void breadcrumbArrow(int x, int y, int z, int width, int height, int indent, Couple<Color> colors) {breadcrumbArrow(x, y, z, width, height, indent, colors.getFirst(), colors.getSecond());}

	// draws a wide chevron-style breadcrumb arrow pointing left
	public static void breadcrumbArrow(int x, int y, int z, int width, int height, int indent, Color startColor, Color endColor) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x - indent, y, z);

		breadcrumbArrow(width, height, indent, startColor, endColor);

		GL11.glPopMatrix();
	}

	private static void breadcrumbArrow(int width, int height, int indent, Color c1, Color c2) {
	    /*
	     * 0,0       x1,y1 ********************* x4,y4 ***** x7,y7
	     *       ****                                     ****
	     *   ****                                     ****
	     * x0,y0     x2,y2                       x5,y5
	     *   ****                                     ****
	     *       ****                                     ****
	     *           x3,y3 ********************* x6,y6 ***** x8,y8
	     *           
	     */
	    float x0 = 0, y0 = height / 2f;
	    float x1 = indent, y1 = 0;
	    float x2 = indent, y2 = height / 2f;
	    float x3 = indent, y3 = height;
	    float x4 = width, y4 = 0;
	    float x5 = width, y5 = height / 2f;
	    float x6 = width, y6 = height;
	    float x7 = indent + width, y7 = 0;
	    float x8 = indent + width, y8 = height;

	    indent = Math.abs(indent);
	    width = Math.abs(width);
	    
	    Color fc1 = Color.mixColors(c1, c2, 0);
	    Color fc2 = Color.mixColors(c1, c2, (indent) / (width + 2f * indent));
	    Color fc3 = Color.mixColors(c1, c2, (indent + width) / (width + 2f * indent));
	    Color fc4 = Color.mixColors(c1, c2, 1);

	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_CULL_FACE);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	    Tessellator tessellator = Tessellator.instance;
	    tessellator.startDrawing(GL11.GL_TRIANGLES);
	    
	    tessellator.setColorRGBA(fc1.getRed(), fc1.getGreen(), fc1.getBlue(), fc1.getAlpha());
	    tessellator.addVertex(x0, y0, 0);
	    tessellator.setColorRGBA(fc2.getRed(), fc2.getGreen(), fc2.getBlue(), fc2.getAlpha());
	    tessellator.addVertex(x1, y1, 0);
	    tessellator.addVertex(x2, y2, 0);

	    tessellator.setColorRGBA(fc1.getRed(), fc1.getGreen(), fc1.getBlue(), fc1.getAlpha());
	    tessellator.addVertex(x0, y0, 0);
	    tessellator.setColorRGBA(fc2.getRed(), fc2.getGreen(), fc2.getBlue(), fc2.getAlpha());
	    tessellator.addVertex(x2, y2, 0);
	    tessellator.addVertex(x3, y3, 0);

	    tessellator.setColorRGBA(fc2.getRed(), fc2.getGreen(), fc2.getBlue(), fc2.getAlpha());
	    tessellator.addVertex(x3, y3, 0);
	    tessellator.addVertex(x1, y1, 0);
	    tessellator.setColorRGBA(fc3.getRed(), fc3.getGreen(), fc3.getBlue(), fc3.getAlpha());
	    tessellator.addVertex(x4, y4, 0);

	    tessellator.setColorRGBA(fc2.getRed(), fc2.getGreen(), fc2.getBlue(), fc2.getAlpha());
	    tessellator.addVertex(x3, y3, 0);
	    tessellator.setColorRGBA(fc3.getRed(), fc3.getGreen(), fc3.getBlue(), fc3.getAlpha());
	    tessellator.addVertex(x4, y4, 0);
	    tessellator.addVertex(x6, y6, 0);

	    tessellator.setColorRGBA(fc3.getRed(), fc3.getGreen(), fc3.getBlue(), fc3.getAlpha());
	    tessellator.addVertex(x5, y5, 0);
	    tessellator.addVertex(x4, y4, 0);
	    tessellator.setColorRGBA(fc4.getRed(), fc4.getGreen(), fc4.getBlue(), fc4.getAlpha());
	    tessellator.addVertex(x7, y7, 0);

	    tessellator.setColorRGBA(fc3.getRed(), fc3.getGreen(), fc3.getBlue(), fc3.getAlpha());
	    tessellator.addVertex(x6, y6, 0);
	    tessellator.addVertex(x5, y5, 0);
	    tessellator.setColorRGBA(fc4.getRed(), fc4.getGreen(), fc4.getBlue(), fc4.getAlpha());
	    tessellator.addVertex(x8, y8, 0);

	    tessellator.draw();

	    GL11.glEnable(GL11.GL_CULL_FACE);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawColoredTexture(Color c, int x, int y, int tex_left, int tex_top, int width, int height) {
		drawColoredTexture(c, x, y, 0, (float) tex_left, (float) tex_top, width, height, 256, 256);
	}

	public static void drawColoredTexture(Color c, int x, int y, int z, float tex_left, float tex_top, int width, int height, int sheet_width, int sheet_height) {
		drawColoredTexture(c, x, x + width, y, y + height, z, width, height, tex_left, tex_top, sheet_width, sheet_height);
	}

	private static void drawColoredTexture(Color c, int left, int right, int top, int bot, int z, int tex_width, int tex_height, float tex_left, float tex_top, int sheet_width, int sheet_height) {
		drawTexturedQuad(c, left, right, top, bot, z, (tex_left + 0.0F) / (float) sheet_width, (tex_left + (float) tex_width) / (float) sheet_width, (tex_top + 0.0F) / (float) sheet_height, (tex_top + (float) tex_height) / (float) sheet_height);
	}

	private static void drawTexturedQuad(Color c, int left, int right, int top, int bot, int z, float u1, float u2, float v1, float v2) {
	    Tessellator tessellator = Tessellator.instance;
	    
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    
	    tessellator.startDrawingQuads();
	    
	    tessellator.setColorRGBA(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	    tessellator.setTextureUV(u1, v2);
	    tessellator.addVertex(left, bot, z);
	    
	    tessellator.setTextureUV(u2, v2);
	    tessellator.addVertex(right, bot, z);
	    
	    tessellator.setTextureUV(u2, v1);
	    tessellator.addVertex(right, top, z);
	    
	    tessellator.setTextureUV(u1, v1);
	    tessellator.addVertex(left, top, z);
	    
	    tessellator.draw();
	    GL11.glDisable(GL11.GL_BLEND);
	}

	public static void flipForGuiRender() {
	    GL11.glScalef(1, -1, 1);
	}

	public static class CustomFramebuffer extends Framebuffer {

	    public CustomFramebuffer(int width, int height, boolean useDepth) {
	        super(width, height, useDepth);
	        this.setFramebufferColor(0, 0, 0, 0);
	    }

	    public static CustomFramebuffer create() {
	        Minecraft mc = Minecraft.getMinecraft();
	        CustomFramebuffer fbo = new CustomFramebuffer(mc.displayWidth, mc.displayHeight, true);
	        fbo.setFramebufferFilter(GL11.GL_LINEAR);
	        return fbo;
	    }

	    public void renderWithAlpha(float alpha) {
	        Minecraft mc = Minecraft.getMinecraft();
	        int screenWidth = mc.displayWidth;
	        int screenHeight = mc.displayHeight;
	        
	        float u = (float)this.framebufferWidth / (float)this.framebufferTextureWidth;
	        float v = (float)this.framebufferHeight / (float)this.framebufferTextureHeight;

	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glDisable(GL11.GL_DEPTH_TEST);
	        GL11.glDepthMask(false);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);

	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.framebufferTexture);

	        Tessellator tessellator = Tessellator.instance;
	        tessellator.startDrawingQuads();
	        tessellator.addVertexWithUV(0, screenHeight, 0, 0, 0);
	        tessellator.addVertexWithUV(screenWidth, screenHeight, 0, u, 0);
	        tessellator.addVertexWithUV(screenWidth, 0, 0, u, v);
	        tessellator.addVertexWithUV(0, 0, 0, 0, v);
	        tessellator.draw();

	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glDepthMask(true);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	    }

	    @Override
	    public void createFramebuffer(int width, int height) {
	        super.createFramebuffer(width, height);
	        if (this.useDepth && net.minecraftforge.client.MinecraftForgeClient.getStencilBits() > 0) {
	            OpenGlHelper.func_153190_b(
	                OpenGlHelper.field_153198_e,
	                org.lwjgl.opengl.EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT,
	                OpenGlHelper.field_153199_f,
	                this.depthBuffer
	            );
	        }
	    }
	}

}
