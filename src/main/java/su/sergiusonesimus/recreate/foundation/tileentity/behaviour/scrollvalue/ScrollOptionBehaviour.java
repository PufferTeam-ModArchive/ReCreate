package su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue;

import net.minecraft.util.IChatComponent;

import su.sergiusonesimus.recreate.foundation.tileentity.SmartTileEntity;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.ValueBoxTransform;

public class ScrollOptionBehaviour<E extends Enum<E> & INamedIconOptions> extends ScrollValueBehaviour {

    private E[] options;

    public ScrollOptionBehaviour(Class<E> enum_, IChatComponent label, SmartTileEntity te, ValueBoxTransform slot) {
        super(label, te, slot);
        options = enum_.getEnumConstants();
        between(0, options.length - 1);
        withStepFunction((c) -> -1);
    }

    INamedIconOptions getIconForSelected() {
        return get();
    }

    public E get() {
        return options[scrollableValue];
    }

}
