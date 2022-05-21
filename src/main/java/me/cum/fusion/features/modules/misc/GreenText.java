
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.client.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class GreenText extends Module
{
    public Boolean suffix;
    public String s;
    
    public GreenText() {
        super("GreenText", "green.", Category.MISC, true, false, false);
        this.suffix = true;
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
            final CPacketChatMessage packet = (CPacketChatMessage)event.getPacket();
            String s = packet.getMessage();
            if (s.startsWith("/")) {
                return;
            }
            s = "> " + s;
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }
            packet.message = s;
        }
    }
    
    @SubscribeEvent
    public void onChatPacketReceive(final PacketEvent.Receive event) {
        if (event.getStage() == 0) {
            event.getPacket();
        }
    }
}
