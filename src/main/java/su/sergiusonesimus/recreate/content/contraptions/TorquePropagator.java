package su.sergiusonesimus.recreate.content.contraptions;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;

public class TorquePropagator {

    static Map<IBlockAccess, Map<Long, KineticNetwork>> networks = new HashMap<>();

    public void onLoadWorld(IBlockAccess world) {
        networks.put(world, new HashMap<>());
        ReCreate.LOGGER.debug("Prepared Kinetic Network Space for dimension " + ((World) world).provider.dimensionId);
    }

    public void onUnloadWorld(IBlockAccess world) {
        networks.remove(world);
        ReCreate.LOGGER.debug("Removed Kinetic Network Space for dimension " + ((World) world).provider.dimensionId);
    }

    public KineticNetwork getOrCreateNetworkFor(KineticTileEntity te) {
        Long id = te.network;
        KineticNetwork network;
        Map<Long, KineticNetwork> map = networks.get(te.getWorldObj());
        if (id == null) return null;

        if (!map.containsKey(id)) {
            network = new KineticNetwork();
            network.id = te.network;
            map.put(id, network);
        }
        network = map.get(id);
        return network;
    }

}
