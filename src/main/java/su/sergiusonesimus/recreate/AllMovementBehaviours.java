package su.sergiusonesimus.recreate;

import java.util.HashMap;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.block.Block;

import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.MovementBehaviour;

public class AllMovementBehaviours {

    private static final HashMap<String, MovementBehaviour> MOVEMENT_BEHAVIOURS = new HashMap<>();

    public static void addMovementBehaviour(String blockType, MovementBehaviour movementBehaviour) {
        if (MOVEMENT_BEHAVIOURS.containsKey(blockType))
            ReCreate.LOGGER.warn("Movement behaviour for " + blockType.toString() + " was overridden");
        MOVEMENT_BEHAVIOURS.put(blockType, movementBehaviour);
    }

    public static void addMovementBehaviour(Block block, MovementBehaviour movementBehaviour) {
        addMovementBehaviour(block.getUnlocalizedName(), movementBehaviour);
    }

    @Nullable
    public static MovementBehaviour of(String blockType) {
        return MOVEMENT_BEHAVIOURS.getOrDefault(blockType, null);
    }

    @Nullable
    public static MovementBehaviour of(Block block) {
        return of(block.getUnlocalizedName());
    }

    public static boolean contains(Block block) {
        return MOVEMENT_BEHAVIOURS.containsKey(block.getUnlocalizedName());
    }

    public static <B extends Block> Consumer<? super B> addMovementBehaviour(MovementBehaviour movementBehaviour) {
        return b -> addMovementBehaviour(b.getUnlocalizedName(), movementBehaviour);
    }

    static void register() {
        // TODO
        // addMovementBehaviour(Blocks.BELL, new BellMovementBehaviour());
        // addMovementBehaviour(Blocks.CAMPFIRE, new CampfireMovementBehaviour());
        //
        // DispenserMovementBehaviour.gatherMovedDispenseItemBehaviours();
        // addMovementBehaviour(Blocks.DISPENSER, new DispenserMovementBehaviour());
        // addMovementBehaviour(Blocks.DROPPER, new DropperMovementBehaviour());
    }
}
