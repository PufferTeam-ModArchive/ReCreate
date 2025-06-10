package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import su.sergiusonesimus.recreate.content.contraptions.goggles.IHaveGoggleInformation;
import su.sergiusonesimus.recreate.foundation.item.TooltipHelper;
import su.sergiusonesimus.recreate.foundation.utility.Lang;

public interface IDisplayAssemblyExceptions {

    default boolean addExceptionToTooltip(List<IChatComponent> tooltip) {
        AssemblyException e = getLastAssemblyException();
        if (e == null) return false;

        if (!tooltip.isEmpty()) tooltip.add(new ChatComponentText(""));

        IChatComponent exception = Lang.translate("gui.assembly.exception");
        exception.getChatStyle()
            .setColor(EnumChatFormatting.GOLD);
        tooltip.add(
            IHaveGoggleInformation.componentSpacing.createCopy()
                .appendSibling(exception));

        String text = e.component.getUnformattedText();
        Arrays.stream(text.split("\n"))
            .forEach(
                l -> TooltipHelper.cutStringTextComponent(l, EnumChatFormatting.GRAY, EnumChatFormatting.WHITE)
                    .forEach(
                        c -> tooltip.add(
                            IHaveGoggleInformation.componentSpacing.createCopy()
                                .appendSibling(c))));

        return true;
    }

    AssemblyException getLastAssemblyException();

}
