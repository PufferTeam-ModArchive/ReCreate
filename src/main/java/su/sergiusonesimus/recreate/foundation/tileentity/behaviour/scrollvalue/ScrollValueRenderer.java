package su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

import su.sergiusonesimus.recreate.AllItems;
import su.sergiusonesimus.recreate.AllKeys;
import su.sergiusonesimus.recreate.ClientProxy;
import su.sergiusonesimus.recreate.foundation.tileentity.SmartTileEntity;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.ValueBox;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.ValueBox.IconValueBox;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.ValueBox.TextValueBox;
import su.sergiusonesimus.recreate.foundation.utility.Lang;
import su.sergiusonesimus.recreate.util.Direction;

public class ScrollValueRenderer {

    public static void tick() {
        Minecraft mc = Minecraft.getMinecraft();
        MovingObjectPosition target = mc.objectMouseOver;
        if (target == null || target.typeOfHit != MovingObjectType.BLOCK) return;

        WorldClient world = mc.theWorld;
        Direction face = Direction.from3DDataValue(target.sideHit);

        ScrollValueBehaviour behaviour = TileEntityBehaviour
            .get(world, target.blockX, target.blockY, target.blockZ, ScrollValueBehaviour.TYPE);
        if (behaviour == null) return;
        if (!behaviour.isActive()) return;
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (behaviour.needsWrench && (heldItem == null || heldItem.getItem() != AllItems.wrench)) return;
        boolean highlight = behaviour.testHit(target.hitVec);

        if (behaviour instanceof BulkScrollValueBehaviour && AllKeys.ctrlDown()) {
            BulkScrollValueBehaviour bulkScrolling = (BulkScrollValueBehaviour) behaviour;
            for (SmartTileEntity smartTileEntity : bulkScrolling.getBulk()) {
                ScrollValueBehaviour other = smartTileEntity.getBehaviour(ScrollValueBehaviour.TYPE);
                if (other != null) addBox(
                    world,
                    smartTileEntity.xCoord,
                    smartTileEntity.yCoord,
                    smartTileEntity.zCoord,
                    face,
                    other,
                    highlight);
            }
        } else addBox(world, target.blockX, target.blockY, target.blockZ, face, behaviour, highlight);
    }

    protected static void addBox(WorldClient world, int x, int y, int z, Direction face, ScrollValueBehaviour behaviour,
        boolean highlight) {
        float inflator = .5f;
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0)
            .expand(inflator, inflator, inflator / 4)
            .getOffsetBoundingBox(0, 0, -inflator / 16);
        IChatComponent label = behaviour.label;
        ValueBox box;

        if (behaviour instanceof ScrollOptionBehaviour) {
            box = new IconValueBox(label, ((ScrollOptionBehaviour<?>) behaviour).getIconForSelected(), bb, x, y, z);
        } else {
            box = new TextValueBox(label, bb, x, y, z, new ChatComponentText(behaviour.formatValue()));
            if (behaviour.unit != null) box.subLabel(
                new ChatComponentText("(").appendSibling(behaviour.unit.apply(behaviour.scrollableValue))
                    .appendText(")"));
        }

        box.scrollTooltip(
            new ChatComponentText("[").appendSibling(Lang.translate("action.scroll"))
                .appendText("]"));
        box.offsetLabel(behaviour.textShift.addVector(20, -10, 0))
            .withColors(0x5A5D5A, 0xB5B7B6)
            .passive(!highlight);

        ClientProxy.OUTLINER.showValueBox(new ChunkCoordinates(x, y, z), box.transform(behaviour.slotPositioning))
            .lineWidth(1 / 64f)
            .highlightFace(face);
    }

}
