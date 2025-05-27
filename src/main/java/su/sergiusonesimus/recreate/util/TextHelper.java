package su.sergiusonesimus.recreate.util;

import java.util.Locale;

import net.minecraft.client.Minecraft;

public class TextHelper {

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
