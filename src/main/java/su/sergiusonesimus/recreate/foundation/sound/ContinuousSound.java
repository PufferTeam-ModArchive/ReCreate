package su.sergiusonesimus.recreate.foundation.sound;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

public class ContinuousSound extends PositionedSound implements ITickableSound {

    private float sharedPitch;
    private SoundScape scape;
    private float relativeVolume;
    private boolean donePlaying = false;

    protected ContinuousSound(ResourceLocation soundResource, SoundScape scape, float sharedPitch,
        float relativeVolume) {
        super(soundResource);
        this.scape = scape;
        this.sharedPitch = sharedPitch;
        this.relativeVolume = relativeVolume;
        this.repeat = true;
        this.field_147665_h = 0;
    }

    public void remove() {
        donePlaying = true;
    }

    @Override
    public float getVolume() {
        return scape.getVolume() * relativeVolume;
    }

    @Override
    public float getPitch() {
        return sharedPitch;
    }

    @Override
    public float getXPosF() {
        return (float) scape.getMeanPos().xCoord;
    }

    @Override
    public float getYPosF() {
        return (float) scape.getMeanPos().yCoord;
    }

    @Override
    public float getZPosF() {
        return (float) scape.getMeanPos().zCoord;
    }

    @Override
    public void update() {}

    @Override
    public boolean isDonePlaying() {
        return donePlaying;
    }

}
