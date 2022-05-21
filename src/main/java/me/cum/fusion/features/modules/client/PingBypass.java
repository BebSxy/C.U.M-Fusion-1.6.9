
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import java.util.concurrent.atomic.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;
import me.cum.fusion.util.*;
import net.minecraft.network.play.server.*;
import java.util.*;

public class PingBypass extends Module
{
    private static PingBypass instance;
    private final AtomicBoolean connected;
    private final Timer pingTimer;
    private final List<Long> pingList;
    public Setting<String> ip;
    public Setting<String> port;
    public Setting<String> serverIP;
    public Setting<Boolean> noFML;
    public Setting<Boolean> getName;
    public Setting<Boolean> average;
    public Setting<Boolean> clear;
    public Setting<Boolean> oneWay;
    public Setting<Integer> delay;
    private long currentPing;
    private long serverPing;
    private StringBuffer name;
    private long averagePing;
    private String serverPrefix;
    
    public PingBypass() {
        super("PingBypass", "Manages Phobos`s internal Server.", Category.CLIENT, false, false, true);
        this.connected = new AtomicBoolean(false);
        this.pingTimer = new Timer();
        this.pingList = new ArrayList<Long>();
        this.ip = (Setting<String>)this.register(new Setting("PhobosIP", (T)"0.0.0.0.0"));
        this.port = (Setting<String>)this.register((Setting)new Setting<String>("Port", "0").setRenderName(true));
        this.serverIP = (Setting<String>)this.register(new Setting("ServerIP", (T)"us.crystalpvp.cc"));
        this.noFML = (Setting<Boolean>)this.register(new Setting("RemoveFML", (T)false));
        this.getName = (Setting<Boolean>)this.register(new Setting("GetName", (T)false));
        this.average = (Setting<Boolean>)this.register(new Setting("Average", (T)false));
        this.clear = (Setting<Boolean>)this.register(new Setting("ClearPings", (T)false));
        this.oneWay = (Setting<Boolean>)this.register(new Setting("OneWay", (T)false));
        this.delay = (Setting<Integer>)this.register(new Setting("KeepAlives", (T)10, (T)1, (T)50));
        this.serverPrefix = "idk";
        PingBypass.instance = this;
    }
    
    public static PingBypass getInstance() {
        if (PingBypass.instance == null) {
            PingBypass.instance = new PingBypass();
        }
        return PingBypass.instance;
    }
    
    public String getPlayerName() {
        if (this.name == null) {
            return null;
        }
        return this.name.toString();
    }
    
    public String getServerPrefix() {
        return this.serverPrefix;
    }
    
    @Override
    public void onLogout() {
        this.averagePing = 0L;
        this.currentPing = 0L;
        this.serverPing = 0L;
        this.pingList.clear();
        this.connected.set(false);
        this.name = null;
    }
    
    @SubscribeEvent
    public void onReceivePacket(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = (SPacketChat)event.getPacket();
            if (packet.chatComponent.getUnformattedText().startsWith("@Clientprefix")) {
                this.serverPrefix = packet.chatComponent.getFormattedText().replace("@Clientprefix", "");
            }
        }
    }
    
    @Override
    public void onTick() {
        if (PingBypass.mc.getConnection() != null && this.isConnected()) {
            if (this.getName.getValue()) {
                PingBypass.mc.getConnection().sendPacket((Packet)new CPacketChatMessage("@Servername"));
                this.getName.setValue(false);
            }
            if (this.serverPrefix.equalsIgnoreCase("idk") && PingBypass.mc.world != null) {
                PingBypass.mc.getConnection().sendPacket((Packet)new CPacketChatMessage("@Servergetprefix"));
            }
            if (this.pingTimer.passedMs(this.delay.getValue() * 1000)) {
                PingBypass.mc.getConnection().sendPacket((Packet)new CPacketKeepAlive(100L));
                this.pingTimer.reset();
            }
            if (this.clear.getValue()) {
                this.pingList.clear();
            }
        }
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            final SPacketChat packetChat = (SPacketChat)event.getPacket();
            if (packetChat.getChatComponent().getFormattedText().startsWith("@Client")) {
                this.name = new StringBuffer(TextUtil.stripColor(packetChat.getChatComponent().getFormattedText().replace("@Client", "")));
                event.setCanceled(true);
            }
        }
        else {
            final SPacketKeepAlive alive;
            if (event.getPacket() instanceof SPacketKeepAlive && (alive = (SPacketKeepAlive)event.getPacket()).getId() > 0L && alive.getId() < 1000L) {
                this.serverPing = alive.getId();
                this.currentPing = (this.oneWay.getValue() ? (this.pingTimer.getPassedTimeMs() / 2L) : this.pingTimer.getPassedTimeMs());
                this.pingList.add(this.currentPing);
                this.averagePing = this.getAveragePing();
            }
        }
    }
    
    @Override
    public String getDisplayInfo() {
        return this.averagePing + "ms";
    }
    
    private long getAveragePing() {
        if (!this.average.getValue() || this.pingList.isEmpty()) {
            return this.currentPing;
        }
        int full = 0;
        for (final long i : this.pingList) {
            full += (int)i;
        }
        return full / this.pingList.size();
    }
    
    public boolean isConnected() {
        return this.connected.get();
    }
    
    public int getPort() {
        int result;
        try {
            result = Integer.parseInt(this.port.getValue());
        }
        catch (NumberFormatException e) {
            return -1;
        }
        return result;
    }
    
    public long getServerPing() {
        return this.serverPing;
    }
}
