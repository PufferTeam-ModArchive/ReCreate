package su.sergiusonesimus.recreate;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import su.sergiusonesimus.recreate.content.contraptions.components.motor.CreativeMotorRenderBlock;
import su.sergiusonesimus.recreate.content.contraptions.components.motor.CreativeMotorTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.components.motor.CreativeMotorTileEntityRenderer;
import su.sergiusonesimus.recreate.content.contraptions.goggles.GogglesModel;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel.CogWheelRenderBlock;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel.CogWheelTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel.CogWheelTileEntityRenderer;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftRenderBlock;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftTileEntityRenderer;
import su.sergiusonesimus.recreate.events.InputEvents;
import su.sergiusonesimus.recreate.foundation.utility.ghost.GhostBlocks;
import su.sergiusonesimus.recreate.foundation.utility.outliner.Outliner;

public class ClientProxy extends CommonProxy {

    int shaftRenderID;
    int creativeMotorRenderID;
    int cogwheelRenderID;

    ModelBiped gogglesArmorModel;

    // TODO
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
        ClientRegistry
            .bindTileEntitySpecialRenderer(CreativeMotorTileEntity.class, new CreativeMotorTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(CogWheelTileEntity.class, new CogWheelTileEntityRenderer());

        // block render ids
        shaftRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new ShaftRenderBlock(shaftRenderID));
        creativeMotorRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new CreativeMotorRenderBlock(creativeMotorRenderID));
        cogwheelRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new CogWheelRenderBlock(cogwheelRenderID));

        // armor model
        gogglesArmorModel = new GogglesModel();
    }

    public static void invalidateRenderers() {
        // TODO
        // BUFFER_CACHE.invalidate();
        //
        // ContraptionRenderDispatcher.reset();
    }

    public static void checkGraphicsFanciness(EntityPlayer player) {
        // TODO This method is probably useless, since there're no fabulous settings on 1.7.10
        // if (!Minecraft.isFancyGraphicsEnabled()) return;
        //
        // if (AllConfigs.CLIENT.ignoreFabulousWarning) return;
        //
        // IChatComponent text = new ChatComponentText("[WARN]");
        // text.getChatStyle()
        // .setColor(EnumChatFormatting.GOLD);
        // text.appendSibling(
        // new ChatComponentText(
        // " Some of Create's visual features will not be available while Fabulous graphics are enabled!"));
        // text.getChatStyle()
        // .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/create dismissFabulousWarning"))
        // .setChatHoverEvent(
        // new HoverEvent(
        // HoverEvent.Action.SHOW_TEXT,
        // new ChatComponentText("Click here to disable this warning")));
        //
        // player.addChatMessage(text);
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

    public ModelBiped getGogglesArmorModel() {
        return gogglesArmorModel;
    }

    public int registerArmorRenderID(String prefix) {
        return RenderingRegistry.addNewArmourRendererPrefix(prefix);
    }

}
