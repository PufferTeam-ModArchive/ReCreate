package su.sergiusonesimus.recreate.foundation.utility.animation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

import su.sergiusonesimus.recreate.foundation.utility.AngleHelper;
import su.sergiusonesimus.recreate.util.ReCreateMath;

// Can replace all Interpolated value classes
// InterpolatedChasingValue, InterpolatedValue, InterpolatedChasingAngle, InterpolatedAngle
public class LerpedFloat {

    protected Interpolater interpolater;
    protected float previousValue;
    protected float value;

    protected Chaser chaseFunction;
    protected float chaseTarget;
    protected float chaseSpeed;

    protected boolean forcedSync;

    public LerpedFloat(Interpolater interpolater) {
        this.interpolater = interpolater;
        startWithValue(0);
        forcedSync = true;
    }

    public static LerpedFloat linear() {
        return new LerpedFloat((p, c, t) -> (float) ReCreateMath.lerp(p, c, t));
    }

    public static LerpedFloat angular() {
        return new LerpedFloat(AngleHelper::angleLerp);
    }

    public LerpedFloat startWithValue(double value) {
        float f = (float) value;
        this.previousValue = f;
        this.chaseTarget = f;
        this.value = f;
        return this;
    }

    public LerpedFloat chase(double value, double speed, Chaser chaseFunction) {
        this.chaseTarget = (float) value;
        this.chaseSpeed = (float) speed;
        this.chaseFunction = chaseFunction;
        return this;
    }

    public void updateChaseTarget(float target) {
        this.chaseTarget = target;
    }

    public boolean updateChaseSpeed(double speed) {
        float prevSpeed = this.chaseSpeed;
        this.chaseSpeed = (float) speed;
        return !ReCreateMath.equal(prevSpeed, speed);
    }

    public void tickChaser() {
        previousValue = value;
        if (chaseFunction == null) return;
        if (ReCreateMath.equal((double) value, chaseTarget)) {
            value = chaseTarget;
            return;
        }
        value = chaseFunction.chase(value, chaseSpeed, chaseTarget);
    }

    public void setValue(double value) {
        this.previousValue = this.value;
        this.value = (float) value;
    }

    public float getValue() {
        return getValue(1);
    }

    public float getValue(float partialTicks) {
        return ReCreateMath.lerp(partialTicks, previousValue, value);
    }

    public boolean settled() {
        return ReCreateMath.equal((double) previousValue, value);
    }

    public float getChaseTarget() {
        return chaseTarget;
    }

    public void forceNextSync() {
        forcedSync = true;
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound compoundNBT = new NBTTagCompound();
        compoundNBT.setFloat("Speed", chaseSpeed);
        compoundNBT.setFloat("Target", chaseTarget);
        compoundNBT.setFloat("Value", value);
        if (forcedSync) compoundNBT.setBoolean("Force", true);
        forcedSync = false;
        return compoundNBT;
    }

    public void readNBT(NBTTagCompound compoundNBT, boolean clientPacket) {
        if (!clientPacket || compoundNBT.hasKey("Force")) startWithValue(compoundNBT.getFloat("Value"));
        readChaser(compoundNBT);
    }

    protected void readChaser(NBTTagCompound compoundNBT) {
        chaseSpeed = compoundNBT.getFloat("Speed");
        chaseTarget = compoundNBT.getFloat("Target");
    }

    @FunctionalInterface
    public interface Interpolater {

        float interpolate(double progress, double current, double target);
    }

    @FunctionalInterface
    public interface Chaser {

        Chaser IDLE = (c, s, t) -> (float) c;
        Chaser EXP = exp(Double.MAX_VALUE);
        Chaser LINEAR = (c, s, t) -> (float) (c + MathHelper.clamp_double(t - c, -s, s));

        static Chaser exp(double maxEffectiveSpeed) {
            return (c, s,
                t) -> (float) (c + MathHelper.clamp_double((t - c) * s, -maxEffectiveSpeed, maxEffectiveSpeed));
        }

        float chase(double current, double speed, double target);
    }

}
