
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;

public class FriendSettings extends Module
{
    private static FriendSettings INSTANCE;
    public Setting<Boolean> notify;
    
    public FriendSettings() {
        super("FriendSettings", "Change aspects of friends", Category.MISC, true, false, false);
        this.notify = (Setting<Boolean>)this.register(new Setting("Notify", (T)false));
        FriendSettings.INSTANCE = this;
    }
    
    public static FriendSettings getInstance() {
        if (FriendSettings.INSTANCE == null) {
            FriendSettings.INSTANCE = new FriendSettings();
        }
        return FriendSettings.INSTANCE;
    }
}
