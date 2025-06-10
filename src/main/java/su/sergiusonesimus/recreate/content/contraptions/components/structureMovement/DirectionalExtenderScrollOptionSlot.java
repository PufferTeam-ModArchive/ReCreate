package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import java.util.function.BiPredicate;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import su.sergiusonesimus.recreate.content.contraptions.base.IRotate;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.CenteredSideValueBoxTransform;
import su.sergiusonesimus.recreate.foundation.utility.AngleHelper;
import su.sergiusonesimus.recreate.foundation.utility.Pair;
import su.sergiusonesimus.recreate.util.Direction;

public class DirectionalExtenderScrollOptionSlot extends CenteredSideValueBoxTransform {

    public DirectionalExtenderScrollOptionSlot(BiPredicate<Pair<Block, Integer>, Direction> allowedDirections) {
        super(allowedDirections);
    }

    @Override
    protected Vec3 getLocalOffset(Block block, int meta) {
        ChunkCoordinates normal = ((IRotate) block).getDirection(meta)
            .getNormal();
        float scale = -2 / 16f;
        Vec3 vec = Vec3.createVectorHelper(normal.posX * scale, normal.posY * scale, normal.posZ * scale);
        return super.getLocalOffset(block, meta).addVector(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    @Override
    protected void rotate(Block block, int meta) {
        Direction facing = getSide();
        if (!facing.getAxis()
            .isHorizontal()) {
            float yRot = AngleHelper.horizontalAngle(facing) - 90;
            GL11.glRotatef(yRot, 0, 1, 0);
        }
        super.rotate(block, meta);
    }
}
