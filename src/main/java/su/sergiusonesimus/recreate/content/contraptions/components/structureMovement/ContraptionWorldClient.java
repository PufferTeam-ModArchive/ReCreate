package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import java.util.List;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;

import su.sergiusonesimus.metaworlds.client.multiplayer.SubWorldClient;

public class ContraptionWorldClient extends SubWorldClient implements ContraptionWorld {

    private Contraption contraption;

    public ContraptionWorldClient(WorldClient parentWorld, int newSubWorldID, NetHandlerPlayClient par1NetClientHandler,
        WorldSettings par2WorldSettings, int par3, EnumDifficulty par4, Profiler par5Profiler,
        Contraption contraption) {
        super(parentWorld, newSubWorldID, par1NetClientHandler, par2WorldSettings, par3, par4, par5Profiler);
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
