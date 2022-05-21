
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;

public class ModuleTools extends Module
{
    private static ModuleTools INSTANCE;
    public Setting<Notifier> notifier;
    public Setting<PopNotifier> popNotifier;
    
    public ModuleTools() {
        super("ModuleTools", "Change settings", Category.CLIENT, true, false, false);
        this.notifier = (Setting<Notifier>)this.register(new Setting("ModuleNotifier", (T)Notifier.PHOBOS));
        this.popNotifier = (Setting<PopNotifier>)this.register(new Setting("PopNotifier", (T)PopNotifier.PHOBOS));
        ModuleTools.INSTANCE = this;
    }
    
    public static ModuleTools getInstance() {
        if (ModuleTools.INSTANCE == null) {
            ModuleTools.INSTANCE = new ModuleTools();
        }
        return ModuleTools.INSTANCE;
    }
    
    public enum Notifier
    {
        TROLLGOD, 
        PHOBOS, 
        FUTURE, 
        DOTGOD;
    }
    
    public enum PopNotifier
    {
        PHOBOS, 
        FUTURE, 
        DOTGOD, 
        NONE;
    }
}
