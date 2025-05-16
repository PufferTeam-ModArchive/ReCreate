package su.sergiusonesimus.recreate.api.event;

import java.lang.reflect.Type;
import java.util.Map;

import cpw.mods.fml.common.eventhandler.Event;
import su.sergiusonesimus.recreate.foundation.tileentity.SmartTileEntity;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.BehaviourType;

/**
 * Event that is fired just before a SmartTileEntity is being deserealized <br>
 * Also if a new one is placed<br>
 * Use it to attach a new {@link TileEntityBehaviour} or replace existing ones
 * (with caution) <br>
 * <br>
 * Actual setup of the behaviours internal workings and data should be done in
 * TileEntityBehaviour#read() and TileEntityBehaviour#initialize()
 * respectively.<br>
 * <br>
 * Because of the earlyness of this event, the added behaviours will have access
 * to the initial nbt read (unless the TE was placed, not loaded), thereby
 * allowing tiles to store and retrieve data for injected behaviours
 */
public class TileEntityBehaviourEvent<T extends SmartTileEntity> extends Event {

    private T smartTileEntity;
    private Map<BehaviourType<?>, TileEntityBehaviour> behaviours;

    public TileEntityBehaviourEvent(T tileEntity, Map<BehaviourType<?>, TileEntityBehaviour> behaviours) {
        smartTileEntity = tileEntity;
        this.behaviours = behaviours;
    }

    public Type getGenericType() {
        return smartTileEntity.getClass();
    }

    public void attach(TileEntityBehaviour behaviour) {
        behaviours.put(behaviour.getType(), behaviour);
    }

    public TileEntityBehaviour remove(BehaviourType<?> type) {
        return behaviours.remove(type);
    }

    public T getTileEntity() {
        return smartTileEntity;
    }

    public int getMeta() {
        return smartTileEntity.getBlockMetadata();
    }

}
