package su.sergiusonesimus.recreate.content.contraptions.goggles;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.GuiIngameForge;

import org.lwjgl.opengl.GL11;

import su.sergiusonesimus.recreate.AllItems;
import su.sergiusonesimus.recreate.ClientProxy;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.foundation.config.CClient;
import su.sergiusonesimus.recreate.foundation.gui.Theme;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.ValueBox;
import su.sergiusonesimus.recreate.foundation.utility.Color;
import su.sergiusonesimus.recreate.foundation.utility.outliner.Outline;
import su.sergiusonesimus.recreate.foundation.utility.outliner.Outliner.OutlineEntry;

public class GoggleOverlayRenderer {

    private static final List<Supplier<Boolean>> customGogglePredicates = new LinkedList<>();
    private static final Map<Object, OutlineEntry> outlines = ClientProxy.OUTLINER.getOutlines();

    public static int hoverTicks = 0;
    public static Integer lastHoveredX = null;
    public static Integer lastHoveredY = null;
    public static Integer lastHoveredZ = null;

    public static void renderOverlay(GuiIngameForge gui, float partialTicks, int width, int height) {
        MovingObjectPosition objectMouseOver = Minecraft.getMinecraft().objectMouseOver;

        if (objectMouseOver == null || objectMouseOver.typeOfHit != MovingObjectType.BLOCK) {
            lastHoveredX = null;
            lastHoveredY = null;
            lastHoveredZ = null;
            hoverTicks = 0;
            return;
        }

        for (OutlineEntry entry : outlines.values()) {
            if (!entry.isAlive()) continue;
            Outline outline = entry.getOutline();
            if (outline instanceof ValueBox && !((ValueBox) outline).isPassive) return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.theWorld;
        ItemStack headSlot = mc.thePlayer.inventory.armorItemInSlot(3);
        TileEntity te = world.getTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

        if (lastHoveredX == null || lastHoveredY == null
            || lastHoveredZ == null
            || (lastHoveredX == objectMouseOver.blockX && lastHoveredY == objectMouseOver.blockY
                && lastHoveredZ == objectMouseOver.blockZ))
            hoverTicks++;
        else hoverTicks = 0;
        lastHoveredX = objectMouseOver.blockX;
        lastHoveredY = objectMouseOver.blockY;
        lastHoveredZ = objectMouseOver.blockZ;

        boolean wearingGoggles = headSlot != null && headSlot.getItem() == AllItems.goggles;
        for (Supplier<Boolean> supplier : customGogglePredicates) wearingGoggles |= supplier.get();

        boolean hasGoggleInformation = te instanceof IHaveGoggleInformation;
        boolean hasHoveringInformation = te instanceof IHaveHoveringInformation;

        boolean goggleAddedInformation = false;
        boolean hoverAddedInformation = false;

        List<IChatComponent> tooltip = new ArrayList<>();

        if (hasGoggleInformation && wearingGoggles) {
            IHaveGoggleInformation gte = (IHaveGoggleInformation) te;
            goggleAddedInformation = gte.addToGoggleTooltip(tooltip, mc.thePlayer.isSneaking());
        }

        if (hasHoveringInformation) {
            if (!tooltip.isEmpty()) tooltip.add(new ChatComponentText(""));
            IHaveHoveringInformation hte = (IHaveHoveringInformation) te;
            hoverAddedInformation = hte.addToTooltip(tooltip, mc.thePlayer.isSneaking());

            if (goggleAddedInformation && !hoverAddedInformation) tooltip.remove(tooltip.size() - 1);
        }

        // TODO
        // if (te instanceof IDisplayAssemblyExceptions) {
        // boolean exceptionAdded = ((IDisplayAssemblyExceptions) te).addExceptionToTooltip(tooltip);
        // if (exceptionAdded) {
        // hasHoveringInformation = true;
        // hoverAddedInformation = true;
        // }
        // }

        // break early if goggle or hover returned false when present
        if ((hasGoggleInformation && !goggleAddedInformation) && (hasHoveringInformation && !hoverAddedInformation))
            return;

        // check for piston poles if goggles are worn
        Block block = world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
        int meta = world.getBlockMetadata(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
        // TODO
        // if (wearingGoggles && AllBlocks.PISTON_EXTENSION_POLE.has(block)) {
        // Direction[] directions = Iterate.directionsInAxis(block.getValue(PistonExtensionPoleBlock.FACING)
        // .getAxis());
        // int poles = 1;
        // boolean pistonFound = false;
        // for (Direction dir : directions) {
        // int attachedPoles = PistonExtensionPoleBlock.PlacementHelper.get()
        // .attachedPoles(world, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ, dir);
        // poles += attachedPoles;
        // pistonFound |= world.getBlockState(pos.relative(dir, attachedPoles + 1))
        // .getBlock() instanceof MechanicalPistonBlock;
        // }
        //
        // if (!pistonFound)
        // return;
        // if (!tooltip.isEmpty())
        // tooltip.add(ChatComponentText.EMPTY);
        //
        // tooltip.add(IHaveGoggleInformation.componentSpacing.createCopy()
        // .append(Lang.translate("gui.goggles.pole_length"))
        // .append(new ChatComponentText(" " + poles)));
        // }

        if (tooltip.isEmpty()) return;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        int titleLinesCount = 1;
        int tooltipTextWidth = 0;
        for (IChatComponent textLine : tooltip) {
            int textLineWidth = mc.fontRenderer.getStringWidth(textLine.getUnformattedText());
            if (textLineWidth > tooltipTextWidth) tooltipTextWidth = textLineWidth;
        }

        int tooltipHeight = 8;
        if (tooltip.size() > 1) {
            tooltipHeight += (tooltip.size() - 1) * 10;
            if (tooltip.size() > titleLinesCount) tooltipHeight += 2; // gap between title lines and next lines
        }

        TooltipScreen tooltipScreen = new TooltipScreen();
        tooltipScreen.setWorldAndResolution(mc, tooltipTextWidth, tooltipHeight);

        CClient cfg = AllConfigs.CLIENT;
        int posX = width / 2 + cfg.overlayOffsetX;
        int posY = height / 2 + cfg.overlayOffsetY;

        posX = Math.min(posX, width - tooltipTextWidth - 20);
        posY = Math.min(posY, height - tooltipHeight - 20);

        float fade = MathHelper.clamp_float((hoverTicks + partialTicks) / 12f, 0, 1);
        Boolean useCustom = cfg.overlayCustomColor;
        Color colorBackground = useCustom ? new Color(cfg.overlayBackgroundColor)
            : Theme.c(Theme.Key.VANILLA_TOOLTIP_BACKGROUND)
                .scaleAlpha(.75f);
        Color colorBorderTop = useCustom ? new Color(cfg.overlayBorderColorTop)
            : Theme.c(Theme.Key.VANILLA_TOOLTIP_BORDER, true)
                .copy();
        Color colorBorderBot = useCustom ? new Color(cfg.overlayBorderColorBot)
            : Theme.c(Theme.Key.VANILLA_TOOLTIP_BORDER, false)
                .copy();

        if (fade < 1) {
            GL11.glTranslatef((1 - fade) * Math.signum(cfg.overlayOffsetX + .5f) * 4, 0, 0);
            colorBackground.scaleAlpha(fade);
            colorBorderTop.scaleAlpha(fade);
            colorBorderBot.scaleAlpha(fade);
        }

        tooltipScreen.drawHoveringText(
            tooltip,
            posX,
            posY + 1,
            tooltipScreen.width,
            tooltipScreen.height,
            colorBackground.getRGB(),
            colorBorderTop.getRGB(),
            colorBorderBot.getRGB(),
            mc.fontRenderer);

        ItemStack item = new ItemStack(AllItems.goggles);
        GuiScreen.itemRender.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), item, posX, posY);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    /**
     * Use this method to add custom entry points to the goggle overay, e.g. custom
     * armor, handheld alternatives, etc.
     */
    public static void registerCustomGoggleCondition(Supplier<Boolean> condition) {
        customGogglePredicates.add(condition);
    }

    public static final class TooltipScreen extends GuiScreen {

        public void drawHoveringText(List<IChatComponent> tooltip, int x, int y, int width, int height,
            int backgroundColor, int borderTopColor, int borderBottomColor, FontRenderer font) {
            for (IChatComponent line : tooltip) {
                int lineWidth = font.getStringWidth(line.getUnformattedText());
                if (lineWidth > width) width = lineWidth;
            }

            drawRect(x - 2, y, x + width + 7, y + height + 6, backgroundColor);
            drawRect(x - 1, y - 1, x + width + 6, y, backgroundColor);
            drawRect(x - 1, y + height + 6, x + width + 6, y + height + 7, backgroundColor);
            drawGradientRect(x - 1, y, x, y + height + 6, borderTopColor, borderBottomColor);
            drawGradientRect(x + width + 5, y, x + width + 6, y + height + 6, borderTopColor, borderBottomColor);
            drawRect(x, y, x + width + 5, y + 1, borderTopColor);
            drawRect(x, y + height + 5, x + width + 5, y + height + 6, borderBottomColor);

            for (int i = 0; i < tooltip.size(); i++) {
                font.drawStringWithShadow(
                    tooltip.get(i)
                        .getFormattedText(),
                    x + 2,
                    y + ((i > 0) ? 5 : 3) + i * 10,
                    0xFFFFFF);
            }
        }
    }

}
