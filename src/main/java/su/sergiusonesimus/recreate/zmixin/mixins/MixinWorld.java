package su.sergiusonesimus.recreate.zmixin.mixins;

import java.util.List;
import java.util.Map;

import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import su.sergiusonesimus.recreate.zmixin.interfaces.IMixinWorldReCreate;

@Mixin(World.class)
public abstract class MixinWorld implements IMixinWorldReCreate {

    @Shadow(remap = false)
    public Map<Integer, World> childSubWorlds;

    @Shadow(remap = true)
    public List playerEntities;

    @Shadow(remap = true)
    public WorldInfo worldInfo;

}
