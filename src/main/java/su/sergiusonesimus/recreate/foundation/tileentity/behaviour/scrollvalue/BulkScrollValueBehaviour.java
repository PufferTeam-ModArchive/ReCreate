package su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue;

import java.util.List;
import java.util.function.Function;

import net.minecraft.util.IChatComponent;
import su.sergiusonesimus.recreate.foundation.tileentity.SmartTileEntity;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.ValueBoxTransform;

public class BulkScrollValueBehaviour extends ScrollValueBehaviour {

	Function<SmartTileEntity, List<? extends SmartTileEntity>> groupGetter;

	public BulkScrollValueBehaviour(IChatComponent label, SmartTileEntity te, ValueBoxTransform slot,
									Function<SmartTileEntity, List<? extends SmartTileEntity>> groupGetter) {
		super(label, te, slot);
		this.groupGetter = groupGetter;
	}

	List<? extends SmartTileEntity> getBulk() {
		return groupGetter.apply(tileEntity);
	}

}
