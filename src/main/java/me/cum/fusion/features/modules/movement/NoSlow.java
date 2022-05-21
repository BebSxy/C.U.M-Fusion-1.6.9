
package me.cum.fusion.features.modules.movement;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.client.settings.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.client.event.*;
import net.minecraft.util.*;

public class NoSlow extends Module
{
    public Setting<Boolean> noSlow;
    public Setting<Boolean> explosions;
    public Setting<Float> horizontal;
    public Setting<Float> vertical;
    private static NoSlow INSTANCE;
    private final boolean sneaking = false;
    private static final KeyBinding[] keys;
    
    public NoSlow() {
        super("NoSlow", "Prevents you from getting slowed down.", Module.Category.MOVEMENT, true, false, false);
        this.noSlow = (Setting<Boolean>)this.register(new Setting("NoSlow", (T)true));
        this.explosions = (Setting<Boolean>)this.register(new Setting("Explosions", (T)false));
        this.horizontal = (Setting<Float>)this.register(new Setting("Horizontal", (T)0.0f, (T)0.0f, (T)100.0f, v -> this.explosions.getValue()));
        this.vertical = (Setting<Float>)this.register(new Setting("Vertical", (T)0.0f, (T)0.0f, (T)100.0f, v -> this.explosions.getValue()));
        this.setInstance();
    }
    
    private void setInstance() {
        NoSlow.INSTANCE = this;
    }
    
    public static NoSlow getInstance() {
        if (NoSlow.INSTANCE == null) {
            NoSlow.INSTANCE = new NoSlow();
        }
        return NoSlow.INSTANCE;
    }
    
    @SubscribeEvent
    public void onPacketReceived(final PacketEvent.Receive event) {
        if (event.getStage() == 0 && NoSlow.mc.player != null) {
            if (event.getPacket() instanceof SPacketEntityVelocity) {
                final SPacketEntityVelocity velocity = (SPacketEntityVelocity)event.getPacket();
                if (velocity.getEntityID() == NoSlow.mc.player.entityId) {
                    if (this.horizontal.getValue() == 0.0f && this.vertical.getValue() == 0.0f) {
                        event.setCanceled(true);
                        return;
                    }
                    velocity.motionX *= (int)(Object)this.horizontal.getValue();
                    velocity.motionY *= (int)(Object)this.vertical.getValue();
                    velocity.motionZ *= (int)(Object)this.horizontal.getValue();
                }
            }
            if (this.explosions.getValue() && event.getPacket() instanceof SPacketExplosion) {
                final SPacketExplosion sPacketExplosion;
                final SPacketExplosion velocity2 = sPacketExplosion = (SPacketExplosion)event.getPacket();
                sPacketExplosion.motionX *= this.horizontal.getValue();
                final SPacketExplosion sPacketExplosion2 = velocity2;
                sPacketExplosion2.motionY *= this.vertical.getValue();
                final SPacketExplosion sPacketExplosion3 = velocity2;
                sPacketExplosion3.motionZ *= this.horizontal.getValue();
            }
        }
    }
    
    @SubscribeEvent
    public void onInput(final InputUpdateEvent event) {
        if (this.noSlow.getValue() && NoSlow.mc.player.isHandActive() && !NoSlow.mc.player.isRiding()) {
            final MovementInput movementInput = event.getMovementInput();
            movementInput.moveStrafe *= 5.0f;
            final MovementInput movementInput2 = event.getMovementInput();
            movementInput2.moveForward *= 5.0f;
        }
    }
    
    static {
        NoSlow.INSTANCE = new NoSlow();
        keys = new KeyBinding[] { NoSlow.mc.gameSettings.keyBindForward, NoSlow.mc.gameSettings.keyBindBack, NoSlow.mc.gameSettings.keyBindLeft, NoSlow.mc.gameSettings.keyBindRight, NoSlow.mc.gameSettings.keyBindJump, NoSlow.mc.gameSettings.keyBindSprint };
    }
}
