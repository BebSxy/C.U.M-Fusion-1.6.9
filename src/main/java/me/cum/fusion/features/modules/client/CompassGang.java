
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import java.awt.*;
import java.net.*;

public class CompassGang extends Module
{
    public CompassGang() {
        super("CompassGang", "Auto Compass Gang!", Category.CLIENT, true, false, false);
    }
    
    @Override
    public String getDisplayInfo() {
        return null;
    }
    
    @Override
    public void onEnable() {
        try {
            Desktop.getDesktop().browse(URI.create("https://discord.gg/djUHaKWEFj"));
            CompassGang.mc.player.sendChatMessage("Join Compass Symbol Clan: https://discord.gg/djUHaKWEFj");
        }
        catch (Exception ex) {}
        this.disable();
    }
}
