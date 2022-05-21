
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.*;

public class RPC extends Module
{
    public static RPC INSTANCE;
    public Setting<String> state;
    public Setting<Boolean> showIP;
    public Setting<Boolean> ezMode;
    
    public RPC() {
        super("RPC", "Discord rich presence", Category.CLIENT, false, false, false);
        this.state = (Setting<String>)this.register(new Setting("State", (T)"C.U.M Fusion Private", "Sets the state of the DiscordRPC."));
        this.showIP = (Setting<Boolean>)this.register(new Setting("ShowIP", (T)Boolean.TRUE, "Shows the server IP in your discord presence."));
        this.ezMode = (Setting<Boolean>)this.register(new Setting("EZMode", (T)Boolean.FALSE, "Compass on top!"));
        RPC.INSTANCE = this;
    }
    
    @Override
    public void onEnable() {
        DiscordPresence.start();
    }
    
    @Override
    public void onDisable() {
        DiscordPresence.stop();
    }
}
