package su.sergiusonesimus.recreate.content.contraptions.goggles;

import java.util.List;

import net.minecraft.util.IChatComponent;

/*
* Implement this Interface in the TileEntity class that wants to add info to the screen
* */
public interface IHaveHoveringInformation {

	default boolean addToTooltip(List<IChatComponent> tooltip, boolean isPlayerSneaking){
		return false;
	}

}
