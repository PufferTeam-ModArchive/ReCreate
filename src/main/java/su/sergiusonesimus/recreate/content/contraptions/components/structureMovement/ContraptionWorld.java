package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.api.SubWorld;

public interface ContraptionWorld extends SubWorld {

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

    public void ejectPassengers();

    public boolean isStalled();

    public void setStalled(boolean isStalled);

    public List<Entity> getPassengers();

}
