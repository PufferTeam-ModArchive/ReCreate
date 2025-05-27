package su.sergiusonesimus.recreate.foundation.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.foundation.block.BlockStressDefaults;
import su.sergiusonesimus.recreate.foundation.block.BlockStressValues.IStressValueProvider;

public class CStress implements IStressValueProvider {

    private final static Map<UniqueIdentifier, Double> capacities = new HashMap<>();
    private final static Map<UniqueIdentifier, Double> impacts = new HashMap<>();

    public static void init(String category, Configuration config) {
        BlockStressDefaults.DEFAULT_IMPACTS.forEach((r, i) -> {
            if (r.modId.equals(ReCreate.ID)) {
                config.getFloat(
                    r.name.substring(5) + "_impact",
                    category,
                    i.floatValue(),
                    0,
                    Float.MAX_VALUE,
                    Comments.su + "\n" + Comments.impact);
            }
        });

        BlockStressDefaults.DEFAULT_CAPACITIES.forEach((r, i) -> {
            if (r.modId.equals(ReCreate.ID)) {
                config.getFloat(
                    r.name.substring(5) + "_capacity",
                    category,
                    i.floatValue(),
                    0,
                    Float.MAX_VALUE,
                    Comments.su + "\n" + Comments.capacity);
            }
        });
    }

    @Override
    public double getImpact(Block block) {
        block = redirectValues(block);
        UniqueIdentifier key = GameRegistry.findUniqueIdentifierFor(block);
        Double value = getImpacts().get(key);
        return value != null ? value : 0;
    }

    @Override
    public double getCapacity(Block block) {
        block = redirectValues(block);
        UniqueIdentifier key = GameRegistry.findUniqueIdentifierFor(block);
        Double value = getCapacities().get(key);
        return value != null ? value : 0;
    }

    @Override
    public boolean hasImpact(Block block) {
        block = redirectValues(block);
        UniqueIdentifier key = GameRegistry.findUniqueIdentifierFor(block);
        return getImpacts().containsKey(key);
    }

    @Override
    public boolean hasCapacity(Block block) {
        block = redirectValues(block);
        UniqueIdentifier key = GameRegistry.findUniqueIdentifierFor(block);
        return getCapacities().containsKey(key);
    }

    protected Block redirectValues(Block block) {
        // TODO
        // if (block instanceof ValveHandleBlock) {
        // return AllBlocks.HAND_CRANK.get();
        // }
        return block;
    }

    public static Map<UniqueIdentifier, Double> getImpacts() {
        return impacts;
    }

    public static Map<UniqueIdentifier, Double> getCapacities() {
        return capacities;
    }

    private static class Comments {

        static String su = "[in Stress Units]";
        static String impact = "Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives.";
        static String capacity = "Configure how much stress a source can accommodate for.";
    }

}
