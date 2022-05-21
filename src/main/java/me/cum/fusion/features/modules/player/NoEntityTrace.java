
package me.cum.fusion.features.modules.player;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;

public class NoEntityTrace extends Module
{
    public static NoEntityTrace INSTANCE;
    public Setting<Boolean> pickaxe;
    
    public NoEntityTrace() {
        super("NoEntityTrace", "No trace", Module.Category.PLAYER, true, false, false);
        this.pickaxe = (Setting<Boolean>)this.register(new Setting("Pickaxe", (T)true));
        this.setInstance();
    }
    
    private void setInstance() {
        NoEntityTrace.INSTANCE = this;
    }
    
    public static NoEntityTrace getInstance() {
        if (NoEntityTrace.INSTANCE == null) {
            NoEntityTrace.INSTANCE = new NoEntityTrace();
        }
        return NoEntityTrace.INSTANCE;
    }
}
