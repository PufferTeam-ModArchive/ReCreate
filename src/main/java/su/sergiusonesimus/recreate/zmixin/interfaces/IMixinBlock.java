package su.sergiusonesimus.recreate.zmixin.interfaces;

import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public interface IMixinBlock {

    public List<AxisAlignedBB> getSelectedBoundingBoxesList(World worldIn, int x, int y, int z);

}
