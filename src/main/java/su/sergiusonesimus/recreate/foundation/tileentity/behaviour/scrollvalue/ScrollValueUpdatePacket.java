package su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import su.sergiusonesimus.recreate.foundation.networking.TileEntityConfigurationPacket;
import su.sergiusonesimus.recreate.foundation.tileentity.SmartTileEntity;
import su.sergiusonesimus.recreate.foundation.tileentity.SyncedTileEntity;

public class ScrollValueUpdatePacket extends TileEntityConfigurationPacket<SmartTileEntity> {

    private int value;

    public ScrollValueUpdatePacket() {}

    public ScrollValueUpdatePacket(int x, int y, int z, int amount) {
        super(x, y, z);
        this.value = amount;
    }

    @Override
    protected void writeSettings(ByteBuf buffer) {
        buffer.writeInt(value);
    }

    @Override
    protected void readSettings(ByteBuf buffer) {
        value = buffer.readInt();
    }

    @Override
    protected void applySettings(SyncedTileEntity te) {
    	SmartTileEntity ste = (SmartTileEntity) te;
        ScrollValueBehaviour behaviour = ste.getBehaviour(ScrollValueBehaviour.TYPE);
        if (behaviour != null) {
            behaviour.setValue(value);
        }
    }

    public static class Handler implements IMessageHandler<ScrollValueUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(ScrollValueUpdatePacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            World world = player.worldObj;

            if (!world.blockExists(message.posX, message.posY, message.posZ)) {
                return null;
            }

            TileEntity te = world.getTileEntity(message.posX, message.posY, message.posZ);
            if (te instanceof SmartTileEntity) {
                message.applySettings((SmartTileEntity) te);
                ((SmartTileEntity) te).markDirty();
            }
            return null;
        }
    }
}