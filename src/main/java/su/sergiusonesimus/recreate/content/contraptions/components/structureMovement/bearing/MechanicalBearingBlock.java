package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.bearing;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.foundation.block.ITE;

public class MechanicalBearingBlock extends BearingBlock implements ITE<MechanicalBearingTileEntity> {

    public MechanicalBearingBlock(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (player.isPlayerSleeping() || player.isRiding() || player.isSneaking()) return false;
        if (player.getHeldItem() == null) {
            if (worldIn.isRemote) return true;
            withTileEntityDo(worldIn, x, y, z, te -> {
                if (te.running) {
                    te.disassemble();
                    return;
                }
                te.assembleNextTick = true;
            });
            return true;
        }
        return false;
    }

    @Override
    public Class<MechanicalBearingTileEntity> getTileEntityClass() {
        return MechanicalBearingTileEntity.class;
    }

}
