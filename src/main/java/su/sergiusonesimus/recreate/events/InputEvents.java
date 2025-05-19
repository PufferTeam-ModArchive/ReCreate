package su.sergiusonesimus.recreate.events;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InputEvents {

    // TODO
    // @SubscribeEvent
    // public void onKeyInput(InputEvent.KeyInputEvent event) {
    // if (Minecraft.getMinecraft().currentScreen != null)
    // return;
    //
    // int key = Keyboard.getEventKey();
    // boolean pressed = Keyboard.getEventKeyState();
    //
    // ClientProxy.SCHEMATIC_HANDLER.onKeyInput(key, pressed);
    // ToolboxHandlerClient.onKeyInput(key, pressed);
    // }

    // TODO
    // @SubscribeEvent
    // public void onMouseInput(InputEvent.MouseInputEvent event) {
    // if (Minecraft.getMinecraft().currentScreen != null)
    // return;
    //
    // int button = Mouse.getEventButton();
    // boolean pressed = Mouse.getEventButtonState();
    //
    // ClientProxy.SCHEMATIC_HANDLER.onMouseInput(button, pressed);
    // ClientProxy.SCHEMATIC_AND_QUILL_HANDLER.onMouseInput(button, pressed);
    // }

    // TODO
    // @SubscribeEvent
    // public void onClickInput(InputEvent.MouseInputEvent event) {
    // if (Minecraft.getMinecraft().currentScreen != null)
    // return;
    //
    // if (Mouse.getEventButton() == Minecraft.getMinecraft().gameSettings.keyBindPickItem.getKeyCode()) {
    // if (ToolboxHandlerClient.onPickItem()) {
    // Mouse.next();
    // }
    // return;
    // }
    //
    // if (Minecraft.getMinecraft().gameSettings.keyBindUseItem.getIsKeyPressed()) {
    // LinkedControllerClientHandler.deactivateInLectern();
    // }
    // }
}
