package su.sergiusonesimus.recreate.content.contraptions.base;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import su.sergiusonesimus.recreate.content.contraptions.KineticDebugger;
import su.sergiusonesimus.recreate.content.contraptions.relays.encased.SplitShaftTileEntity;
import su.sergiusonesimus.recreate.foundation.utility.Color;
import su.sergiusonesimus.recreate.foundation.utility.Iterate;
import su.sergiusonesimus.recreate.util.AnimationTickHolder;
import su.sergiusonesimus.recreate.util.Direction;
import su.sergiusonesimus.recreate.util.Direction.Axis;

public abstract class KineticTileEntityRenderer extends TileEntitySpecialRenderer {

    public static boolean rainbowMode = false;

    public static float getAngleForTe(KineticTileEntity te, int x, int y, int z, Axis axis) {
        float time = AnimationTickHolder.getRenderTime();
        float offset = getRotationOffsetForPosition(te, x, y, z, axis);
        float angle = ((time * te.getSpeed() * 3f / 10 + offset) % 360) / 180 * (float) Math.PI;
        return angle;
    }

    protected static float getRotationOffsetForPosition(KineticTileEntity te, int x, int y, int z, final Axis axis) {
        float offset = /* ICogWheel.isLargeCog(te.getBlockType()) ? 11.25f : */0;
        if (shouldOffset(x, y, z, axis)) offset = 22.5f;
        return offset;
    }

    protected static boolean shouldOffset(int x, int y, int z, final Axis axis) {
        double d = (((axis == Axis.X) ? 0 : x) + ((axis == Axis.Y) ? 0 : y) + ((axis == Axis.Z) ? 0 : z)) % 2;
        return d == 0;
    }

    public static Color getColor(KineticTileEntity te) {
        Color result = Color.WHITE;
        if (KineticDebugger.isActive()) {
            rainbowMode = true;
            result = te.hasNetwork() ? Color.generateFromLong(te.network) : Color.WHITE;
        } else {
            float overStressedEffect = te.effects.overStressedEffect;
            if (overStressedEffect != 0) {
                if (overStressedEffect > 0) result = Color.WHITE.mixWith(Color.RED, overStressedEffect);
                else result = Color.WHITE.mixWith(Color.SPRING_GREEN, -overStressedEffect);
            }
        }
        return result;
    }

}
