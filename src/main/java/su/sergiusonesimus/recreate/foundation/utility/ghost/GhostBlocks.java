package su.sergiusonesimus.recreate.foundation.utility.ghost;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;

public class GhostBlocks {

    public static double getBreathingAlpha() {
        double period = 2500;
        double timer = System.currentTimeMillis() % period;
        double offset = Math.cos((float) ((2d / period) * Math.PI * timer));
        return 0.55d - 0.2d * offset;
    }

    final Map<Object, Entry> ghosts = new HashMap<Object, Entry>();

    public GhostBlockParams showGhostState(Object slot, Block block, int meta) {
        return showGhostState(slot, block, meta, 1);
    }

    public GhostBlockParams showGhostState(Object slot, Block block, int meta, int ttl) {
        Entry e = refresh(slot, GhostBlockRenderer.transparent(), GhostBlockParams.of(block, meta), ttl);
        return e.params;
    }

    public GhostBlockParams showGhost(Object slot, GhostBlockRenderer ghost, GhostBlockParams params, int ttl) {
        Entry e = refresh(slot, ghost, params, ttl);
        return e.params;
    }

    private Entry refresh(Object slot, GhostBlockRenderer ghost, GhostBlockParams params, int ttl) {
        if (!ghosts.containsKey(slot)) ghosts.put(slot, new Entry(ghost, params, ttl));

        Entry e = ghosts.get(slot);
        e.ticksToLive = ttl;
        e.params = params;
        e.ghost = ghost;
        return e;
    }

    public void tickGhosts() {
        ghosts.forEach((slot, entry) -> entry.ticksToLive--);
        ghosts.entrySet()
            .removeIf(
                e -> !e.getValue()
                    .isAlive());
    }

    public void renderAll() {
        ghosts.forEach((slot, entry) -> {
            GhostBlockRenderer ghost = entry.ghost;
            ghost.render(entry.params);
        });
    }

    static class Entry {

        private GhostBlockRenderer ghost;
        private GhostBlockParams params;
        private int ticksToLive;

        public Entry(GhostBlockRenderer ghost, GhostBlockParams params) {
            this(ghost, params, 1);
        }

        public Entry(GhostBlockRenderer ghost, GhostBlockParams params, int ttl) {
            this.ghost = ghost;
            this.params = params;
            this.ticksToLive = ttl;
        }

        public boolean isAlive() {
            return ticksToLive >= 0;
        }
    }
}
