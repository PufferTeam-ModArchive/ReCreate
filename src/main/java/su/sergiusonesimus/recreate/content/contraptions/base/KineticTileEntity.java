package su.sergiusonesimus.recreate.content.contraptions.base;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.content.contraptions.KineticNetwork;
import su.sergiusonesimus.recreate.content.contraptions.RotationPropagator;
import su.sergiusonesimus.recreate.content.contraptions.base.IRotate.SpeedLevel;
import su.sergiusonesimus.recreate.content.contraptions.base.IRotate.StressImpact;
import su.sergiusonesimus.recreate.content.contraptions.goggles.IHaveGoggleInformation;
import su.sergiusonesimus.recreate.content.contraptions.goggles.IHaveHoveringInformation;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.ICogWheel;
import su.sergiusonesimus.recreate.foundation.block.BlockStressValues;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.foundation.item.TooltipHelper;
import su.sergiusonesimus.recreate.foundation.tileentity.SmartTileEntity;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.foundation.utility.Lang;
import su.sergiusonesimus.recreate.util.Direction;
import su.sergiusonesimus.recreate.util.Direction.Axis;
import su.sergiusonesimus.recreate.util.Direction.AxisDirection;
import su.sergiusonesimus.recreate.util.TextHelper;

public class KineticTileEntity extends SmartTileEntity implements IHaveGoggleInformation, IHaveHoveringInformation {

    public @Nullable Long network;
    public @Nullable Integer sourceX;
    public @Nullable Integer sourceY;
    public @Nullable Integer sourceZ;
    public boolean networkDirty;
    public boolean updateSpeed;
    public int preventSpeedUpdate;

    protected KineticEffectHandler effects;
    protected float speed;
    protected float capacity;
    protected float stress;
    protected boolean overStressed;
    protected boolean wasMoved;

    private int flickerTally;
    private int networkSize;
    private int validationCountdown;
    protected float lastStressApplied;
    protected float lastCapacityProvided;

    public KineticTileEntity() {
        super();
        effects = new KineticEffectHandler(this);
        updateSpeed = true;
    }

    @Override
    public void initialize() {
        if (hasNetwork() && !worldObj.isRemote) {
            KineticNetwork network = getOrCreateNetwork();
            if (!network.initialized) network.initFromTE(capacity, stress, networkSize);
            network.addSilently(this, lastCapacityProvided, lastStressApplied);
        }

        super.initialize();
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote && needsSpeedUpdate()) attachKinetics();

        super.updateEntity();
        effects.tick();

        if (worldObj.isRemote) {
            cachedBoundingBox = null; // cache the bounding box for every frame between ticks
            // TODO
            // this.tickAudio();
            return;
        }

        if (validationCountdown-- <= 0) {
            validationCountdown = AllConfigs.SERVER.kinetics.kineticValidationFrequency;
            validateKinetics();
        }

        if (getFlickerScore() > 0) flickerTally = getFlickerScore() - 1;

        if (networkDirty) {
            if (hasNetwork()) getOrCreateNetwork().updateNetwork();
            networkDirty = false;
        }
    }

    private void validateKinetics() {
        if (hasSource()) {
            if (!hasNetwork()) {
                removeSource();
                return;
            }

            if (!worldObj.blockExists(sourceX, sourceY, sourceZ)) return;

            TileEntity tileEntity = worldObj.getTileEntity(sourceX, sourceY, sourceZ);
            KineticTileEntity sourceTe = tileEntity instanceof KineticTileEntity ? (KineticTileEntity) tileEntity
                : null;
            if (sourceTe == null || sourceTe.speed == 0) {
                removeSource();
                detachKinetics();
                return;
            }

            return;
        }

        if (speed != 0) {
            if (getGeneratedSpeed() == 0) speed = 0;
        }
    }

    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        networkDirty = false;
        this.capacity = maxStress;
        this.stress = currentStress;
        this.networkSize = networkSize;
        boolean overStressed = maxStress < currentStress && IRotate.StressImpact.isEnabled();

        if (overStressed != this.overStressed) {
            float prevSpeed = getSpeed();
            this.overStressed = overStressed;
            onSpeedChanged(prevSpeed);
            sendData();
        }
    }

    protected Block getStressConfigKey() {
        return getBlockType();
    }

    public float calculateStressApplied() {
        float impact = (float) BlockStressValues.getImpact(getStressConfigKey());
        this.lastStressApplied = impact;
        return impact;
    }

    public float calculateAddedStressCapacity() {
        float capacity = (float) BlockStressValues.getCapacity(getStressConfigKey());
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    public void onSpeedChanged(float previousSpeed) {
        boolean fromOrToZero = (previousSpeed == 0) != (getSpeed() == 0);
        boolean directionSwap = !fromOrToZero && Math.signum(previousSpeed) != Math.signum(getSpeed());
        if (fromOrToZero || directionSwap) flickerTally = getFlickerScore() + 5;
    }

    @Override
    protected void setRemovedNotDueToChunkUnload() {
        if (!worldObj.isRemote) {
            if (hasNetwork()) getOrCreateNetwork().remove(this);
            detachKinetics();
        }
        super.setRemovedNotDueToChunkUnload();
    }

    @Override
    protected void write(NBTTagCompound compound, boolean clientPacket) {
        compound.setFloat("Speed", speed);

        if (needsSpeedUpdate()) compound.setBoolean("NeedsSpeedUpdate", true);

        if (hasSource()) {
            NBTTagCompound networkTag = new NBTTagCompound();
            networkTag.setInteger("X", sourceX);
            networkTag.setInteger("Y", sourceY);
            networkTag.setInteger("Z", sourceZ);
            compound.setTag("Source", networkTag);
        }

        if (hasNetwork()) {
            NBTTagCompound networkTag = new NBTTagCompound();
            networkTag.setLong("Id", this.network);
            networkTag.setFloat("Stress", stress);
            networkTag.setFloat("Capacity", capacity);
            networkTag.setInteger("Size", networkSize);

            if (lastStressApplied != 0) networkTag.setFloat("AddedStress", lastStressApplied);
            if (lastCapacityProvided != 0) networkTag.setFloat("AddedCapacity", lastCapacityProvided);

            compound.setTag("Network", networkTag);
        }

        super.write(compound, clientPacket);
    }

    public boolean needsSpeedUpdate() {
        return updateSpeed;
    }

    @Override
    protected void fromTag(NBTTagCompound compound, boolean clientPacket) {
        boolean overStressedBefore = overStressed;
        clearKineticInformation();

        // DO NOT READ kinetic information when placed after movement
        if (wasMoved) {
            super.fromTag(compound, clientPacket);
            return;
        }

        speed = compound.getFloat("Speed");

        if (compound.hasKey("Source")) {
            NBTTagCompound networkTag = compound.getCompoundTag("Source");
            this.sourceX = networkTag.getInteger("X");
            this.sourceY = networkTag.getInteger("Y");
            this.sourceZ = networkTag.getInteger("Z");
        }

        if (compound.hasKey("Network")) {
            NBTTagCompound networkTag = compound.getCompoundTag("Network");
            network = networkTag.getLong("Id");
            stress = networkTag.getFloat("Stress");
            capacity = networkTag.getFloat("Capacity");
            networkSize = networkTag.getInteger("Size");
            lastStressApplied = networkTag.getFloat("AddedStress");
            lastCapacityProvided = networkTag.getFloat("AddedCapacity");
            overStressed = capacity < stress && StressImpact.isEnabled();
        }

        super.fromTag(compound, clientPacket);

        if (clientPacket && overStressedBefore != overStressed && speed != 0) effects.triggerOverStressedEffect();

        // TODO
        // if (clientPacket)
        // InstancedRenderDispatcher.enqueueUpdate(this);
    }

    public float getGeneratedSpeed() {
        return 0;
    }

    public boolean isSource() {
        return getGeneratedSpeed() != 0;
    }

    public float getSpeed() {
        if (overStressed) return 0;
        return getTheoreticalSpeed();
    }

    public float getTheoreticalSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean hasSource() {
        return sourceX != null && sourceY != null && sourceZ != null;
    }

    public void setSource(int sourceX, int sourceY, int sourceZ) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.sourceZ = sourceZ;
        if (worldObj == null || worldObj.isRemote) return;

        TileEntity tileEntity = worldObj.getTileEntity(sourceX, sourceY, sourceZ);
        if (!(tileEntity instanceof KineticTileEntity)) {
            removeSource();
            return;
        }

        KineticTileEntity sourceTe = (KineticTileEntity) tileEntity;
        setNetwork(sourceTe.network);
    }

    public void removeSource() {
        float prevSpeed = getSpeed();

        speed = 0;
        sourceX = null;
        sourceY = null;
        sourceZ = null;
        setNetwork(null);

        onSpeedChanged(prevSpeed);
    }

    public void setNetwork(@Nullable Long networkIn) {
        if (network == networkIn) return;
        if (network != null) getOrCreateNetwork().remove(this);

        network = networkIn;

        if (networkIn == null) return;

        network = networkIn;
        KineticNetwork network = getOrCreateNetwork();
        network.initialized = true;
        network.add(this);
    }

    public KineticNetwork getOrCreateNetwork() {
        return ReCreate.TORQUE_PROPAGATOR.getOrCreateNetworkFor(this);
    }

    public boolean hasNetwork() {
        return network != null;
    }

    public void attachKinetics() {
        updateSpeed = false;
        RotationPropagator.handleAdded(worldObj, this.xCoord, this.yCoord, this.zCoord, this);
    }

    public void detachKinetics() {
        RotationPropagator.handleRemoved(worldObj, this.xCoord, this.yCoord, this.zCoord, this);
    }

    public boolean isSpeedRequirementFulfilled() {
        if (!(this.blockType instanceof IRotate)) return true;
        IRotate def = (IRotate) this.blockType;
        SpeedLevel minimumRequiredSpeedLevel = def.getMinimumRequiredSpeedLevel();
        if (minimumRequiredSpeedLevel == null) return true;
        if (minimumRequiredSpeedLevel == SpeedLevel.MEDIUM)
            return Math.abs(getSpeed()) >= AllConfigs.SERVER.kinetics.mediumSpeed;
        if (minimumRequiredSpeedLevel == SpeedLevel.FAST)
            return Math.abs(getSpeed()) >= AllConfigs.SERVER.kinetics.fastSpeed;
        return true;
    }

    public static void switchToBlockState(World world, int x, int y, int z, Block block, int meta) {
        if (world.isRemote) return;

        TileEntity tileEntityIn = world.getTileEntity(x, y, z);
        Block currentBlock = world.getBlock(x, y, z);
        int currentMeta = world.getBlockMetadata(x, y, z);
        boolean isKinetic = tileEntityIn instanceof KineticTileEntity;

        if (currentBlock == block && currentMeta == meta) return;
        if (tileEntityIn == null || !isKinetic) {
            world.setBlock(x, y, z, block, meta, 3);
            return;
        }

        KineticTileEntity tileEntity = (KineticTileEntity) tileEntityIn;
        if (block instanceof KineticBlock
            && !((KineticBlock) block).areStatesKineticallyEquivalent(currentBlock, currentMeta, block, meta)) {
            if (tileEntity.hasNetwork()) tileEntity.getOrCreateNetwork()
                .remove(tileEntity);
            tileEntity.detachKinetics();
            tileEntity.removeSource();
        }

        world.setBlock(x, y, z, block, meta, 2);
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {}

    @SuppressWarnings("static-access")
	@Override
     public boolean addToTooltip(List<IChatComponent> tooltip, boolean isPlayerSneaking) {
	     boolean notFastEnough = !isSpeedRequirementFulfilled() && getSpeed() != 0;
	    
	     if (overStressed && AllConfigs.CLIENT.enableOverstressedTooltip) {
			 IChatComponent addition = TextHelper.plainCopy(componentSpacing)
				 .appendSibling(Lang.translate("gui.stressometer.overstressed"));
			 addition.getChatStyle().setColor(EnumChatFormatting.GOLD);
		     tooltip.add(addition);
		     IChatComponent hint = Lang.translate("gui.contraptions.network_overstressed");
		     List<IChatComponent> cutString = TooltipHelper.cutTextComponent(hint, EnumChatFormatting.GRAY, EnumChatFormatting.WHITE);
		     for (int i = 0; i < cutString.size(); i++)
		     tooltip.add(TextHelper.plainCopy(componentSpacing).appendSibling(cutString.get(i)));
		     return true;
	     }
	    
	     if (notFastEnough) {
	    	 IChatComponent speedRequirement = Lang.translate("tooltip.speedRequirement");
	    	 speedRequirement.getChatStyle().setColor(EnumChatFormatting.GOLD);
		     tooltip.add(TextHelper.plainCopy(componentSpacing).appendSibling(speedRequirement));
		     IChatComponent hint = Lang.translate("gui.contraptions.not_fast_enough", StatCollector.translateToLocal(this.blockType.getLocalizedName()));
		     List<IChatComponent> cutString = TooltipHelper.cutTextComponent(hint, EnumChatFormatting.GRAY, EnumChatFormatting.WHITE);
		     for (int i = 0; i < cutString.size(); i++)
		     tooltip.add(TextHelper.plainCopy(componentSpacing)
		     .appendSibling(cutString.get(i)));
		     return true;
	     }
	    
	     return false;
     }
    
     @Override
     public boolean addToGoggleTooltip(List<IChatComponent> tooltip, boolean isPlayerSneaking) {
	     boolean added = false;
	     float stressAtBase = calculateStressApplied();
	    
	     if (calculateStressApplied() != 0 && StressImpact.isEnabled()) {
		     tooltip.add(TextHelper.plainCopy(componentSpacing).appendSibling(Lang.translate("gui.goggles.kinetic_stats")));
		     IChatComponent stressImpact = Lang.translate("tooltip.stressImpact");
		     stressImpact.getChatStyle().setColor(EnumChatFormatting.GRAY);
		     tooltip.add(TextHelper.plainCopy(componentSpacing).appendSibling(stressImpact));
		    
		     float stressTotal = stressAtBase * Math.abs(getTheoreticalSpeed());
		    
		     IChatComponent stressComponent = new ChatComponentText(" " + IHaveGoggleInformation.format(stressTotal))
			     .appendSibling(Lang.translate("generic.unit.stress")).appendText(" ");
		     stressComponent.getChatStyle().setColor(EnumChatFormatting.AQUA);
		     IChatComponent gogglesAtCurrentSpeed = Lang.translate("gui.goggles.at_current_speed");
		     gogglesAtCurrentSpeed.getChatStyle().setColor(EnumChatFormatting.DARK_GRAY);
		     tooltip.add(TextHelper.plainCopy(componentSpacing).appendSibling(stressComponent)
	    		 .appendSibling(gogglesAtCurrentSpeed));
		    
		     added = true;
	     }
    
	     return added;
     }

    public void clearKineticInformation() {
        speed = 0;
        sourceX = null;
        sourceY = null;
        sourceZ = null;
        network = null;
        overStressed = false;
        stress = 0;
        capacity = 0;
        lastStressApplied = 0;
        lastCapacityProvided = 0;
    }

    public void warnOfMovement() {
        wasMoved = true;
    }

    public int getFlickerScore() {
        return flickerTally;
    }

    public static float convertToDirection(float axisSpeed, Direction d) {
        return d.getAxisDirection() == AxisDirection.POSITIVE ? axisSpeed : -axisSpeed;
    }

    public static float convertToLinear(float speed) {
        return speed / 512f;
    }

    public static float convertToAngular(float speed) {
        return speed * 3 / 10f;
    }

    public boolean isOverStressed() {
        return overStressed;
    }

    // Custom Propagation

    /**
     * Specify ratio of transferred rotation from this kinetic component to a
     * specific other.
     *
     * @param target           other Kinetic TE to transfer to
     * @param diff             difference in position (to.pos - from.pos)
     * @param connectedViaAxes whether these kinetic blocks are connected via mutual
     *                         IRotate.hasShaftTowards()
     * @param connectedViaCogs whether these kinetic blocks are connected via mutual
     *                         IRotate.hasIntegratedCogwheel()
     * @return factor of rotation speed from this TE to other. 0 if no rotation is
     *         transferred, or the standard rules apply (integrated shafts/cogs)
     */
    public float propagateRotationTo(KineticTileEntity target, Vec3 diff, boolean connectedViaAxes,
        boolean connectedViaCogs) {
        return 0;
    }

    /**
     * Specify additional locations the rotation propagator should look for
     * potentially connected components. Neighbour list contains offset positions in
     * all 6 directions by default.
     *
     * @param block
     * @param meta
     * @param neighbours
     * @return
     */
    public List<ChunkCoordinates> addPropagationLocations(IRotate block, int meta, List<ChunkCoordinates> neighbours) {
        if (!canPropagateDiagonally(block)) return neighbours;

        Axis axis = block.getAxis(meta);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; x <= 1; y++) {
                for (int z = -1; x <= 1; z++) {
                    if (axis.choose(x, y, z) != 0) continue;
                    if (Vec3.createVectorHelper(x, y, z)
                        .squareDistanceTo(0, 0, 0)
                        != Vec3.createVectorHelper(0, 0, 0)
                            .squareDistanceTo(1, 1, 0))
                        continue;
                    neighbours.add(new ChunkCoordinates(this.xCoord + x, this.yCoord + y, this.zCoord + z));
                }
            }
        }
        return neighbours;
    }

    /**
     * Specify whether this component can propagate speed to the other in any
     * circumstance. Shaft and cogwheel connections are already handled by internal
     * logic. Does not have to be specified on both ends, it is assumed that this
     * relation is symmetrical.
     * 
     * Originally it required a BlockState. But we can get both Block and its meta from TileEntity.
     *
     * @param other
     * @return true if this and the other component should check their propagation
     *         factor and are not already connected via integrated cogs or shafts
     */
    public boolean isCustomConnection(KineticTileEntity other) {
        return false;
    }

    protected boolean canPropagateDiagonally(IRotate block) {
        return ICogWheel.isSmallCog((Block) block);
    }

    // @Override
    // public void requestModelDataUpdate() {
    // super.requestModelDataUpdate();
    // if (!this.remove)
    // DistExecutor.unsafeRunWhenOn(Side.CLIENT, () -> () -> InstancedRenderDispatcher.enqueueUpdate(this));
    // }

    protected AxisAlignedBB cachedBoundingBox;

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        if (cachedBoundingBox == null) {
            cachedBoundingBox = makeRenderBoundingBox();
        }
        return cachedBoundingBox;
    }

    protected AxisAlignedBB makeRenderBoundingBox() {
        return super.getRenderBoundingBox();
    }

    // TODO
    // @SideOnly(Side.CLIENT)
    // public void tickAudio() {
    // float componentSpeed = Math.abs(getSpeed());
    // if (componentSpeed == 0)
    // return;
    // float pitch = MathHelper.clamp_float((componentSpeed / 256f) + .45f, .85f, 1f);
    //
    // if (isNoisy())
    // SoundScapes.play(AmbienceGroup.KINETIC, worldPosition, pitch);
    //
    // Block block = this.blockType;
    // if (ICogWheel.isSmallCog(block) || ICogWheel.isLargeCog(block)/* || block instanceof GearboxBlock*/)
    // SoundScapes.play(AmbienceGroup.COG, worldPosition, pitch);
    // }

    protected boolean isNoisy() {
        return true;
    }

}
