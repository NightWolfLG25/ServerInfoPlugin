package de.magicwolf.ggchat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class ChatOverlayStore {
    private ChatOverlayStore() {}

    public enum Type { MSG, PLOT, MONEY_IN, MONEY_OUT, NORMAL }

    public record Entry(String text, Type type, String time) {}

    private static final Deque<Entry> ENTRIES = new ArrayDeque<>();
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Pattern MONEY = Pattern.compile("(?i).*(?:\\+|-)?\\s*\\d[\\d.,]*\\s*(?:\\$|€|dollar).*|.*(?:geld|kontostand|bezahlt|erhalten|bekommen|abgezogen|überwiesen|ueberwiesen).*\\d.*");
    private static String lastRaw = "";
    private static long lastAt = 0L;

    public static synchronized void accept(String raw) {
        if (raw == null) return;
        String text = raw.replaceAll("§[0-9A-FK-ORa-fk-or]", "").trim();
        if (text.isEmpty()) return;

        long now = System.currentTimeMillis();
        if (text.equals(lastRaw) && now - lastAt < 350L) return;
        lastRaw = text;
        lastAt = now;

        Type type = classify(text);
        if (type == null) return;
        ENTRIES.addLast(new Entry(text, type, LocalTime.now().format(CLOCK)));
        trim();
    }

    private static Type classify(String text) {
        String s = text.toLowerCase(Locale.ROOT);

        if (ChatOverlayConfig.captureMoney && isMoney(s)) {
            if (containsAny(s, "erhalten", "bekommen", "gutgeschrieben", "eingang", "+")) return Type.MONEY_IN;
            if (containsAny(s, "bezahlt", "abgezogen", "gesendet", "überwiesen", "ueberwiesen", "ausgang", "-")) return Type.MONEY_OUT;
            return Type.NORMAL;
        }
        if (ChatOverlayConfig.captureMsg && keywordMatch(s, ChatOverlayConfig.msgKeywords)) return Type.MSG;
        if (ChatOverlayConfig.capturePlotChat && keywordMatch(s, ChatOverlayConfig.plotKeywords)) return Type.PLOT;

        // Additional common GrieferGames forms that do not always contain a literal /msg or /p chat token.
        if (ChatOverlayConfig.captureMsg && (s.contains(" -> mir") || s.contains("mir -> ") || s.startsWith("von ") || s.contains("flüstert"))) {
            return Type.MSG;
        }
        if (ChatOverlayConfig.capturePlotChat && (s.contains("[plot") || s.contains("[pchat") || s.contains("plot-chat"))) {
            return Type.PLOT;
        }
        return null;
    }

    private static boolean isMoney(String s) {
        return MONEY.matcher(s).matches();
    }

    private static boolean keywordMatch(String s, String csv) {
        if (csv == null || csv.isBlank()) return false;
        for (String token : csv.split(",")) {
            token = token.trim().toLowerCase(Locale.ROOT);
            if (!token.isEmpty() && s.contains(token)) return true;
        }
        return false;
    }

    private static boolean containsAny(String value, String... needles) {
        for (String needle : needles) if (value.contains(needle)) return true;
        return false;
    }

    private static void trim() {
        int cap = Math.max(20, ChatOverlayConfig.maxMessages);
        while (ENTRIES.size() > cap) ENTRIES.removeFirst();
    }

    public static synchronized List<Entry> snapshot() {
        trim();
        return new ArrayList<>(ENTRIES);
    }

    public static synchronized void clear() {
        ENTRIES.clear();
    }
}
