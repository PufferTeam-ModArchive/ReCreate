package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class ContraptionDisassemblyPacket implements IMessage {

    int entityID;

    public ContraptionDisassemblyPacket(int entityID) {
        this.entityID = entityID;
    }

    public void fromBytes(ByteBuf buffer) {
        entityID = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(entityID);
    }

    public static class Handler implements IMessageHandler<ContraptionDisassemblyPacket, IMessage> {

        @Override
        public IMessage onMessage(ContraptionDisassemblyPacket message, MessageContext ctx) {
            // AbstractContraptionEntity.handleDisassemblyPacket(message);
            return null;
        }
    }

}
