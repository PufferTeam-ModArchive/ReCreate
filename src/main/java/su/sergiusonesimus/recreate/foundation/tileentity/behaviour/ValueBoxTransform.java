package su.sergiusonesimus.recreate.foundation.tileentity.behaviour;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.Vec3;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.content.contraptions.base.DirectionalKineticBlock;
import su.sergiusonesimus.recreate.content.contraptions.base.HorizontalAxisKineticBlock;
import su.sergiusonesimus.recreate.foundation.utility.AngleHelper;
import su.sergiusonesimus.recreate.util.VecHelper;

public abstract class ValueBoxTransform {

    protected float scale = getScale();

    protected abstract Vec3 getLocalOffset(Block block, int meta);

    protected abstract void rotate(Block block, int meta);

    public boolean testHit(Block block, int meta, Vec3 localHit) {
        Vec3 offset = getLocalOffset(block, meta);
        if (offset == null) return false;
        return localHit.distanceTo(offset) < scale / 2;
    }

    public void transform(Block block, int meta) {
        Vec3 position = getLocalOffset(block, meta);
        if (position == null) return;

        GL11.glTranslatef((float) position.xCoord, (float) position.yCoord, (float) position.zCoord);
        rotate(block, meta);
        GL11.glScalef(scale, scale, scale);
    }

    public boolean shouldRender(Block block, int meta) {
        return block.getMaterial() != Material.air && getLocalOffset(block, meta) != null;
    }

    protected Vec3 rotateHorizontally(Block block, int meta, Vec3 vec) {
        float yRot = 0;
        if (block instanceof DirectionalKineticBlock)
            yRot = AngleHelper.horizontalAngle(((DirectionalKineticBlock) block).getDirection(meta));
        if (block instanceof HorizontalAxisKineticBlock)
            yRot = AngleHelper.horizontalAngle(((HorizontalAxisKineticBlock) block).getDirection(meta));
        return VecHelper.rotateCentered(vec, yRot, Axis.Y);
    }

    protected float getScale() {
        return .4f;
    }

    protected float getFontScale() {
        return 1 / 64f;
    }

    public static abstract class Dual extends ValueBoxTransform {

        protected boolean first;

        public Dual(boolean first) {
            this.first = first;
        }

        public boolean isFirst() {
            return first;
        }

        public static Pair<ValueBoxTransform, ValueBoxTransform> makeSlots(Function<Boolean, ? extends Dual> factory) {
            return Pair.of(factory.apply(true), factory.apply(false));
        }

        public boolean testHit(Block block, int meta, Vec3 localHit) {
            Vec3 offset = getLocalOffset(block, meta);
            if (offset == null) return false;
            return localHit.distanceTo(offset) < scale / 3.5f;
        }

    }

    public static abstract class Sided extends ValueBoxTransform {

        protected Direction direction = Direction.UP;

        public Sided fromSide(Direction direction) {
            this.direction = direction;
            return this;
        }

        @Override
        protected Vec3 getLocalOffset(Block block, int meta) {
            Vec3 location = getSouthLocation();
            location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(getSide()), Axis.Y);
            location = VecHelper.rotateCentered(location, AngleHelper.verticalAngle(getSide()), Axis.X);
            return location;
        }

        protected abstract Vec3 getSouthLocation();

        @Override
        protected void rotate(Block block, int meta) {
            float yRot = AngleHelper.horizontalAngle(getSide()) + 180;
            float xRot = getSide() == Direction.UP ? 90 : getSide() == Direction.DOWN ? 270 : 0;
            GL11.glRotatef(yRot, 0, 1, 0);
            GL11.glRotatef(xRot, 1, 0, 0);
        }

        @Override
        public boolean shouldRender(Block block, int meta) {
            return super.shouldRender(block, meta) && isSideActive(block, meta, getSide());
        }

        @Override
        public boolean testHit(Block block, int meta, Vec3 localHit) {
            return isSideActive(block, meta, getSide()) && super.testHit(block, meta, localHit);
        }

        protected boolean isSideActive(Block block, int meta, Direction direction) {
            return true;
        }

        public Direction getSide() {
            return direction;
        }

    }

}
