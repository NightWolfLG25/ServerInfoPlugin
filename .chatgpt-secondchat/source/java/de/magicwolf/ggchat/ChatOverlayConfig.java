package de.magicwolf.ggchat;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ChatOverlayConfig {
    private ChatOverlayConfig() {}

    public static boolean enabled = true;
    public static boolean captureMsg = true;
    public static boolean capturePlotChat = true;
    public static boolean captureMoney = true;
    public static boolean onlyGrieferGames = true;
    public static boolean showTimestamps = true;
    public static boolean showHeader = true;
    public static boolean showBackground = true;

    public static int width = 420;
    public static int height = 170;
    public static int rightMargin = 8;
    public static int bottomMargin = 42;
    public static int opacity = 150;
    public static int maxMessages = 180;

    public static int msgColor = 0x55FFFF;
    public static int plotColor = 0xFFAA00;
    public static int normalColor = 0xFFFFFF;
    public static int moneyInColor = 0x55FF55;
    public static int moneyOutColor = 0xFF5555;

    public static String msgKeywords = "[msg],msg:,flüstert,fluestert,privat,-> mir,mir ->,von ";
    public static String plotKeywords = "[pchat],pchat,plot-chat,plotchat,/p chat";

    private static final String FILE_NAME = "magicwolf_gg_chat_overlay_v110.properties";

    public static Path path() {
        return FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
    }

    public static synchronized void load() {
        resetDefaults();
        Path path = path();
        if (Files.isRegularFile(path)) {
            Properties p = new Properties();
            try (InputStream in = Files.newInputStream(path)) {
                p.load(in);
                enabled = bool(p, "enabled", enabled);
                captureMsg = bool(p, "captureMsg", captureMsg);
                capturePlotChat = bool(p, "capturePlotChat", capturePlotChat);
                captureMoney = bool(p, "captureMoney", captureMoney);
                onlyGrieferGames = bool(p, "onlyGrieferGames", onlyGrieferGames);
                showTimestamps = bool(p, "showTimestamps", showTimestamps);
                showHeader = bool(p, "showHeader", showHeader);
                showBackground = bool(p, "showBackground", showBackground);
                width = integer(p, "width", width, 220, 900);
                height = integer(p, "height", height, 80, 600);
                rightMargin = integer(p, "rightMargin", rightMargin, 0, 500);
                bottomMargin = integer(p, "bottomMargin", bottomMargin, 0, 500);
                opacity = integer(p, "opacity", opacity, 0, 255);
                maxMessages = integer(p, "maxMessages", maxMessages, 20, 1000);
                msgColor = color(p, "msgColor", msgColor);
                plotColor = color(p, "plotColor", plotColor);
                normalColor = color(p, "normalColor", normalColor);
                moneyInColor = color(p, "moneyInColor", moneyInColor);
                moneyOutColor = color(p, "moneyOutColor", moneyOutColor);
                msgKeywords = p.getProperty("msgKeywords", msgKeywords).trim();
                plotKeywords = p.getProperty("plotKeywords", plotKeywords).trim();
            } catch (Exception e) {
                System.err.println("[MagicWolf] SecondChat Config konnte nicht komplett gelesen werden: " + e.getMessage());
            }
        }
        save();
    }

    public static synchronized void save() {
        Properties p = new Properties();
        p.setProperty("enabled", Boolean.toString(enabled));
        p.setProperty("captureMsg", Boolean.toString(captureMsg));
        p.setProperty("capturePlotChat", Boolean.toString(capturePlotChat));
        p.setProperty("captureMoney", Boolean.toString(captureMoney));
        p.setProperty("onlyGrieferGames", Boolean.toString(onlyGrieferGames));
        p.setProperty("showTimestamps", Boolean.toString(showTimestamps));
        p.setProperty("showHeader", Boolean.toString(showHeader));
        p.setProperty("showBackground", Boolean.toString(showBackground));
        p.setProperty("width", Integer.toString(width));
        p.setProperty("height", Integer.toString(height));
        p.setProperty("rightMargin", Integer.toString(rightMargin));
        p.setProperty("bottomMargin", Integer.toString(bottomMargin));
        p.setProperty("opacity", Integer.toString(opacity));
        p.setProperty("maxMessages", Integer.toString(maxMessages));
        p.setProperty("msgColor", String.format("#%06X", msgColor & 0xFFFFFF));
        p.setProperty("plotColor", String.format("#%06X", plotColor & 0xFFFFFF));
        p.setProperty("normalColor", String.format("#%06X", normalColor & 0xFFFFFFF));
        p.setProperty("moneyInColor", String.format("#%06X", moneyInColor & 0xFFFFFF));
        p.setProperty("moneyOutColor", String.format("#%06X", moneyOutColor & 0xFFFFFF));
        p.setProperty("msgKeywords", msgKeywords);
        p.setProperty("plotKeywords", plotKeywords);
        try {
            Files.createDirectories(path().getParent());
            try (OutputStream out = Files.newOutputStream(path())) {
                p.store(out, "MagicWolf GG Second Chat - coded by NightWolfLG");
            }
        } catch (IOException e) {
            System.err.println("[MagicWolf] SecondChat Config konnte nicht gespeichert werden: " + e.getMessage());
        }
    }

    public static void resetDefaults() {
        enabled = true;
        captureMsg = true;
        capturePlotChat = true;
        captureMoney = true;
        onlyGrieferGames = true;
        showTimestamps = true;
        showHeader = true;
        showBackground = true;
        width = 420;
        height = 170;
        rightMargin = 8;
        bottomMargin = 42;
        opacity = 150;
        maxMessages = 180;
        msgColor = 0x55FFFF;
        plotColor = 0xFFAA00;
        normalColor = 0xFFFFFF;
        moneyInColor = 0x55FF55;
        moneyOutColor = 0xFF5555;
        msgKeywords = "[msg],msg:,fluestert,privat,-> mir,mir ->,von ";
        plotKeywords = "[pchat],pchat,plot-chat,plotchat,/p chat";
    }

    private static boolean bool(Properties p, String key, boolean def) {
        return Boolean.parseBoolean(p.getProperty(key, Boolean.toString(def)));
    }

    private static int integer(Properties p, String key, int def, int min, int max) {
        try {
            return Math.max(min, Math.min(max, Integer.parseInt(p.getProperty(key, Integer.toString(def)).trim())));
        } catch (NumberFormatException ignored) {
            return def;
        }
    }

    private static int color(Properties p, String key, int def) {
        String value = p.getProperty(key);
        if (value == null) return def;
        try {
            value = value.trim();
            if (value.startsWith("#")) value = value.substring(1);
            return Integer.parseInt(value, 16) & 0xFFFFFF;
        } catch (NumberFormatException ignored) {
            return def;
        }
    }
}
