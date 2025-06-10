package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

import su.sergiusonesimus.metaworlds.world.SubWorldServer;

public class ContraptionWorldServer extends SubWorldServer implements ContraptionWorld {

    private Contraption contraption;

    public ContraptionWorldServer(WorldServer parentWorld, int newSubWorldID, MinecraftServer par1MinecraftServer,
        ISaveHandler par2ISaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings, Profiler par6Profiler,
        Contraption contraption) {
        super(
            parentWorld,
            newSubWorldID,
            par1MinecraftServer,
            par2ISaveHandler,
            par3Str,
            par4,
            par5WorldSettings,
            par6Profiler);
        this.contraption = contraption;
    }

    @Override
    public Contraption getContraption() {
        return contraption;
    }

    @Override
    public void setContraption(Contraption contraption) {
        this.contraption = contraption;
    }

    @Override
    public void tick() {
        contraption.tick();
        super.tick();
    }

    @Override
    public void handleStallInformation(float x, float y, float z, float angle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addSittingPassenger(Entity passenger, int seatIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removePassenger(Entity passenger) {
        // TODO Auto-generated method stub

    }

    @Override
    public Vec3 getPassengerPosition(Entity passenger, float partialTicks) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canAddPassenger(Entity passenger) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void tickActors() {
        // TODO Auto-generated method stub

    }

    @Override
    public void ejectPassengers() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isStalled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setStalled(boolean isStalled) {
        // TODO Auto-generated method stub

    }

    @Override
    public float getStalledAngle() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Entity> getPassengers() {
        // TODO Auto-generated method stub
        return null;
    }

}
