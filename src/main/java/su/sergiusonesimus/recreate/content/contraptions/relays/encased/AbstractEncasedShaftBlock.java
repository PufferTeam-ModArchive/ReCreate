package su.sergiusonesimus.recreate.content.contraptions.relays.encased;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.content.contraptions.base.RotatedPillarKineticBlock;

public class AbstractEncasedShaftBlock extends RotatedPillarKineticBlock {

    public AbstractEncasedShaftBlock(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean hasShaftTowards(IBlockAccess world, int x, int y, int z, Direction face) {
        return face.getAxis() == this.getAxis(world.getBlockMetadata(x, y, z));
    }

}
