package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft;

import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.SimpleKineticTileEntity;

public class ShaftTileEntity extends SimpleKineticTileEntity {
	
	@Override
    public void updateContainingBlockInfo()
    {
        this.blockType = AllBlocks.shaft;
    }

}
