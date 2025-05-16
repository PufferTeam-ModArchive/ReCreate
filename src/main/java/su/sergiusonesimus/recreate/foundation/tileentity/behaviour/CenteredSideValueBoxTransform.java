package su.sergiusonesimus.recreate.foundation.tileentity.behaviour;

import java.util.function.BiPredicate;

import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import su.sergiusonesimus.recreate.foundation.utility.Pair;
import su.sergiusonesimus.recreate.util.Direction;
import su.sergiusonesimus.recreate.util.VecHelper;

public class CenteredSideValueBoxTransform extends ValueBoxTransform.Sided {

	private BiPredicate<Pair<Block, Integer>, Direction> allowedDirections;

	public CenteredSideValueBoxTransform() {
		this((b, d) -> true);
	}
	
	public CenteredSideValueBoxTransform(BiPredicate<Pair<Block, Integer>, Direction> allowedDirections) {
		this.allowedDirections = allowedDirections;
	}

	@Override
	protected Vec3 getSouthLocation() {
		return VecHelper.voxelSpace(8, 8, 16);
	}

	@Override
	protected boolean isSideActive(Block block, int meta, Direction direction) {
		return allowedDirections.test(Pair.of(block, meta), direction);
	}

}
