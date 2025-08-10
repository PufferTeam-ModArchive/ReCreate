package su.sergiusonesimus.recreate.content.contraptions.relays.encased;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.foundation.block.ITE;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ClutchBlock extends AbstractRedstoneShaftBlock implements ITE<ClutchTileEntity> {
    public ClutchBlock(Material materialIn, boolean isLit) {
        super(materialIn, isLit);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypeWood);
        this.setBlockTextureName("planks_oak");
    }

    @Override
    public Class<ClutchTileEntity> getTileEntityClass() {
        return ClutchTileEntity.class;
    }

    @Override
    public Block getUnlitBlock() {
        return AllBlocks.clutch;
    }

    @Override
    public Block getLitBlock() {
        return AllBlocks.lit_clutch;
    }
}
