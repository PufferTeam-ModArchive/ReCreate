package su.sergiusonesimus.recreate.content.contraptions.transmission;

import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.SimpleKineticTileEntity;
import su.sergiusonesimus.recreate.util.Direction;

public class GearshiftTileEntity extends SplitShaftTileEntity {

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            if (face != getSourceFacing() && ((AbstractRedstoneShaftBlock) blockType).isPowered()) {
                return -1;
            }
        }
        return 1;
    }

}
