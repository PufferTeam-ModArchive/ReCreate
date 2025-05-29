package su.sergiusonesimus.recreate.foundation.sound;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;

import su.sergiusonesimus.recreate.AllSounds;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.foundation.utility.Pair;
import su.sergiusonesimus.recreate.util.AnimationTickHolder;

public class SoundScapes {

    static final int MAX_AMBIENT_SOURCE_DISTANCE = 16;
    static final int UPDATE_INTERVAL = 5;
    static final int SOUND_VOLUME_ARG_MAX = 15;

    public enum AmbienceGroup {

        KINETIC(SoundScapes::kinetic),
        COG(SoundScapes::cogwheel),
        CRUSHING(SoundScapes::crushing),
        MILLING(SoundScapes::milling),

        ;

        private BiFunction<Float, AmbienceGroup, SoundScape> factory;

        private AmbienceGroup(BiFunction<Float, AmbienceGroup, SoundScape> factory) {
            this.factory = factory;
        }

        public SoundScape instantiate(float pitch) {
            return factory.apply(pitch, this);
        }

    }

    private static SoundScape kinetic(float pitch, AmbienceGroup group) {
        return new SoundScape(pitch, group).continuous("minecraft:minecart.inside", .25f, 1);
    }

    private static SoundScape cogwheel(float pitch, AmbienceGroup group) {
        return new SoundScape(pitch, group).continuous(AllSounds.COGS.getMainSoundName(), 1.5f, 1);
    }

    private static SoundScape crushing(float pitch, AmbienceGroup group) {
        return new SoundScape(pitch, group).repeating(AllSounds.CRUSHING_1.getMainSoundName(), 1.545f, .75f, 1)
            .repeating(AllSounds.CRUSHING_2.getMainSoundName(), 0.425f, .75f, 2)
            .repeating(AllSounds.CRUSHING_3.getMainSoundName(), 2f, 1.75f, 2);
    }

    private static SoundScape milling(float pitch, AmbienceGroup group) {
        return new SoundScape(pitch, group).repeating(AllSounds.CRUSHING_1.getMainSoundName(), 1.545f, .75f, 1)
            .repeating(AllSounds.CRUSHING_2.getMainSoundName(), 0.425f, .75f, 2);
    }

    enum PitchGroup {
        VERY_LOW,
        LOW,
        NORMAL,
        HIGH,
        VERY_HIGH
    }

    private static Map<AmbienceGroup, Map<PitchGroup, Set<ChunkCoordinates>>> counter = new IdentityHashMap<>();
    private static Map<Pair<AmbienceGroup, PitchGroup>, SoundScape> activeSounds = new HashMap<>();

    public static void play(AmbienceGroup group, int posX, int posY, int posZ, float pitch) {
        play(group, new ChunkCoordinates(posX, posY, posZ), pitch);
    }

    public static void play(AmbienceGroup group, ChunkCoordinates pos, float pitch) {
        if (!AllConfigs.CLIENT.enableAmbientSounds) return;
        if (!outOfRange(pos)) addSound(group, pos, pitch);
    }

    public static void tick() {
        activeSounds.values()
            .forEach(SoundScape::tick);

        if (AnimationTickHolder.getTicks() % UPDATE_INTERVAL != 0) return;

        boolean disable = !AllConfigs.CLIENT.enableAmbientSounds;
        for (Iterator<Entry<Pair<AmbienceGroup, PitchGroup>, SoundScape>> iterator = activeSounds.entrySet()
            .iterator(); iterator.hasNext();) {

            Entry<Pair<AmbienceGroup, PitchGroup>, SoundScape> entry = iterator.next();
            Pair<AmbienceGroup, PitchGroup> key = entry.getKey();
            SoundScape value = entry.getValue();

            if (disable || getSoundCount(key.getFirst(), key.getSecond()) == 0) {
                value.remove();
                iterator.remove();
            }
        }

        counter.values()
            .forEach(
                m -> m.values()
                    .forEach(Set::clear));
    }

    private static void addSound(AmbienceGroup group, ChunkCoordinates pos, float pitch) {
        PitchGroup groupFromPitch = getGroupFromPitch(pitch);
        Set<ChunkCoordinates> set = counter.computeIfAbsent(group, ag -> new IdentityHashMap<>())
            .computeIfAbsent(groupFromPitch, pg -> new HashSet<>());
        set.add(pos);

        Pair<AmbienceGroup, PitchGroup> pair = Pair.of(group, groupFromPitch);
        activeSounds.computeIfAbsent(pair, $ -> {
            SoundScape soundScape = group.instantiate(pitch);
            soundScape.play();
            return soundScape;
        });
    }

    public static void invalidateAll() {
        counter.clear();
        activeSounds.forEach(($, sound) -> sound.remove());
        activeSounds.clear();
    }

    protected static boolean outOfRange(ChunkCoordinates pos) {
        return Math.sqrt(getCameraPos().getDistanceSquaredToChunkCoordinates(pos)) >= MAX_AMBIENT_SOURCE_DISTANCE;
    }

    protected static ChunkCoordinates getCameraPos() {
        Entity renderViewEntity = Minecraft.getMinecraft().renderViewEntity;
        if (renderViewEntity == null) return new ChunkCoordinates(0, 0, 0);
        return new ChunkCoordinates(
            (int) Math.floor(renderViewEntity.posX),
            (int) Math.floor(renderViewEntity.posY + renderViewEntity.getEyeHeight()),
            (int) Math.floor(renderViewEntity.posZ));
    }

    public static int getSoundCount(AmbienceGroup group, PitchGroup pitchGroup) {
        return getAllLocations(group, pitchGroup).size();
    }

    public static Set<ChunkCoordinates> getAllLocations(AmbienceGroup group, PitchGroup pitchGroup) {
        return counter.getOrDefault(group, Collections.emptyMap())
            .getOrDefault(pitchGroup, Collections.emptySet());
    }

    public static PitchGroup getGroupFromPitch(float pitch) {
        if (pitch < .70) return PitchGroup.VERY_LOW;
        if (pitch < .90) return PitchGroup.LOW;
        if (pitch < 1.10) return PitchGroup.NORMAL;
        if (pitch < 1.30) return PitchGroup.HIGH;
        return PitchGroup.VERY_HIGH;
    }

}
