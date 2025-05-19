package su.sergiusonesimus.recreate;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import su.sergiusonesimus.metaworlds.EventHookContainer;
import su.sergiusonesimus.recreate.content.contraptions.components.motor.CreativeMotorRenderBlock;
import su.sergiusonesimus.recreate.content.contraptions.components.motor.CreativeMotorTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.components.motor.CreativeMotorTileEntityRenderer;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel.CogWheelRenderBlock;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel.CogWheelTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel.CogWheelTileEntityRenderer;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftRenderBlock;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftTileEntityRenderer;
import su.sergiusonesimus.recreate.events.InputEvents;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.foundation.utility.ghost.GhostBlocks;
import su.sergiusonesimus.recreate.foundation.utility.outliner.Outliner;

public class ClientProxy extends CommonProxy {

    int shaftRenderID;
    int creativeMotorRenderID;
    int cogwheelRenderID;
	
	//TODO
	public static final Outliner OUTLINER = new Outliner();
	public static final GhostBlocks GHOST_BLOCKS = new GhostBlocks();
	
    public void preInit(FMLPreInitializationEvent event) {
    	
		AllKeys.register();
		
    }

    @Override
	public void doOnLoadRegistration() {
    	InputEvents inputEvents = new InputEvents();
        FMLCommonHandler.instance()
	        .bus()
	        .register(inputEvents);
        MinecraftForge.EVENT_BUS.register(inputEvents);
    	
    	// tile entities
        ClientRegistry.bindTileEntitySpecialRenderer(ShaftTileEntity.class, new ShaftTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(CreativeMotorTileEntity.class, new CreativeMotorTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(CogWheelTileEntity.class, new CogWheelTileEntityRenderer());
        
        // block render ids
        shaftRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new ShaftRenderBlock(shaftRenderID));
        creativeMotorRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new CreativeMotorRenderBlock(creativeMotorRenderID));
        cogwheelRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new CogWheelRenderBlock(cogwheelRenderID));
	}

	public int getShaftBlockRenderID() {
		return shaftRenderID;
	}

	public int getCreativeMotorBlockRenderID() {
		return creativeMotorRenderID;
	}

	public int getCogWheelBlockRenderID() {
		return cogwheelRenderID;
	}

	//TODO
//	public static void invalidateRenderers() {
//		BUFFER_CACHE.invalidate();
//
//		ContraptionRenderDispatcher.reset();
//	}

	//TODO
//	public static void checkGraphicsFanciness() {
//		Minecraft mc = Minecraft.getMinecraft();
//		if (mc.thePlayer == null)
//			return;
//
//		if (mc.options.graphicsMode != GraphicsStatus.FABULOUS)
//			return;
//
//		if (AllConfigs.CLIENT.ignoreFabulousWarning)
//			return;
//
//		IChatComponent text = ComponentUtils.wrapInSquareBrackets(new ChatComponentText("WARN"))
//			.withStyle(EnumChatFormatting.GOLD)
//			.append(new ChatComponentText(
//				" Some of Create's visual features will not be available while Fabulous graphics are enabled!"))
//			.withStyle(style -> style
//				.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/create dismissFabulousWarning"))
//				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
//					new ChatComponentText("Click here to disable this warning"))));
//
//		mc.gui.handleChat(ChatType.CHAT, text, mc.thePlayer.getUniqueID());
//	}

}
