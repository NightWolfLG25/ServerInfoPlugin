package de.magicwolf.ggchat;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public final class ServerGuard {
    private ServerGuard() {}

    public static boolean allowed() {
        if (!ChatOverlayConfig.onlyGrieferGames) return true;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return false;
        Object server = mc.getCurrentServer();
        if (server == null) return false;
        String address = extractAddress(server);
        if (address == null) return false;
        String s = address.toLowerCase(Locale.ROOT);
        return s.contains("griefergames") || s.contains("griefergames.net");
    }

    private static String extractAddress(Object server) {
        for (String name : new String[]{"ip", "address", "getIp", "getAddress"}) {
            try {
                Method m = server.getClass().getMethod(name);
                Object value = m.invoke(server);
                if (value != null) return value.toString();
            } catch (Exception ignored) {}
            try {
                Field f = server.getClass().getDeclaredField(name);
                f.setAccessible(true);
                Object value = f.get(server);
                if (value != null) return value.toString();
            } catch (Exception ignored) {}
        }
        return server.toString();
    }
}
