
package me.cum.fusion.features.modules.movement;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.util.*;
import net.minecraft.entity.*;

public class AntiWeb extends Module
{
    private final Setting<Boolean> HoleOnly;
    public Setting<Float> timerSpeed;
    public float speed;
    
    public AntiWeb() {
        super("AntiWeb", "Turns on timer when in a web", Module.Category.MOVEMENT, true, false, false);
        this.timerSpeed = (Setting<Float>)this.register(new Setting("Speed", (T)4.0f, (T)0.1f, (T)50.0f));
        this.speed = 1.0f;
        this.HoleOnly = (Setting<Boolean>)this.register(new Setting("HoleOnly", (T)true));
    }
    
    public void onEnable() {
        this.speed = this.timerSpeed.getValue();
    }
    
    public void onUpdate() {
        if (this.HoleOnly.getValue()) {
            if (AntiWeb.mc.player.isInWeb && EntityUtil.isInHole((Entity)AntiWeb.mc.player)) {
                AntiWeb.mc.timer.tickLength = 50.0f / ((this.timerSpeed.getValue() == 0.0f) ? 0.1f : this.timerSpeed.getValue());
            }
            else {
                AntiWeb.mc.timer.tickLength = 50.0f;
            }
            if (AntiWeb.mc.player.onGround && EntityUtil.isInHole((Entity)AntiWeb.mc.player)) {
                AntiWeb.mc.timer.tickLength = 50.0f;
            }
        }
        if (!this.HoleOnly.getValue()) {
            if (AntiWeb.mc.player.isInWeb) {
                AntiWeb.mc.timer.tickLength = 50.0f / ((this.timerSpeed.getValue() == 0.0f) ? 0.1f : this.timerSpeed.getValue());
            }
            else {
                AntiWeb.mc.timer.tickLength = 50.0f;
            }
            if (AntiWeb.mc.player.onGround) {
                AntiWeb.mc.timer.tickLength = 50.0f;
            }
        }
    }
}
