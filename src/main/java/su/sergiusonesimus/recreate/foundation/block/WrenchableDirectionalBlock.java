package su.sergiusonesimus.recreate.foundation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Rotation;
import su.sergiusonesimus.recreate.content.contraptions.wrench.IWrenchable;
import su.sergiusonesimus.recreate.foundation.utility.DirectionHelper;

public class WrenchableDirectionalBlock extends Block implements IWrenchable {

    public WrenchableDirectionalBlock(Material material) {
        super(material);
    }

    @Override
    public int getRotatedBlockMeta(World world, int x, int y, int z, int face) {
        int originalMeta = world.getBlockMetadata(x, y, z);
        Direction facing = getDirection(originalMeta);
        Direction targetedFace = getDirection(face);

        if (facing.getAxis() == targetedFace.getAxis()) return originalMeta;

        Direction newFacing = DirectionHelper.rotateAround(facing, targetedFace.getAxis());
        int newMeta = getMetaFromDirection(newFacing);

        world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
        return newMeta;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        if (placer != null) {
            Direction nearestLookingDirection = Direction.getNearestLookingDirection(placer);
            worldIn.setBlockMetadataWithNotify(
                x,
                y,
                z,
                getMetaFromDirection(
                    placer.isSneaking() ? nearestLookingDirection : nearestLookingDirection.getOpposite()),
                2);
        }
    }

    public int rotate(World world, int x, int y, int z, Rotation rot) {
        int meta = world.getBlockMetadata(x, y, z);
        int newMeta = this.getMetaFromDirection(rot.rotate(this.getDirection(meta)));
        if (newMeta != meta) world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        return newMeta;
    }

    @Override
    public Direction getDirection(int meta) {
        return Direction.from3DDataValue(meta % 6);
    }

    @Override
    public int getMetaFromDirection(Direction direction) {
        return direction.get3DDataValue();
    }

}
