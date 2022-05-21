
package me.cum.fusion.features.modules.movement;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.*;
import me.cum.fusion.util.*;
import net.minecraft.entity.*;
import net.minecraft.client.entity.*;

public class Speed extends Module
{
    private final Timer timer;
    Setting<Mode> mode;
    Setting<Double> yPortSpeed;
    Setting<Boolean> step;
    Setting<Double> vanillaSpeed;
    public boolean hop;
    private double prevY;
    public boolean move;
    
    public Speed() {
        super("Speed", "YPort Speed.", Module.Category.MOVEMENT, false, false, false);
        this.timer = new Timer();
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", (T)Mode.yPort));
        this.yPortSpeed = (Setting<Double>)this.register(new Setting("YPort Speed", (T)0.6, (T)0.5, (T)1.5, v -> this.mode.getValue() == Mode.yPort));
        this.step = (Setting<Boolean>)this.register(new Setting("Step", (T)true, v -> this.mode.getValue() == Mode.yPort));
        this.vanillaSpeed = (Setting<Double>)this.register(new Setting("Vanilla Speed", (T)1.0, (T)1.7, (T)10.0, v -> this.mode.getValue() == Mode.Vanilla));
    }
    
    private void jump() {
        this.hop = true;
        this.prevY = Util.mc.player.posY;
        Util.mc.player.jump();
    }
    
    public void onEnable() {
        PlayerUtil.getBaseMoveSpeed();
        if (this.step.getValue()) {
            if (fullNullCheck()) {
                return;
            }
            Speed.mc.player.stepHeight = 2.0f;
        }
    }
    
    public void onDisable() {
        Fusion.timerManager.reset();
        this.timer.reset();
        if (this.step.getValue()) {
            Speed.mc.player.stepHeight = 0.6f;
        }
    }
    
    public void onUpdate() {
        if (nullCheck()) {
            this.disable();
            return;
        }
        if (this.mode.getValue() == Mode.Vanilla) {
            if (Speed.mc.player == null || Speed.mc.world == null) {
                return;
            }
            final double[] calc = MathUtil.directionSpeed(this.vanillaSpeed.getValue() / 10.0);
            Speed.mc.player.motionX = calc[0];
            Speed.mc.player.motionZ = calc[1];
        }
        if (this.mode.getValue() == Mode.yPort) {
            if (!PlayerUtil.isMoving((EntityLivingBase)Speed.mc.player) || (Speed.mc.player.isInWater() && Speed.mc.player.isInLava()) || Speed.mc.player.collidedHorizontally) {
                return;
            }
            if (Speed.mc.player.onGround) {
                Fusion.timerManager.setTimer(1.15f);
                Speed.mc.player.jump();
                PlayerUtil.setSpeed((EntityLivingBase)Speed.mc.player, PlayerUtil.getBaseMoveSpeed() + this.yPortSpeed.getValue() / 10.0);
            }
            else {
                Speed.mc.player.motionY = -1.0;
                Fusion.timerManager.reset();
            }
        }
        if (this.mode.getValue() == Mode.onGround) {
            if (this.hop & Util.mc.player.posY >= this.prevY + 0.399994) {
                Util.mc.player.motionY = -0.9;
                Util.mc.player.posY = this.prevY;
                this.hop = false;
            }
            if (Util.mc.player.moveForward != 0.0f & !Util.mc.player.collidedHorizontally) {
                if (Util.mc.player.moveForward == 0.0f & Util.mc.player.moveStrafing == 0.0f) {
                    Util.mc.player.motionX = 0.0;
                    Util.mc.player.motionZ = 0.0;
                    if (Util.mc.player.collidedVertically) {
                        Util.mc.player.jump();
                        this.move = true;
                    }
                    if (this.move & Util.mc.player.collidedVertically) {
                        this.move = false;
                    }
                }
                if (Util.mc.player.collidedVertically) {
                    final EntityPlayerSP player = Util.mc.player;
                    player.motionX *= 1.0379;
                    final EntityPlayerSP player2 = Util.mc.player;
                    player2.motionZ *= 1.0379;
                    this.jump();
                }
                if (this.hop & !this.move & Util.mc.player.posY >= this.prevY + 0.399994) {
                    Util.mc.player.motionY = -100.0;
                    Util.mc.player.posY = this.prevY;
                    this.hop = false;
                }
            }
        }
    }
    
    public enum Mode
    {
        yPort, 
        Vanilla, 
        onGround;
    }
}
