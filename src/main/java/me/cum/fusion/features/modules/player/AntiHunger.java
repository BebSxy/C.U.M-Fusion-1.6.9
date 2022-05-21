
package me.cum.fusion.features.modules.player;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.client.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class AntiHunger extends Module
{
    public Setting<Boolean> cancelSprint;
    public Setting<Boolean> ground;
    
    public AntiHunger() {
        super("AntiHunger", "Prevents you from getting Hungry.", Module.Category.PLAYER, true, false, false);
        this.cancelSprint = (Setting<Boolean>)this.register(new Setting("CancelSprint", (T)true));
        this.ground = (Setting<Boolean>)this.register(new Setting("Ground", (T)true));
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (this.ground.getValue() && event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.onGround = (AntiHunger.mc.player.fallDistance >= 0.0f || AntiHunger.mc.playerController.isHittingBlock);
        }
        if (this.cancelSprint.getValue() && event.getPacket() instanceof CPacketEntityAction) {
            final CPacketEntityAction packet2 = (CPacketEntityAction)event.getPacket();
            if (packet2.getAction() == CPacketEntityAction.Action.START_SPRINTING || packet2.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                event.setCanceled(true);
            }
        }
    }
}
