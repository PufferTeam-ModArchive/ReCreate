package su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.recreate.AllItems;
import su.sergiusonesimus.recreate.AllKeys;
import su.sergiusonesimus.recreate.AllSounds;
import su.sergiusonesimus.recreate.foundation.tileentity.SmartTileEntity;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.ValueBoxTransform.Sided;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue.ScrollValueBehaviour.StepContext;
import su.sergiusonesimus.recreate.foundation.utility.animation.PhysicalFloat;
import su.sergiusonesimus.recreate.util.Direction;
import su.sergiusonesimus.recreate.util.ReCreateMath;

public class ScrollValueHandler {

    private static float lastPassiveScroll = 0.0f;
    private static float passiveScroll = 0.0f;
    private static float passiveScrollDirection = 1f;
    private static final PhysicalFloat wrenchCog = PhysicalFloat.create()
        .withDrag(0.3);

    @SideOnly(Side.CLIENT)
    public static boolean onScroll(double delta) {
        Minecraft mc = Minecraft.getMinecraft();
        MovingObjectPosition objectMouseOver = mc.objectMouseOver;
        if (objectMouseOver == null || objectMouseOver.typeOfHit != MovingObjectType.BLOCK) return false;

        WorldClient world = mc.theWorld;

        ScrollValueBehaviour scrolling = TileEntityBehaviour.get(
            world,
            objectMouseOver.blockX,
            objectMouseOver.blockY,
            objectMouseOver.blockZ,
            ScrollValueBehaviour.TYPE);
        if (scrolling == null) return false;
        if (!scrolling.isActive()) return false;
        if (mc.thePlayer.isPlayerSleeping() || mc.thePlayer.isRiding()) return false;
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (scrolling.needsWrench && (heldItem == null || heldItem.getItem() != AllItems.wrench)) return false;

        passiveScrollDirection = (float) -delta;
        wrenchCog.bump(3, -delta * 10);
        int prev = scrolling.scrollableValue;

        if (scrolling.slotPositioning instanceof Sided)
            ((Sided) scrolling.slotPositioning).fromSide(Direction.from3DDataValue(objectMouseOver.sideHit));
        if (!scrolling.testHit(objectMouseOver.hitVec)) return false;

        if (scrolling instanceof BulkScrollValueBehaviour && AllKeys.ctrlDown()) {
            BulkScrollValueBehaviour bulkScrolling = (BulkScrollValueBehaviour) scrolling;
            for (SmartTileEntity te : bulkScrolling.getBulk()) {
                ScrollValueBehaviour other = te.getBehaviour(ScrollValueBehaviour.TYPE);
                if (other != null) applyTo(delta, other);
            }

        } else applyTo(delta, scrolling);

        if (prev != scrolling.scrollableValue) {
            float pitch = (scrolling.scrollableValue - scrolling.min) / (float) (scrolling.max - scrolling.min);
            pitch = ReCreateMath.lerp(pitch, 1.5f, 2f);
            AllSounds.SCROLL_VALUE.play(
                world,
                mc.thePlayer,
                objectMouseOver.blockX,
                objectMouseOver.blockY,
                objectMouseOver.blockZ,
                1,
                pitch);
        }
        return true;
    }

    public static float getScroll(float partialTicks) {
        return wrenchCog.getValue(partialTicks) + ReCreateMath.lerp(partialTicks, lastPassiveScroll, passiveScroll);
    }

    @SideOnly(Side.CLIENT)
    public static void tick() {
        if (!Minecraft.getMinecraft()
            .isGamePaused()) {
            lastPassiveScroll = passiveScroll;
            wrenchCog.tick();
            passiveScroll += passiveScrollDirection * 0.5;
        }
    }

    protected static void applyTo(double delta, ScrollValueBehaviour scrolling) {
        scrolling.ticksUntilScrollPacket = 10;
        int valueBefore = scrolling.scrollableValue;

        StepContext context = new StepContext();
        context.control = AllKeys.ctrlDown();
        context.shift = AllKeys.shiftDown();
        context.currentValue = scrolling.scrollableValue;
        context.forward = delta > 0;

        double newValue = scrolling.scrollableValue + Math.signum(delta) * scrolling.step.apply(context);
        scrolling.scrollableValue = (int) MathHelper.clamp_double(newValue, scrolling.min, scrolling.max);

        if (valueBefore != scrolling.scrollableValue) scrolling.clientCallback.accept(scrolling.scrollableValue);
    }

}
