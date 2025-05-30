package su.sergiusonesimus.recreate;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import su.sergiusonesimus.recreate.foundation.utility.Pair;

public class AllTags {

    public enum NameSpace {

        MOD(ReCreate.ID, false, true),
        FORGE("forge"),
        TIC("tconstruct")

        ;

        public final String id;
        public final boolean optionalDefault;
        public final boolean alwaysDatagenDefault;

        NameSpace(String id) {
            this(id, true, false);
        }

        NameSpace(String id, boolean optionalDefault, boolean alwaysDatagenDefault) {
            this.id = id;
            this.optionalDefault = optionalDefault;
            this.alwaysDatagenDefault = alwaysDatagenDefault;
        }

    }

    public enum AllBlockTags {

        BRITTLE,
        FAN_HEATERS,
        FAN_TRANSPARENT,
        SAFE_NBT,
        SAILS,
        SEATS,
        TOOLBOXES,
        VALVE_HANDLES,
        WINDMILL_SAILS,
        WINDOWABLE,
        WRENCH_PICKUP,

        ORE_OVERRIDE_STONE,

        WG_STONE(NameSpace.FORGE),

        SLIMY_LOGS(NameSpace.TIC),

        ;

        public final NameSpace namespace;
        public final List<Pair<Block, Integer>> blocks;

        AllBlockTags() {
            this(NameSpace.MOD);
        }

        AllBlockTags(NameSpace namespace) {
            this.namespace = namespace;
            this.blocks = new ArrayList<Pair<Block, Integer>>();
        }

        public boolean matches(Block block) {
            return matches(block, 0);
        }

        public boolean matches(Block block, int meta) {
            for (Pair<Block, Integer> pair : blocks) {
                Block blockToMatch = pair.getFirst();
                int metaToMatch = pair.getSecond();
                if (block == blockToMatch && (metaToMatch == -1 || meta == metaToMatch)) {
                    return true;
                }
            }
            return false;
        }

        public AllBlockTags add(Block block) {
            add(block, -1);
            return this;
        }

        public AllBlockTags add(Block... values) {
            for (Block block : values) {
                add(block);
            }
            return this;
        }

        public AllBlockTags add(Block block, int meta) {
            blocks.add(Pair.of(block, meta));
            return this;
        }

    }

    public enum AllItemTags {

        CREATE_INGOTS,
        CRUSHED_ORES,
        SANDPAPER,
        SEATS,
        TOOLBOXES,
        UPRIGHT_ON_BELT,
        VALVE_HANDLES,

        BEACON_PAYMENT(NameSpace.FORGE),
        PLATES(NameSpace.FORGE)

        ;

        public final NameSpace namespace;
        public final List<Pair<Item, Integer>> items;

        AllItemTags() {
            this(NameSpace.MOD);
        }

        AllItemTags(NameSpace namespace) {
            this.namespace = namespace;
            this.items = new ArrayList<Pair<Item, Integer>>();
        }

        public boolean matches(ItemStack stack) {
            return matches(stack.getItem(), stack.getItemDamage());
        }

        public boolean matches(Item item) {
            return matches(item, 0);
        }

        public boolean matches(Item item, int damage) {
            for (Pair<Item, Integer> pair : items) {
                Item itemToMatch = pair.getFirst();
                int damageToMatch = pair.getSecond();
                if (item == itemToMatch && (damageToMatch == -1 || damage == damageToMatch)) {
                    return true;
                }
            }
            return false;
        }

        public AllItemTags add(Item item) {
            add(item, -1);
            return this;
        }

        public AllItemTags add(Item... values) {
            for (Item item : values) add(item);
            return this;
        }

        public AllItemTags add(Item item, int damage) {
            items.add(Pair.of(item, damage));
            return this;
        }

    }

    // TODO
    // public enum AllFluidTags {
    //
    // NO_INFINITE_DRAINING(NameSpace.MOD, true, false),
    //
    // HONEY(NameSpace.FORGE)
    //
    // ;
    //
    // public final Tag.Named<Fluid> tag;
    //
    // AllFluidTags() {
    // this(MOD);
    // }
    //
    // AllFluidTags(NameSpace namespace) {
    // this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
    // }
    //
    // AllFluidTags(NameSpace namespace, String path) {
    // this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
    // }
    //
    // AllFluidTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
    // this(namespace, null, optional, alwaysDatagen);
    // }
    //
    // AllFluidTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
    // ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
    // if (optional) {
    // tag = FluidTags.createOptional(id);
    // } else {
    // tag = FluidTags.bind(id.toString());
    // }
    // if (alwaysDatagen) {
    // REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, prov -> prov.tag(tag));
    // }
    // }
    //
    // public boolean matches(Fluid fluid) {
    // return fluid != null && fluid.is(tag);
    // }
    //
    // public void add(Fluid... values) {
    // REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, prov -> prov.tag(tag)
    // .add(values));
    // }
    //
    // public void includeIn(Tag.Named<Fluid> parent) {
    // REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, prov -> prov.tag(parent)
    // .addTag(tag));
    // }
    //
    // public void includeIn(AllFluidTags parent) {
    // includeIn(parent.tag);
    // }
    //
    // public void includeAll(Tag.Named<Fluid> child) {
    // REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, prov -> prov.tag(tag)
    // .addTag(child));
    // }
    //
    // private static void loadClass() {}
    //
    // }

    public static void register() {
        // AllFluidTags.loadClass();

        // TODO
        // AllItemTags.CREATE_INGOTS.includeIn(AllItemTags.BEACON_PAYMENT);
        // AllItemTags.CREATE_INGOTS.includeIn(Tags.Items.INGOTS);

        // Also honey bottle, but there's none on 1.7.10
        AllItemTags.UPRIGHT_ON_BELT.add(Items.glass_bottle, Items.potionitem, Items.experience_bottle, Items.cake);

        AllBlockTags.WINDMILL_SAILS.add(Blocks.wool);

        // Also bell
        AllBlockTags.BRITTLE.add(Blocks.wooden_door, Blocks.iron_door, Blocks.bed, Blocks.flower_pot, Blocks.cocoa);

        // Also campfires
        AllBlockTags.FAN_TRANSPARENT
            .add(Blocks.fence, Blocks.fence_gate, Blocks.nether_brick_fence, Blocks.iron_bars, Blocks.fire);

        // Also magma blocks, campfires and soul stuff
        AllBlockTags.FAN_HEATERS.add(Blocks.lava, Blocks.flowing_lava, Blocks.fire);
        AllBlockTags.SAFE_NBT.add(Blocks.standing_sign, Blocks.wall_sign);

        // Also observers and targets
        AllBlockTags.WRENCH_PICKUP.add(
            Blocks.rail,
            Blocks.activator_rail,
            Blocks.detector_rail,
            Blocks.golden_rail,
            Blocks.stone_button,
            Blocks.wooden_button,
            Blocks.heavy_weighted_pressure_plate,
            Blocks.light_weighted_pressure_plate,
            Blocks.stone_pressure_plate,
            Blocks.wooden_pressure_plate,
            Blocks.redstone_wire,
            Blocks.redstone_torch,
            Blocks.unlit_redstone_torch,
            Blocks.powered_repeater,
            Blocks.unpowered_repeater,
            Blocks.lever,
            Blocks.powered_comparator,
            Blocks.unpowered_comparator,
            Blocks.piston,
            Blocks.piston_extension,
            Blocks.piston_head,
            Blocks.sticky_piston,
            Blocks.tripwire,
            Blocks.tripwire_hook,
            Blocks.daylight_detector);

        // TODO
        // AllBlockTags.ORE_OVERRIDE_STONE.includeAll(BlockTags.STONE_ORE_REPLACEABLES);
    }

}
