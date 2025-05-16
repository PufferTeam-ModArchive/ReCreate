package su.sergiusonesimus.recreate.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import su.sergiusonesimus.recreate.foundation.utility.Color;
import su.sergiusonesimus.recreate.util.Direction.Axis;
import su.sergiusonesimus.recreate.util.VecHelper;

public class EntityRotationIndicatorFX extends EntityFX {

    private static final ResourceLocation TEXTURE = new ResourceLocation("yourmod", "textures/particles/glitter.png");

    protected float radius;
    protected float radius1;
    protected float radius2;
    protected float speed;
    protected Axis axis;
    protected Vec3 origin;
    protected Vec3 offset;
    protected boolean isVisible;

    private EntityRotationIndicatorFX(World world, double x, double y, double z, int color, float radius1,
        float radius2, float speed, Axis axis, int lifeSpan, boolean isVisible) {
        super(world, x, y, z);
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.origin = Vec3.createVectorHelper(x, y, z);
        this.particleScale *= 0.75F;
        this.particleMaxAge = lifeSpan + this.rand.nextInt(32);
        Color localColor = new Color(color);
        // Making our color lighter
        float multiplyer = 0.5f;
        this.setRBGColorF(
            (localColor.getRed() * (1 - multiplyer) + 255 * multiplyer) / 255f,
            (localColor.getGreen() * (1 - multiplyer) + 255 * multiplyer) / 255f,
            (localColor.getBlue() * (1 - multiplyer) + 255 * multiplyer) / 255f);
        this.radius1 = radius1;
        this.radius = radius1;
        this.radius2 = radius2;
        this.speed = speed;
        this.axis = axis;
        this.isVisible = isVisible;
        this.offset = axis == Axis.X || axis == Axis.Z ? Vec3.createVectorHelper(0, 1, 0)
            : Vec3.createVectorHelper(1, 0, 0);
        move(0, 0, 0);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        radius += (radius2 - radius) * .1f;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float rotationX, float rotationZ,
        float rotationYZ, float rotationXY, float rotationXZ) {
        if (!isVisible) return;
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

        int frame = 7 - this.particleAge * 8 / this.particleMaxAge;

        float uMin = 1.0F / 8f * (float) frame;
        float uMax = 1.0F / 8f * (float) (frame + 1);
        float vMin = 0.0F;
        float vMax = 1.0F;
        float scale = 0.1F * this.particleScale;

        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tessellator.addVertexWithUV(
            (double) (x - rotationX * scale - rotationXY * scale),
            (double) (y - rotationZ * scale),
            (double) (z - rotationYZ * scale - rotationXZ * scale),
            (double) uMax,
            (double) vMax);
        tessellator.addVertexWithUV(
            (double) (x - rotationX * scale + rotationXY * scale),
            (double) (y + rotationZ * scale),
            (double) (z - rotationYZ * scale + rotationXZ * scale),
            (double) uMax,
            (double) vMin);
        tessellator.addVertexWithUV(
            (double) (x + rotationX * scale + rotationXY * scale),
            (double) (y + rotationZ * scale),
            (double) (z + rotationYZ * scale + rotationXZ * scale),
            (double) uMin,
            (double) vMin);
        tessellator.addVertexWithUV(
            (double) (x + rotationX * scale - rotationXY * scale),
            (double) (y - rotationZ * scale),
            (double) (z + rotationYZ * scale - rotationXZ * scale),
            (double) uMin,
            (double) vMax);
    }

    public void move(double x, double y, double z) {
        float time = worldObj.getTotalWorldTime();
        // TODO This method is for ponder worlds. We probably don't need it right now
        // float time = AnimationTickHolder.getTicks(worldObj);
        float angle = (float) ((time * speed) % 360)
            - (speed / 2 * particleAge * (((float) particleAge) / particleMaxAge));
        if (speed < 0 && axis == Axis.Y) angle += 180;
        Vec3 position = VecHelper.rotate(VecHelper.scale(offset, radius), angle, axis)
            .addVector(origin.xCoord, origin.yCoord, origin.zCoord);
        this.posX = position.xCoord;
        this.posY = position.yCoord;
        this.posZ = position.zCoord;
    }

}
