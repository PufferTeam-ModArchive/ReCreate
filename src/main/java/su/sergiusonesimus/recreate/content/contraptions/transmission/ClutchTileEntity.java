package su.sergiusonesimus.recreate.content.contraptions.transmission;

import su.sergiusonesimus.recreate.util.Direction;

public class ClutchTileEntity extends SplitShaftTileEntity{
    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            if (face != getSourceFacing() && ((AbstractRedstoneShaftBlock) blockType).isPowered())
                return 0;
        }
        return 1;
    }
}
