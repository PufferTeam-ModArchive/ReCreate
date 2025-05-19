package su.sergiusonesimus.recreate.foundation.utility.ghost;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;

public class GhostBlockParams {

    protected final Block block;
    protected final int meta;
    protected int posX;
    protected int posY;
    protected int posZ;
    protected Supplier<Float> alphaSupplier;

    private GhostBlockParams(Block block, int meta) {
        this.block = block;
        this.meta = meta;
        this.posX = 0;
        this.posY = 0;
        this.posZ = 0;
        this.alphaSupplier = () -> 1f;
    }

    public static GhostBlockParams of(Block block, int meta) {
        return new GhostBlockParams(block, meta);
    }

    public static GhostBlockParams of(Block block) {
        return of(block, 0);
    }

    public GhostBlockParams at(int x, int y, int z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        return this;
    }

    public GhostBlockParams at(ChunkCoordinates pos) {
        return this.at(pos.posX, pos.posY, pos.posZ);
    }

    public GhostBlockParams alpha(Supplier<Float> alphaSupplier) {
        this.alphaSupplier = alphaSupplier;
        return this;
    }

    public GhostBlockParams alpha(float alpha) {
        return this.alpha(() -> alpha);
    }

    public GhostBlockParams breathingAlpha() {
        return this.alpha(() -> (float) GhostBlocks.getBreathingAlpha());
    }
}
