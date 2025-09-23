package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.metaworlds.util.Direction.AxisDirection;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.AllSounds;
import su.sergiusonesimus.recreate.content.contraptions.base.IRotate;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.AssemblyException;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionCollider;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.DirectionalExtenderScrollOptionSlot;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston.MechanicalPistonBlock.PistonState;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.ValueBoxTransform;
import su.sergiusonesimus.recreate.foundation.utility.ServerSpeedProvider;

public class MechanicalPistonTileEntity extends LinearActuatorTileEntity {

    public PistonState state = PistonState.RETRACTED;

    protected boolean hadCollisionWithOtherPiston;
    protected int extensionLength;

    public MechanicalPistonTileEntity() {
        super();
    }

    @Override
    protected void fromTag(NBTTagCompound compound, boolean clientPacket) {
        extensionLength = compound.getInteger("ExtensionLength");
        state = PistonState.values()[compound.getInteger("PistonState")];
        super.fromTag(compound, clientPacket);
    }

    @Override
    protected void write(NBTTagCompound tag, boolean clientPacket) {
        tag.setInteger("ExtensionLength", extensionLength);
        tag.setInteger("PistonState", state.ordinal());
        super.write(tag, clientPacket);
    }

    @Override
    public void assemble() throws AssemblyException {
        if (!(worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) instanceof MechanicalPistonBlock piston)) return;

        Direction direction = piston.getDirection(this.getBlockMetadata());

        // Collect Construct
        PistonContraption contraption = new PistonContraption(this.getWorld(), this, direction, getMovementSpeed() < 0);
        if (!contraption.assemble(worldObj, this.xCoord, this.yCoord, this.zCoord)) return;

        Direction positive = Direction.get(AxisDirection.POSITIVE, direction.getAxis());
        Direction movementDirection = getSpeed() > 0 ^ direction.getAxis() != Axis.Z ? positive
            : positive.getOpposite();

        if (ContraptionCollider.isCollidingWithWorld(worldObj, contraption, movementDirection)) return;

        // Check if not at limit already
        extensionLength = contraption.extensionLength;
        float resultingOffset = contraption.initialExtensionProgress + Math.signum(getMovementSpeed()) * .5f;
        if (resultingOffset <= 0 || resultingOffset >= extensionLength) {
            return;
        }

        // Run
        running = true;
        offset = contraption.initialExtensionProgress;
        sendData();
        clientOffsetDiff = 0;

        // TODO Also might be a source of problems
        contraption.removeBlocksFromWorld(worldObj);
        movedContraption = contraption;
        resetContraptionToOffset();
        forceMove = true;

        AllSounds.CONTRAPTION_ASSEMBLE.playOnServer(worldObj, this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public void disassemble() {
        if (!running && movedContraption == null) return;
        if (!isInvalid()) this.state = PistonState.EXTENDED;
        if (movedContraption != null) {
            resetContraptionToOffset();
            movedContraption.disassemble();
            AllSounds.CONTRAPTION_DISASSEMBLE.playOnServer(worldObj, this.xCoord, this.yCoord, this.zCoord);
        }
        running = false;
        movedContraption = null;
        sendData();

        if (isInvalid()) AllBlocks.mechanical_piston
            .onBlockHarvested(worldObj, this.xCoord, this.yCoord, this.zCoord, this.getBlockMetadata(), null);
    }

    @Override
    protected void collided() {
        super.collided();
        if (!running && getMovementSpeed() > 0) assembleNextTick = true;
    }

    @Override
    public float getMovementSpeed() {
        float movementSpeed = MathHelper.clamp_float(convertToLinear(getSpeed()), -.49f, .49f);
        if (worldObj.isRemote) movementSpeed *= ServerSpeedProvider.get();
        Direction pistonDirection = ((MechanicalPistonBlock) getBlockType()).getDirection(getBlockMetadata());
        int movementModifier = pistonDirection.getAxisDirection()
            .getStep() * (pistonDirection.getAxis() == Axis.Z ? -1 : 1);
        movementSpeed = movementSpeed * -movementModifier + clientOffsetDiff / 2f;

        int extensionRange = getExtensionRange();
        movementSpeed = MathHelper.clamp_float(movementSpeed, 0 - offset, extensionRange - offset);
        return movementSpeed;
    }

    @Override
    protected int getExtensionRange() {
        return extensionLength;
    }

    @Override
    protected void visitNewPosition() {}

    @Override
    protected Vec3 toMotionVector(float speed) {
        Direction pistonDirection = ((MechanicalPistonBlock) getBlockType()).getDirection(getBlockMetadata());
        ChunkCoordinates normal = pistonDirection.getNormal();
        return Vec3.createVectorHelper(normal.posX * speed, normal.posY * speed, normal.posZ * speed);
    }

    @Override
    protected Vec3 toPosition(float offset) {
        Direction pistonDirection = ((MechanicalPistonBlock) getBlockType()).getDirection(getBlockMetadata());
        ChunkCoordinates normal = pistonDirection.getNormal();
        Vec3 position = Vec3
            .createVectorHelper(normal.posX * (offset + 1), normal.posY * (offset + 1), normal.posZ * (offset + 1));
        return position.addVector(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D);
    }

    @Override
    protected ValueBoxTransform getMovementModeSlot() {
        return new DirectionalExtenderScrollOptionSlot((state, d) -> {
            Axis axis = d.getAxis();
            Axis extensionAxis = ((MechanicalPistonBlock) getBlockType()).getAxis(getBlockMetadata());
            Axis shaftAxis = ((IRotate) state.getFirst()).getAxis(state.getSecond());
            return extensionAxis != axis && shaftAxis != axis;
        });
    }

    @Override
    protected int getInitialOffset() {
        return movedContraption == null ? 0 : ((PistonContraption) movedContraption).initialExtensionProgress;
    }

}
