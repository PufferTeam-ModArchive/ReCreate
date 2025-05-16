package su.sergiusonesimus.recreate.content.contraptions.components;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import su.sergiusonesimus.recreate.content.contraptions.base.GeneratingKineticTileEntity;
import su.sergiusonesimus.recreate.foundation.utility.animation.InterpolatedChasingValue;

public class FlywheelTileEntity extends GeneratingKineticTileEntity {

    private float generatedCapacity;
    private float generatedSpeed;
    private int stoppingCooldown;

    // Client
    InterpolatedChasingValue visualSpeed = new InterpolatedChasingValue();
    float angle;

    public void setRotation(float speed, float capacity) {
        if (generatedSpeed != speed || generatedCapacity != capacity) {

            if (speed == 0) {
                if (stoppingCooldown == 0) stoppingCooldown = 40;
                return;
            }

            stoppingCooldown = 0;
            generatedSpeed = speed;
            generatedCapacity = capacity;
            updateGeneratedRotation();
        }
    }

    @Override
    public float getGeneratedSpeed() {
        // TODO
        // return convertToDirection(generatedSpeed, getBlockState().getValue(FlywheelBlock.HORIZONTAL_FACING));
        return 0;
    }

    @Override
    public float calculateAddedStressCapacity() {
        return lastCapacityProvided = generatedCapacity;
    }

    @Override
    public AxisAlignedBB makeRenderBoundingBox() {
        int inflator = 2;
        return super.makeRenderBoundingBox().expand(inflator, inflator, inflator);
    }

    @Override
    public void write(NBTTagCompound compound, boolean clientPacket) {
        compound.setFloat("GeneratedSpeed", generatedSpeed);
        compound.setFloat("GeneratedCapacity", generatedCapacity);
        compound.setInteger("Cooldown", stoppingCooldown);
        super.write(compound, clientPacket);
    }

    @Override
    protected void fromTag(NBTTagCompound compound, boolean clientPacket) {
        generatedSpeed = compound.getFloat("GeneratedSpeed");
        generatedCapacity = compound.getFloat("GeneratedCapacity");
        stoppingCooldown = compound.getInteger("Cooldown");
        super.fromTag(compound, clientPacket);
        if (clientPacket) visualSpeed.withSpeed(1 / 32f)
            .target(getGeneratedSpeed());
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote) {
            float targetSpeed = isVirtual() ? speed : getGeneratedSpeed();
            visualSpeed.target(targetSpeed);
            visualSpeed.tick();
            angle += visualSpeed.value * 3 / 10f;
            angle %= 360;
            return;
        }

        /*
         * After getting moved by pistons the generatedSpeed attribute reads 16 but the
         * actual speed stays at 0, if it happens update rotation
         */
        if (getGeneratedSpeed() != 0 && getSpeed() == 0) updateGeneratedRotation();

        if (stoppingCooldown == 0) return;

        stoppingCooldown--;
        if (stoppingCooldown == 0) {
            generatedCapacity = 0;
            generatedSpeed = 0;
            updateGeneratedRotation();
        }
    }
}
