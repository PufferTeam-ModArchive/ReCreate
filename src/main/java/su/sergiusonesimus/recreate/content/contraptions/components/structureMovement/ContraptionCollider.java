package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.AxisDirection;
import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.world.IMixinWorld;
import su.sergiusonesimus.recreate.AllMovementBehaviours;
import su.sergiusonesimus.recreate.content.contraptions.components.actors.BlockBreakingMovementBehaviour;
import su.sergiusonesimus.recreate.util.VecHelper;
import su.sergiusonesimus.recreate.zmixin.interfaces.IMixinWorldReCreate;

public class ContraptionCollider {

    public static boolean collideBlocks(TranslatingContraption contraption) {
        if (!contraption.supportsTerrainCollision() || contraption.parentWorld == null) return false;

        World world = ((IMixinWorld) contraption.parentWorld).getParentWorld();
        Vec3 motion = contraption.getMotion();
        AxisAlignedBB bounds = contraption.getBoundingBox();
        Vec3 position = contraption.getPosition();
        ChunkCoordinates gridPos = new ChunkCoordinates(
            MathHelper.floor_double(position.xCoord),
            MathHelper.floor_double(position.yCoord),
            MathHelper.floor_double(position.zCoord));

        if (contraption == null || bounds == null || motion.equals(VecHelper.ZERO)) return false;

        Direction movementDirection = Direction.getNearest(motion);

        // Blocks in the world
        if (movementDirection.getAxisDirection() == AxisDirection.POSITIVE) {
            ChunkCoordinates normal = movementDirection.getNormal();
            gridPos.posX += normal.posX;
            gridPos.posY += normal.posY;
            gridPos.posZ += normal.posZ;
        }
        if (isCollidingWithWorld(world, contraption, movementDirection)) return true;

        // Other moving Contraptions
        for (TranslatingContraption otherContraption : ((IMixinWorldReCreate) world).getContraptionsWithinAABB(
            TranslatingContraption.class,
            bounds.expand(1, 1, 1),
            c -> !c.equals(contraption))) {

            if (!otherContraption.supportsTerrainCollision()) continue;

            Vec3 otherMotion = otherContraption.getMotion();
            AxisAlignedBB otherBounds = otherContraption.getBoundingBox();

            if (otherMotion == null || otherBounds == null) continue;

            if (!bounds.offset(motion.xCoord, motion.yCoord, motion.zCoord)
                .intersectsWith(otherBounds.offset(otherMotion.xCoord, otherMotion.yCoord, otherMotion.zCoord)))
                continue;

            for (ChunkCoordinates colliderPos : contraption.getColliders(world, movementDirection)) {
                if (!otherContraption.getBlocks()
                    .contains(
                        otherContraption.getContraptionWorld()
                            .transformBlockToLocal(
                                contraption.getContraptionWorld()
                                    .transformBlockToGlobal(colliderPos))))
                    continue;
                return true;
            }
        }

        return false;
    }

    public static boolean isCollidingWithWorld(World world, TranslatingContraption contraption,
        Direction movementDirection) {
        if (contraption.contraptionWorld == null) return false;
        ChunkCoordinates normal = movementDirection.getNormal();
        for (ChunkCoordinates pos : contraption.getColliders(world, movementDirection)) {
            // TODO Might need to convert this position to local coordinates of another world
            Vec3 globalPos = contraption.getContraptionWorld()
                .transformToGlobal(pos.posX + 0.5D, pos.posY + 0.5D, pos.posZ + 0.5D)
                .addVector(normal.posX * 0.5D, normal.posY * 0.5D, normal.posZ * 0.5D);
            ChunkCoordinates colliderPos = new ChunkCoordinates(
                MathHelper.floor_double(globalPos.xCoord),
                MathHelper.floor_double(globalPos.yCoord),
                MathHelper.floor_double(globalPos.zCoord));

            if (!world.blockExists(colliderPos.posX, colliderPos.posY, colliderPos.posZ)) return true;
            if (world == contraption.parentWorld && colliderPos.posX == contraption.controllerX
                && colliderPos.posY == contraption.controllerY
                && colliderPos.posZ == contraption.controllerZ) continue;

            Block collidedBlock = world.getBlock(colliderPos.posX, colliderPos.posY, colliderPos.posZ);
            Block collidingBlock = contraption.contraptionWorld.getBlock(pos.posX, pos.posY, pos.posZ);

            if (AllMovementBehaviours.contains(collidingBlock)) {
                MovementBehaviour movementBehaviour = AllMovementBehaviours.of(collidingBlock);
                if (movementBehaviour instanceof BlockBreakingMovementBehaviour behaviour) {
                    // TODO
                    // if (!behaviour.canBreak(world, colliderPos, collidedBlock)
                    // && !collidedBlock.getCollisionShape(world, pos)
                    // .isEmpty()) {
                    // return true;
                    // }
                    continue;
                }
            }

            // TODO
            // if (AllBlocks.PULLEY_MAGNET.has(collidedBlock) && pos.equals(BlockPos.ZERO)
            // && movementDirection == Direction.UP) continue;
            if (collidedBlock == Blocks.cocoa) continue;
            if (!collidedBlock.getMaterial()
                .isReplaceable()) {
                List<AxisAlignedBB> collisionBoxes = new ArrayList<AxisAlignedBB>();
                collidedBlock.addCollisionBoxesToList(
                    world,
                    colliderPos.posX,
                    colliderPos.posY,
                    colliderPos.posZ,
                    collidedBlock
                        .getCollisionBoundingBoxFromPool(world, colliderPos.posX, colliderPos.posY, colliderPos.posZ),
                    collisionBoxes,
                    null);
                if (!collisionBoxes.isEmpty()) return true;
            }
        }
        return false;
    }

}
