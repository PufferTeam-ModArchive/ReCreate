package su.sergiusonesimus.recreate.foundation.utility;

import java.text.BreakIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import su.sergiusonesimus.recreate.util.TextHelper;

public final class FontHelper {

    private FontHelper() {}

    public static List<String> cutString(FontRenderer font, String text, int maxWidthPerLine) {
        Locale locale = TextHelper.getLocaleFromLanguageCode(
            Minecraft.getMinecraft()
                .getLanguageManager()
                .getCurrentLanguage()
                .getLanguageCode());

        List<String> words = new LinkedList<String>();
        BreakIterator iterator = BreakIterator.getLineInstance(locale);
        iterator.setText(text);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String word = text.substring(start, end);
            words.add(word);
        }

        List<String> lines = new LinkedList<String>();
        StringBuilder currentLine = new StringBuilder();
        int width = 0;

        for (String word : words) {
            int newWidth = font.getStringWidth(word);
            if (width + newWidth > maxWidthPerLine) {
                if (width > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    width = 0;
                }
                if (newWidth > maxWidthPerLine) {
                    lines.add(word);
                    continue;
                }
            }
            currentLine.append(word);
            width += newWidth;
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    public static void drawSplitString(FontRenderer font, String text, int x, int y, int width, int color) {
        List<String> lines = cutString(font, text, width);

        for (String line : lines) {
            int lineWidth = font.getStringWidth(line);
            int drawX = font.getBidiFlag() ? x + width - lineWidth : x;

            font.drawString(line, drawX, y, color);
            y += 9;
        }
    }

    private static int draw(FontRenderer fontRenderer, String text, float x, float y, int color, boolean dropShadow) {

        if (text == null) {
            return 0;
        }

        int width;
        if (dropShadow) {
            width = fontRenderer.drawStringWithShadow(text, (int) x, (int) y, color);
        } else {
            width = fontRenderer.drawString(text, (int) x, (int) y, color);
        }

        return width;
    }

}
