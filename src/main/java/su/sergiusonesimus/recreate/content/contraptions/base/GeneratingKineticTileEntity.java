package su.sergiusonesimus.recreate.content.contraptions.base;

import net.minecraft.tileentity.TileEntity;

import su.sergiusonesimus.recreate.content.contraptions.KineticNetwork;
import su.sergiusonesimus.recreate.content.contraptions.base.IRotate.SpeedLevel;

public abstract class GeneratingKineticTileEntity extends KineticTileEntity {

    public boolean reActivateSource;

    protected void notifyStressCapacityChange(float capacity) {
        getOrCreateNetwork().updateCapacityFor(this, capacity);
    }

    @Override
    public void removeSource() {
        if (hasSource() && isSource()) reActivateSource = true;
        super.removeSource();
    }

    @Override
    public void setSource(int sourceX, int sourceY, int sourceZ) {
        super.setSource(sourceX, sourceY, sourceZ);
        TileEntity tileEntity = worldObj.getTileEntity(sourceX, sourceY, sourceZ);
        if (!(tileEntity instanceof KineticTileEntity)) return;
        KineticTileEntity sourceTe = (KineticTileEntity) tileEntity;
        if (reActivateSource && Math.abs(sourceTe.getSpeed()) >= Math.abs(getGeneratedSpeed()))
            reActivateSource = false;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (reActivateSource) {
            updateGeneratedRotation();
            reActivateSource = false;
        }
    }

    // TODO
    // @Override
    // public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
    // boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    //
    // float stressBase = calculateAddedStressCapacity();
    // if (stressBase != 0 && IRotate.StressImpact.isEnabled()) {
    // tooltip.add(componentSpacing.plainCopy().append(Lang.translate("gui.goggles.generator_stats")));
    // tooltip.add(componentSpacing.plainCopy().append(Lang.translate("tooltip.capacityProvided").withStyle(ChatFormatting.GRAY)));
    //
    // float speed = getTheoreticalSpeed();
    // if (speed != getGeneratedSpeed() && speed != 0)
    // stressBase *= getGeneratedSpeed() / speed;
    //
    // speed = Math.abs(speed);
    // float stressTotal = stressBase * speed;
    //
    // tooltip.add(
    // componentSpacing.plainCopy()
    // .append(new TextComponent(" " + IHaveGoggleInformation.format(stressTotal))
    // .append(Lang.translate("generic.unit.stress"))
    // .withStyle(ChatFormatting.AQUA))
    // .append(" ")
    // .append(Lang.translate("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY)));
    //
    // added = true;
    // }
    //
    // return added;
    // }

    public void updateGeneratedRotation() {
        float speed = getGeneratedSpeed();
        float prevSpeed = this.speed;

        if (worldObj.isRemote) return;

        if (prevSpeed != speed) {
            if (!hasSource()) {
                SpeedLevel levelBefore = SpeedLevel.of(this.speed);
                SpeedLevel levelafter = SpeedLevel.of(speed);
                if (levelBefore != levelafter) effects.queueRotationIndicators();
            }

            applyNewSpeed(prevSpeed, speed);
        }

        if (hasNetwork() && speed != 0) {
            KineticNetwork network = getOrCreateNetwork();
            notifyStressCapacityChange(calculateAddedStressCapacity());
            getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
            network.updateStress();
        }

        onSpeedChanged(prevSpeed);
        sendData();
    }

    public void applyNewSpeed(float prevSpeed, float speed) {

        // Speed changed to 0
        if (speed == 0) {
            if (hasSource()) {
                notifyStressCapacityChange(0);
                getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
                return;
            }
            detachKinetics();
            setSpeed(0);
            setNetwork(null);
            return;
        }

        // Now turning - create a new Network
        if (prevSpeed == 0) {
            setSpeed(speed);
            setNetwork(createNetworkId());
            attachKinetics();
            return;
        }

        // Change speed when overpowered by other generator
        if (hasSource()) {

            // Staying below Overpowered speed
            if (Math.abs(prevSpeed) >= Math.abs(speed)) {
                if (Math.signum(prevSpeed) != Math.signum(speed)) {
                    this.blockType.dropBlockAsItem(worldObj, xCoord, yCoord, zCoord, blockMetadata, 0);
                    worldObj.setBlockToAir(xCoord, yCoord, zCoord);
                }
                return;
            }

            // Faster than attached network -> become the new source
            detachKinetics();
            setSpeed(speed);
            sourceX = null;
            sourceY = null;
            sourceZ = null;
            setNetwork(createNetworkId());
            attachKinetics();
            return;
        }

        // Reapply source
        detachKinetics();
        setSpeed(speed);
        attachKinetics();
    }

    public Long createNetworkId() {
        return ((long) this.xCoord & 0xFFFFFFL) << 38 | ((long) this.yCoord & 0xFFL) << 26
            | ((long) this.zCoord & 0xFFFFFFL);
    }
}
