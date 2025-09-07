package su.sergiusonesimus.recreate.content.contraptions.components.waterwheel;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.content.contraptions.base.GeneratingKineticTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.base.IRotate;
import su.sergiusonesimus.recreate.foundation.config.CKinetics;
import su.sergiusonesimus.recreate.foundation.utility.Iterate;

public class WaterWheelTileEntity extends GeneratingKineticTileEntity {

    private Map<Direction, Float> flows;

    public WaterWheelTileEntity() {
        super();
        flows = new HashMap<>();
        for (Direction d : Iterate.directions) setFlow(d, 0);
        setLazyTickRate(20);
    }

    @Override
    protected void fromTag(NBTTagCompound compound, boolean clientPacket) {
        super.fromTag(compound, clientPacket);
        if (compound.hasKey("Flows")) {
            for (Direction d : Iterate.directions) setFlow(
                d,
                compound.getCompoundTag("Flows")
                    .getFloat(d.getSerializedName()));
        }
    }

    @Override
    public void write(NBTTagCompound compound, boolean clientPacket) {
        NBTTagCompound flows = new NBTTagCompound();
        for (Direction d : Iterate.directions) flows.setFloat(d.getSerializedName(), this.flows.get(d));
        compound.setTag("Flows", flows);

        super.write(compound, clientPacket);
    }

    public void setFlow(Direction direction, float speed) {
        Float currentSpeed = flows.get(direction);
        if (currentSpeed == null || currentSpeed != speed) {
            flows.put(direction, speed);
            this.notifyUpdate();
        }
    }

    @Override
    public float getGeneratedSpeed() {
        float speed = 0;
        for (Float f : flows.values()) speed += f;
        if (speed != 0) speed += CKinetics.waterWheelBaseSpeed * Math.signum(speed);
        return speed;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.getBlockType() instanceof WaterWheelBlock block)
            block.updateAllSides(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    protected AxisAlignedBB makeRenderBoundingBox() {
        float borderX = 0;
        float borderY = 0;
        float borderZ = 0;
        float f = 2F;
        switch (((IRotate) this.getBlockType()).getAxis(this.getBlockMetadata())) {
            case X:
                borderY = borderZ = f;
                break;
            case Y:
                borderX = borderZ = f;
                break;
            case Z:
                borderX = borderY = f;
                break;
        }
        return AxisAlignedBB.getBoundingBox(
            xCoord - borderX,
            yCoord - borderY,
            zCoord - borderZ,
            xCoord + borderX,
            yCoord + borderY,
            zCoord + borderZ);
    }

}
