package su.sergiusonesimus.recreate.content.contraptions.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import su.sergiusonesimus.recreate.foundation.item.ItemDescription.Palette;
import su.sergiusonesimus.recreate.util.Direction;
import su.sergiusonesimus.recreate.util.Rotation;

public abstract class KineticBlock extends Block implements IRotate {

    protected static final Palette color = Palette.Red;

    public KineticBlock(Material materialIn) {
        super(materialIn);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        // onBlockAdded is useless for init, as sometimes the TE gets re-instantiated

        // however, if a block change occurs that does not change kinetic connections,
        // we can prevent a major re-propagation here

        TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (tileEntity instanceof KineticTileEntity) {
            KineticTileEntity kineticTileEntity = (KineticTileEntity) tileEntity;
            kineticTileEntity.preventSpeedUpdate = 0;
            int meta = worldIn.getBlockMetadata(x, y, z);

            if (tileEntity.blockType != this || tileEntity.blockMetadata != meta) return;
            if (this.hasTileEntity() != tileEntity.blockType.hasTileEntity()) return;
            if (!areStatesKineticallyEquivalent(tileEntity.blockType, tileEntity.blockMetadata, this, meta)) return;

            kineticTileEntity.preventSpeedUpdate = 2;
        }
        updateTileEntity(worldIn, x, y, z);
    }

    @Override
    public boolean hasShaftTowards(IBlockAccess world, int x, int y, int z, Direction face) {
        return false;
    }

    protected boolean areStatesKineticallyEquivalent(Block oldBlock, int oldMeta, Block newBlock, int newMeta) {
        if (oldBlock != newBlock) return false;
        return getAxis(newMeta) == getAxis(oldMeta);
    }

    public void updateTileEntity(World worldIn, int x, int y, int z) {
        if (worldIn.isRemote) return;

        TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (!(tileEntity instanceof KineticTileEntity)) return;
        KineticTileEntity kte = (KineticTileEntity) tileEntity;

        if (kte.preventSpeedUpdate > 0) {
            kte.preventSpeedUpdate--;
            return;
        }

        // Remove previous information when block is added
        kte.warnOfMovement();
        kte.clearKineticInformation();
        kte.updateSpeed = true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        if (worldIn.isRemote) return;

        TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (!(tileEntity instanceof KineticTileEntity)) return;

        KineticTileEntity kte = (KineticTileEntity) tileEntity;
        kte.effects.queueRotationIndicators();
    }

    public float getParticleTargetRadius() {
        return .65f;
    }

    public float getParticleInitialRadius() {
        return .75f;
    }
    
    public abstract int rotate(World world, int x, int y, int z, Rotation rot);

	@Override
    public int getDamageValue(World worldIn, int x, int y, int z)
    {
        return 0;
    }

}
