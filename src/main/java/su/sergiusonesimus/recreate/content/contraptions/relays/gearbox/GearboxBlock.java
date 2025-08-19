package su.sergiusonesimus.recreate.content.contraptions.relays.gearbox;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.content.contraptions.relays.encased.AbstractEncasedShaftBlock;
import su.sergiusonesimus.recreate.foundation.block.ITE;

public class GearboxBlock extends AbstractEncasedShaftBlock implements ITE<GearboxTileEntity> {

    public static IIcon gearboxTop;
    public static IIcon gearboxSide;

    public GearboxBlock(Material materialIn) {
        super(materialIn);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypeWood);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setBlockTextureName("planks_spruce");

    }

    @Override
    public Class<GearboxTileEntity> getTileEntityClass() {
        return GearboxTileEntity.class;
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
    public boolean hasShaftTowards(IBlockAccess world, int x, int y, int z, Direction face) {
        int meta = world.getBlockMetadata(x, y, z);
        if (face.getAxis() == this.getAxis(meta) || face.getAxis() == this.getSecondAxis(meta)) {
            return true;
        }
        return false;
    }

    public Direction.Axis getSecondAxis(int meta) {
        switch (meta) {
            default:
            case 0:
                return Direction.Axis.Z;
            case 1:
                return Direction.Axis.Y;
            case 2:
                return Direction.Axis.X;
        }
    }

    @Override
    public int getRenderType() {
        return ReCreate.proxy.getGearboxBlockRenderID();
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        gearboxTop = reg.registerIcon(ReCreate.ID + ":gearbox_top");
        gearboxSide = reg.registerIcon(ReCreate.ID + ":gearbox");
    }
}
