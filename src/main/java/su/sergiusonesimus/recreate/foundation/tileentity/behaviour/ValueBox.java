package su.sergiusonesimus.recreate.foundation.tileentity.behaviour;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import su.sergiusonesimus.recreate.foundation.gui.AllIcons;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.ValueBoxTransform.Sided;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue.INamedIconOptions;
import su.sergiusonesimus.recreate.foundation.utility.Color;
import su.sergiusonesimus.recreate.foundation.utility.Lang;
import su.sergiusonesimus.recreate.foundation.utility.outliner.ChasingAABBOutline;

public class ValueBox extends ChasingAABBOutline {

    // TODO
    protected IChatComponent label;
    protected IChatComponent sublabel = new ChatComponentText("");
    protected IChatComponent scrollTooltip = new ChatComponentText("");
    protected Vec3 labelOffset = Vec3.createVectorHelper(0, 0, 0);

    protected int passiveColor;
    protected int highlightColor;
    public boolean isPassive;

    protected int posX;
    protected int posY;
    protected int posZ;
    protected ValueBoxTransform transform;
    protected Block block;
    protected int meta;

    public ValueBox(IChatComponent label, AxisAlignedBB bb, int x, int y, int z) {
        super(bb);
        this.label = label;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.block = Minecraft.getMinecraft().theWorld.getBlock(x, y, z);
        this.meta = Minecraft.getMinecraft().theWorld.getBlockMetadata(x, y, z);
    }

    public ValueBox transform(ValueBoxTransform transform) {
        this.transform = transform;
        return this;
    }

    public ValueBox offsetLabel(Vec3 offset) {
        this.labelOffset = offset;
        return this;
    }

    public ValueBox subLabel(IChatComponent sublabel) {
        this.sublabel = sublabel;
        return this;
    }

    public ValueBox scrollTooltip(IChatComponent scrollTip) {
        this.scrollTooltip = scrollTip;
        return this;
    }

    public ValueBox withColors(int passive, int highlight) {
        this.passiveColor = passive;
        this.highlightColor = highlight;
        return this;
    }

    public ValueBox passive(boolean passive) {
        this.isPassive = passive;
        return this;
    }

    @Override
    public void render(float partialTicks) {
        boolean hasTransform = transform != null;
        if (transform instanceof Sided && params.getHighlightedFace() != null) {
            ((Sided) transform).fromSide(params.getHighlightedFace());
        }
        if (hasTransform && !transform.shouldRender(block, meta)) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslated(posX, posY, posZ);

        if (hasTransform) {
            transform.transform(block, meta);
        }
        params.colored(isPassive ? passiveColor : highlightColor);
        super.render(partialTicks);
        float fontScale = hasTransform ? -transform.getFontScale() : -1 / 64f;

        GL11.glScalef(fontScale, fontScale, fontScale);
        GL11.glPushMatrix();
        renderContents();
        GL11.glPopMatrix();

        if (!isPassive) {
            GL11.glPushMatrix();
            GL11.glTranslated(17.5, -0.5, 7);
            GL11.glTranslated(labelOffset.xCoord, labelOffset.yCoord, labelOffset.zCoord);

            renderHoveringText(label);

            if (!sublabel.toString()
                .isEmpty()) {
                GL11.glTranslated(0, 10, 0);
                renderHoveringText(sublabel);
            }

            if (!scrollTooltip.getFormattedText()
                .isEmpty()) {
                GL11.glTranslated(0, 10, 0);
                renderHoveringText(scrollTooltip, 0x998899, 0x111111);
            }

            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
    }

    public void renderContents() {}

    public static class ItemValueBox extends ValueBox {

        ItemStack stack;
        int count;

        public ItemValueBox(IChatComponent label, AxisAlignedBB bb, int x, int y, int z, ItemStack stack, int count) {
            super(label, bb, x, y, z);
            this.stack = stack;
            this.count = count;
        }

        @Override
        public void renderContents() {
            super.renderContents();
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            IChatComponent countString = new ChatComponentText(count == 0 ? "*" : count + "");
            GL11.glPushMatrix();
            GL11.glTranslatef(17.5f, -5f, 7f);

            // TODO
            // boolean isFilter = stack.getItem() instanceof ItemFilter;
            boolean isEmpty = stack.stackSize == 0;
            float scale = 1.5f;
            GL11.glTranslatef(-fontRenderer.getStringWidth(countString.getUnformattedText()), 0, 0);

            /*
             * if (isFilter)
             * GL11.translate(3, 8, 7.25f);
             * else
             */ if (isEmpty) {
                GL11.glTranslatef(-17, -2, 3f);
                scale = 2f;
            } else GL11.glTranslatef(-7, 10, 10 + 1 / 4f);

            GL11.glScalef(scale, scale, scale);
            drawString(countString, 0, 0, /* isFilter ? 0xFFFFFF : */ 0xEDEDED);
            GL11.glTranslatef(0, 0, -1 / 16f);
            drawString(countString, 1 - 1 / 8f, 1 - 1 / 8f, 0x4F4F4F);
            GL11.glPopMatrix();
        }

    }

    public static class TextValueBox extends ValueBox {

        IChatComponent text;

        public TextValueBox(IChatComponent label, AxisAlignedBB bb, int x, int y, int z, IChatComponent text) {
            super(label, bb, x, y, z);
            this.text = text;
        }

        @Override
        public void renderContents() {
            super.renderContents();
            FontRenderer font = Minecraft.getMinecraft().fontRenderer;
            float scale = 4;
            GL11.glPushMatrix();
            GL11.glScalef(scale, scale, 1);
            GL11.glTranslatef(0.5f, -4.5f, 5);
            boolean useUnicode = Minecraft.getMinecraft().gameSettings.forceUnicodeFont;

            int stringWidth = font.getStringWidth(text.getUnformattedText());
            float numberScale = 1;
            float dY = 0;
            switch (text.getUnformattedText()
                .length()) {
                default:
                    dY = useUnicode ? 0 : 0.5f;
                    break;
                case 3:
                    numberScale = useUnicode ? 1 : 0.7f;
                    dY = useUnicode ? 0 : 2.25f;
                    break;
                case 4:
                    numberScale = useUnicode ? 0.75f : 0.5f;
                    dY = useUnicode ? 1.625f : 4.5f;
                    break;
            }

            GL11.glScalef(numberScale, numberScale, 1);
            GL11.glTranslatef(-stringWidth / 2F, dY, 0);

            renderHoveringText(text, 0xEDEDED, 0x4f4f4f);
            GL11.glPopMatrix();
        }

    }

    public static class IconValueBox extends ValueBox {

        AllIcons icon;

        public IconValueBox(IChatComponent label, INamedIconOptions iconValue, AxisAlignedBB bb, int x, int y, int z) {
            super(label, bb, x, y, z);
            subLabel(Lang.translate(iconValue.getTranslationKey()));
            icon = iconValue.getIcon();
        }

        @Override
        public void renderContents() {
            super.renderContents();
            float scale = 4 * 16;
            GL11.glPushMatrix();
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(-.5f, -.5f, 1 / 32f);
            icon.render(0xFFFFFF);
            GL11.glPopMatrix();
        }

    }

    // util

    protected void renderHoveringText(IChatComponent text) {
        renderHoveringText(text, highlightColor, Color.mixColors(passiveColor, 0, 0.75f));
    }

    protected void renderHoveringText(IChatComponent text, int color, int shadowColor) {
        GL11.glPushMatrix();

        drawString(text, 0, 0, color);

        double shadowOffset = Minecraft.getMinecraft().gameSettings.forceUnicodeFont ? .5 : 1;

        GL11.glTranslated(shadowOffset, shadowOffset, -.25);

        drawString(text, 0, 0, shadowColor);

        GL11.glPopMatrix();
    }

    private static void drawString(IChatComponent text, float x, float y, int color) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        String formattedText = text.getFormattedText();
        fontRenderer.drawString(formattedText, (int) x, (int) y, color);
    }

}
