
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class CancelSwing extends Module
{
    public Setting<Switch> switchSetting;
    public Setting<Swing> swing;
    
    public CancelSwing() {
        super("Swing", "si", Module.Category.PLAYER, true, false, false);
        this.switchSetting = new Setting<Switch>("Switch", Switch.ONEDOTEIGHT);
        this.swing = new Setting<Swing>("Swing", Swing.MAINHAND);
        this.register((Setting)this.switchSetting);
        this.register((Setting)this.swing);
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketAnimation) {
            if (this.swing.getValue() == Swing.MAINHAND) {
                CancelSwing.mc.player.swingingHand = EnumHand.MAIN_HAND;
            }
            if (this.swing.getValue() == Swing.OFFHAND) {
                CancelSwing.mc.player.swingingHand = EnumHand.OFF_HAND;
            }
            if (this.swing.getValue() == Swing.CANCEL) {
                event.setCanceled(true);
            }
        }
    }
    
    public void onTick() {
        if (fullNullCheck()) {
            return;
        }
        if (this.switchSetting.getValue() == Switch.ONEDOTEIGHT && CancelSwing.mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            CancelSwing.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            CancelSwing.mc.entityRenderer.itemRenderer.itemStackMainHand = CancelSwing.mc.player.getHeldItemMainhand();
        }
    }
    
    public enum Switch
    {
        ONEDOTEIGHT, 
        ONEDOTNINE;
    }
    
    public enum Swing
    {
        MAINHAND, 
        OFFHAND, 
        CANCEL;
    }
}
