package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import java.util.Set;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;

public abstract class TranslatingContraption extends Contraption {

    protected Set<ChunkCoordinates> cachedColliders;
    protected Direction cachedColliderDirection;

    public TranslatingContraption() {
        super();
    }

    // TODO
    // public Set<ChunkCoordinates> getColliders(World world, Direction movementDirection) {
    // if (getBlocks() == null)
    // return Collections.emptySet();
    // if (cachedColliders == null || cachedColliderDirection != movementDirection) {
    // cachedColliders = new HashSet<>();
    // cachedColliderDirection = movementDirection;
    //
    // for (StructureBlockInfo info : getBlocks().values()) {
    // ChunkCoordinates offsetPos = info.pos.relative(movementDirection);
    // if (info.state.getCollisionShape(world, offsetPos)
    // .isEmpty())
    // continue;
    // if (getBlocks().containsKey(offsetPos)
    // && !getBlocks().get(offsetPos).state.getCollisionShape(world, offsetPos)
    // .isEmpty())
    // continue;
    // cachedColliders.add(info.pos);
    // }
    //
    // }
    // return cachedColliders;
    // }

    @Override
    public void removeBlocksFromWorld(World world, int offsetX, int offsetY, int offsetZ) {
        int count = blocks.size();
        super.removeBlocksFromWorld(world, offsetX, offsetY, offsetZ);
        if (count != blocks.size()) {
            cachedColliders = null;
        }
    }

    @Override
    public boolean canBeStabilized(Direction facing, int localPosX, int localPosY, int localPosZ) {
        return false;
    }

}
