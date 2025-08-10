package su.sergiusonesimus.recreate.content.contraptions.transmission;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.AbstractShaftBlock;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftTileEntity;
import su.sergiusonesimus.recreate.foundation.block.ITE;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GearshiftBlock extends AbstractRedstoneShaftBlock implements ITE<GearshiftTileEntity> {
    public GearshiftBlock(Material materialIn, boolean isLit) {
        super(materialIn, isLit);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypeWood);
        this.setBlockTextureName("planks_oak");
    }

    @Override
    public Class<GearshiftTileEntity> getTileEntityClass() {
        return GearshiftTileEntity.class;
    }

    @Override
    public Block getUnlitBlock() {
        return AllBlocks.gearshift;
    }

    @Override
    public Block getLitBlock() {
        return AllBlocks.lit_gearshift;
    }
}
