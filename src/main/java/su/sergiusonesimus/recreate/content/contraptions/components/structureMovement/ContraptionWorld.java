package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.metaworlds.api.SubWorld;
import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.world.IMixinWorld;
import su.sergiusonesimus.recreate.foundation.networking.AllPackets;

public interface ContraptionWorld extends SubWorld {

    public default void onContraptionStalled() {
        if (((World) this).isRemote) return;
        AllPackets.CHANNEL.sendToAll(
            new ContraptionStallPacket(
                this.getSubWorldID(),
                this.getTranslationX(),
                this.getTranslationY(),
                this.getTranslationZ(),
                getStalledAngle()));
    }

    @SideOnly(Side.CLIENT)
    static void handleStallPacket(ContraptionStallPacket packet) {
        World subworld = ((IMixinWorld) Minecraft.getMinecraft().theWorld).getSubWorld(packet.subworldID);
        if (!(subworld instanceof ContraptionWorld)) return;
        ((ContraptionWorld) subworld).handleStallInformation(packet.x, packet.y, packet.z, packet.angle);
    }

    public void handleStallInformation(float x, float y, float z, float angle);

    public static boolean isContraption(World world) {
        return world instanceof ContraptionWorld;
    }

    public Contraption getContraption();

    public void setContraption(Contraption contraption);

    public default boolean supportsTerrainCollision() {
        return this instanceof TranslatingContraption;
    }

    public void addSittingPassenger(Entity passenger, int seatIndex);

    public void removePassenger(Entity passenger);

    public Vec3 getPassengerPosition(Entity passenger, float partialTicks);

    public boolean canAddPassenger(Entity passenger);

    public void tickActors();

    public void ejectPassengers();

    public boolean isStalled();

    public void setStalled(boolean isStalled);

    public float getStalledAngle();

    public List<Entity> getPassengers();

}
