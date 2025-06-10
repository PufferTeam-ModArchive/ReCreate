package su.sergiusonesimus.recreate.foundation.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.world.IBlockAccess;

public class WorldAttached<T> {

    static List<Map<IBlockAccess, ?>> allMaps = new ArrayList<>();
    Map<IBlockAccess, T> attached;
    private final Function<IBlockAccess, T> factory;

    public WorldAttached(Function<IBlockAccess, T> factory) {
        this.factory = factory;
        attached = new HashMap<>();
        allMaps.add(attached);
    }

    public static void invalidateWorld(IBlockAccess world) {
        allMaps.forEach(m -> m.remove(world));
    }

    @Nonnull
    public T get(IBlockAccess world) {
        T t = attached.get(world);
        if (t != null) return t;
        T entry = factory.apply(world);
        put(world, entry);
        return entry;
    }

    public void put(IBlockAccess world, T entry) {
        attached.put(world, entry);
    }

}
