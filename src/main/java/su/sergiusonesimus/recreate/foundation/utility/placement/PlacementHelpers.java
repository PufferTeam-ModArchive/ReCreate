package su.sergiusonesimus.recreate.foundation.utility.placement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.foundation.config.CClient;
import su.sergiusonesimus.recreate.foundation.gui.AllGuiTextures;
import su.sergiusonesimus.recreate.foundation.utility.AngleHelper;
import su.sergiusonesimus.recreate.foundation.utility.Pair;
import su.sergiusonesimus.recreate.foundation.utility.animation.InterpolatedChasingAngle;
import su.sergiusonesimus.recreate.foundation.utility.animation.InterpolatedChasingValue;
import su.sergiusonesimus.recreate.util.VecHelper;

@SuppressWarnings("deprecation")
public class PlacementHelpers {

	private static final List<IPlacementHelper> helpers = new ArrayList<>();
	private static int animationTick = 0;
	private static final InterpolatedChasingValue angle = new InterpolatedChasingAngle().withSpeed(0.25f);
	private static Integer targetX = null;
	private static Integer targetY = null;
	private static Integer targetZ = null;
	private static Integer lastTargetX = null;
	private static Integer lastTargetY = null;
	private static Integer lastTargetZ = null;

	public static int register(IPlacementHelper helper) {
		helpers.add(helper);
		return helpers.size() - 1;
	}

	public static IPlacementHelper get(int id) {
		if (id < 0 || id >= helpers.size())
			throw new ArrayIndexOutOfBoundsException("id " + id + " for placement helper not known");

		return helpers.get(id);
	}

	@SideOnly(Side.CLIENT)
	public static void tick() {
		setTarget(null, null, null);
		checkHelpers();

		if (targetX == null || targetY == null || targetZ == null) {
			if (animationTick > 0)
				animationTick = Math.max(animationTick - 2, 0);

			return;
		}

		if (animationTick < 10)
			animationTick++;

	}

	@SideOnly(Side.CLIENT)
	private static void checkHelpers() {
		Minecraft mc = Minecraft.getMinecraft();
		WorldClient world = mc.theWorld;

		if (world == null)
			return;

		MovingObjectPosition hitResult =  mc.objectMouseOver;
		if (hitResult == null || hitResult.typeOfHit != MovingObjectType.BLOCK)
			return;

		if (mc.thePlayer == null)
			return;

		if (mc.thePlayer.isSneaking())//for now, disable all helpers when sneaking TODO add helpers that respect sneaking but still show position
			return;

		ItemStack heldItem = mc.thePlayer.getHeldItem();
		List<IPlacementHelper> filteredForHeldItem = helpers.stream().filter(helper -> helper.matchesItem(heldItem)).collect(Collectors.toList());
		if (filteredForHeldItem.isEmpty())
			return;

		int posX = hitResult.blockX;
		int posY = hitResult.blockY;
		int posZ = hitResult.blockZ;
		Block block = world.getBlock(posX, posY, posZ);
		int meta = world.getBlockMetadata(posX, posY, posZ);

		List<IPlacementHelper> filteredForState = filteredForHeldItem.stream().filter(helper -> helper.matchesBlock(block, meta)).collect(Collectors.toList());
		if (filteredForState.isEmpty())
			return;

		for (IPlacementHelper h : filteredForState) {
			PlacementOffset offset = h.getOffset(mc.thePlayer, world, block, meta, posX, posY, posZ, hitResult, heldItem);

			if (offset.isSuccessful()) {
				h.renderAt(posX, posY, posZ, block, meta, hitResult, offset);
				ChunkCoordinates offsetCK = offset.getPos();
				setTarget(offsetCK.posX, offsetCK.posY, offsetCK.posZ);
				break;
			}

		}
	}

	static void setTarget(Integer targetX, Integer targetY, Integer targetZ) {
		PlacementHelpers.targetX = targetX;
		PlacementHelpers.targetY = targetY;
		PlacementHelpers.targetZ = targetZ;

		if (targetX == null || targetY == null || targetZ == null)
			return;

		if (lastTargetX == null || lastTargetY == null || lastTargetZ == null) {
			lastTargetX = targetX;
			lastTargetY = targetY;
			lastTargetZ = targetZ;
			return;
		}

		if (lastTargetX != targetX || lastTargetY != targetY || lastTargetZ != targetZ) {
			lastTargetX = targetX;
			lastTargetY = targetY;
			lastTargetZ = targetZ;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
	    if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS)
	        return;

	    Minecraft mc = Minecraft.getMinecraft();
	    EntityPlayer player = mc.thePlayer;

	    if (player != null && animationTick > 0) {
	        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
	        
	        float screenY = res.getScaledHeight() / 2f;
	        float screenX = res.getScaledWidth() / 2f;
	        float progress = getCurrentAlpha();

	        drawDirectionIndicator(screenX, screenY, progress, event.partialTicks);
	    }
	}

	public static float getCurrentAlpha() {
		return Math.min(animationTick / 10f/* + event.getPartialTicks()*/, 1f);
	}

	@SideOnly(Side.CLIENT)
	private static void drawDirectionIndicator(float centerX, float centerY, float progress, float partialTicks) {
	    float r = .8f;
	    float g = .8f;
	    float b = .8f;
	    float a = progress * progress;

	    Vec3 projTarget = VecHelper.projectToPlayerView(VecHelper.getCenterOf(lastTargetX, lastTargetY, lastTargetZ),
    		partialTicks);

	    Vec3 target = Vec3.createVectorHelper(projTarget.xCoord, projTarget.yCoord, 0);
	    if (projTarget.zCoord > 0) {
	        target.xCoord = -target.xCoord;
	        target.yCoord = -target.yCoord;
	    }

	    Vec3 norm = target.normalize();
	    Vec3 ref = Vec3.createVectorHelper(0, 1, 0);
	    float targetAngle = AngleHelper.deg(Math.acos(norm.dotProduct(ref)));

	    angle.withSpeed(0.25f);

	    if (norm.xCoord < 0) {
	        targetAngle = 360 - targetAngle;
	    }

	    if (animationTick < 10)
	        angle.set(targetAngle);

	    angle.target(targetAngle);
	    angle.tick();

	    float snapSize = 22.5f;
	    float snappedAngle = (snapSize * Math.round(angle.get(0f) / snapSize)) % 360f;

	    float length = 10;

	    CClient.PlacementIndicatorSetting mode = AllConfigs.CLIENT.placementIndicator;
	    if (mode == CClient.PlacementIndicatorSetting.TRIANGLE)
	        fadedArrow(centerX, centerY, r, g, b, a, length, snappedAngle);
	    else if (mode == CClient.PlacementIndicatorSetting.TEXTURE)
	        textured(centerX, centerY, a, snappedAngle);
	}

	private static void fadedArrow(float centerX, float centerY, float r, float g, float b, float a, float length, float snappedAngle) {
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	    GL11.glPushMatrix();
	    GL11.glTranslatef(centerX, centerY, 5);
	    GL11.glRotatef(snappedAngle, 0, 0, 1);
	    double scale = AllConfigs.CLIENT.indicatorScale;
	    GL11.glScaled(scale, scale, 1);

	    Tessellator tessellator = Tessellator.instance;
	    tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
	    tessellator.setColorRGBA_F(r, g, b, a);
	    tessellator.addVertex(0, -(10 + length), 0);

	    tessellator.setColorRGBA_F(r, g, b, 0f);
	    tessellator.addVertex(-9, -3, 0);
	    tessellator.addVertex(-6, -6, 0);
	    tessellator.addVertex(-3, -8, 0);
	    tessellator.addVertex(0, -8.5f, 0);
	    tessellator.addVertex(3, -8, 0);
	    tessellator.addVertex(6, -6, 0);
	    tessellator.addVertex(9, -3, 0);

	    tessellator.draw();
	    
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glPopMatrix();
	}

	private static void textured(float centerX, float centerY, float alpha, float snappedAngle) {
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    Minecraft.getMinecraft().getTextureManager().bindTexture(AllGuiTextures.PLACEMENT_INDICATOR_SHEET.location);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	    GL11.glPushMatrix();
	    GL11.glTranslatef(centerX, centerY, 50);
	    float scale = AllConfigs.CLIENT.indicatorScale * .75f;
	    GL11.glScalef(scale, scale, 1);
	    GL11.glScalef(12, 12, 1);

	    float index = snappedAngle / 22.5f;
	    float tex_size = 16f/256f;

	    float tx = 0;
	    float ty = index * tex_size;
	    float tw = 1f;
	    float th = tex_size;

	    Tessellator tessellator = Tessellator.instance;
	    tessellator.startDrawingQuads();
	    tessellator.setColorRGBA_F(1f, 1f, 1f, alpha);
	    
	    tessellator.addVertexWithUV(-1, -1, 0, tx, ty);
	    tessellator.addVertexWithUV(-1, 1, 0, tx, ty + th);
	    tessellator.addVertexWithUV(1, 1, 0, tx + tw, ty + th);
	    tessellator.addVertexWithUV(1, -1, 0, tx + tw, ty);

	    tessellator.draw();

	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glPopMatrix();
	}

}
