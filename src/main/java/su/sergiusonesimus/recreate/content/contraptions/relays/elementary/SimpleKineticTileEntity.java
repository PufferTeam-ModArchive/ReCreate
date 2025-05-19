package su.sergiusonesimus.recreate.content.contraptions.relays.elementary;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

import su.sergiusonesimus.recreate.content.contraptions.base.IRotate;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel.ICogWheel;
import su.sergiusonesimus.recreate.util.ReCreateMath;

public class SimpleKineticTileEntity extends KineticTileEntity {

    @Override
    public AxisAlignedBB makeRenderBoundingBox() {
        int inflator = 1;
        return AxisAlignedBB
            .getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1)
            .expand(inflator, inflator, inflator);
    }

    @Override
    public List<ChunkCoordinates> addPropagationLocations(IRotate block, int meta, List<ChunkCoordinates> neighbours) {
        if (!ICogWheel.isLargeCog((Block) block)) return super.addPropagationLocations(block, meta, neighbours);

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (ReCreateMath.equal(
                        Vec3.createVectorHelper(x, y, z)
                            .squareDistanceTo(0, 0, 0),
                        Vec3.createVectorHelper(0, 0, 0)
                            .squareDistanceTo(1, 1, 0)))
                        neighbours.add(new ChunkCoordinates(this.xCoord + x, this.yCoord + y, this.zCoord + z));
                }
            }
        }
        return neighbours;
    }

    @Override
    protected boolean isNoisy() {
        return false;
    }

}
