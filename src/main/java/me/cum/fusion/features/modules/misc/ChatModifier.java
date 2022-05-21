
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.util.*;
import net.minecraft.entity.player.*;
import me.cum.fusion.features.command.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.client.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.util.math.*;

public class ChatModifier extends Module
{
    public Setting<Suffix> suffix;
    public Setting<Boolean> clean;
    public Setting<Boolean> infinite;
    public Setting<Boolean> autoQMain;
    public Setting<Boolean> qNotification;
    public Setting<Integer> qDelay;
    private final Timer timer;
    private static ChatModifier INSTANCE;
    
    public ChatModifier() {
        super("Chat", "Modifies your chat", Category.MISC, true, false, false);
        this.suffix = (Setting<Suffix>)this.register(new Setting("Suffix", (T)Suffix.NONE, "Your Suffix."));
        this.clean = (Setting<Boolean>)this.register(new Setting("CleanChat", (T)Boolean.FALSE, "Cleans your chat"));
        this.infinite = (Setting<Boolean>)this.register(new Setting("Infinite", (T)Boolean.FALSE, "Makes your chat infinite."));
        this.autoQMain = (Setting<Boolean>)this.register(new Setting("AutoQMain", (T)Boolean.FALSE, "Spams AutoQMain"));
        this.qNotification = (Setting<Boolean>)this.register(new Setting("QNotification", (T)Boolean.FALSE, v -> this.autoQMain.getValue()));
        this.qDelay = (Setting<Integer>)this.register(new Setting("QDelay", (T)9, (T)1, (T)90, v -> this.autoQMain.getValue()));
        this.timer = new Timer();
        this.setInstance();
    }
    
    private void setInstance() {
        ChatModifier.INSTANCE = this;
    }
    
    public static ChatModifier getInstance() {
        if (ChatModifier.INSTANCE == null) {
            ChatModifier.INSTANCE = new ChatModifier();
        }
        return ChatModifier.INSTANCE;
    }
    
    @Override
    public void onUpdate() {
        if (this.autoQMain.getValue()) {
            if (!this.shouldSendMessage((EntityPlayer)ChatModifier.mc.player)) {
                return;
            }
            if (this.qNotification.getValue()) {
                Command.sendMessage("<AutoQueueMain> Sending message: /queue main");
            }
            ChatModifier.mc.player.sendChatMessage("/queue main");
            this.timer.reset();
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
            final CPacketChatMessage packet = (CPacketChatMessage)event.getPacket();
            String s = packet.getMessage();
            if (s.startsWith("/")) {
                return;
            }
            if (this.suffix.getValue() == Suffix.Fusion) {
                s += "  \u1d04.\u1d1c.\u1d0d \ua730\u1d1c\ua731\u026a\u1d0f\u0274";
            }
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }
            packet.message = s;
        }
    }
    
    private boolean shouldSendMessage(final EntityPlayer player) {
        return player.dimension == 1 && this.timer.passedS(this.qDelay.getValue()) && player.getPosition().equals((Object)new Vec3i(0, 240, 0));
    }
    
    static {
        ChatModifier.INSTANCE = new ChatModifier();
    }
    
    public enum Suffix
    {
        NONE, 
        Fusion;
    }
}
