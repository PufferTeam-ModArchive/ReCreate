package su.sergiusonesimus.recreate.foundation.utility.placement;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

import su.sergiusonesimus.recreate.foundation.utility.Pair;

public class PlacementOffset {

    private final boolean success;
    private ChunkCoordinates pos;
    private Function<Pair<Block, Integer>, Pair<Block, Integer>> stateTransform;
    private Block ghostBlock;
    private Integer ghostMeta;

    private PlacementOffset(boolean success) {
        this.success = success;
        this.pos = new ChunkCoordinates(0, 0, 0);
        this.stateTransform = Function.identity();
        this.ghostBlock = null;
        this.ghostMeta = 0;
    }

    public static PlacementOffset fail() {
        return new PlacementOffset(false);
    }

    public static PlacementOffset success() {
        return new PlacementOffset(true);
    }

    public static PlacementOffset success(ChunkCoordinates pos) {
        return success().at(pos);
    }

    public static PlacementOffset success(ChunkCoordinates pos,
        Function<Pair<Block, Integer>, Pair<Block, Integer>> transform) {
        return success().at(pos)
            .withTransform(transform);
    }

    public PlacementOffset at(ChunkCoordinates pos) {
        this.pos = pos;
        return this;
    }

    public PlacementOffset withTransform(Function<Pair<Block, Integer>, Pair<Block, Integer>> stateTransform) {
        this.stateTransform = stateTransform;
        return this;
    }

    public PlacementOffset withGhostState(Block ghostBlock, int ghostMeta) {
        this.ghostBlock = ghostBlock;
        this.ghostMeta = ghostMeta;
        return this;
    }

    public PlacementOffset withGhostState(Block ghostBlock) {
        return this.withGhostState(ghostBlock, 0);
    }

    public boolean isSuccessful() {
        return success;
    }

    public ChunkCoordinates getPos() {
        return pos;
    }

    public Function<Pair<Block, Integer>, Pair<Block, Integer>> getTransform() {
        return stateTransform;
    }

    public boolean hasGhostBlock() {
        return ghostBlock != null;
    }

    public Block getGhostBlock() {
        return ghostBlock;
    }

    public Integer getGhostMeta() {
        return ghostMeta;
    }

    public boolean isReplaceable(World world) {
        if (!success) return false;

        return world.getBlock(pos.posX, pos.posY, pos.posZ)
            .getMaterial()
            .isReplaceable();
    }

    public boolean placeInWorld(World world, ItemBlock itemBlock, EntityPlayer player, MovingObjectPosition ray) {

        if (!isReplaceable(world)) return false;

        if (world.isRemote) return true;

        int newX = pos.posX;
        int newY = pos.posY;
        int newZ = pos.posZ;

        if (!player.canPlayerEdit(newX, newY, newZ, ray.sideHit, player.getHeldItem())) return false;

        Pair<Block, Integer> state = stateTransform.apply(
            Pair.of(
                itemBlock.field_150939_a,
                player.getHeldItem()
                    .getItemDamage()));
        Block transformedBlock = state.getFirst();
        int transformedMeta = state.getSecond();

        BlockSnapshot snapshot = new BlockSnapshot(world, newX, newY, newZ, transformedBlock, transformedMeta);
        world.setBlock(newX, newY, newZ, transformedBlock, transformedMeta, 2);

        PlaceEvent event = new PlaceEvent(snapshot, IPlacementHelper.BLOCK, player);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            snapshot.restore(true, false);
            return false;
        }

        Block newBlock = world.getBlock(newX, newY, newZ);
        int newMeta = world.getBlockMetadata(newX, newY, newZ);
        SoundType soundtype = newBlock.stepSound;
        world.playSoundEffect(
            newX,
            newY,
            newZ,
            soundtype.getBreakSound(),
            (soundtype.getVolume() + 1.0F) / 2.0F,
            soundtype.getPitch() * 0.8F);

        if (!player.capabilities.isCreativeMode) player.getHeldItem().stackSize--;

        return true;
    }
}
