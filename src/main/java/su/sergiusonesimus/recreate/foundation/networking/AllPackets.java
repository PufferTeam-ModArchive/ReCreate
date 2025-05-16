package su.sergiusonesimus.recreate.foundation.networking;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.scrollvalue.ScrollValueUpdatePacket;

public class AllPackets {
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("recreate");
    private static int packetId = 0;

    public static void registerPackets() {
        // Client -> Server
        registerPacket(ScrollValueUpdatePacket.Handler.class, ScrollValueUpdatePacket.class, Side.SERVER);

        // Server -> Client
    	//TODO
        //registerPacket(SymmetryEffectPacket.class, Side.CLIENT);
    }

    private static <T extends IMessage> void registerPacket(Class<? extends IMessageHandler<T, IMessage>> handler, Class<T> type, Side side) {
        CHANNEL.registerMessage(handler, type, packetId++, side);
    }
}