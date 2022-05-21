
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import net.minecraft.util.math.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.network.play.server.*;
import net.minecraft.init.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.command.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.cum.fusion.event.events.*;
import me.cum.fusion.features.modules.client.*;
import java.awt.*;
import me.cum.fusion.util.*;

public class ChorusPredict extends Module
{
    private final Timer renderTimer;
    private BlockPos pos;
    private final Setting<Boolean> debug;
    private final Setting<Integer> renderDelay;
    private final Setting<Boolean> rainbow;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    private final Setting<Integer> outlineAlpha;
    
    public ChorusPredict() {
        super("ChorusView", "Predicts Chorus", Category.COMBAT, true, false, false);
        this.renderTimer = new Timer();
        this.debug = (Setting<Boolean>)this.register(new Setting("Debug", (T)true));
        this.renderDelay = (Setting<Integer>)this.register(new Setting("RenderDelay", (T)4000, (T)0, (T)4000));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)false));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.outlineAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
            if (packet.getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT || packet.getSound() == SoundEvents.ENTITY_ENDERMEN_TELEPORT) {
                this.renderTimer.reset2();
                this.pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
                if (this.debug.getValue()) {
                    Command.sendMessage("A player chorused to: " + ChatFormatting.AQUA + "X: " + this.pos.getX() + ", Y: " + this.pos.getY() + ", Z: " + this.pos.getZ());
                }
            }
        }
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (this.pos != null) {
            if (this.renderTimer.passed(this.renderDelay.getValue())) {
                this.pos = null;
                return;
            }
            RenderUtil.drawBoxESP(this.pos, ((boolean)this.rainbow.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.outlineAlpha.getValue()), 1.5f, true, true, this.alpha.getValue());
        }
    }
}
