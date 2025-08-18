package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.glue;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import su.sergiusonesimus.metaworlds.util.Direction;

public class GlueEffectPacket implements IMessage {

    protected int posX, posY, posZ;
    protected Direction direction;
    protected boolean fullBlock;

    public GlueEffectPacket() {}

    public GlueEffectPacket(int x, int y, int z, Direction direction, boolean fullBlock) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.direction = direction;
        this.fullBlock = fullBlock;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.posX = buffer.readInt();
        this.posY = buffer.readInt();
        this.posZ = buffer.readInt();
        this.direction = Direction.from3DDataValue(buffer.readByte());
        this.fullBlock = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(posX);
        buffer.writeInt(posY);
        buffer.writeInt(posZ);
        buffer.writeByte(direction.get3DDataValue());
        buffer.writeBoolean(fullBlock);
    }

    public static class Handler implements IMessageHandler<GlueEffectPacket, IMessage> {

        @Override
        public IMessage onMessage(GlueEffectPacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            Vec3 playerPos = mc.thePlayer.getPosition(1);
            Vec3 gluePos = Vec3.createVectorHelper(message.posX, message.posY, message.posZ);
            if (playerPos.distanceTo(gluePos) >= 100) return null;
            SuperGlueItem.spawnParticles(
                mc.theWorld,
                message.posX,
                message.posY,
                message.posZ,
                message.direction,
                message.fullBlock);

            return null;
        }
    }

}
