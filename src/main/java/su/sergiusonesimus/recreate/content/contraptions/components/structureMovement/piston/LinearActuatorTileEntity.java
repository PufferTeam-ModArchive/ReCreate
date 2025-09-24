package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.world.IMixinWorld;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.AssemblyException;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.Contraption;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionCollider;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionWorld;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.IControlContraption;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.IDisplayAssemblyExceptions;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.TranslatingContraption;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.ValueBoxTransform;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue.ScrollOptionBehaviour;
import su.sergiusonesimus.recreate.foundation.utility.Lang;
import su.sergiusonesimus.recreate.foundation.utility.ServerSpeedProvider;
import su.sergiusonesimus.recreate.util.VecHelper;

public abstract class LinearActuatorTileEntity extends KineticTileEntity
    implements IControlContraption, IDisplayAssemblyExceptions {

    public float offset;
    public boolean running;
    public boolean assembleNextTick;
    public boolean needsContraption;
    public Contraption movedContraption;
    protected Integer contraptionWorldID;
    protected boolean forceMove;
    protected ScrollOptionBehaviour<MovementMode> movementMode;
    protected boolean waitingForSpeedChange;
    protected AssemblyException lastException;

    // Custom position sync
    protected float clientOffsetDiff;

    public LinearActuatorTileEntity() {
        super();
        setLazyTickRate(3);
        forceMove = true;
        needsContraption = true;
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        movementMode = new ScrollOptionBehaviour<>(
            MovementMode.class,
            Lang.translate("contraptions.movement_mode"),
            this,
            getMovementModeSlot());
        movementMode.requiresWrench();
        movementMode.withCallback(t -> waitingForSpeedChange = false);
        behaviours.add(movementMode);
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

        // TODO This may work wrongly, might return to this later
        if (movedContraption != null && movedContraption.beingRemoved) movedContraption = null;

        if (worldObj.isRemote) clientOffsetDiff *= .75f;

        if (waitingForSpeedChange) {
            if (movedContraption != null) {
                if (worldObj.isRemote) {
                    float syncSpeed = clientOffsetDiff / 2f;
                    offset += syncSpeed;
                    movedContraption.setMotion(toMotionVector(syncSpeed));
                    return;
                }
                movedContraption.setMotion(VecHelper.ZERO);
            }
            return;
        }

        if (!worldObj.isRemote && assembleNextTick) {
            assembleNextTick = false;
            if (running) {
                if (getSpeed() == 0) tryDisassemble();
                else sendData();
                return;
            } else {
                if (getSpeed() != 0) try {
                    assemble();
                    lastException = null;
                } catch (AssemblyException e) {
                    lastException = e;
                }
                sendData();
            }
            return;
        }

        if (!running) return;

        boolean contraptionPresent = movedContraption != null;
        if (needsContraption && !contraptionPresent) return;

        float movementSpeed = getMovementSpeed();
        float newOffset = offset + movementSpeed;
        if ((int) newOffset != (int) offset) visitNewPosition();

        if (contraptionPresent) {
            if (moveAndCollideContraption()) {
                movedContraption.setMotion(VecHelper.ZERO);
                offset = getGridOffset(offset);
                resetContraptionToOffset();
                collided();
                return;
            }
        }

        if (!contraptionPresent || !movedContraption.stalled) offset = newOffset;

        int extensionRange = getExtensionRange();
        if (offset <= 0 || offset >= extensionRange) {
            offset = offset <= 0 ? 0 : extensionRange;
            if (!worldObj.isRemote) {
                moveAndCollideContraption();
                resetContraptionToOffset();
                tryDisassemble();
                if (waitingForSpeedChange) {
                    forceMove = true;
                    sendData();
                }
            }
            return;
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (movedContraption != null && !worldObj.isRemote) sendData();
    }

    protected int getGridOffset(float offset) {
        return MathHelper.clamp_int((int) (offset + .5f), 0, getExtensionRange());
    }

    public float getInterpolatedOffset(float partialTicks) {
        float interpolatedOffset = MathHelper
            .clamp_float(offset + (partialTicks - .5f) * getMovementSpeed(), 0, getExtensionRange());
        return interpolatedOffset;
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        assembleNextTick = true;
        waitingForSpeedChange = false;

        if (movedContraption != null && Math.signum(prevSpeed) != Math.signum(getSpeed()) && prevSpeed != 0) {
            movedContraption.stop(worldObj);
        }
    }

    @Override
    protected void setRemovedNotDueToChunkUnload() {
        this.tileEntityInvalid = true;
        if (!worldObj.isRemote) disassemble();
        super.setRemovedNotDueToChunkUnload();
    }

    @Override
    protected void write(NBTTagCompound compound, boolean clientPacket) {
        compound.setBoolean("Running", running);
        compound.setBoolean("Waiting", waitingForSpeedChange);
        compound.setFloat("Offset", offset);
        AssemblyException.write(compound, lastException);
        super.write(compound, clientPacket);

        if (clientPacket && forceMove) {
            compound.setBoolean("ForceMovement", forceMove);
            forceMove = false;
        }
    }

    @Override
    protected void fromTag(NBTTagCompound compound, boolean clientPacket) {
        boolean forceMovement = compound.hasKey("ForceMovement");
        float offsetBefore = offset;

        running = compound.getBoolean("Running");
        waitingForSpeedChange = compound.getBoolean("Waiting");
        offset = compound.getFloat("Offset");
        lastException = AssemblyException.read(compound);
        super.fromTag(compound, clientPacket);

        if (!clientPacket) return;
        if (forceMovement) resetContraptionToOffset();
        else if (running) {
            clientOffsetDiff = offset - offsetBefore;
            offset = offsetBefore;
        }
        if (!running) movedContraption = null;
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return lastException;
    }

    public abstract void disassemble();

    protected abstract void assemble() throws AssemblyException;

    protected abstract int getExtensionRange();

    protected abstract int getInitialOffset();

    protected abstract ValueBoxTransform getMovementModeSlot();

    protected abstract Vec3 toMotionVector(float speed);

    protected abstract Vec3 toPosition(float offset);

    protected void visitNewPosition() {}

    protected void tryDisassemble() {
        if (isInvalid()) {
            disassemble();
            return;
        }
        if (movementMode.get() == MovementMode.MOVE_NEVER_PLACE) {
            waitingForSpeedChange = true;
            return;
        }
        int initial = getInitialOffset();
        if ((int) (offset + .5f) != initial && movementMode.get() == MovementMode.MOVE_PLACE_RETURNED) {
            waitingForSpeedChange = true;
            return;
        }
        disassemble();
    }

    protected boolean moveAndCollideContraption() {
        if (movedContraption == null) return false;
        if (movedContraption.stalled) {
            movedContraption.setMotion(VecHelper.ZERO);
            return false;
        }

        movedContraption.setMotion(getMotionVector());
        return ContraptionCollider.collideBlocks((TranslatingContraption) movedContraption);
    }

    protected void collided() {
        if (worldObj.isRemote) {
            waitingForSpeedChange = true;
            return;
        }
        offset = getGridOffset(offset - getMovementSpeed());
        resetContraptionToOffset();
        tryDisassemble();
    }

    protected void resetContraptionToOffset() {
        if (movedContraption == null) return;
        Vec3 vec = toPosition(offset);
        movedContraption.setPosition(vec);
        if (getSpeed() == 0 || waitingForSpeedChange) movedContraption.setMotion(VecHelper.ZERO);
    }

    public float getMovementSpeed() {
        float movementSpeed = MathHelper.clamp_float(convertToLinear(getSpeed()), -.49f, .49f) + clientOffsetDiff / 2f;
        if (worldObj.isRemote) movementSpeed *= ServerSpeedProvider.get();
        return movementSpeed;
    }

    public Vec3 getMotionVector() {
        return toMotionVector(getMovementSpeed());
    }

    @Override
    public void onStall() {
        if (!worldObj.isRemote) {
            forceMove = true;
            sendData();
        }
    }

    public void onLengthBroken() {
        offset = 0;
        sendData();
    }

    @Override
    public void attach(Contraption contraption) {
        this.movedContraption = contraption;
        if (!worldObj.isRemote) {
            this.running = true;
            sendData();
        }
    }

    @Override
    public boolean isAttachedTo(Contraption contraption) {
        return movedContraption == contraption;
    }

    @Override
    public boolean isValid() {
        return !isInvalid();
    }

    @Override
    public ChunkCoordinates getPosition() {
        return new ChunkCoordinates(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public int getPositionX() {
        return this.xCoord;
    }

    @Override
    public int getPositionY() {
        return this.yCoord;
    }

    @Override
    public int getPositionZ() {
        return this.zCoord;
    }
}
