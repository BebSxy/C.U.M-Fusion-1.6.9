
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import net.minecraft.item.*;
import net.minecraft.client.settings.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.util.*;

public class AutoEat extends Module
{
    private int lastSlot;
    private boolean eating;
    
    public AutoEat() {
        super("AutoEat", "eats automatically for lazy ppl", Category.MISC, true, false, false);
    }
    
    private boolean isValid(final ItemStack stack, final int food) {
        return stack.getItem() instanceof ItemFood && 20 - food >= ((ItemFood)stack.getItem()).getHealAmount(stack);
    }
    
    @Override
    public void onUpdate() {
        if (this.eating && !AutoEat.mc.player.isHandActive()) {
            if (this.lastSlot != -1) {
                AutoEat.mc.player.inventory.currentItem = this.lastSlot;
                this.lastSlot = -1;
            }
            this.eating = false;
            KeyBinding.setKeyBindState(AutoEat.mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            return;
        }
        if (this.eating) {
            return;
        }
        final FoodStats stats = AutoEat.mc.player.getFoodStats();
        if (this.isValid(AutoEat.mc.player.getHeldItemOffhand(), stats.getFoodLevel())) {
            AutoEat.mc.player.setActiveHand(EnumHand.OFF_HAND);
            this.eating = true;
            KeyBinding.setKeyBindState(AutoEat.mc.gameSettings.keyBindUseItem.getKeyCode(), true);
            AutoEat.mc.playerController.processRightClick((EntityPlayer)AutoEat.mc.player, (World)AutoEat.mc.world, EnumHand.MAIN_HAND);
        }
        else {
            for (int i = 0; i < 9; ++i) {
                if (this.isValid(AutoEat.mc.player.inventory.getStackInSlot(i), stats.getFoodLevel())) {
                    this.lastSlot = AutoEat.mc.player.inventory.currentItem;
                    AutoEat.mc.player.inventory.currentItem = i;
                    this.eating = true;
                    KeyBinding.setKeyBindState(AutoEat.mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                    AutoEat.mc.playerController.processRightClick((EntityPlayer)AutoEat.mc.player, (World)AutoEat.mc.world, EnumHand.MAIN_HAND);
                    return;
                }
            }
        }
    }
}
