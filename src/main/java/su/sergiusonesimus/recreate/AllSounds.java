package su.sergiusonesimus.recreate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.foundation.utility.Couple;
import su.sergiusonesimus.recreate.foundation.utility.Pair;

public class AllSounds {

    public static final Map<ResourceLocation, SoundEntry> entries = new HashMap<>();
    public static final SoundEntry

    // SCHEMATICANNON_LAUNCH_BLOCK = create("schematicannon_launch_block").playExisting("random.explode", .1f,
    // 1.1f).build(),
    //
    // SCHEMATICANNON_FINISH = create("schematicannon_finish").playExisting("note.harp", 1, .7f).build(),

    DEPOT_SLIDE = create("depot_slide").playExisting(Block.soundTypeGravel.getBreakSound(), .125f, 1.5f)
        .build(),

        DEPOT_PLOP = create("depot_plop").playExisting("random.click", .25f, 1.25f)
            .build(),

        FUNNEL_FLAP = create("funnel_flap").playExisting("random.click", .125f, 1.5f)
            .playExisting(Block.soundTypeCloth.getBreakSound(), .0425f, .75f)
            .build(),

        SLIME_ADDED = create("slime_added").playExisting("mob.slime.big")
            .build(),

        MECHANICAL_PRESS_ACTIVATION = create("mechanical_press_activation")
            .playExisting(Block.soundTypeAnvil.func_150496_b(), .125f, 1f)
            .playExisting("random.break", .5f, 1f)
            .build(),

        MECHANICAL_PRESS_ACTIVATION_ON_BELT = create("mechanical_press_activation_belt")
            .playExisting(Block.soundTypeCloth.getBreakSound(), .75f, 1f)
            .playExisting("random.break", .15f, .75f)
            .build(),

        MIXING = create("mixing").playExisting(Block.soundTypeStone.getBreakSound(), .125f, .5f)
            .playExisting(Block.soundTypePiston.getBreakSound(), .125f, .5f)
            .build(),

        CRANKING = create("cranking").playExisting(Block.soundTypeWood.getBreakSound(), .075f, .5f)
            .playExisting("random.click", .025f, .5f)
            .build(),

        WORLDSHAPER_PLACE = create("worldshaper_place").playExisting("note.bd")
            .build(),

        SCROLL_VALUE = create("scroll_value").playExisting("note.snare", .124f, 1f)
            .build(),

        CONFIRM = create("confirm").playExisting("note.harp", 0.5f, 0.8f)
            .build(),

        DENY = create("deny").playExisting("note.bassattack", 1f, 0.5f)
            .build(),

        COGS = create("random.cogs").build(),

        FWOOMP = create("random.fwoomp").build(),

        POTATO_HIT = create("potato_hit").playExisting("random.break", .75f, .75f)
            .playExisting(Block.soundTypeGrass.getBreakSound(), .75f, 1.25f)
            .build(),

        CONTRAPTION_ASSEMBLE = create("contraption_assemble").playExisting("random.door_open", .5f, .5f)
            .playExisting("random.chestopen", .045f, .74f)
            .build(),

        CONTRAPTION_DISASSEMBLE = create("contraption_disassemble").playExisting("random.door_close", .35f, .75f)
            .build(),

        WRENCH_ROTATE = create("wrench_rotate").playExisting("random.door_close", .25f, 1.25f)
            .build(),

        WRENCH_REMOVE = create("wrench_remove").playExisting("random.pop", .25f, .75f)
            .playExisting(Block.soundTypeMetal.getBreakSound(), .25f, .75f)
            .build(),

        CRAFTER_CLICK = create("crafter_click").playExisting(Block.soundTypeMetal.getBreakSound(), .25f, 1)
            .playExisting("random.door_open", .125f, 1)
            .build(),

        CRAFTER_CRAFT = create("crafter_craft").playExisting("random.break", .125f, .75f)
            .build(),

        SANDING_SHORT = create("random.sanding.short").build(),

        SANDING_LONG = create("random.sanding.long").build(),

        CONTROLLER_CLICK = create("controller_click").playExisting("random.click", .35f, 1f)
            .build(),

        CONTROLLER_PUT = create("controller_put").playExisting("random.chestclosed", 1f, 1f)
            .build(),

        CONTROLLER_TAKE = create("controller_take").playExisting("random.pop", 1f, 1f)
            .build(),

        SAW_ACTIVATE_WOOD = create("saw_activate_wood")
            .playExisting(Block.soundTypeWood.getStepResourcePath(), .75f, 1.5f)
            .build(),

        SAW_ACTIVATE_STONE = create("saw_activate_stone")
            .playExisting(Block.soundTypeStone.getStepResourcePath(), .125f, 1.25f)
            .build(),

        BLAZE_MUNCH = create("blaze_munch").playExisting("random.eat", .5f, 1f)
            .build(),

        CRUSHING_1 = create("crushing_1").playExisting(Block.soundTypePiston.getBreakSound())
            .build(),

        CRUSHING_2 = create("crushing_2").playExisting(Block.soundTypeGrass.getBreakSound())
            .build(),

        CRUSHING_3 = create("crushing_3").playExisting(Block.soundTypeMetal.getBreakSound())
            .build(),

        // TODO
        // PECULIAR_BELL_USE = create("peculiar_bell_use").playExisting(SoundEvents.BELL_BLOCK).build(),

        HAUNTED_BELL_CONVERT = create("haunted_bell_convert").build(),

        HAUNTED_BELL_USE = create("haunted_bell_use").build();

    private static SoundEntryBuilder create(String name) {
        return create(ReCreate.asResource(name));
    }

    public static SoundEntryBuilder create(ResourceLocation id) {
        return new SoundEntryBuilder(id);
    }

    public static class SoundEntryBuilder {

        protected ResourceLocation id;
        protected List<Pair<String, Couple<Float>>> wrappedEvents;

        public SoundEntryBuilder(ResourceLocation id) {
            wrappedEvents = new ArrayList<>();
            this.id = id;
        }

        public SoundEntryBuilder playExisting(String name, float volume, float pitch) {
            wrappedEvents.add(Pair.of(name, Couple.create(volume, pitch)));
            return this;
        }

        public SoundEntryBuilder playExisting(String name) {
            return playExisting(name, 1, 1);
        }

        public SoundEntry build() {
            SoundEntry entry = wrappedEvents.isEmpty() ? new CustomSoundEntry(id)
                : new WrappedSoundEntry(id, wrappedEvents);
            entries.put(entry.getId(), entry);
            return entry;
        }

    }

    public static abstract class SoundEntry {

        protected ResourceLocation id;

        public SoundEntry(ResourceLocation id) {
            this.id = id;
        }

        public abstract String getMainSoundName();

        public ResourceLocation getId() {
            return id;
        }

        public void playOnServer(World world, int posX, int posY, int posZ) {
            playOnServer(world, posX, posY, posZ, 1, 1);
        }

        public void playOnServer(World world, int posX, int posY, int posZ, float volume, float pitch) {
            play(world, null, posX, posY, posZ, volume, pitch);
        }

        public void play(World world, EntityPlayer entity, int posX, int posY, int posZ) {
            play(world, entity, posX, posY, posZ, 1, 1);
        }

        public void playFrom(Entity entity) {
            playFrom(entity, 1, 1);
        }

        public void playFrom(Entity entity, float volume, float pitch) {
            entity.playSound(getMainSoundName(), volume, pitch);
        }

        public void play(World world, EntityPlayer entity, int posX, int posY, int posZ, float volume, float pitch) {
            play(world, entity, posX + 0.5, posY + 0.5, posZ + 0.5, volume, pitch);
        }

        public void play(World world, EntityPlayer entity, Vec3 pos, float volume, float pitch) {
            play(world, entity, pos.xCoord, pos.yCoord, pos.zCoord, volume, pitch);
        }

        public abstract void play(World world, EntityPlayer entity, double x, double y, double z, float volume,
            float pitch);

        public void playAt(World world, int posX, int posY, int posZ, float volume, float pitch, boolean fade) {
            playAt(world, posX + .5, posY + .5, posZ + .5, volume, pitch, fade);
        }

        public void playAt(World world, Vec3 pos, float volume, float pitch, boolean fade) {
            playAt(world, pos.xCoord, pos.yCoord, pos.zCoord, volume, pitch, fade);
        }

        public abstract void playAt(World world, double x, double y, double z, float volume, float pitch, boolean fade);

    }

    private static class WrappedSoundEntry extends SoundEntry {

        private List<Pair<String, Couple<Float>>> wrappedEvents;

        public WrappedSoundEntry(ResourceLocation id, List<Pair<String, Couple<Float>>> wrappedEvents) {
            super(id);
            this.wrappedEvents = wrappedEvents;
        }

        @Override
        public String getMainSoundName() {
            return wrappedEvents.get(0)
                .getFirst();
        }

        @Override
        public void play(World world, EntityPlayer entity, double x, double y, double z, float volume, float pitch) {
            for (Pair<String, Couple<Float>> pair : wrappedEvents) {
                Couple<Float> volPitch = pair.getSecond();
                world.playSoundAtEntity(
                    entity,
                    pair.getFirst(),
                    volPitch.getFirst() * volume,
                    volPitch.getSecond() * pitch);
            }
        }

        @Override
        public void playAt(World world, double x, double y, double z, float volume, float pitch, boolean fade) {
            for (Pair<String, Couple<Float>> pair : wrappedEvents) {
                Couple<Float> volPitch = pair.getSecond();
                world.playSound(
                    x,
                    y,
                    z,
                    pair.getFirst(),
                    volPitch.getFirst() * volume,
                    volPitch.getSecond() * pitch,
                    fade);
            }
        }
    }

    private static class CustomSoundEntry extends SoundEntry {

        public CustomSoundEntry(ResourceLocation id) {
            super(id);
        }

        @Override
        public String getMainSoundName() {
            return id.toString();
        }

        @Override
        public void play(World world, EntityPlayer entity, double x, double y, double z, float volume, float pitch) {
            world.playSoundAtEntity(entity, id.toString(), volume, pitch);
        }

        @Override
        public void playAt(World world, double x, double y, double z, float volume, float pitch, boolean fade) {
            world.playSound(x, y, z, id.toString(), volume, pitch, fade);
        }

    }

}
