package su.sergiusonesimus.recreate.foundation.block;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class BlockStressDefaults {

    /**
     * Increment this number if all stress entries should be forced to update in the next release.
     * Worlds from the previous version will overwrite potentially changed values
     * with the new defaults.
     */
    public static final int FORCED_UPDATE_VERSION = 2;

    public static final Map<UniqueIdentifier, Double> DEFAULT_IMPACTS = new HashMap<>();
    public static final Map<UniqueIdentifier, Double> DEFAULT_CAPACITIES = new HashMap<>();

    public static void setDefaultImpact(UniqueIdentifier blockId, double impact) {
        DEFAULT_IMPACTS.put(blockId, impact);
    }

    public static void setDefaultCapacity(UniqueIdentifier blockId, double capacity) {
        DEFAULT_CAPACITIES.put(blockId, capacity);
    }

    public static void setNoImpact(Block block) {
        setImpact(block, 0);
    }

    public static void setImpact(Block block, double impact) {
        setDefaultImpact(GameRegistry.findUniqueIdentifierFor(block), impact);
    }

    public static void setCapacity(Block block, double capacity) {
        setDefaultCapacity(GameRegistry.findUniqueIdentifierFor(block), capacity);
    }

}
