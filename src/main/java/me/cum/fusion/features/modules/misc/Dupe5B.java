
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.client.gui.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class Dupe5B extends Module
{
    private Setting<Integer> dropCount;
    public Setting<Boolean> shulkerCheck;
    public Setting<Boolean> dropAll;
    
    public Dupe5B() {
        super("Dupe5B", "Auto Dupe in 5b5t.org", Category.MISC, true, false, false);
        this.dropCount = (Setting<Integer>)this.register(new Setting("Drop Count", (T)1, (T)1, (T)60));
        this.shulkerCheck = (Setting<Boolean>)this.register(new Setting("Shulker Check", (T)false));
        this.dropAll = (Setting<Boolean>)this.register(new Setting("Drop All", (T)false));
    }
    
    @SubscribeEvent
    public void onUpdate(final TickEvent.ClientTickEvent event) {
        if (Dupe5B.mc.player.getHeldItem(EnumHand.MAIN_HAND).getCount() > 1 && (!this.shulkerCheck.getValue() || Dupe5B.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemShulkerBox)) {
            Dupe5B.mc.displayGuiScreen((GuiScreen)null);
            Dupe5B.mc.getConnection().sendPacket((Packet)new CPacketCloseWindow());
            for (int i = 0; i < this.dropCount.getValue(); ++i) {
                Dupe5B.mc.player.dropItem(false);
            }
            if (this.dropAll.getValue()) {
                Dupe5B.mc.player.dropItem(true);
            }
        }
    }
}
