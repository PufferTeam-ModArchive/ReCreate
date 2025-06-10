package su.sergiusonesimus.recreate.foundation.config;

import java.util.HashMap;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public enum ContraptionMovementSetting {

    MOVABLE,
    NO_PICKUP,
    UNMOVABLE;

    private static HashMap<Block, Supplier<ContraptionMovementSetting>> registry = new HashMap<>();

    public static void register(Block block, Supplier<ContraptionMovementSetting> setting) {
        registry.put(block, setting);
    }

    static {
        register(Blocks.mob_spawner, () -> AllConfigs.SERVER.kinetics.spawnerMovement);
        register(Blocks.obsidian, () -> AllConfigs.SERVER.kinetics.obsidianMovement);
    }

    @Nullable
    public static ContraptionMovementSetting get(Block block) {
        if (block instanceof IMovementSettingProvider)
            return ((IMovementSettingProvider) block).getContraptionMovementSetting();
        Supplier<ContraptionMovementSetting> supplier = registry.get(block);
        return supplier == null ? null : supplier.get();
    }

    public interface IMovementSettingProvider {

        ContraptionMovementSetting getContraptionMovementSetting();
    }
}
