package su.sergiusonesimus.recreate.content.contraptions.relays.elementary;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import su.sergiusonesimus.recreate.content.contraptions.base.RotatedPillarKineticBlock;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.wrench.IWrenchableWithBracket;
import su.sergiusonesimus.recreate.foundation.block.ITE;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.util.Direction;

public abstract class AbstractShaftBlock extends RotatedPillarKineticBlock
	implements ITE<ShaftTileEntity>, IWrenchableWithBracket {

	public AbstractShaftBlock(Material materialIn) {
		super(materialIn);
	}

	@Override
    public boolean onWrenched(World world, int x, int y, int z, int face, EntityPlayer player) {
		return IWrenchableWithBracket.super.onWrenched(world, x, y, z, face, player);
	}

	@Override
    public void onBlockPreDestroy(World worldIn, int x, int y, int z, int meta)
    {
		removeBracket(worldIn, x, y, z, true).ifPresent(stack -> {
			EntityItem bracketStack = new EntityItem(worldIn, x, y, z, stack);
			worldIn.spawnEntityInWorld(bracketStack);
		});
		super.onBlockPreDestroy(worldIn, x, y, z, meta);
    }

	// IRotate:

	@Override
	public boolean hasShaftTowards(IBlockAccess world, int x, int y, int z, Direction face) {
		return face.getAxis() == this.getAxis(world.getBlockMetadata(x, y, z));
	}

	@Override
	public Optional<ItemStack> removeBracket(IBlockAccess world, int x, int y, int z, boolean inOnReplacedContext) {
		BracketedTileEntityBehaviour behaviour = TileEntityBehaviour.get(world, x, y, z, BracketedTileEntityBehaviour.TYPE);
		if (behaviour == null)
			return Optional.empty();
		Block bracket = behaviour.getBracket();
		int bracketMeta = behaviour.getBracketMeta();
		behaviour.removeBracket(inOnReplacedContext);
		if (bracket == Blocks.air)
			return Optional.empty();
		return Optional.of(new ItemStack(bracket, 1, bracketMeta));
	}

	@Override
	public Class<ShaftTileEntity> getTileEntityClass() {
		return ShaftTileEntity.class;
	}

}
