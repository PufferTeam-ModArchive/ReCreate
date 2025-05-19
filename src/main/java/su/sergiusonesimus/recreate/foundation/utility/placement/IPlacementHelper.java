package su.sergiusonesimus.recreate.foundation.utility.placement;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.ClientProxy;
import su.sergiusonesimus.recreate.foundation.utility.Iterate;
import su.sergiusonesimus.recreate.foundation.utility.Pair;
import su.sergiusonesimus.recreate.util.Direction;
import su.sergiusonesimus.recreate.util.VecHelper;

public interface IPlacementHelper {

    /**
     * used as an identifier in SuperGlueHandler to skip blocks placed by helpers
     */
    Block BLOCK = Blocks.air;
    Integer META = 0;

    /**
     * @return a predicate that gets tested with the items held in the players hands<br>
     *         should return true if this placement helper is active with the given item
     */
    Predicate<ItemStack> getItemPredicate();

    /**
     * @return a predicate that gets tested with the block the player is looking at<br>
     *         should return true if this placement helper is active with the given block
     */
    Predicate<Pair<Block, Integer>> getBlockPredicate();

    /**
     *
     * @param player the player that activated the placement helper
     * @param world  the world that the placement helper got activated in
     * @param block  the Block that the player is looking at or clicked on
     * @param meta   the metadata of the Block that the player is looking at or clicked on
     * @param x      the x coordinate of the Block the player is looking at or clicked on
     * @param y      the y coordinate of the Block the player is looking at or clicked on
     * @param z      the z coordinate of the Block the player is looking at or clicked on
     * @param ray    the exact raytrace result
     *
     * @return the PlacementOffset object describing where to place the new block.
     */
    PlacementOffset getOffset(EntityPlayer player, World world, Block block, int meta, int x, int y, int z,
        MovingObjectPosition ray);

    // sets the offset's ghost state with the default state of the held block item, this is used in PlacementHelpers and
    // can be ignored in most cases
    default PlacementOffset getOffset(EntityPlayer player, World world, Block block, int meta, int x, int y, int z,
        MovingObjectPosition ray, ItemStack heldItem) {
        PlacementOffset offset = getOffset(player, world, block, meta, x, y, z, ray);
        if (heldItem.getItem() instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock) heldItem.getItem();
            offset = offset.withGhostState(itemBlock.field_150939_a, heldItem.getItemDamage());
        }
        return offset;
    }

    /**
     * overwrite this method if your placement helper needs a different rendering than the default ghost state
     *
     * @param x      the x coordinate of the Block the player is looking at or clicked on
     * @param y      the y coordinate of the Block the player is looking at or clicked on
     * @param z      the z coordinate of the Block the player is looking at or clicked on
     * @param block  the Block that the player is looking at or clicked on
     * @param meta   the metadata of the Block that the player is looking at or clicked on
     * @param ray    the exact raytrace result
     * @param offset the PlacementOffset returned by
     *               {@link #getOffset(EntityPlayer, World, Block, int, int, int, int, MovingObjectPosition)}<br>
     *               the offset will always be successful if this method is called
     */
    default void renderAt(int x, int y, int z, Block block, int meta, MovingObjectPosition ray,
        PlacementOffset offset) {
        displayGhost(offset);
    }

    // RIP
    static void renderArrow(Vec3 center, Vec3 target, Direction arrowPlane) {
        renderArrow(center, target, arrowPlane, 1D);
    }

    static void renderArrow(Vec3 center, Vec3 target, Direction arrowPlane, double distanceFromCenter) {
        Vec3 direction = target.subtract(center)
            .normalize();
        ChunkCoordinates arrowNormal = arrowPlane.getNormal();
        Vec3 facing = Vec3.createVectorHelper(arrowNormal.posX, arrowNormal.posY, arrowNormal.posZ);
        Vec3 start = center.addVector(direction.xCoord, direction.yCoord, direction.zCoord);
        double multiplier = distanceFromCenter - 1;
        Vec3 offset = Vec3.createVectorHelper(
            direction.xCoord * multiplier,
            direction.yCoord * multiplier,
            direction.zCoord * multiplier);
        multiplier = .25;
        Vec3 offsetA = direction.crossProduct(facing)
            .normalize();
        offsetA = Vec3
            .createVectorHelper(offsetA.xCoord * multiplier, offsetA.yCoord * multiplier, offsetA.zCoord * multiplier);
        Vec3 offsetB = facing.crossProduct(direction)
            .normalize();
        offsetB = Vec3
            .createVectorHelper(offsetA.xCoord * multiplier, offsetA.yCoord * multiplier, offsetA.zCoord * multiplier);
        multiplier = .75;
        Vec3 scaledDirection = Vec3.createVectorHelper(
            direction.xCoord * multiplier,
            direction.yCoord * multiplier,
            direction.zCoord * multiplier);
        Vec3 endA = center.addVector(
            scaledDirection.xCoord + offsetA.xCoord,
            scaledDirection.yCoord + offsetA.yCoord,
            scaledDirection.zCoord + offsetA.zCoord);
        Vec3 endB = center.addVector(
            scaledDirection.xCoord + offsetB.xCoord,
            scaledDirection.yCoord + offsetB.yCoord,
            scaledDirection.zCoord + offsetB.zCoord);
        ClientProxy.OUTLINER
            .showLine(
                "placementArrowA" + center + target,
                start.addVector(offset.xCoord, offset.yCoord, offset.zCoord),
                endA.addVector(offset.xCoord, offset.yCoord, offset.zCoord))
            .lineWidth(1 / 16f);
        ClientProxy.OUTLINER
            .showLine(
                "placementArrowB" + center + target,
                start.addVector(offset.xCoord, offset.yCoord, offset.zCoord),
                endB.addVector(offset.xCoord, offset.yCoord, offset.zCoord))
            .lineWidth(1 / 16f);
    }

    default void displayGhost(PlacementOffset offset) {
        if (!offset.hasGhostBlock()) return;

        Pair<Block, Integer> transformedPair = offset.getTransform()
            .apply(Pair.of(offset.getGhostBlock(), offset.getGhostMeta()));
        ClientProxy.GHOST_BLOCKS.showGhostState(this, transformedPair.getFirst(), transformedPair.getSecond())
            .at(offset.getPos())
            .breathingAlpha();
    }

    static List<Direction> orderedByDistanceOnlyAxis(int x, int y, int z, Vec3 hit, Direction.Axis axis) {
        return orderedByDistance(x, y, z, hit, dir -> dir.getAxis() == axis);
    }

    static List<Direction> orderedByDistanceOnlyAxis(int x, int y, int z, Vec3 hit, Direction.Axis axis,
        Predicate<Direction> includeDirection) {
        return orderedByDistance(
            x,
            y,
            z,
            hit,
            ((Predicate<Direction>) dir -> dir.getAxis() == axis).and(includeDirection));
    }

    static List<Direction> orderedByDistanceExceptAxis(int x, int y, int z, Vec3 hit, Direction.Axis axis) {
        return orderedByDistance(x, y, z, hit, dir -> dir.getAxis() != axis);
    }

    static List<Direction> orderedByDistanceExceptAxis(int x, int y, int z, Vec3 hit, Direction.Axis axis,
        Predicate<Direction> includeDirection) {
        return orderedByDistance(
            x,
            y,
            z,
            hit,
            ((Predicate<Direction>) dir -> dir.getAxis() != axis).and(includeDirection));
    }

    static List<Direction> orderedByDistanceExceptAxis(int x, int y, int z, Vec3 hit, Direction.Axis first,
        Direction.Axis second) {
        return orderedByDistanceExceptAxis(x, y, z, hit, first, d -> d.getAxis() != second);
    }

    static List<Direction> orderedByDistanceExceptAxis(int x, int y, int z, Vec3 hit, Direction.Axis first,
        Direction.Axis second, Predicate<Direction> includeDirection) {
        return orderedByDistanceExceptAxis(
            x,
            y,
            z,
            hit,
            first,
            ((Predicate<Direction>) d -> d.getAxis() != second).and(includeDirection));
    }

    static List<Direction> orderedByDistance(int x, int y, int z, Vec3 hit) {
        return orderedByDistance(x, y, z, hit, _$ -> true);
    }

    static List<Direction> orderedByDistance(int x, int y, int z, Vec3 hit, Predicate<Direction> includeDirection) {
        Vec3 centerToHit = VecHelper.getCenterOf(x, y, z)
            .subtract(hit);
        return Arrays.stream(Iterate.directions)
            .filter(includeDirection)
            .map(dir -> {
                ChunkCoordinates dirNormal = dir.getNormal();
                return Pair.of(
                    dir,
                    Vec3.createVectorHelper(dirNormal.posX, dirNormal.posY, dirNormal.posZ)
                        .distanceTo(centerToHit));
            })
            .sorted(Comparator.comparingDouble(Pair::getSecond))
            .map(Pair::getFirst)
            .collect(Collectors.toList());
    }

    default boolean matchesItem(ItemStack item) {
        return getItemPredicate().test(item);
    }

    default boolean matchesBlock(Block block, int meta) {
        return getBlockPredicate().test(Pair.of(block, meta));
    }
}
