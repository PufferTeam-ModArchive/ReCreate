package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.bearing;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.world.IMixinWorld;
import su.sergiusonesimus.recreate.AllSounds;
import su.sergiusonesimus.recreate.content.contraptions.base.DirectionalKineticBlock;
import su.sergiusonesimus.recreate.content.contraptions.base.GeneratingKineticTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.AssemblyException;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.Contraption;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionWorld;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.IDisplayAssemblyExceptions;
import su.sergiusonesimus.recreate.foundation.item.TooltipHelper;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue.ScrollOptionBehaviour;
import su.sergiusonesimus.recreate.foundation.utility.AngleHelper;
import su.sergiusonesimus.recreate.foundation.utility.Lang;
import su.sergiusonesimus.recreate.foundation.utility.ServerSpeedProvider;
import su.sergiusonesimus.recreate.util.ReCreateMath;

public class MechanicalBearingTileEntity extends GeneratingKineticTileEntity
    implements IBearingTileEntity, IDisplayAssemblyExceptions {

    protected ScrollOptionBehaviour<RotationMode> movementMode;
    protected BearingContraption movedContraption;
    protected Integer contraptionWorldID;
    protected float angle;
    protected boolean running;
    protected boolean assembleNextTick;
    protected float clientAngleDiff;
    protected AssemblyException lastException;

    private float prevAngle;

    public MechanicalBearingTileEntity() {
        super();
        setLazyTickRate(3);
    }

    @Override
    public boolean isWoodenTop() {
        return false;
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        movementMode = new ScrollOptionBehaviour<>(
            RotationMode.class,
            Lang.translate("contraptions.movement_mode"),
            this,
            getMovementModeSlot());
        movementMode.requiresWrench();
        behaviours.add(movementMode);
    }

    @Override
    protected void setRemovedNotDueToChunkUnload() {
        if (!worldObj.isRemote) disassemble();
        super.setRemovedNotDueToChunkUnload();
    }

    @Override
    public void write(NBTTagCompound compound, boolean clientPacket) {
        compound.setBoolean("Running", running);
        compound.setFloat("Angle", angle);
        if (contraptionWorldID != null) compound.setInteger("ContraptionWorldID", contraptionWorldID);
        AssemblyException.write(compound, lastException);
        super.write(compound, clientPacket);
    }

    @Override
    protected void fromTag(NBTTagCompound compound, boolean clientPacket) {
        if (wasMoved) {
            super.fromTag(compound, clientPacket);
            return;
        }

        float angleBefore = angle;
        running = compound.getBoolean("Running");
        angle = compound.getFloat("Angle");
        if (compound.hasKey("ContraptionWorldID")) contraptionWorldID = compound.getInteger("ContraptionWorldID");
        lastException = AssemblyException.read(compound);
        super.fromTag(compound, clientPacket);
        if (!clientPacket) return;
        if (running) {
            clientAngleDiff = AngleHelper.getShortestAngleDiff(angleBefore, angle);
            angle = angleBefore;
        } else movedContraption = null;
    }

    @Override
    public float getInterpolatedAngle(float partialTicks) {
        if (isVirtual()) return ReCreateMath.lerp(partialTicks + .5f, prevAngle, angle);
        if (movedContraption == null || movedContraption.stalled || !running) partialTicks = 0;
        return ReCreateMath.lerp(partialTicks, angle, angle + getAngularSpeed());
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        assembleNextTick = true;

        if (movedContraption != null && Math.signum(prevSpeed) != Math.signum(getSpeed()) && prevSpeed != 0) {
            movedContraption.stop(worldObj);
        }
    }

    public float getAngularSpeed() {
        float speed = convertToAngular(isWindmill() ? getGeneratedSpeed() : getSpeed());
        if (getSpeed() == 0) speed = 0;
        if (worldObj.isRemote) {
            speed *= ServerSpeedProvider.get();
            speed += clientAngleDiff/* / 3f */;
        }
        return speed;
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return lastException;
    }

    protected boolean isWindmill() {
        return false;
    }

    public void assemble() {
        Block block = this.getBlockType();
        int meta = this.getBlockMetadata();
        if (!(block instanceof BearingBlock bearing)) return;

        Direction direction = bearing.getDirection(meta);
        movedContraption = new BearingContraption(this.getWorld(), this, isWindmill(), direction);
        try {
            if (!movedContraption.assemble(worldObj, xCoord, yCoord, zCoord)) return;

            lastException = null;
        } catch (AssemblyException e) {
            lastException = e;
            sendData();
            return;
        }

        // TODO
        // if (isWindmill())
        // AllTriggers.triggerForNearbyPlayers(AllTriggers.WINDMILL, worldObj, worldPosition, 5);
        // if (contraption.getSailBlocks() >= 16 * 8)
        // AllTriggers.triggerForNearbyPlayers(AllTriggers.MAXED_WINDMILL, worldObj, worldPosition, 5);

        movedContraption.removeBlocksFromWorld(worldObj);
        movedContraption.preInit();
        ChunkCoordinates offset = direction.getNormal();
        movedContraption.anchorX = xCoord + offset.posX;
        movedContraption.anchorY = yCoord + offset.posY;
        movedContraption.anchorZ = zCoord + offset.posZ;
        movedContraption.init();
        contraptionWorldID = movedContraption.getContraptionWorld()
            .getSubWorldID();

        AllSounds.CONTRAPTION_ASSEMBLE.playOnServer(worldObj, xCoord, yCoord, zCoord);

        running = true;
        angle = 0;
        sendData();
        updateGeneratedRotation();
    }

    public void disassemble() {
        if (movedContraption == null) return;
        movedContraption.onRemoved();
        angle = 0;
        if (isWindmill()) applyRotation();
        if (movedContraption != null) {
            movedContraption.disassemble();
            AllSounds.CONTRAPTION_DISASSEMBLE.playOnServer(worldObj, xCoord, yCoord, zCoord);
        }

        contraptionWorldID = null;
        movedContraption = null;
        running = false;
        updateGeneratedRotation();
        assembleNextTick = false;
        sendData();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (movedContraption == null && contraptionWorldID != null) {
            ContraptionWorld contraptionWorld = (ContraptionWorld) ((IMixinWorld) this.getWorldObj())
                .getSubWorld(contraptionWorldID);
            if (contraptionWorld != null && contraptionWorld.getContraption() != null) {
                this.attach(contraptionWorld.getContraption());
                movedContraption.tick();
            } else {
                contraptionWorldID = null;
            }
        }

        prevAngle = angle;
        if (worldObj.isRemote) clientAngleDiff /= 2;

        if (!worldObj.isRemote && assembleNextTick) {
            assembleNextTick = false;
            if (running) {
                boolean canDisassemble = movementMode.get() == RotationMode.ROTATE_PLACE
                    || (isNearInitialAngle() && movementMode.get() == RotationMode.ROTATE_PLACE_RETURNED);
                if (speed == 0 && (canDisassemble || movedContraption == null
                    || movedContraption.getBlocks()
                        .isEmpty())) {
                    if (movedContraption != null) movedContraption.stop(worldObj);
                    disassemble();
                    return;
                }
            } else {
                if (speed == 0 && !isWindmill()) return;
                assemble();
            }
        }

        if (!running) return;

        if (!(movedContraption != null && movedContraption.getContraptionWorld()
            .isStalled())) {
            float angularSpeed = getAngularSpeed();
            float newAngle = angle + angularSpeed;
            angle = (float) (newAngle % 360);
        }

        applyRotation();
    }

    public boolean isNearInitialAngle() {
        return Math.abs(angle) < 45 || Math.abs(angle) > 7 * 45;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (movedContraption != null && !worldObj.isRemote) sendData();
    }

    protected void applyRotation() {
        if (movedContraption == null || worldObj.isRemote) return;
        movedContraption.setAngle(angle);
        movedContraption.setSpeed(getAngularSpeed());
        Block block = this.getBlockType();
        int meta = this.getBlockMetadata();
        if (block instanceof DirectionalKineticBlock)
            movedContraption.setFacing(((DirectionalKineticBlock) block).getDirection(meta));
    }

    @Override
    public void attach(Contraption contraption) {
        Block block = this.getBlockType();
        int meta = this.getBlockMetadata();
        if (!(contraption instanceof BearingContraption)) return;
        if (!(block instanceof DirectionalKineticBlock)) return;

        this.movedContraption = (BearingContraption) contraption;
        markDirty();
        ChunkCoordinates offset = ((DirectionalKineticBlock) block).getDirection(meta)
            .getNormal();
        movedContraption.anchorX = xCoord + offset.posX;
        movedContraption.anchorY = yCoord + offset.posY;
        movedContraption.anchorZ = zCoord + offset.posZ;
        if (!worldObj.isRemote) {
            this.running = true;
            sendData();
        }
    }

    @Override
    public void onStall() {
        if (!worldObj.isRemote) sendData();
    }

    @Override
    public boolean isValid() {
        return !isInvalid();
    }

    @Override
    public boolean isAttachedTo(Contraption contraption) {
        return movedContraption == contraption;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean addToTooltip(List<IChatComponent> tooltip, boolean isPlayerSneaking) {
        if (super.addToTooltip(tooltip, isPlayerSneaking)) return true;
        if (isPlayerSneaking) return false;
        if (!isWindmill() && getSpeed() == 0) return false;
        if (running) return false;
        Block block = this.getBlockType();
        int meta = this.getBlockMetadata();
        if (!(block instanceof BearingBlock)) return false;

        ChunkCoordinates normal = ((BearingBlock) block).getDirection(meta)
            .getNormal();
        Block attachedBlock = worldObj.getBlock(xCoord + normal.posX, yCoord + normal.posY, zCoord + normal.posZ);
        if (attachedBlock.getMaterial()
            .isReplaceable()) return false;
        TooltipHelper.addHint(tooltip, "hint.empty_bearing");
        return true;
    }

    @Override
    public ChunkCoordinates getPosition() {
        return new ChunkCoordinates(xCoord, yCoord, zCoord);
    }

    @Override
    public int getPositionX() {
        return xCoord;
    }

    @Override
    public int getPositionY() {
        return yCoord;
    }

    @Override
    public int getPositionZ() {
        return zCoord;
    }

    @Override
    public void setAngle(float forcedAngle) {
        angle = forcedAngle;
        applyRotation();
    }

}
