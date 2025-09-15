package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.bearing;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.recreate.AllModelTextures;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.foundation.block.ITE;

public class WindmillBearingBlock extends BearingBlock implements ITE<WindmillBearingTileEntity> {

    public WindmillBearingBlock(Material material) {
        super(material);
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
    public Class<WindmillBearingTileEntity> getTileEntityClass() {
        return WindmillBearingTileEntity.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        bearingTop = iconRegister.registerIcon(ReCreate.ID + ":bearing_top_wooden");
        bearingSide = iconRegister.registerIcon(ReCreate.ID + ":windmill_bearing_side");
    }

    @Override
    public AllModelTextures getTexture() {
        return AllModelTextures.WINDMILL_BEARING;
    }

}
