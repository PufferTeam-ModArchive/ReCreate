package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class ContraptionStallPacket implements IMessage {

    int subworldID;
    float x;
    float y;
    float z;
    float angle;

    public ContraptionStallPacket(int subworldID, double posX, double posY, double posZ, float angle) {
        this.subworldID = subworldID;
        this.x = (float) posX;
        this.y = (float) posY;
        this.z = (float) posZ;
        this.angle = angle;
    }

    public void fromBytes(ByteBuf buffer) {
        subworldID = buffer.readInt();
        x = buffer.readFloat();
        y = buffer.readFloat();
        z = buffer.readFloat();
        angle = buffer.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(subworldID);
        writeAll(buffer, x, y, z, angle);
    }

    private void writeAll(ByteBuf buffer, float... floats) {
        for (float f : floats) buffer.writeFloat(f);
    }

    public static class Handler implements IMessageHandler<ContraptionStallPacket, IMessage> {

        @Override
        public IMessage onMessage(ContraptionStallPacket message, MessageContext ctx) {
            ContraptionWorld.handleStallPacket(message);
            return null;
        }
    }

}
