
package me.cum.fusion.manager;

import me.cum.fusion.features.*;
import net.minecraft.network.*;
import java.util.*;

public class PacketManager extends Feature
{
    private final List<Packet<?>> noEventPackets;
    
    public PacketManager() {
        this.noEventPackets = new ArrayList<Packet<?>>();
    }
    
    public void sendPacketNoEvent(final Packet<?> packet) {
        if (packet != null && !nullCheck()) {
            this.noEventPackets.add(packet);
            PacketManager.mc.player.connection.sendPacket((Packet)packet);
        }
    }
    
    public boolean shouldSendPacket(final Packet<?> packet) {
        if (this.noEventPackets.contains(packet)) {
            this.noEventPackets.remove(packet);
            return false;
        }
        return true;
    }
}
