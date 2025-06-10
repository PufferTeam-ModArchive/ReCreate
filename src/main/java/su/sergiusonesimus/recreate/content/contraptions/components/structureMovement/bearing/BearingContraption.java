package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.bearing;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.AllTags.AllBlockTags;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.AllSubWorldTypes;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.AssemblyException;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.Contraption;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionType;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionWorld;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.util.Direction;

public class BearingContraption extends Contraption {

    protected int sailBlocks;
    protected Direction facing;

    private boolean isWindmill;

    public BearingContraption() {
        super();
    }

    public BearingContraption(boolean isWindmill, Direction facing) {
        super();
        this.isWindmill = isWindmill;
        this.facing = facing;
    }

    @Override
    public boolean assemble(World world, int x, int y, int z) throws AssemblyException {
        ChunkCoordinates normal = facing.getNormal();
        if (!searchMovedStructure(world, x + normal.posX, y + normal.posY, z + normal.posZ, null)) return false;
        startMoving(world);
        if (isWindmill && sailBlocks < AllConfigs.SERVER.kinetics.minimumWindmillSails)
            throw AssemblyException.notEnoughSails(sailBlocks);
        if (this.blocks.isEmpty()) return false;
        return true;
    }

    @Override
    protected ContraptionType getType() {
        return ContraptionType.BEARING;
    }

    @Override
    protected boolean isAnchoringBlockAt(int x, int y, int z) {
        ChunkCoordinates relativeAnchor = facing.getOpposite()
            .getNormal();
        relativeAnchor.posX += anchorX;
        relativeAnchor.posY += anchorY;
        relativeAnchor.posZ += anchorZ;
        return relativeAnchor.posX == x && relativeAnchor.posY == y && relativeAnchor.posZ == z;
    }

    @Override
    public void addBlock(World world, int x, int y, int z) {
        ChunkCoordinates localPos = this.toLocalPos(x, y, z);
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (!getBlocks().contains(localPos) && AllBlockTags.WINDMILL_SAILS.matches(block, meta)) sailBlocks++;
        super.addBlock(world, x, y, z);
    }

    @Override
    public NBTTagCompound writeNBT(boolean spawnPacket) {
        NBTTagCompound tag = super.writeNBT(spawnPacket);
        tag.setInteger("Sails", sailBlocks);
        tag.setInteger("Facing", facing.get3DDataValue());
        return tag;
    }

    @Override
    public void readNBT(NBTTagCompound tag, boolean spawnData) {
        sailBlocks = tag.getInteger("Sails");
        facing = Direction.from3DDataValue(tag.getInteger("Facing"));
        super.readNBT(tag, spawnData);
    }

    public int getSailBlocks() {
        return sailBlocks;
    }

    public Direction getFacing() {
        return facing;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    @Override
    public boolean canBeStabilized(Direction facing, int localX, int localY, int localZ) {
        if (facing.getOpposite() == this.facing && localX == 0 && localY == 0 && localZ == 0) return false;
        return facing.getAxis() == this.facing.getAxis();
    }

    public boolean isStalled() {
        return getContraptionWorld().isStalled();
    }

    public void setAngle(float angle) {
        ContraptionWorld contraption = this.getContraptionWorld();
        double currentAngle = 0;
        switch (facing.getAxis()) {
            case X:
                currentAngle = contraption.getRotationRoll();
                break;
            case Y:
                currentAngle = contraption.getRotationYaw();
                break;
            case Z:
                currentAngle = contraption.getRotationPitch();
                break;
        }
        // A small fix to marry Create's 360 degree system with MetaWorlds' unlimited system
        if (Math.abs(currentAngle) > 360 || (currentAngle * angle <= 0))
            angle += Math.floor(currentAngle / 360 + (angle < 0 ? 1 : 0)) * 360;
        switch (facing.getAxis()) {
            case X:
                contraption.setRotationRoll(angle);
                break;
            case Y:
                contraption.setRotationYaw(angle);
                break;
            case Z:
                contraption.setRotationPitch(angle);
                break;
        }
    }

    public double getAngle() {
        ContraptionWorld contraption = this.getContraptionWorld();
        switch (facing.getAxis()) {
            case X:
                return contraption.getRotationRoll();
            case Y:
                return contraption.getRotationYaw();
            case Z:
                return contraption.getRotationPitch();
            default:
                return 0;
        }
    }

    public void setSpeed(float speed) {
        ContraptionWorld contraption = this.getContraptionWorld();
        switch (facing.getAxis()) {
            case X:
                contraption.setRotationRollSpeed(speed);
                break;
            case Y:
                contraption.setRotationYawSpeed(speed);
                break;
            case Z:
                contraption.setRotationPitchSpeed(speed);
                break;
        }
    }

    @Override
    public String getSubWorldType() {
        return AllSubWorldTypes.SUBWORLD_TYPE_CONTRAPTION_BEARING;
    }
}
