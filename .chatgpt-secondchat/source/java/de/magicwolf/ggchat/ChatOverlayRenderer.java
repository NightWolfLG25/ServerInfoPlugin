package de.magicwolf.ggchat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ChatOverlayRenderer {
    private ChatOverlayRenderer() {}

    public static void render(GuiGraphics graphics) {
        if (!ChatOverlayConfig.enabled || !ServerGuard.allowed()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.font == null || mc.options.hideGui) return;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int width = Math.min(ChatOverlayConfig.width, Math.max(180, sw - 12));
        int height = Math.min(ChatOverlayConfig.height, Math.max(70, sh - 12));
        int x = Math.max(2, sw - ChatOverlayConfig.rightMargin - width);
        int y = Math.max(2, sh - ChatOverlayConfig.bottomMargin - height);
        Font font = mc.font;

        if (ChatOverlayConfig.showBackground) {
            int alpha = (Math.max(0, Math.min(255, ChatOverlayConfig.opacity)) & 0xFF) << 24;
            graphics.fill(x, y, x + width, y + height, alpha | 0x101018);
            graphics.fill(x, y, x + width, y + 1, 0xAA7B2CFF);
        }

        int top = y + 5;
        if (ChatOverlayConfig.showHeader) {
            graphics.drawString(font, "MagicWolf GG Second Chat", x + 6, top, 0xD8C6FF, false);
            graphics.drawString(font, "MSG | Plot/PChat | Geld", x + 6, top + font.lineHeight + 1, 0xA0A0A0, false);
            top += font.lineHeight * 2 + 5;
        }

        int bottom = y + height - 5;
        List<ChatOverlayStore.Entry> entries = ChatOverlayStore.snapshot();
        if (entries.isEmpty()) {
            graphics.drawString(font, "Warte auf MSG, Plot-Chat oder Geldbewegung ...", x + 6, bottom - font.lineHeight, 0x8A8A8A, false);
            return;
        }

        List<VisualLine> lines = new ArrayList<>();
        int usable = Math.max(80, width - 12);
        for (ChatOverlayStore.Entry entry : entries) {
            String prefix = ChatOverlayConfig.showTimestamps ? "[" + entry.time() + "] " : "";
            List<String> wrapped = wrap(font, prefix + entry.text(), usable);
            for (String line : wrapped) lines.add(new VisualLine(line, color(entry.type())));
        }

        Collections.reverse(lines);
        int yy = bottom - font.lineHeight;
        for (VisualLine line : lines) {
            if (yy < top) break;
            graphics.drawString(font, line.text, x + 6, yy, line.color, true);
            yy -= font.lineHeight + 1;
        }
    }

    private static List<String> wrap(Font font, String text, int maxWidth) {
        List<String> out = new ArrayList<>();
        String remaining = text;
        while (!remaining.isEmpty()) {
            if (font.width(remaining) <= maxWidth) {
                out.add(remaining);
                break;
            }
            int cut = remaining.length();
            while (cut > 1 && font.width(remaining.substring(0, cut)) > maxWidth) cut--;
            int space = remaining.lastIndexOf(' ', cut);
            if (space > 4) cut = space;
            out.add(remaining.substring(0, cut).trim());
            remaining = remaining.substring(cut).trim();
        }
        return out;
    }

    private static int color(ChatOverlayStore.Type type) {
        return switch (type) {
            case MSG -> ChatOverlayConfig.msgColor;
            case PLOT -> ChatOverlayConfig.plotColor;
            case MONEY_IN -> ChatOverlayConfig.moneyInColor;
            case MONEY_OUT -> ChatOverlayConfig.moneyOutColor;
            case NORMAL -> ChatOverlayConfig.normalColor;
        };
    }

    private record VisualLine(String text, int color) {}
}
