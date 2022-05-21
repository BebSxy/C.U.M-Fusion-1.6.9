
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.util.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.server.*;
import net.minecraft.init.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class AutoFish extends Module
{
    public boolean cast;
    public boolean nospam;
    public Setting<Boolean> swing;
    
    public AutoFish() {
        super("AutoFish", "this is how u can get good enchant books on oldfag.org", Category.MISC, true, false, false);
        this.nospam = false;
        this.swing = (Setting<Boolean>)this.register(new Setting("Swing", (T)true));
    }
    
    @Override
    public void onEnable() {
        this.cast = true;
        this.nospam = true;
    }
    
    @Override
    public void onTick() {
        if (this.cast && this.nospam) {
            AutoFish.mc.rightClickMouse();
            if (this.swing.getValue()) {
                AutoFish.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            this.nospam = false;
        }
    }
    
    @SubscribeEvent
    public void onPacketReceived(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
            if (packet.getSound() == SoundEvents.ENTITY_BOBBER_SPLASH) {
                AutoFish.mc.rightClickMouse();
                if (this.swing.getValue()) {
                    AutoFish.mc.player.swingArm(EnumHand.MAIN_HAND);
                }
                this.cast = true;
                this.nospam = true;
            }
        }
    }
}
