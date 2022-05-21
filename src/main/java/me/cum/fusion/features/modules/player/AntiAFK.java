
package me.cum.fusion.features.modules.player;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import java.util.*;
import net.minecraft.network.*;
import net.minecraft.inventory.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

public class AntiAFK extends Module
{
    private final Random random;
    private final Setting<Boolean> swing;
    private final Setting<Boolean> turn;
    private final Setting<Boolean> jump;
    private final Setting<Boolean> sneak;
    private final Setting<Boolean> interact;
    private final Setting<Boolean> tabcomplete;
    private final Setting<Boolean> msgs;
    private final Setting<Boolean> window;
    private final Setting<Boolean> swap;
    private final Setting<Boolean> dig;
    private final Setting<Boolean> move;
    
    public AntiAFK() {
        super("AntiAFK", "Stop servers attempting to kick u for being AFK.", Module.Category.PLAYER, true, false, false);
        this.swing = (Setting<Boolean>)this.register(new Setting("Swing", (T)true));
        this.turn = (Setting<Boolean>)this.register(new Setting("Turn", (T)true));
        this.jump = (Setting<Boolean>)this.register(new Setting("Jump", (T)true));
        this.sneak = (Setting<Boolean>)this.register(new Setting("Sneak", (T)true));
        this.interact = (Setting<Boolean>)this.register(new Setting("InteractBlock", (T)false));
        this.tabcomplete = (Setting<Boolean>)this.register(new Setting("TabComplete", (T)true));
        this.msgs = (Setting<Boolean>)this.register(new Setting("ChatMsgs", (T)true));
        this.window = (Setting<Boolean>)this.register(new Setting("WindowClick", (T)true));
        this.swap = (Setting<Boolean>)this.register(new Setting("ItemSwap", (T)true));
        this.dig = (Setting<Boolean>)this.register(new Setting("HitBlock", (T)true));
        this.move = (Setting<Boolean>)this.register(new Setting("Move", (T)true));
        this.random = new Random();
    }
    
    public void onUpdate() {
        if (AntiAFK.mc.player.ticksExisted % 45 == 0 && this.swing.getValue()) {
            AntiAFK.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (AntiAFK.mc.player.ticksExisted % 20 == 0 && this.turn.getValue()) {
            AntiAFK.mc.player.rotationYaw = (float)(this.random.nextInt(360) - 180);
        }
        if (AntiAFK.mc.player.ticksExisted % 60 == 0 && this.jump.getValue() && AntiAFK.mc.player.onGround) {
            AntiAFK.mc.player.jump();
        }
        if (AntiAFK.mc.player.ticksExisted % 50 == 0 && this.sneak.getValue() && !AntiAFK.mc.player.isSneaking()) {
            AntiAFK.mc.player.setSneaking(true);
            AntiAFK.mc.player.setSneaking(false);
        }
        if (AntiAFK.mc.player.ticksExisted % 30 == 0 && this.interact.getValue()) {
            final BlockPos blockPos = AntiAFK.mc.objectMouseOver.getBlockPos();
            if (!AntiAFK.mc.world.isAirBlock(blockPos)) {
                AntiAFK.mc.playerController.clickBlock(blockPos, AntiAFK.mc.objectMouseOver.sideHit);
            }
        }
        if (AntiAFK.mc.player.ticksExisted % 80 == 0 && this.tabcomplete.getValue() && !AntiAFK.mc.player.isDead) {
            AntiAFK.mc.player.connection.sendPacket((Packet)new CPacketTabComplete("/" + UUID.randomUUID().toString().replace('-', 'v'), AntiAFK.mc.player.getPosition(), false));
        }
        if (AntiAFK.mc.player.ticksExisted % 200 == 0 && this.msgs.getValue() && !AntiAFK.mc.player.isDead) {
            AntiAFK.mc.player.sendChatMessage("Compaass Symbol owns all: https://discord.gg/w9aBGtZHDb " + this.random.nextInt());
        }
        if (AntiAFK.mc.player.ticksExisted % 125 == 0 && this.window.getValue() && !AntiAFK.mc.player.isDead) {
            AntiAFK.mc.player.connection.sendPacket((Packet)new CPacketClickWindow(1, 1, 1, ClickType.CLONE, new ItemStack(Blocks.OBSIDIAN), (short)1));
        }
        if (AntiAFK.mc.player.ticksExisted % 70 == 0 && this.swap.getValue() && !AntiAFK.mc.player.isDead) {
            AntiAFK.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, AntiAFK.mc.player.getPosition(), EnumFacing.DOWN));
        }
        if (AntiAFK.mc.player.ticksExisted % 50 == 0 && this.dig.getValue()) {
            AntiAFK.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, AntiAFK.mc.player.getPosition(), EnumFacing.DOWN));
        }
        if (AntiAFK.mc.player.ticksExisted % 150 == 0 && this.move.getValue()) {
            AntiAFK.mc.gameSettings.keyBindForward.pressed = (AntiAFK.mc.player.ticksExisted % 10 == 0);
            AntiAFK.mc.gameSettings.keyBindBack.pressed = (AntiAFK.mc.player.ticksExisted % 15 == 0);
            AntiAFK.mc.gameSettings.keyBindLeft.pressed = (AntiAFK.mc.player.ticksExisted % 20 == 0);
            AntiAFK.mc.gameSettings.keyBindRight.pressed = (AntiAFK.mc.player.ticksExisted % 25 == 0);
        }
    }
}
