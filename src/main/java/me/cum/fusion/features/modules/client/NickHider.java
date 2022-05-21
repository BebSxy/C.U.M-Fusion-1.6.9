
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.command.*;

public class NickHider extends Module
{
    public final Setting<String> NameString;
    private static NickHider instance;
    
    public NickHider() {
        super("Media", "Changes name", Category.CLIENT, false, false, false);
        this.NameString = (Setting<String>)this.register(new Setting("Name", (T)"New Name Here..."));
        NickHider.instance = this;
    }
    
    @Override
    public void onEnable() {
        Command.sendMessage(ChatFormatting.GRAY + "Success! Name succesfully changed to " + ChatFormatting.GREEN + this.NameString.getValue());
    }
    
    public static NickHider getInstance() {
        if (NickHider.instance == null) {
            NickHider.instance = new NickHider();
        }
        return NickHider.instance;
    }
}
