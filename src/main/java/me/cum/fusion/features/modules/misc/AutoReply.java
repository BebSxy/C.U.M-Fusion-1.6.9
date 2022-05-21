
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class AutoReply extends Module
{
    public AutoReply() {
        super("AutoReply", "replys to msgs automatically", Category.MISC, true, false, false);
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = (SPacketChat)event.getPacket();
            if (packet.getChatComponent() instanceof TextComponentString) {
                final String component = packet.getChatComponent().getFormattedText();
                if (component.toLowerCase().contains("whispers: ")) {
                    AutoReply.mc.player.sendChatMessage("/r Join Compass Symbol Now!: https://discord.gg/w9aBGtZHDb");
                }
            }
        }
    }
}
