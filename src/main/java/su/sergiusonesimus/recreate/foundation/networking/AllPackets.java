package su.sergiusonesimus.recreate.foundation.networking;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import su.sergiusonesimus.metaworlds.network.MetaMagicNetwork;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionStallPacket;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionWorldCreatePacket;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.glue.GlueEffectPacket;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue.ScrollValueUpdatePacket;
import su.sergiusonesimus.recreate.foundation.utility.ServerSpeedProvider;

public class AllPackets {

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("recreate");
    private static int packetId = 0;

    public static void registerPackets() {
        // Client -> Server
        registerPacket(ScrollValueUpdatePacket.Handler.class, ScrollValueUpdatePacket.class, Side.SERVER);

        // Server -> Client
        registerPacket(GlueEffectPacket.Handler.class, GlueEffectPacket.class, Side.CLIENT);
        // registerPacket(ContraptionDisassemblyPacket.Handler.class, ContraptionDisassemblyPacket.class, Side.CLIENT);
        registerPacket(ContraptionStallPacket.Handler.class, ContraptionStallPacket.class, Side.CLIENT);
        registerPacket(ServerSpeedProvider.Packet.Handler.class, ServerSpeedProvider.Packet.class, Side.CLIENT);

        // MetaWorlds packets
        MetaMagicNetwork.registerPacket(
            ContraptionWorldCreatePacket.Handler.class,
            ContraptionWorldCreatePacket.class,
            Side.CLIENT);
    }

    private static <T extends IMessage> void registerPacket(Class<? extends IMessageHandler<T, IMessage>> handler,
        Class<T> type, Side side) {
        CHANNEL.registerMessage(handler, type, packetId++, side);
    }
}
