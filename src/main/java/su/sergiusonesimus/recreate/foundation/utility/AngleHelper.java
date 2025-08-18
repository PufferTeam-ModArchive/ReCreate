package su.sergiusonesimus.recreate.foundation.utility;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.util.ReCreateMath;

public class AngleHelper {

    public static float horizontalAngle(Direction facing) {
        if (facing.getAxis()
            .isVertical()) return 0;
        float angle = facing.toYRot();
        if (facing.getAxis() == Axis.X) angle = -angle;
        return angle;
    }

    public static float verticalAngle(Direction facing) {
        return facing == Direction.UP ? -90 : facing == Direction.DOWN ? 90 : 0;
    }

    public static float rad(double angle) {
        if (angle == 0) return 0;
        return (float) (angle / 180 * Math.PI);
    }

    public static float deg(double angle) {
        if (angle == 0) return 0;
        return (float) (angle * 180 / Math.PI);
    }

    public static float angleLerp(double pct, double current, double target) {
        return (float) (current + getShortestAngleDiff(current, target) * pct);
    }

    public static float getShortestAngleDiff(double current, double target) {
        current = current % 360;
        target = target % 360;
        return (float) (((((target - current) % 360) + 540) % 360) - 180);
    }

    public static float getShortestAngleDiff(double current, double target, float hint) {
        float diff = getShortestAngleDiff(current, target);
        if (ReCreateMath.equal(Math.abs(diff), 180) && Math.signum(diff) != Math.signum(hint)) {
            return diff + 360 * Math.signum(hint);
        }
        return diff;
    }

}
