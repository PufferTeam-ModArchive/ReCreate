package su.sergiusonesimus.recreate.foundation.utility.outliner;

import net.minecraft.util.AxisAlignedBB;
import su.sergiusonesimus.recreate.util.ReCreateMath;

public class ChasingAABBOutline extends AABBOutline {

	AxisAlignedBB targetBB;
	AxisAlignedBB prevBB;

	public ChasingAABBOutline(AxisAlignedBB bb) {
		super(bb);
		prevBB = bb.expand(0, 0, 0);
		targetBB = bb.expand(0, 0, 0);
	}

	public void target(AxisAlignedBB target) {
		targetBB = target;
	}

	@Override
	public void tick() {
		prevBB = bb;
		setBounds(interpolateBBs(bb, targetBB, .5f));
	}

	@Override
	public void render(float pt) {
		renderBB(interpolateBBs(prevBB, bb, pt));
	}

	private static AxisAlignedBB interpolateBBs(AxisAlignedBB current, AxisAlignedBB target, float pt) {
		return AxisAlignedBB.getBoundingBox(ReCreateMath.lerp(pt, current.minX, target.minX),
			ReCreateMath.lerp(pt, current.minY, target.minY), ReCreateMath.lerp(pt, current.minZ, target.minZ),
			ReCreateMath.lerp(pt, current.maxX, target.maxX), ReCreateMath.lerp(pt, current.maxY, target.maxY),
			ReCreateMath.lerp(pt, current.maxZ, target.maxZ));
	}

}
