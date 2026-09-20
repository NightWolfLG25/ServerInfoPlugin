package de.magicwolf.ggchat;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(MagicWolfGGChatOverlay.MOD_ID)
public final class MagicWolfGGChatOverlay {
    public static final String MOD_ID = "magicwolf_gg_chat_overlay";
    public static final String VERSION = "2.0.1";

    public MagicWolfGGChatOverlay() {
        ChatOverlayConfig.load();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.registerConfigScreen(ChatOverlayConfigScreen::new);
        System.out.println("[MagicWolf] GG Second Chat " + VERSION + " geladen - coded by NightWolfLG");
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!ChatOverlayConfig.enabled || !ServerGuard.allowed()) {
            return;
        }
        if (event.getMessage() != null) {
            ChatOverlayStore.accept(event.getMessage().getString());
        }
    }

    @SubscribeEvent
    public void onRender(RenderGuiEvent.Post event) {
        ChatOverlayRenderer.render(event.getGuiGraphics());
    }
}
