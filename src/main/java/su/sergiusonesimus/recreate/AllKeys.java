package su.sergiusonesimus.recreate;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;

public enum AllKeys {

    TOOL_MENU("toolmenu", Keyboard.KEY_LMENU),
    ACTIVATE_TOOL("", Keyboard.KEY_LCONTROL),
    TOOLBELT("toolbelt", Keyboard.KEY_LMENU),;

    private KeyBinding keybind;
    private String description;
    private int key;
    private boolean modifiable;

    private AllKeys(String description, int defaultKey) {
        this.description = ReCreate.ID + ".keyinfo." + description;
        this.key = defaultKey;
        this.modifiable = !description.isEmpty();
    }

    public static void register() {
        for (AllKeys key : values()) {
            key.keybind = new KeyBinding(key.description, key.key, ReCreate.NAME);
            if (!key.modifiable) continue;

            ClientRegistry.registerKeyBinding(key.keybind);
        }
    }

    public KeyBinding getKeybind() {
        return keybind;
    }

    public boolean isPressed() {
        if (!modifiable) return isKeyDown(key);
        return keybind.getIsKeyPressed();
    }

    public String getBoundKey() {
        return Keyboard.getKeyName(keybind.getKeyCode());
    }

    public int getBoundCode() {
        return keybind.getKeyCode();
    }

    public static boolean isKeyDown(int key) {
        return Keyboard.isKeyDown(key);
    }

    public static boolean ctrlDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
    }

    public static boolean shiftDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    public static boolean altDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
    }
}
