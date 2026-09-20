package de.magicwolf.ggchat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class ChatOverlayConfigScreen extends Screen {
    private final Screen parent;
    private final List<Row> rows = new ArrayList<>();

    public ChatOverlayConfigScreen(Screen parent) {
        super(Component.literal("MagicWolf GG Second Chat"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        rows.clear();
        int center = this.width / 2;
        int startY = 52;
        int rowH = 20;
        rows.add(Row.toggle(center - 145, startY + rowH * 0, 290, "Second Chat aktiv", () -> ChatOverlayConfig.enabled, v -> ChatOverlayConfig.enabled = v));
        rows.add(Row.toggle(center - 145, startY + rowH * 1, 290, "MSG / Privatnachrichten", () -> ChatOverlayConfig.captureMsg, v -> ChatOverlayConfig.captureMsg = v));
        rows.add(Row.toggle(center - 145, startY + rowH * 2, 290, "Plot-Chat / PChat", () -> ChatOverlayConfig.capturePlotChat, v -> ChatOverlayConfig.capturePlotChat = v));
        rows.add(Row.toggle(center - 145, startY + rowH * 3, 290, "Geldbewegungen", () -> ChatOverlayConfig.captureMoney, v -> ChatOverlayConfig.captureMoney = v));
        rows.add(Row.toggle(center - 145, startY + rowH * 4, 290, "Nur auf GrieferGames", () -> ChatOverlayConfig.onlyGrieferGames, v -> ChatOverlayConfig.onlyGrieferGames = v));
        rows.add(Row.toggle(center - 145, startY + rowH * 5, 290, "Zeitstempel", () -> ChatOverlayConfig.showTimestamps, v -> ChatOverlayConfig.showTimestamps = v));
        rows.add(Row.toggle(center - 145, startY + rowH * 6, 290, "Kopfzeile", () -> ChatOverlayConfig.showHeader, v -> ChatOverlayConfig.showHeader = v));
        rows.add(Row.toggle(center - 145, startY + rowH * 7, 290, "Hintergrund", () -> ChatOverlayConfig.showBackground, v -> ChatOverlayConfig.showBackground = v));
        rows.add(Row.number(center - 145, startY + rowH * 8, 290, "Breite", () -> ChatOverlayConfig.width, v -> ChatOverlayConfig.width = clamp(v, 220, 900), 20));
        rows.add(Row.number(center - 145, startY + rowH * 9, 290, "Höhe", () -> ChatOverlayConfig.height, v -> ChatOverlayConfig.height = clamp(v, 80, 600), 10));
        rows.add(Row.number(center - 145, startY + rowH * 10, 290, "Abstand rechts", () -> ChatOverlayConfig.rightMargin, v -> ChatOverlayConfig.rightMargin = clamp(v, 0, 500), 5));
        rows.add(Row.number(center - 145, startY + rowH * 11, 290, "Abstand unten", () -> ChatOverlayConfig.bottomMargin, v -> ChatOverlayConfig.bottomMargin = clamp(v, 0, 500), 5));
        rows.add(Row.number(center - 145, startY + rowH * 12, 290, "Deckkraft", () -> ChatOverlayConfig.opacity, v -> ChatOverlayConfig.opacity = clamp(v, 0, 255), 15));
        rows.add(Row.number(center - 145, startY + rowH * 13, 290, "Nachrichtenpuffer", () -> ChatOverlayConfig.maxMessages, v -> ChatOverlayConfig.maxMessages = clamp(v, 20, 1000), 20));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, 0xE0101017);
        int center = this.width / 2;
        g.drawCenteredString(this.font, "MagicWolf GG Second Chat", center, 14, 0xD8C6FF);
        g.drawCenteredString(this.font, "Forge 1.21.x | coded by NightWolfLG", center, 28, 0x909090);

        for (Row row : rows) row.render(g, this.font, mouseX, mouseY);

        int by = this.height - 31;
        drawButton(g, center - 145, by, 92, 20, "Zurücksetzen", mouseX, mouseY);
        drawButton(g, center - 46, by, 92, 20, "Chat leeren", mouseX, mouseY);
        drawButton(g, center + 53, by, 92, 20, "Fertig", mouseX, mouseY);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (Row row : rows) if (row.click(mouseX, mouseY)) return true;
            int center = this.width / 2;
            int by = this.height - 31;
            if (inside(mouseX, mouseY, center - 145, by, 92, 20)) {
                ChatOverlayConfig.resetDefaults();
                ChatOverlayConfig.save();
                return true;
            }
            if (inside(mouseX, mouseY, center - 46, by, 92, 20)) {
                ChatOverlayStore.clear();
                return true;
            }
            if (inside(mouseX, mouseY, center + 53, by, 92, 20)) {
                onClose();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        ChatOverlayConfig.save();
        if (this.minecraft != null) this.minecraft.setScreen(parent);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static void drawButton(GuiGraphics g, int x, int y, int w, int h, String text, int mx, int my) {
        boolean hover = inside(mx, my, x, y, w, h);
        g.fill(x, y, x + w, y + h, hover ? 0xFF5A3B92 : 0xFF34244F);
        g.fill(x, y, x + w, y + 1, hover ? 0xFFD2BBFF : 0xFF8564BD);
        g.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, text, x + w / 2, y + 6, 0xFFFFFF);
    }

    private interface BoolGet { boolean get(); }
    private interface BoolSet { void set(boolean value); }
    private interface IntGet { int get(); }
    private interface IntSet { void set(int value); }

    private static final class Row {
        final int x, y, w;
        final String label;
        final BoolGet boolGet;
        final BoolSet boolSet;
        final IntGet intGet;
        final IntSet intSet;
        final int step;

        private Row(int x, int y, int w, String label, BoolGet bg, BoolSet bs, IntGet ig, IntSet is, int step) {
            this.x = x; this.y = y; this.w = w; this.label = label;
            this.boolGet = bg; this.boolSet = bs; this.intGet = ig; this.intSet = is; this.step = step;
        }

        static Row toggle(int x, int y, int w, String label, BoolGet get, BoolSet set) {
            return new Row(x, y, w, label, get, set, null, null, 0);
        }

        static Row number(int x, int y, int w, String label, IntGet get, IntSet set, int step) {
            return new Row(x, y, w, label, null, null, get, set, step);
        }

        void render(GuiGraphics g, net.minecraft.client.gui.Font font, int mx, int my) {
            boolean hover = inside(mx, my, x, y, w, 18);
            g.fill(x, y, x + w, y + 18, hover ? 0x703D3150 : 0x50251D30);
            g.drawString(font, label, x + 6, y + 5, 0xE8E8E8, false);
            if (boolGet != null) {
                boolean value = boolGet.get();
                String s = value ? "AN" : "AUS";
                int c = value ? 0x55FF88 : 0xFF7777;
                g.drawString(font, s, x + w - font.width(s) - 8, y + 5, c, false);
            } else {
                String s = "-   " + intGet.get() + "   +";
                g.drawString(font, s, x + w - font.width(s) - 8, y + 5, 0xD9C7FF, false);
            }
        }

        boolean click(double mx, double my) {
            if (!inside(mx, my, x, y, w, 18)) return false;
            if (boolGet != null) {
                boolSet.set(!boolGet.get());
            } else {
                int third = w / 3;
                if (mx < x + w - third) intSet.set(intGet.get() - step);
                else intSet.set(intGet.get() + step);
            }
            ChatOverlayConfig.save();
            return true;
        }
    }
}
