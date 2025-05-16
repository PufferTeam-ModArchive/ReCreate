package su.sergiusonesimus.recreate.foundation.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import su.sergiusonesimus.recreate.ReCreate;

public class Lang {

	public static ChatComponentTranslation translate(String key, Object... args) {
		return createTranslationTextComponent(key, args);
	}

	public static ChatComponentTranslation createTranslationTextComponent(String key, Object... args) {
		return new ChatComponentTranslation(ReCreate.ID + "." + key, args);
	}

	public static void sendStatus(EntityPlayer player, String key, Object... args) {
		player.addChatComponentMessage(createTranslationTextComponent(key, args));
	}

	public static List<IChatComponent> translatedOptions(String prefix, String... keys) {
		List<IChatComponent> result = new ArrayList<>(keys.length);
		for (String key : keys)
			result.add(translate(prefix + "." + key));

		return result;
	}

	public static String asId(String name) {
		return name.toLowerCase(Locale.ROOT);
	}
	
	public static String nonPluralId(String name) {
		String asId = asId(name);
		return asId.endsWith("s") ? asId.substring(0, asId.length() - 1) : asId;
	}

}
