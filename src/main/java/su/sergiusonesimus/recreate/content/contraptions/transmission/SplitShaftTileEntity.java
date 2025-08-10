package su.sergiusonesimus.recreate.content.contraptions.transmission;

import net.minecraft.block.Block;
import su.sergiusonesimus.recreate.content.contraptions.KineticNetwork;
import su.sergiusonesimus.recreate.content.contraptions.base.DirectionalShaftHalvesTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.SimpleKineticTileEntity;
import su.sergiusonesimus.recreate.util.Direction;

public abstract class SplitShaftTileEntity extends DirectionalShaftHalvesTileEntity {

    public abstract float getRotationSpeedModifier(Direction face);
}
