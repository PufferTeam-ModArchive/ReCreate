package su.sergiusonesimus.recreate.content.contraptions.goggles;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import su.sergiusonesimus.recreate.foundation.utility.Lang;
import su.sergiusonesimus.recreate.util.TextHelper;

/*
 * Implement this Interface in the TileEntity class that wants to add info to the screen
 */
public interface IHaveGoggleInformation {

    Format numberFormat = new Format();
    String spacing = "    ";
    IChatComponent componentSpacing = new ChatComponentText(spacing);

    /**
     * this method will be called when looking at a TileEntity that implemented this
     * interface
     *
     * @return {@code true} if the tooltip creation was successful and should be displayed,
     *         or {@code false} if the overlay should not be displayed
     */
    default boolean addToGoggleTooltip(List<IChatComponent> tooltip, boolean isPlayerSneaking) {
        return false;
    }

    static String format(double d) {
        return numberFormat.get()
            .format(d)
            .replace("\u00A0", " ");
    }

    default boolean containedFluidTooltip(List<IChatComponent> tooltip, boolean isPlayerSneaking,
        LazyOptional<IFluidHandler> handler) {
        tooltip.add(
            componentSpacing.createCopy()
                .appendSibling(Lang.translate("gui.goggles.fluid_container")));
        ChatComponentTranslation mb = Lang.translate("generic.unit.millibuckets");
        Optional<IFluidHandler> resolve = handler.resolve();
        if (!resolve.isPresent()) return false;

        IFluidHandler tank = resolve.get();
        int tanksAmount = tank.getTankInfo(ForgeDirection.UNKNOWN).length;
        if (tanksAmount == 0) return false;

        IChatComponent indent = new ChatComponentText(spacing + " ");

        boolean isEmpty = true;
        for (int i = 0; i < tanksAmount; i++) {
            FluidStack fluidStack = tank.getTankInfo(ForgeDirection.UNKNOWN)[i].fluid;
            if (fluidStack.amount == 0) continue;

            IChatComponent fluidName = new ChatComponentText(fluidStack.getLocalizedName());
            fluidName.getChatStyle()
                .setColor(EnumChatFormatting.GRAY);
            IChatComponent contained = new ChatComponentText(format(fluidStack.amount)).appendSibling(mb);
            contained.getChatStyle()
                .setColor(EnumChatFormatting.GOLD);
            IChatComponent slash = new ChatComponentText(" / ");
            slash.getChatStyle()
                .setColor(EnumChatFormatting.GRAY);
            IChatComponent capacity = new ChatComponentText(
                format(tank.getTankInfo(ForgeDirection.UNKNOWN)[i].capacity)).appendSibling(mb);
            capacity.getChatStyle()
                .setColor(EnumChatFormatting.DARK_GRAY);

            tooltip.add(
                indent.createCopy()
                    .appendSibling(fluidName));
            tooltip.add(
                indent.createCopy()
                    .appendSibling(contained)
                    .appendSibling(slash)
                    .appendSibling(capacity));

            isEmpty = false;
        }

        if (tanksAmount > 1) {
            if (isEmpty) tooltip.remove(tooltip.size() - 1);
            return true;
        }

        if (!isEmpty) return true;

        IChatComponent capacity = Lang.translate("gui.goggles.fluid_container.capacity");
        capacity.getChatStyle()
            .setColor(EnumChatFormatting.GRAY);
        IChatComponent amount = new ChatComponentText(format(tank.getTankInfo(ForgeDirection.UNKNOWN)[0].capacity))
            .appendSibling(mb);
        amount.getChatStyle()
            .setColor(EnumChatFormatting.GOLD);

        tooltip.add(
            indent.createCopy()
                .appendSibling(capacity)
                .appendSibling(amount));
        return true;
    }

    class Format {

        private NumberFormat format = NumberFormat.getNumberInstance(Locale.ROOT);

        private Format() {}

        public NumberFormat get() {
            return format;
        }

        public void update() {
            format = NumberFormat.getInstance(
                TextHelper.getLocaleFromLanguageCode(
                    Minecraft.getMinecraft()
                        .getLanguageManager()
                        .getCurrentLanguage()
                        .getLanguageCode()));
            format.setMaximumFractionDigits(2);
            format.setMinimumFractionDigits(0);
            format.setGroupingUsed(true);
        }

    }

}
