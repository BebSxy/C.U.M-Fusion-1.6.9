
package me.cum.fusion.features.modules.troll;

import me.cum.fusion.features.modules.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;

public class Suicide extends Module
{
    public Suicide() {
        super("suicide", "for lazy ppl who cant type /kill", Module.Category.TROLL, true, false, false);
    }
    
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        Suicide.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("/kill"));
    }
}
