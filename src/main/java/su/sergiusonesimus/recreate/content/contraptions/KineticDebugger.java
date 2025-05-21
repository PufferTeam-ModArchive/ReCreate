package su.sergiusonesimus.recreate.content.contraptions;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.ClientProxy;
import su.sergiusonesimus.recreate.content.contraptions.base.IRotate;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntityRenderer;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.foundation.utility.Color;
import su.sergiusonesimus.recreate.util.Direction;
import su.sergiusonesimus.recreate.util.Direction.Axis;
import su.sergiusonesimus.recreate.util.Direction.AxisDirection;
import su.sergiusonesimus.recreate.util.VecHelper;

public class KineticDebugger {

    public static void tick() {
        if (!isActive()) {
            if (KineticTileEntityRenderer.rainbowMode) {
                KineticTileEntityRenderer.rainbowMode = false;
                // TODO?
                // ClientProxy.BUFFER_CACHE.invalidate();
            }
            return;
        }

        KineticTileEntity te = getSelectedTE();
        if (te == null) return;

        World world = Minecraft.getMinecraft().theWorld;
        int toOutlineX;
        int toOutlineY;
        int toOutlineZ;
        if (te.hasSource()) {
            toOutlineX = te.sourceX;
            toOutlineY = te.sourceY;
            toOutlineZ = te.sourceZ;
        } else {
            toOutlineX = te.xCoord;
            toOutlineY = te.yCoord;
            toOutlineZ = te.zCoord;
        }
        Block block = te.getBlockType();
        int meta = te.getBlockMetadata();
        Block sourceBlock = world.getBlock(toOutlineX, toOutlineY, toOutlineZ);
        sourceBlock.setBlockBoundsBasedOnState(world, toOutlineX, toOutlineY, toOutlineZ);
        AxisAlignedBB shape = sourceBlock.getCollisionBoundingBoxFromPool(world, toOutlineX, toOutlineY, toOutlineZ);

        if (te.getTheoreticalSpeed() != 0 && shape != null) ClientProxy.OUTLINER.chaseAABB("kineticSource", shape)
            .lineWidth(1 / 16f)
            .colored(
                te.hasSource() ? Color.generateFromLong(te.network)
                    .getRGB() : 0xffcc00);

        if (block instanceof IRotate) {
            Axis axis = ((IRotate) block).getAxis(meta);
            ChunkCoordinates offset = Direction.get(AxisDirection.POSITIVE, axis)
                .getNormal();
            Vec3 center = VecHelper.getCenterOf(te.xCoord, te.yCoord, te.zCoord);
            ClientProxy.OUTLINER
                .showLine(
                    "rotationAxis",
                    center.addVector(offset.posX, offset.posY, offset.posZ),
                    center.addVector(-offset.posX, -offset.posY, -offset.posZ))
                .lineWidth(1 / 16f);
        }

    }

    public static boolean isActive() {
        return Minecraft.getMinecraft().gameSettings.showDebugInfo && AllConfigs.CLIENT.rainbowDebug;
    }

    public static KineticTileEntity getSelectedTE() {
        Minecraft mc = Minecraft.getMinecraft();
        MovingObjectPosition mop = mc.objectMouseOver;
        WorldClient world = mc.theWorld;
        if (mop == null) return null;
        if (world == null) return null;
        if (mop.typeOfHit != MovingObjectType.BLOCK) return null;

        TileEntity te = world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
        if (!(te instanceof KineticTileEntity)) return null;

        return (KineticTileEntity) te;
    }

}
