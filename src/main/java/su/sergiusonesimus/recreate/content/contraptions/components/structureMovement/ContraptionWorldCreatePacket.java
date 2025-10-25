package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import su.sergiusonesimus.metaworlds.MetaworldsMod;
import su.sergiusonesimus.metaworlds.api.SubWorldTypeManager;
import su.sergiusonesimus.recreate.foundation.utility.NBTHelper;

public class ContraptionWorldCreatePacket implements IMessage {

    public Integer subWorldID;
    public Integer subWorldType;
    NBTTagCompound contraptionData;

    public ContraptionWorldCreatePacket() {}

    public ContraptionWorldCreatePacket(ContraptionWorld sourceWorld) {
        this.subWorldID = sourceWorld.getSubWorldID();
        this.subWorldType = SubWorldTypeManager.getTypeID(sourceWorld.getSubWorldType());
        contraptionData = sourceWorld.getContraption()
            .writeNBT(true);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        subWorldID = buf.readInt();
        subWorldType = buf.readInt();
        contraptionData = NBTHelper.readNBTTagCompound(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(subWorldID);
        buf.writeInt(subWorldType);
        NBTHelper.writeNBTTagCompound(contraptionData, buf);
    }

    public static class Handler implements IMessageHandler<ContraptionWorldCreatePacket, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ContraptionWorldCreatePacket message, MessageContext ctx) {
            String curSubWorldType = SubWorldTypeManager.getTypeByID(message.subWorldType);
            ContraptionWorld contraptionWorld = (ContraptionWorld) SubWorldTypeManager
                .getSubWorldInfoProvider(curSubWorldType)
                .create(MetaworldsMod.proxy.getMainWorld(), message.subWorldID);
            Contraption contraption = ContraptionType.fromType(message.contraptionData.getString("Type"));
            contraption.readNBT(message.contraptionData, true);
            contraption.contraptionWorld = (World) contraptionWorld;
            contraptionWorld.setContraption(contraption);
            return null;
        }
    }

}
