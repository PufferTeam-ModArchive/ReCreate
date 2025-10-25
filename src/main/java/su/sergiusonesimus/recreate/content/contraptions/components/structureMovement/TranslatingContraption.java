package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;

public abstract class TranslatingContraption extends ControlledContraption {

    protected Set<ChunkCoordinates> cachedColliders;
    protected Direction cachedColliderDirection;

    public TranslatingContraption() {
        super();
    }

    public TranslatingContraption(World parentWorld, IControlContraption controller, Axis rotationAxis) {
        super(parentWorld, controller, rotationAxis);
    }

    public Set<ChunkCoordinates> getColliders(World world, Direction movementDirection) {
        if (getBlocks() == null || contraptionWorld == null) return Collections.emptySet();
        if (cachedColliders == null || cachedColliderDirection != movementDirection) {
            cachedColliders = new HashSet<ChunkCoordinates>();
            cachedColliderDirection = movementDirection;
            ChunkCoordinates normal = movementDirection.getNormal();

            for (ChunkCoordinates blockPos : getBlocks()) {
                Block block = contraptionWorld.getBlock(blockPos.posX, blockPos.posY, blockPos.posZ);
                List<AxisAlignedBB> collisionBoxes = new ArrayList<AxisAlignedBB>();
                block.addCollisionBoxesToList(
                    contraptionWorld,
                    blockPos.posX,
                    blockPos.posY,
                    blockPos.posZ,
                    block
                        .getCollisionBoundingBoxFromPool(contraptionWorld, blockPos.posX, blockPos.posY, blockPos.posZ),
                    collisionBoxes,
                    null);
                if (collisionBoxes.isEmpty()) continue;
                ChunkCoordinates offsetPos = new ChunkCoordinates(blockPos);
                offsetPos.posX += normal.posX;
                offsetPos.posY += normal.posY;
                offsetPos.posZ += normal.posZ;
                if (getBlocks().contains(offsetPos)) {
                    block = contraptionWorld.getBlock(offsetPos.posX, offsetPos.posY, offsetPos.posZ);
                    collisionBoxes.clear();
                    block.addCollisionBoxesToList(
                        contraptionWorld,
                        offsetPos.posX,
                        offsetPos.posY,
                        offsetPos.posZ,
                        block.getCollisionBoundingBoxFromPool(
                            contraptionWorld,
                            offsetPos.posX,
                            offsetPos.posY,
                            offsetPos.posZ),
                        collisionBoxes,
                        null);
                    if (!collisionBoxes.isEmpty()) continue;
                }
                cachedColliders.add(blockPos);
            }

        }
        return cachedColliders;
    }

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
