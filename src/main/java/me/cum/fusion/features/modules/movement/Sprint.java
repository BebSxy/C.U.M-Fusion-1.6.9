
package me.cum.fusion.features.modules.movement;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class Sprint extends Module
{
    private static Sprint INSTANCE;
    public Setting<Mode> mode;
    
    public Sprint() {
        super("Sprint", "Modifies sprinting", Module.Category.MOVEMENT, false, false, false);
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", (T)Mode.LEGIT));
        this.setInstance();
    }
    
    public static Sprint getInstance() {
        if (Sprint.INSTANCE == null) {
            Sprint.INSTANCE = new Sprint();
        }
        return Sprint.INSTANCE;
    }
    
    private void setInstance() {
        Sprint.INSTANCE = this;
    }
    
    @SubscribeEvent
    public void onSprint(final MoveEvent event) {
        if (event.getStage() == 1 && this.mode.getValue() == Mode.RAGE && (Sprint.mc.player.movementInput.moveForward != 0.0f || Sprint.mc.player.movementInput.moveStrafe != 0.0f)) {
            event.setCanceled(true);
        }
    }
    
    public void onUpdate() {
        switch (this.mode.getValue()) {
            case RAGE: {
                if ((!Sprint.mc.gameSettings.keyBindForward.isKeyDown() && !Sprint.mc.gameSettings.keyBindBack.isKeyDown() && !Sprint.mc.gameSettings.keyBindLeft.isKeyDown() && !Sprint.mc.gameSettings.keyBindRight.isKeyDown()) || Sprint.mc.player.isSneaking() || Sprint.mc.player.collidedHorizontally) {
                    break;
                }
                if (Sprint.mc.player.getFoodStats().getFoodLevel() <= 6.0f) {
                    break;
                }
                Sprint.mc.player.setSprinting(true);
                break;
            }
            case LEGIT: {
                if (!Sprint.mc.gameSettings.keyBindForward.isKeyDown() || Sprint.mc.player.isSneaking() || Sprint.mc.player.isHandActive() || Sprint.mc.player.collidedHorizontally || Sprint.mc.player.getFoodStats().getFoodLevel() <= 6.0f) {
                    break;
                }
                if (Sprint.mc.currentScreen != null) {
                    break;
                }
                Sprint.mc.player.setSprinting(true);
                break;
            }
        }
    }
    
    public void onDisable() {
        if (!nullCheck()) {
            Sprint.mc.player.setSprinting(false);
        }
    }
    
    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }
    
    static {
        Sprint.INSTANCE = new Sprint();
    }
    
    public enum Mode
    {
        LEGIT, 
        RAGE;
    }
}
