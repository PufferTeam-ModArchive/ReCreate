package su.sergiusonesimus.recreate.content.contraptions.relays.encased;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.content.contraptions.RotationPropagator;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;
import su.sergiusonesimus.recreate.foundation.block.ITE;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class GearshiftBlock extends AbstractRedstoneShaftBlock implements ITE<GearshiftTileEntity> {
    public GearshiftBlock(Material materialIn, boolean isLit) {
        super(materialIn, isLit);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypeWood);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return ReCreate.proxy.getGearshiftBlockRenderID();
    }
}
