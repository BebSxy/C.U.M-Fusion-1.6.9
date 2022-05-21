
package me.cum.fusion.features.modules.troll;

import me.cum.fusion.features.modules.*;
import java.util.*;
import me.cum.fusion.features.command.*;
import net.minecraft.entity.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.item.*;
import net.minecraft.entity.item.*;

public class EzDupe extends Module
{
    private final Random random;
    
    public EzDupe() {
        super("EzDupe", "Ez dupe.", Module.Category.TROLL, true, false, false);
        this.random = new Random();
    }
    
    public void onEnable() {
        final EntityPlayerSP player = EzDupe.mc.player;
        final WorldClient world = EzDupe.mc.world;
        if (player == null || EzDupe.mc.world == null) {
            return;
        }
        final ItemStack itemStack = player.getHeldItemMainhand();
        if (itemStack.isEmpty()) {
            Command.sendMessage("You need to hold an item in hand to dupe!");
            this.disable();
            return;
        }
        final int count = this.random.nextInt(31) + 1;
        for (int i = 0; i <= count; ++i) {
            final EntityItem entityItem = player.dropItem(itemStack.copy(), false, true);
            if (entityItem != null) {
                world.addEntityToWorld(entityItem.entityId, (Entity)entityItem);
            }
        }
        final int total = count * itemStack.getCount();
        player.sendChatMessage("I just used C.U.M Fusion dupe and got " + total + " " + itemStack.getDisplayName() + " thanks to BrianGamer!");
        this.disable();
    }
}
