
package me.cum.fusion.features.modules.troll;

import me.cum.fusion.features.modules.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;

public class Coordleaker extends Module
{
    public Coordleaker() {
        super("Coordleaker", "CAUTION!: This module is only for trolling", Module.Category.TROLL, true, false, false);
    }
    
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        Coordleaker.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("lol my coords are: " + Math.floor(Coordleaker.mc.player.posX) + ", " + Math.floor(Coordleaker.mc.player.posY) + ", " + Math.floor(Coordleaker.mc.player.posZ) + "! come and kill me."));
    }
}
