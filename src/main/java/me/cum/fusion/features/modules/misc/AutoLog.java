
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.*;
import net.minecraft.util.text.*;
import net.minecraft.network.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.server.*;
import net.minecraft.init.*;
import me.cum.fusion.util.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class AutoLog extends Module
{
    private final Setting<Float> health;
    private final Setting<Boolean> bed;
    private final Setting<Float> range;
    private final Setting<Boolean> logout;
    private static AutoLog INSTANCE;
    
    public AutoLog() {
        super("AutoLog", "Logs when in danger.", Category.MISC, false, false, false);
        this.health = (Setting<Float>)this.register(new Setting("Health", (T)16.0f, (T)0.1f, (T)36.0f));
        this.bed = (Setting<Boolean>)this.register(new Setting("Beds", (T)true));
        this.range = (Setting<Float>)this.register(new Setting("BedRange", (T)6.0f, (T)0.1f, (T)36.0f, v -> this.bed.getValue()));
        this.logout = (Setting<Boolean>)this.register(new Setting("LogoutOff", (T)true));
        this.setInstance();
    }
    
    private void setInstance() {
        AutoLog.INSTANCE = this;
    }
    
    public static AutoLog getInstance() {
        if (AutoLog.INSTANCE == null) {
            AutoLog.INSTANCE = new AutoLog();
        }
        return AutoLog.INSTANCE;
    }
    
    @Override
    public void onTick() {
        if (!nullCheck() && AutoLog.mc.player.getHealth() <= this.health.getValue()) {
            Fusion.moduleManager.disableModule("AutoReconnect");
            AutoLog.mc.player.connection.sendPacket((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("AutoLogged")));
            if (this.logout.getValue()) {
                this.disable();
            }
        }
    }
    
    @SubscribeEvent
    public void onReceivePacket(final PacketEvent.Receive event) {
        final SPacketBlockChange packet;
        if (event.getPacket() instanceof SPacketBlockChange && this.bed.getValue() && (packet = (SPacketBlockChange)event.getPacket()).getBlockState().getBlock() == Blocks.BED && AutoLog.mc.player.getDistanceSqToCenter(packet.getBlockPosition()) <= MathUtil.square(this.range.getValue())) {
            Fusion.moduleManager.disableModule("AutoReconnect");
            AutoLog.mc.player.connection.sendPacket((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("AutoLogged")));
            if (this.logout.getValue()) {
                this.disable();
            }
        }
    }
    
    static {
        AutoLog.INSTANCE = new AutoLog();
    }
}
