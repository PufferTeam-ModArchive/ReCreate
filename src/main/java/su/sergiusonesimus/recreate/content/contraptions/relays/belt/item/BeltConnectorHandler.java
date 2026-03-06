package su.sergiusonesimus.recreate.content.contraptions.relays.belt.item;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftBlock;
import su.sergiusonesimus.recreate.content.contraptions.wrench.IWrenchable;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;

public class BeltConnectorHandler {

    private static Random r = new Random();

    @SuppressWarnings("static-access")
    public static void tick() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        World world = Minecraft.getMinecraft().theWorld;

        if (player == null || world == null) return;
        if (Minecraft.getMinecraft().currentScreen != null) return;

        // TODO
        // for (InteractionHand hand : InteractionHand.values()) {
        ItemStack heldItem = player.getHeldItem();

        if (heldItem == null || !(heldItem.getItem() instanceof ItemBlock itemBlock)
            || Block.getBlockFromItem(itemBlock) != AllBlocks.belt) return;
        if (!heldItem.hasTagCompound()) return;

        NBTTagCompound tag = heldItem.getTagCompound();
        if (!tag.hasKey("FirstPulley")) return;

        NBTTagCompound firstPulley = tag.getCompoundTag("FirstPulley");
        int firstX = firstPulley.getInteger("X");
        int firstY = firstPulley.getInteger("Y");
        int firstZ = firstPulley.getInteger("Z");

        if (!(world.getBlock(firstX, firstY, firstZ) instanceof IWrenchable axisBlock)) return;
        Axis axis = axisBlock.getAxis(world.getBlockMetadata(firstX, firstY, firstZ));

        MovingObjectPosition rayTrace = Minecraft.getMinecraft().objectMouseOver;
        if (rayTrace == null || rayTrace.typeOfHit != MovingObjectType.BLOCK) {
            if (r.nextInt(50) == 0) {
                world.spawnParticle(
                    "reddust",
                    firstX + .5f + randomOffset(.25f),
                    firstY + .5f + randomOffset(.25f),
                    firstZ + .5f + randomOffset(.25f),
                    .3f,
                    .9f,
                    .5f);
            }
            return;
        }

        int selectedX = rayTrace.blockX;
        int selectedY = rayTrace.blockY;
        int selectedZ = rayTrace.blockZ;

        Block selectedBlock = world.getBlock(selectedX, selectedY, selectedZ);
        if (selectedBlock.getMaterial()
            .isReplaceable()) return;
        if (!ShaftBlock.isShaft(selectedBlock)) {
            ChunkCoordinates normal = Direction.from3DDataValue(rayTrace.sideHit)
                .getNormal();
            selectedX += normal.posX;
            selectedY += normal.posY;
            selectedZ += normal.posZ;
        }
        Vec3 start = Vec3.createVectorHelper(firstX, firstY, firstZ);
        Vec3 end = Vec3.createVectorHelper(selectedX, selectedY, selectedZ);
        Vec3 actualDiff = start.subtract(end);
        if (actualDiff.lengthVector() > AllConfigs.SERVER.kinetics.maxBeltLength) return;

        boolean canConnect = BeltConnectorItem.validateAxis(world, selectedX, selectedY, selectedZ)
            && BeltConnectorItem.canConnect(world, firstX, firstY, firstZ, selectedX, selectedY, selectedZ);

        end = Vec3
            .createVectorHelper(
                axis.choose(actualDiff.xCoord, 0, 0),
                axis.choose(0, actualDiff.yCoord, 0),
                axis.choose(0, 0, actualDiff.zCoord))
            .subtract(end);
        Vec3 diff = start.subtract(end);

        double x = Math.abs(diff.xCoord);
        double y = Math.abs(diff.yCoord);
        double z = Math.abs(diff.zCoord);
        float length = (float) Math.max(x, Math.max(y, z));
        Vec3 step = diff.normalize();

        int sames = ((x == y) ? 1 : 0) + ((y == z) ? 1 : 0) + ((z == x) ? 1 : 0);
        if (sames == 0) {
            List<Vec3> validDiffs = new LinkedList<>();
            for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) for (int k = -1; k <= 1; k++) {
                if (axis.choose(i, j, k) != 0) continue;
                if (axis == Axis.Y && i != 0 && k != 0) continue;
                if (i == 0 && j == 0 && k == 0) continue;
                validDiffs.add(Vec3.createVectorHelper(i, j, k));
            }
            int closestIndex = 0;
            float closest = Float.MAX_VALUE;
            for (Vec3 validDiff : validDiffs) {
                double distanceTo = step.distanceTo(validDiff);
                if (distanceTo < closest) {
                    closest = (float) distanceTo;
                    closestIndex = validDiffs.indexOf(validDiff);
                }
            }
            step = validDiffs.get(closestIndex);
        }

        if (axis == Axis.Y && step.xCoord != 0 && step.zCoord != 0) return;

        step = Vec3.createVectorHelper(Math.signum(step.xCoord), Math.signum(step.yCoord), Math.signum(step.zCoord));
        for (float f = 0; f < length; f += .0625f) {
            Vec3 position = start.addVector(step.xCoord * f, step.yCoord * f, step.zCoord * f);
            if (r.nextInt(10) == 0) {
                world.spawnParticle(
                    "reddust",
                    position.xCoord + .5f,
                    position.yCoord + .5f,
                    position.zCoord + .5f,
                    canConnect ? .3f : .9f,
                    canConnect ? .9f : .3f,
                    .5f);
            }
        }

        return;
        // TODO
        // }
    }

    private static float randomOffset(float range) {
        return (r.nextFloat() - .5f) * 2 * range;
    }

}
