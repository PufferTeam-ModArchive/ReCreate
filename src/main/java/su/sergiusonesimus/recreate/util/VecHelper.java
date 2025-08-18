package su.sergiusonesimus.recreate.util;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import su.sergiusonesimus.metaworlds.util.Direction.Axis;

public class VecHelper {

    public static final Vec3 ZERO = Vec3.createVectorHelper(0, 0, 0);
    public static final Vec3 CENTER_OF_ORIGIN = Vec3.createVectorHelper(.5, .5, .5);

    public static Vec3 scale(Vec3 vec, double multiplier) {
        return Vec3.createVectorHelper(vec.xCoord * multiplier, vec.yCoord * multiplier, vec.zCoord * multiplier);
    }

    public static Vec3 rotate(Vec3 vec, Vec3 rotationVec) {
        return rotate(vec, rotationVec.xCoord, rotationVec.yCoord, rotationVec.zCoord);
    }

    public static Vec3 rotate(Vec3 vec, double xRot, double yRot, double zRot) {
        return rotate(rotate(rotate(vec, xRot, Axis.X), yRot, Axis.Y), zRot, Axis.Z);
    }

    public static Vec3 rotateCentered(Vec3 vec, double deg, Axis axis) {
        Vec3 shift = Vec3.createVectorHelper(.5, .5, .5);
        return VecHelper.rotate(shift.subtract(vec), deg, axis)
            .addVector(shift.xCoord, shift.yCoord, shift.zCoord);
    }

    public static Vec3 rotate(Vec3 vec, double deg, Axis axis) {
        if (deg == 0) return vec;
        if (vec == ZERO) return vec;

        float angle = (float) (deg / 180f * Math.PI);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x = vec.xCoord;
        double y = vec.yCoord;
        double z = vec.zCoord;

        if (axis == Axis.X) return Vec3.createVectorHelper(x, y * cos - z * sin, z * cos + y * sin);
        if (axis == Axis.Y) return Vec3.createVectorHelper(x * cos + z * sin, y, z * cos - x * sin);
        if (axis == Axis.Z) return Vec3.createVectorHelper(x * cos - y * sin, y * cos + x * sin, z);
        return vec;
    }

    // public static Vec3 mirrorCentered(Vec3 vec, Mirror mirror) {
    // Vec3 shift = getCenterOf(0, 0, 0);
    // return VecHelper.mirror(shift.subtract(vec), mirror).add(shift);
    // }
    //
    // public static Vec3 mirror(Vec3 vec, Mirror mirror) {
    // if (mirror == null || mirror == Mirror.NONE)
    // return vec;
    // if (vec == ZERO)
    // return vec;
    //
    // double x = vec.xCoord;
    // double y = vec.yCoord;
    // double z = vec.zCoord;
    //
    // if (mirror == Mirror.LEFT_RIGHT)
    // return Vec3.createVectorHelper(x, y, -z);
    // if (mirror == Mirror.FRONT_BACK)
    // return Vec3.createVectorHelper(-x, y, z);
    // return vec;
    // }

    public static Vec3 lookAt(Vec3 vec, Vec3 fwd) {
        fwd = fwd.normalize();
        Vec3 up = Vec3.createVectorHelper(0, 1, 0);
        double dot = fwd.dotProduct(up);
        if (Math.abs(dot) > 1 - 1.0E-3) up = Vec3.createVectorHelper(0, 0, dot > 0 ? 1 : -1);
        Vec3 right = fwd.crossProduct(up)
            .normalize();
        up = right.crossProduct(fwd)
            .normalize();
        double x = vec.xCoord * right.xCoord + vec.yCoord * up.xCoord + vec.zCoord * fwd.xCoord;
        double y = vec.xCoord * right.yCoord + vec.yCoord * up.yCoord + vec.zCoord * fwd.yCoord;
        double z = vec.xCoord * right.zCoord + vec.yCoord * up.zCoord + vec.zCoord * fwd.zCoord;
        return Vec3.createVectorHelper(x, y, z);
    }

    public static boolean isVecPointingTowards(Vec3 vec, ForgeDirection direction) {
        return Vec3.createVectorHelper(direction.offsetX, direction.offsetY, direction.offsetZ)
            .dotProduct(vec.normalize()) > 0.125; // slight tolerance to activate perpendicular movement actors
    }

    public static Vec3 getCenterOf(ChunkCoordinates pos) {
        return getCenterOf(pos.posX, pos.posY, pos.posZ);
    }

    public static Vec3 getCenterOf(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) return CENTER_OF_ORIGIN;
        return Vec3.createVectorHelper(x + 0.5f, y + 0.5f, z + 0.5f);
    }

    public static Vec3 offsetRandomly(Vec3 vec, Random r, float radius) {
        return Vec3.createVectorHelper(
            vec.xCoord + (r.nextFloat() - .5f) * 2 * radius,
            vec.yCoord + (r.nextFloat() - .5f) * 2 * radius,
            vec.zCoord + (r.nextFloat() - .5f) * 2 * radius);
    }

    public static Vec3 axisAlingedPlaneOf(Vec3 vec) {
        vec = vec.normalize();
        return Vec3.createVectorHelper(1 - Math.abs(vec.xCoord), 1 - Math.abs(vec.yCoord), 1 - Math.abs(vec.zCoord));
    }

    public static Vec3 axisAlingedPlaneOf(ForgeDirection face) {
        return axisAlingedPlaneOf(Vec3.createVectorHelper(face.offsetX, face.offsetY, face.offsetZ));
    }

    public static NBTTagList writeNBT(Vec3 vec) {
        NBTTagList listnbt = new NBTTagList();
        listnbt.appendTag(new NBTTagDouble(vec.xCoord));
        listnbt.appendTag(new NBTTagDouble(vec.yCoord));
        listnbt.appendTag(new NBTTagDouble(vec.zCoord));
        return listnbt;
    }

    public static Vec3 readNBT(NBTTagList list) {
        if (list.tagCount() == 0) return ZERO;
        return Vec3.createVectorHelper(list.func_150309_d(0), list.func_150309_d(1), list.func_150309_d(2));
    }

    public static Vec3 voxelSpace(double x, double y, double z) {
        return Vec3.createVectorHelper(x / 16f, y / 16f, z / 16f);
    }

    public static Integer getCoordinate(int x, int y, int z, Axis axis) {
        Integer result = null;
        switch (axis.toString()) {
            case "x":
                result = x;
                break;
            case "y":
                result = y;
                break;
            case "z":
                result = z;
                break;
        }
        return result;
    }

    public static Double getCoordinate(Vec3 vec, Axis axis) {
        Double result = null;
        switch (axis.toString()) {
            case "x":
                result = vec.xCoord;
                break;
            case "y":
                result = vec.yCoord;
                break;
            case "z":
                result = vec.zCoord;
                break;
        }
        return result;
    }

    public static boolean onSameAxis(int x1, int y1, int z1, int x2, int y2, int z2, Axis axis) {
        if (x1 == x2 && y1 == y2 && z1 == z2) return true;
        Axis[] otherAxes = { Axis.X, Axis.Y, Axis.Z };
        for (Axis otherAxis : otherAxes) if (axis != otherAxis)
            if (getCoordinate(x1, y1, z1, otherAxis) != getCoordinate(x2, y2, z2, otherAxis)) return false;
        return true;
    }

    public static Vec3 clamp(Vec3 vec, float maxLength) {
        if (vec.lengthVector() > maxLength) {
            Vec3 normalizedVec = vec.normalize();
            return Vec3.createVectorHelper(
                normalizedVec.xCoord * maxLength,
                normalizedVec.yCoord * maxLength,
                normalizedVec.zCoord * maxLength);
        } else return vec;
    }

    public static Vec3 lerp(float p, Vec3 from, Vec3 to) {
        Vec3 subtractedVec = from.subtract(to);
        return from.addVector(subtractedVec.xCoord * p, subtractedVec.yCoord * p, subtractedVec.zCoord * p);
    }

    public static Vec3 clampComponentWise(Vec3 vec, float maxLength) {
        return Vec3.createVectorHelper(
            MathHelper.clamp_double(vec.xCoord, -maxLength, maxLength),
            MathHelper.clamp_double(vec.yCoord, -maxLength, maxLength),
            MathHelper.clamp_double(vec.zCoord, -maxLength, maxLength));
    }

    public static Vec3 project(Vec3 vec, Vec3 ontoVec) {
        if (ontoVec.xCoord == 0 && ontoVec.yCoord == 0 && ontoVec.zCoord == 0) return ZERO;
        double scaleModifier = vec.dotProduct(ontoVec) / (ontoVec.lengthVector() * ontoVec.lengthVector());
        return Vec3.createVectorHelper(
            ontoVec.xCoord * scaleModifier,
            ontoVec.yCoord * scaleModifier,
            ontoVec.zCoord * scaleModifier);
    }

    @Nullable
    public static Vec3 intersectSphere(Vec3 origin, Vec3 lineDirection, Vec3 sphereCenter, double radius) {
        if (lineDirection.equals(ZERO)) return null;
        if (lineDirection.lengthVector() != 1) lineDirection = lineDirection.normalize();

        Vec3 diff = sphereCenter.subtract(origin);
        double lineDotDiff = lineDirection.dotProduct(diff);
        double delta = lineDotDiff * lineDotDiff - (diff.lengthVector() * diff.lengthVector() - radius * radius);
        if (delta < 0) return null;
        double t = -lineDotDiff + Math.sqrt(delta);
        return origin.addVector(lineDirection.xCoord * t, lineDirection.yCoord * t, lineDirection.zCoord * t);
    }

    public static Vec3 projectToPlayerView(Vec3 target, float partialTicks) {
        /*
         * The (centered) location on the screen of the given 3d point in the world.
         * Result is (dist right of center screen, dist up from center screen, if < 0, then in front of view plane)
         */
        EntityLivingBase renderViewEntity = Minecraft.getMinecraft().renderViewEntity;
        Vec3 camera_pos = Vec3.createVectorHelper(renderViewEntity.posX, renderViewEntity.posY, renderViewEntity.posZ);

        float yaw = renderViewEntity.prevRotationYaw
            + (renderViewEntity.rotationYaw - renderViewEntity.prevRotationYaw) * partialTicks;
        float pitch = renderViewEntity.prevRotationPitch
            + (renderViewEntity.rotationPitch - renderViewEntity.prevRotationPitch) * partialTicks;

        Vec3 result = Vec3.createVectorHelper(
            camera_pos.xCoord - target.xCoord,
            camera_pos.yCoord - target.yCoord,
            camera_pos.zCoord - target.zCoord);

        float yawRad = (float) Math.toRadians(yaw);
        float cosYaw = (float) Math.cos(yawRad);
        float sinYaw = (float) Math.sin(yawRad);
        double newX = result.xCoord * cosYaw - result.zCoord * sinYaw;
        double newZ = result.zCoord * cosYaw + result.xCoord * sinYaw;
        result.xCoord = newX;
        result.zCoord = newZ;

        float pitchRad = (float) Math.toRadians(pitch);
        float cosPitch = (float) Math.cos(pitchRad);
        float sinPitch = (float) Math.sin(pitchRad);
        double newY = result.yCoord * cosPitch + result.zCoord * sinPitch;
        newZ = result.zCoord * cosPitch - result.yCoord * sinPitch;
        result.yCoord = newY;
        result.zCoord = newZ;

        // ----- compensate for view bobbing (if active) -----
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.viewBobbing && renderViewEntity instanceof EntityPlayer) {
            EntityPlayer playerentity = (EntityPlayer) renderViewEntity;
            float distwalked_modified = playerentity.distanceWalkedModified;

            float f = distwalked_modified - playerentity.prevDistanceWalkedModified;
            float f1 = -(distwalked_modified + f * partialTicks);
            float f2 = playerentity.prevCameraYaw
                + (playerentity.cameraYaw - playerentity.prevCameraYaw) * partialTicks;

            float xBobFactor = Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F;
            result.yCoord += xBobFactor;

            float zBobFactor = MathHelper.sin(f1 * (float) Math.PI) * f2 * 3.0F;
            result.xCoord += zBobFactor;

            double bobX = MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5F;
            double bobY = -Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2);
            result.xCoord += bobX;
            result.yCoord += bobY;
        }

        // ----- adjust for fov -----
        float fov = mc.gameSettings.fovSetting;
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        float half_height = res.getScaledHeight() / 2f;
        float scale_factor = half_height / ((float) result.zCoord * (float) Math.tan(Math.toRadians(fov / 2)));

        return Vec3.createVectorHelper(
            -(float) result.xCoord * scale_factor,
            (float) result.yCoord * scale_factor,
            (float) result.zCoord);
    }

}
