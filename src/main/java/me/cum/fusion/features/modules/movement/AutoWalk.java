
package me.cum.fusion.features.modules.movement;

import me.cum.fusion.features.modules.*;
import net.minecraft.client.settings.*;

public class AutoWalk extends Module
{
    public AutoWalk() {
        super("AutoWalk", "Automatically walks in a straight line", Module.Category.MOVEMENT, true, false, false);
    }
    
    public void onUpdate() {
        KeyBinding.setKeyBindState(AutoWalk.mc.gameSettings.keyBindForward.getKeyCode(), true);
    }
    
    public void onDisable() {
        KeyBinding.setKeyBindState(AutoWalk.mc.gameSettings.keyBindForward.getKeyCode(), false);
    }
}
