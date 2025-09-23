package su.sergiusonesimus.recreate.zmixin.interfaces;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.Contraption;

public interface IMixinWorldReCreate {

    public abstract World createContraptionWorld(Contraption contraption);

    public abstract World createContraptionWorld(int newSubWorldID, Contraption contraption);

    /**
     * Will get all contraptions intersecting the specified AABB excluding the one passed into it. Args:
     * contraptionToExclude, aabb
     */
    public List<Contraption> getContraptionsWithinAABBExcludingContraption(Contraption contraption, AxisAlignedBB bb);

    public List<Contraption> getContraptionsWithinAABBExcludingContraption(Contraption contraption, AxisAlignedBB bb,
        Predicate<Contraption> selector);

    /**
     * Returns all contraptions of the specified class type which intersect with the AABB. Args: contraptionClass, aabb
     */
    public <T extends Contraption> List<T> getContraptionsWithinAABB(Class<T> contraptionClass, AxisAlignedBB bb);

    public <T extends Contraption> List<T> getContraptionsWithinAABB(Class<T> contraptionClass, AxisAlignedBB bb,
        Predicate<T> selector);

}
