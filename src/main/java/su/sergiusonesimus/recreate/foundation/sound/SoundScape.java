package su.sergiusonesimus.recreate.foundation.sound;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.foundation.sound.SoundScapes.AmbienceGroup;
import su.sergiusonesimus.recreate.foundation.sound.SoundScapes.PitchGroup;
import su.sergiusonesimus.recreate.util.AnimationTickHolder;
import su.sergiusonesimus.recreate.util.ReCreateMath;
import su.sergiusonesimus.recreate.util.VecHelper;

class SoundScape {

    List<ContinuousSound> continuous;
    List<RepeatingSound> repeating;
    private float pitch;
    private AmbienceGroup group;
    private Vec3 meanPos;
    private PitchGroup pitchGroup;

    public SoundScape(float pitch, AmbienceGroup group) {
        this.pitchGroup = SoundScapes.getGroupFromPitch(pitch);
        this.pitch = pitch;
        this.group = group;
        continuous = new ArrayList<>();
        repeating = new ArrayList<>();
    }

    public SoundScape continuous(String soundName, float relativeVolume, float relativePitch) {
        return add(new ContinuousSound(new ResourceLocation(soundName), this, pitch * relativePitch, relativeVolume));
    }

    public SoundScape repeating(String soundName, float relativeVolume, float relativePitch, int delay) {
        return add(new RepeatingSound(soundName, this, pitch * relativePitch, relativeVolume, delay));
    }

    public SoundScape add(ContinuousSound continuousSound) {
        continuous.add(continuousSound);
        return this;
    }

    public SoundScape add(RepeatingSound repeatingSound) {
        repeating.add(repeatingSound);
        return this;
    }

    public void play() {
        for (ContinuousSound sound : continuous) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(sound);
        }
    }

    public void tick() {
        if (AnimationTickHolder.getTicks() % SoundScapes.UPDATE_INTERVAL == 0) meanPos = null;
        repeating.forEach(RepeatingSound::tick);
    }

    public void remove() {
        continuous.forEach(ContinuousSound::remove);
    }

    public Vec3 getMeanPos() {
        return meanPos == null ? meanPos = determineMeanPos() : meanPos;
    }

    private Vec3 determineMeanPos() {
        meanPos = Vec3.createVectorHelper(0, 0, 0);
        int amount = 0;
        for (ChunkCoordinates blockPos : SoundScapes.getAllLocations(group, pitchGroup)) {
            Vec3 blockCenter = VecHelper.getCenterOf(blockPos);
            meanPos = meanPos.addVector(blockCenter.xCoord, blockCenter.yCoord, blockCenter.zCoord);
            amount++;
        }
        if (amount == 0) return meanPos;
        float scale = 1f / amount;
        return Vec3.createVectorHelper(meanPos.xCoord * scale, meanPos.yCoord * scale, meanPos.zCoord * scale);
    }

    public float getVolume() {
        Entity renderViewEntity = Minecraft.getMinecraft().renderViewEntity;
        float distanceMultiplier = 0;
        if (renderViewEntity != null) {
            Vec3 cameraPos = Vec3
                .createVectorHelper(renderViewEntity.posX, renderViewEntity.posY, renderViewEntity.posZ);
            double distanceTo = cameraPos.distanceTo(getMeanPos());
            distanceMultiplier = (float) ReCreateMath.lerp(distanceTo / SoundScapes.MAX_AMBIENT_SOURCE_DISTANCE, 2, 0);
        }
        int soundCount = SoundScapes.getSoundCount(group, pitchGroup);
        float max = AllConfigs.CLIENT.ambientVolumeCap;
        float argMax = (float) SoundScapes.SOUND_VOLUME_ARG_MAX;
        return MathHelper.clamp_float(soundCount / (argMax * 10f), 0.025f, max) * distanceMultiplier;
    }

}
