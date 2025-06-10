package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.bearing;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.api.SubWorld;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.AssemblyException;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.Contraption;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionType;
import su.sergiusonesimus.recreate.util.Direction;

public class StabilizedContraption extends Contraption {

    private Direction facing;

    public StabilizedContraption() {
        super();
    }

    public StabilizedContraption(Direction facing) {
        super();
        this.facing = facing;
    }

    @Override
    public boolean assemble(World world, int x, int y, int z) throws AssemblyException {
        ChunkCoordinates normal = facing.getNormal();
        if (!searchMovedStructure(world, x + normal.posX, y + normal.posY, z + normal.posZ, null)) return false;
        // TODO
        // startMoving(world);
        if (((SubWorld) this.contraptionWorld).isEmpty()) return false;
        return true;
    }

    @Override
    protected boolean isAnchoringBlockAt(int x, int y, int z) {
        return false;
    }

    @Override
    protected ContraptionType getType() {
        return ContraptionType.STABILIZED;
    }

    @Override
    public NBTTagCompound writeNBT(boolean spawnPacket) {
        NBTTagCompound tag = super.writeNBT(spawnPacket);
        tag.setInteger("Facing", facing.get3DDataValue());
        return tag;
    }

    @Override
    public void readNBT(NBTTagCompound tag, boolean spawnData) {
        facing = Direction.from3DDataValue(tag.getInteger("Facing"));
        super.readNBT(tag, spawnData);
    }

    @Override
    public boolean canBeStabilized(Direction facing, int localX, int localY, int localZ) {
        return false;
    }

    public Direction getFacing() {
        return facing;
    }

    @Override
    public String getSubWorldType() {
        // TODO Auto-generated method stub
        return null;
    }
}
