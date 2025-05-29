package su.sergiusonesimus.recreate.foundation.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.Vec3;

import su.sergiusonesimus.recreate.util.AnimationTickHolder;

public class RepeatingSound {

    private String name;
    private float sharedPitch;
    private int repeatDelay;
    private SoundScape scape;
    private float relativeVolume;

    public RepeatingSound(String soundName, SoundScape scape, float sharedPitch, float relativeVolume,
        int repeatDelay) {
        this.name = soundName;
        this.scape = scape;
        this.sharedPitch = sharedPitch;
        this.relativeVolume = relativeVolume;
        this.repeatDelay = Math.max(1, repeatDelay);
    }

    public void tick() {
        if (AnimationTickHolder.getTicks() % repeatDelay != 0) return;

        WorldClient world = Minecraft.getMinecraft().theWorld;
        Vec3 meanPos = scape.getMeanPos();

        world.playSound(
            meanPos.xCoord,
            meanPos.yCoord,
            meanPos.zCoord,
            name,
            scape.getVolume() * relativeVolume,
            sharedPitch,
            true);
    }

}
