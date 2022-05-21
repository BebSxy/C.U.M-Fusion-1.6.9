
package me.cum.fusion;

import club.minnced.discord.rpc.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import me.cum.fusion.features.modules.client.*;

public class DiscordPresence
{
    private static final DiscordRPC rpc;
    public static DiscordRichPresence presence;
    private static Thread thread;
    private static int index;
    
    public static void start() {
        final DiscordEventHandlers handlers = new DiscordEventHandlers();
        DiscordPresence.rpc.Discord_Initialize("917578875377168455", handlers, true, "");
        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        DiscordPresence.presence.details = ((Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) ? "In the main menu " : ("Cumming " + ((Minecraft.getMinecraft().currentServerData != null) ? (RPC.INSTANCE.showIP.getValue() ? ("on " + Minecraft.getMinecraft().currentServerData.serverIP + ".") : " multiplayer.") : " singleplayer.")));
        DiscordPresence.presence.state = RPC.INSTANCE.state.getValue();
        DiscordPresence.presence.largeImageKey = "compass";
        DiscordPresence.presence.largeImageText = "C.U.M Fusion Private Client";
        DiscordPresence.presence.partyId = "ae488379-351d-4a4f-ad32-2b9b01c91657";
        DiscordPresence.presence.partySize = 1;
        DiscordPresence.presence.partyMax = 10;
        DiscordPresence.presence.joinSecret = "join";
        DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
        DiscordRichPresence presence;
        String string;
        String string2;
        final StringBuilder sb;
        (DiscordPresence.thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                DiscordPresence.rpc.Discord_RunCallbacks();
                presence = DiscordPresence.presence;
                if (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) {
                    string = "In the main menu.";
                }
                else {
                    new StringBuilder().append("Cumming ");
                    if (Minecraft.getMinecraft().currentServerData != null) {
                        if (RPC.INSTANCE.showIP.getValue()) {
                            string2 = "on " + Minecraft.getMinecraft().currentServerData.serverIP + ".";
                        }
                        else {
                            string2 = " multiplayer.";
                        }
                    }
                    else {
                        string2 = " singleplayer.";
                    }
                    string = sb.append(string2).toString();
                }
                presence.details = string;
                DiscordPresence.presence.state = RPC.INSTANCE.state.getValue();
                if (RPC.INSTANCE.ezMode.getValue()) {
                    if (DiscordPresence.index == 12) {
                        DiscordPresence.index = 1;
                    }
                    DiscordPresence.presence.largeImageKey = "compass" + DiscordPresence.index;
                    ++DiscordPresence.index;
                }
                DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException ex) {}
            }
        }, "RPC-Callback-Handler")).start();
    }
    
    public static void stop() {
        if (DiscordPresence.thread != null && !DiscordPresence.thread.isInterrupted()) {
            DiscordPresence.thread.interrupt();
        }
        DiscordPresence.rpc.Discord_Shutdown();
    }
    
    static {
        DiscordPresence.index = 1;
        rpc = DiscordRPC.INSTANCE;
        DiscordPresence.presence = new DiscordRichPresence();
    }
}
