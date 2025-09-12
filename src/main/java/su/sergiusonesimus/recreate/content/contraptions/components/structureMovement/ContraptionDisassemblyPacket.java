package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * This one is probably not needed, since disassembly is already handled by MetaWorlds
 */
public class ContraptionDisassemblyPacket implements IMessage {

    int subworldID;

    public ContraptionDisassemblyPacket(int entityID) {
        this.subworldID = entityID;
    }

    public void fromBytes(ByteBuf buffer) {
        subworldID = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(subworldID);
    }

    public static class Handler implements IMessageHandler<ContraptionDisassemblyPacket, IMessage> {

        @Override
        public IMessage onMessage(ContraptionDisassemblyPacket message, MessageContext ctx) {
            // Contraption.handleDisassemblyPacket(message);
            return null;
        }
    }

}
