package su.sergiusonesimus.recreate.util;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class TextHelper {

    /**
     * 
     * @param original
     * @return A copy of the original chat component without any formatting
     */
    public static IChatComponent plainCopy(IChatComponent original) {
        IChatComponent result = null;
        if (original instanceof ChatComponentText) result = new ChatComponentText(original.getFormattedText());
        return result;
    }

    public static Locale getLocaleFromLanguageCode(String languageCode) {
        if (languageCode == null || languageCode.isEmpty()) {
            return Locale.getDefault();
        }

        String[] parts = languageCode.split("_");
        if (parts.length == 1) {
            return new Locale(parts[0]);
        } else if (parts.length >= 2) {
            return new Locale(parts[0], parts[1]);
        }

        return Locale.getDefault();
    }

    public static Locale getCurrentLocale() {
        String langCode = Minecraft.getMinecraft()
            .getLanguageManager()
            .getCurrentLanguage()
            .getLanguageCode();
        return getLocaleFromLanguageCode(langCode);
    }

}
