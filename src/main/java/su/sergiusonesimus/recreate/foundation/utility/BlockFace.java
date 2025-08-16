package su.sergiusonesimus.recreate.foundation.utility;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import su.sergiusonesimus.metaworlds.util.Direction;

public class BlockFace extends Pair<ChunkCoordinates, Direction> {

    public BlockFace(int x, int y, int z, int side) {
        this(new ChunkCoordinates(x, y, z), Direction.from3DDataValue(side));
    }

    public BlockFace(int x, int y, int z, Direction second) {
        this(new ChunkCoordinates(x, y, z), second);
    }

    public BlockFace(ChunkCoordinates first, Direction second) {
        super(first, second);
    }

    public boolean isEquivalent(BlockFace other) {
        if (equals(other)) return true;
        return getConnectedPos().equals(other.getPos()) && getPos().equals(other.getConnectedPos());
    }

    public ChunkCoordinates getPos() {
        return getFirst();
    }

    public Direction getFace() {
        return getSecond();
    }

    public Direction getOppositeFace() {
        return getSecond().getOpposite();
    }

    public BlockFace getOpposite() {
        return new BlockFace(getConnectedPos(), getOppositeFace());
    }

    public ChunkCoordinates getConnectedPos() {
        ChunkCoordinates pos = getPos();
        ChunkCoordinates result = new ChunkCoordinates(pos.posX, pos.posY, pos.posZ);
        ChunkCoordinates normal = getFace().getNormal();
        result.posX += normal.posX;
        result.posY += normal.posY;
        result.posZ += normal.posZ;
        return result;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound compoundNBT = new NBTTagCompound();
        ChunkCoordinates pos = getPos();
        compoundNBT.setIntArray("Pos", new int[] { pos.posX, pos.posY, pos.posZ });
        NBTHelper.writeEnum(compoundNBT, "Face", getFace());
        return compoundNBT;
    }

    public static BlockFace fromNBT(NBTTagCompound compound) {
        int[] pos = compound.getIntArray("Pos");
        return new BlockFace(
            new ChunkCoordinates(pos[0], pos[1], pos[2]),
            NBTHelper.readEnum(compound, "Face", Direction.class));
    }

}
