package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.content.contraptions.base.DirectionalKineticBlock;
import su.sergiusonesimus.recreate.content.contraptions.base.HorizontalKineticBlock;
import su.sergiusonesimus.recreate.content.contraptions.base.IRotate;
import su.sergiusonesimus.recreate.content.contraptions.base.RotatedPillarKineticBlock;
import su.sergiusonesimus.recreate.foundation.utility.Pair;
import su.sergiusonesimus.recreate.foundation.utility.placement.IPlacementHelper;
import su.sergiusonesimus.recreate.foundation.utility.placement.PlacementHelpers;
import su.sergiusonesimus.recreate.foundation.utility.placement.PlacementOffset;

public class CogWheelItemBlock extends ItemBlock {

    boolean large;

    private final int placementHelperId;
    private final int integratedCogHelperId;

    public CogWheelItemBlock(Block block) {
        super(block);

        large = ((CogWheelBlock) block).isLarge;
        placementHelperId = PlacementHelpers.register(large ? new LargeCogHelper() : new SmallCogHelper());
        integratedCogHelperId = PlacementHelpers
            .register(large ? new IntegratedLargeCogHelper() : new IntegratedSmallCogHelper());
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int metadata) {
        CogWheelBlock cogwheel = (CogWheelBlock) field_150939_a;
        Axis axis = cogwheel.getAxisForPlacement(world, x, y, z, player);
        if (axis == null || player == null || player.isSneaking()) axis = Direction.from3DDataValue(side)
            .getAxis();
        if (!CogWheelBlock.isValidCogwheelPosition(ICogWheel.isLargeCog(cogwheel), world, x, y, z, axis)) return false;
        int localMeta = cogwheel.getMetaFromAxis(axis);
        // TODO
        // triggerShiftingGearsAdvancement(world, x, y, z, cogwheel, localMeta, player);
        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, localMeta);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        MovingObjectPosition ray = new MovingObjectPosition(
            x,
            y,
            z,
            side,
            Vec3.createVectorHelper(hitX, hitY, hitZ),
            true);
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (helper.matchesBlock(block, meta) && player != null && !player.isSneaking()) {
            if (helper.getOffset(player, world, block, meta, x, y, z, ray)
                .placeInWorld(world, this, player, ray)) {
                return true;
            } else {
                if (Direction.from3DDataValue(side)
                    .getAxis() != ((IRotate) block).getAxis(meta)) return false;
            }
        }

        if (integratedCogHelperId != -1) {
            helper = PlacementHelpers.get(integratedCogHelperId);

            if (helper.matchesBlock(block, meta) && player != null && !player.isSneaking()) {
                return helper.getOffset(player, world, block, meta, x, y, z, ray)
                    .placeInWorld(world, this, player, ray);
            }
        }

        return super.onItemUse(itemStack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    // TODO
    // protected void triggerShiftingGearsAdvancement(Level world, BlockPos pos, BlockState state, Player player) {
    // if (world.isClientSide || player == null)
    // return;
    //
    // Axis axis = state.getValue(CogWheelBlock.AXIS);
    // for (Axis perpendicular1 : Iterate.axes) {
    // if (perpendicular1 == axis)
    // continue;
    // Direction d1 = Direction.get(AxisDirection.POSITIVE, perpendicular1);
    // for (Axis perpendicular2 : Iterate.axes) {
    // if (perpendicular1 == perpendicular2)
    // continue;
    // if (axis == perpendicular2)
    // continue;
    // Direction d2 = Direction.get(AxisDirection.POSITIVE, perpendicular2);
    // for (int offset1 : Iterate.positiveAndNegative) {
    // for (int offset2 : Iterate.positiveAndNegative) {
    // BlockPos connectedPos = pos.relative(d1, offset1)
    // .relative(d2, offset2);
    // BlockState blockState = world.getBlockState(connectedPos);
    // if (!(blockState.getBlock() instanceof CogWheelBlock))
    // continue;
    // if (blockState.getValue(CogWheelBlock.AXIS) != axis)
    // continue;
    // if (ICogWheel.isLargeCog(blockState) == large)
    // continue;
    // AllTriggers.triggerFor(AllTriggers.SHIFTING_GEARS, player);
    // }
    // }
    // }
    // }
    // }

    private static class SmallCogHelper extends DiagonalCogHelper {

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return ((Predicate<ItemStack>) ICogWheel::isSmallCogItem).and(ICogWheel::isDedicatedCogItem);
        }

        @Override
        public PlacementOffset getOffset(EntityPlayer player, World world, Block block, int meta, int x, int y, int z,
            MovingObjectPosition ray) {
            if (hitOnShaft(world, block, meta, ray)) return PlacementOffset.fail();

            if (!ICogWheel.isLargeCog(block)) {
                Axis axis = ((IRotate) block).getAxis(meta);
                List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(x, y, z, ray.hitVec, axis);

                for (Direction dir : directions) {
                    ChunkCoordinates dirNormal = dir.getNormal();
                    int newX = x + dirNormal.posX;
                    int newY = y + dirNormal.posY;
                    int newZ = z + dirNormal.posZ;

                    if (!CogWheelBlock.isValidCogwheelPosition(false, world, newX, newY, newZ, axis)) continue;

                    if (!world.getBlock(newX, newY, newZ)
                        .getMaterial()
                        .isReplaceable()) continue;

                    return PlacementOffset.success(new ChunkCoordinates(newX, newY, newZ), s -> {
                        s.setSecond(meta);
                        return s;
                    });

                }

                return PlacementOffset.fail();
            }

            return super.getOffset(player, world, block, meta, x, y, z, ray);
        }
    }

    private static class LargeCogHelper extends DiagonalCogHelper {

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return ((Predicate<ItemStack>) ICogWheel::isLargeCogItem).and(ICogWheel::isDedicatedCogItem);
        }

        @Override
        public Predicate<Pair<Block, Integer>> getBlockPredicate() {
            return pair -> ICogWheel.isLargeCog(pair.getFirst()) && ICogWheel.isDedicatedCogWheel(pair.getFirst());
        }

        @Override
        public PlacementOffset getOffset(EntityPlayer player, World world, Block block, int meta, int x, int y, int z,
            MovingObjectPosition ray) {
            if (hitOnShaft(world, block, meta, ray)) return PlacementOffset.fail();

            if (ICogWheel.isLargeCog(block)) {
                Axis axis = ((IRotate) block).getAxis(meta);
                Direction side = IPlacementHelper.orderedByDistanceOnlyAxis(x, y, z, ray.hitVec, axis)
                    .get(0);
                List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(x, y, z, ray.hitVec, axis);
                for (Direction dir : directions) {
                    ChunkCoordinates dirNormal = dir.getNormal();
                    ChunkCoordinates sideNormal = side.getNormal();
                    int newX = x + dirNormal.posX + sideNormal.posX;
                    int newY = y + dirNormal.posY + sideNormal.posY;
                    int newZ = z + dirNormal.posZ + sideNormal.posZ;

                    if (!CogWheelBlock.isValidCogwheelPosition(true, world, newX, newY, newZ, dir.getAxis())) continue;

                    if (!world.getBlock(newX, newY, newZ)
                        .getMaterial()
                        .isReplaceable()) continue;

                    return PlacementOffset.success(new ChunkCoordinates(newX, newY, newZ), s -> {
                        s.setSecond(((IRotate) block).getMetaFromDirection(dir));
                        return s;
                    });
                }

                return PlacementOffset.fail();
            }

            return super.getOffset(player, world, block, meta, x, y, z, ray);
        }
    }

    public abstract static class DiagonalCogHelper implements IPlacementHelper {

        @Override
        public Predicate<Pair<Block, Integer>> getBlockPredicate() {
            return s -> ICogWheel.isSmallCog(s.getFirst()) || ICogWheel.isLargeCog(s.getFirst());
        }

        @Override
        public PlacementOffset getOffset(EntityPlayer player, World world, Block block, int meta, int x, int y, int z,
            MovingObjectPosition ray) {
            // diagonal gears of different size
            Axis axis = ((IRotate) block).getAxis(meta);
            Direction closest = IPlacementHelper.orderedByDistanceExceptAxis(x, y, z, ray.hitVec, axis)
                .get(0);
            List<Direction> directions = IPlacementHelper
                .orderedByDistanceExceptAxis(x, y, z, ray.hitVec, axis, d -> d.getAxis() != closest.getAxis());

            for (Direction dir : directions) {
                ChunkCoordinates dirNormal = dir.getNormal();
                ChunkCoordinates closestNormal = closest.getNormal();
                int newX = x + dirNormal.posX + closestNormal.posX;
                int newY = y + dirNormal.posY + closestNormal.posY;
                int newZ = z + dirNormal.posZ + closestNormal.posZ;
                if (!world.getBlock(newX, newY, newZ)
                    .getMaterial()
                    .isReplaceable()) continue;

                if (!CogWheelBlock.isValidCogwheelPosition(ICogWheel.isLargeCog(block), world, newX, newY, newZ, axis))
                    continue;

                return PlacementOffset.success(new ChunkCoordinates(newX, newY, newZ), s -> {
                    s.setSecond(meta);
                    return s;
                });
            }

            return PlacementOffset.fail();
        }

        protected boolean hitOnShaft(World world, Block block, int meta, MovingObjectPosition ray) {
            double inflator = 0.001;
            AxisAlignedBB shaft = AllBlocks.shaft
                .getCollisionBoundingBoxFromPool(world, ray.blockX, ray.blockY, ray.blockZ)
                .expand(inflator, inflator, inflator);
            Vec3 vec = ray.hitVec;
            if (vec.xCoord >= 0 && vec.xCoord <= 1
                && vec.yCoord >= 0
                && vec.yCoord <= 1
                && vec.zCoord >= 0
                && vec.zCoord <= 1) shaft = shaft.offset(-ray.blockX, -ray.blockY, -ray.blockZ);
            return shaft.isVecInside(ray.hitVec);
        }
    }

    public static class IntegratedLargeCogHelper implements IPlacementHelper {

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return ((Predicate<ItemStack>) ICogWheel::isLargeCogItem).and(ICogWheel::isDedicatedCogItem);
        }

        @Override
        public Predicate<Pair<Block, Integer>> getBlockPredicate() {
            return s -> !ICogWheel.isDedicatedCogWheel(s.getFirst()) && ICogWheel.isSmallCog(s.getFirst());
        }

        @Override
        public PlacementOffset getOffset(EntityPlayer player, World world, Block block, int meta, int x, int y, int z,
            MovingObjectPosition ray) {
            Direction face = Direction.from3DDataValue(ray.sideHit);
            Axis newAxis;

            if (block instanceof HorizontalKineticBlock) newAxis = ((HorizontalKineticBlock) block).getAxis(meta);
            else if (block instanceof DirectionalKineticBlock)
                newAxis = ((DirectionalKineticBlock) block).getAxis(meta);
            else if (block instanceof RotatedPillarKineticBlock)
                newAxis = ((RotatedPillarKineticBlock) block).getAxis(meta);
            else newAxis = Axis.Y;

            if (face.getAxis() == newAxis) return PlacementOffset.fail();

            List<Direction> directions = IPlacementHelper
                .orderedByDistanceExceptAxis(x, y, z, ray.hitVec, face.getAxis(), newAxis);

            for (Direction d : directions) {
                ChunkCoordinates dNormal = d.getNormal();
                int newX = x + dNormal.posX;
                int newY = y + dNormal.posY;
                int newZ = z + dNormal.posZ;

                if (!world.getBlock(newX, newY, newZ)
                    .getMaterial()
                    .isReplaceable()) continue;

                if (!CogWheelBlock.isValidCogwheelPosition(false, world, newX, newY, newZ, newAxis))
                    return PlacementOffset.fail();

                return PlacementOffset.success(new ChunkCoordinates(newX, newY, newZ), s -> {
                    s.setSecond(((CogWheelBlock) block).getMetaFromAxis(newAxis));
                    return s;
                });
            }

            return PlacementOffset.fail();
        }

    }

    public static class IntegratedSmallCogHelper implements IPlacementHelper {

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return ((Predicate<ItemStack>) ICogWheel::isSmallCogItem).and(ICogWheel::isDedicatedCogItem);
        }

        @Override
        public Predicate<Pair<Block, Integer>> getBlockPredicate() {
            return s -> !ICogWheel.isDedicatedCogWheel(s.getFirst()) && ICogWheel.isSmallCog(s.getFirst());
        }

        @Override
        public PlacementOffset getOffset(EntityPlayer player, World world, Block block, int meta, int x, int y, int z,
            MovingObjectPosition ray) {
            Direction face = Direction.from3DDataValue(ray.sideHit);
            Axis newAxis;

            if (block instanceof HorizontalKineticBlock) newAxis = ((HorizontalKineticBlock) block).getAxis(meta);
            else if (block instanceof DirectionalKineticBlock)
                newAxis = ((DirectionalKineticBlock) block).getAxis(meta);
            else if (block instanceof RotatedPillarKineticBlock)
                newAxis = ((RotatedPillarKineticBlock) block).getAxis(meta);
            else newAxis = Axis.Y;

            if (face.getAxis() == newAxis) return PlacementOffset.fail();

            List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(x, y, z, ray.hitVec, newAxis);

            for (Direction d : directions) {
                ChunkCoordinates dNormal = d.getNormal();
                int newX = x + dNormal.posX;
                int newY = y + dNormal.posY;
                int newZ = z + dNormal.posZ;

                if (!world.getBlock(newX, newY, newZ)
                    .getMaterial()
                    .isReplaceable()) continue;

                if (!CogWheelBlock.isValidCogwheelPosition(false, world, newX, newY, newZ, newAxis))
                    return PlacementOffset.fail();

                return PlacementOffset.success()
                    .at(new ChunkCoordinates(newX, newY, newZ))
                    .withTransform(s -> {
                        s.setSecond(((CogWheelBlock) block).getMetaFromAxis(newAxis));
                        return s;
                    });
            }

            return PlacementOffset.fail();
        }

    }

}
