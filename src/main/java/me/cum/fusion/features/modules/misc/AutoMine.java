
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.util.*;

public class AutoMine extends Module
{
    public AutoMine() {
        super("AutoMine", "for lazy ppl who want to mine", Category.MISC, true, false, false);
    }
    
    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (AutoMine.mc.objectMouseOver != null) {
            AutoMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, AutoMine.mc.objectMouseOver.getBlockPos(), EnumFacing.UP));
        }
        AutoMine.mc.player.swingArm(EnumHand.MAIN_HAND);
    }
}
