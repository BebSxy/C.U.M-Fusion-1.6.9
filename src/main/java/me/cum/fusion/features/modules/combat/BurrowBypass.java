
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.util.math.*;
import net.minecraftforge.registries.*;
import me.cum.fusion.util.*;
import net.minecraft.util.*;
import net.minecraft.network.play.client.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import me.cum.fusion.features.command.*;

public class BurrowBypass extends Module
{
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> onlyHole;
    private final Setting<Boolean> helpingBlocks;
    private final Setting<Boolean> chat;
    private final Setting<Boolean> packet;
    private final Setting<Integer> blocksPerTick;
    private BlockPos placePos;
    private BlockPos playerPos;
    private int blockSlot;
    private int obbySlot;
    private int lastBlock;
    private int blocksThisTick;
    
    public BurrowBypass() {
        super("BurrowBypass", "Self Anvil (on some servers bypasses burrow patches).", Category.COMBAT, true, false, false);
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", (T)true));
        this.onlyHole = (Setting<Boolean>)this.register(new Setting("HoleOnly", (T)false));
        this.helpingBlocks = (Setting<Boolean>)this.register(new Setting("HelpingBlocks", (T)true));
        this.chat = (Setting<Boolean>)this.register(new Setting("Chat Msgs", (T)true));
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", (T)false));
        this.blocksPerTick = (Setting<Integer>)this.register(new Setting("Blocks/Tick", (T)2, (T)1, (T)8));
    }
    
    @Override
    public void onEnable() {
        this.playerPos = new BlockPos(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY, BurrowBypass.mc.player.posZ);
        this.placePos = this.playerPos.offset(EnumFacing.UP, 2);
        this.blockSlot = this.findBlockSlot();
        this.obbySlot = InventoryUtil.findHotbarBlock((Class<? extends IForgeRegistryEntry.Impl>)BlockObsidian.class);
        this.lastBlock = BurrowBypass.mc.player.inventory.currentItem;
        if (!this.doFirstChecks()) {
            this.disable();
        }
    }
    
    @Override
    public void onTick() {
        this.blocksThisTick = 0;
        this.doSelfAnvil();
    }
    
    private void doSelfAnvil() {
        if (this.helpingBlocks.getValue() && BlockUtil.isPositionPlaceable(this.placePos, false, true) == 2) {
            InventoryUtil.switchToHotbarSlot(this.obbySlot, false);
            this.doHelpBlocks();
        }
        if (this.blocksThisTick < this.blocksPerTick.getValue() && BlockUtil.isPositionPlaceable(this.placePos, false, true) == 3) {
            InventoryUtil.switchToHotbarSlot(this.blockSlot, false);
            BlockUtil.placeBlock(this.placePos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false);
            InventoryUtil.switchToHotbarSlot(this.lastBlock, false);
            BurrowBypass.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BurrowBypass.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.disable();
        }
    }
    
    private void doHelpBlocks() {
        if (this.blocksThisTick >= this.blocksPerTick.getValue()) {
            return;
        }
        for (final EnumFacing side1 : EnumFacing.values()) {
            if (side1 != EnumFacing.DOWN && BlockUtil.isPositionPlaceable(this.placePos.offset(side1), false, true) == 3) {
                BlockUtil.placeBlock(this.placePos.offset(side1), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false);
                ++this.blocksThisTick;
                return;
            }
        }
        for (final EnumFacing side1 : EnumFacing.values()) {
            if (side1 != EnumFacing.DOWN) {
                for (final EnumFacing side2 : EnumFacing.values()) {
                    if (BlockUtil.isPositionPlaceable(this.placePos.offset(side1).offset(side2), false, true) == 3) {
                        BlockUtil.placeBlock(this.placePos.offset(side1).offset(side2), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false);
                        ++this.blocksThisTick;
                        return;
                    }
                }
            }
        }
        for (final EnumFacing side1 : EnumFacing.values()) {
            for (final EnumFacing side2 : EnumFacing.values()) {
                for (final EnumFacing side3 : EnumFacing.values()) {
                    if (BlockUtil.isPositionPlaceable(this.placePos.offset(side1).offset(side2).offset(side3), false, true) == 3) {
                        BlockUtil.placeBlock(this.placePos.offset(side1).offset(side2).offset(side3), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false);
                        ++this.blocksThisTick;
                        return;
                    }
                }
            }
        }
    }
    
    private int findBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack item = BurrowBypass.mc.player.inventory.getStackInSlot(i);
            if (item.getItem() instanceof ItemBlock) {
                final Block block = Block.getBlockFromItem(BurrowBypass.mc.player.inventory.getStackInSlot(i).getItem());
                if (block instanceof BlockFalling) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private boolean doFirstChecks() {
        final int canPlace = BlockUtil.isPositionPlaceable(this.placePos, false, true);
        if (fullNullCheck() || !BurrowBypass.mc.world.isAirBlock(this.playerPos)) {
            return false;
        }
        if (!BlockUtil.isBothHole(this.playerPos) && this.onlyHole.getValue()) {
            return false;
        }
        if (this.blockSlot == -1) {
            if (this.chat.getValue()) {
                Command.sendMessage("<" + this.getDisplayName() + "> \u00c2§cNo Anvils in hotbar.");
            }
            return false;
        }
        if (canPlace == 2) {
            if (!this.helpingBlocks.getValue()) {
                if (this.chat.getValue()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> \u00c2§cNowhere to place.");
                }
                return false;
            }
            if (this.obbySlot == -1) {
                if (this.chat.getValue()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> \u00c2§cNo Obsidian in hotbar.");
                }
                return false;
            }
        }
        else if (canPlace != 3) {
            if (this.chat.getValue()) {
                Command.sendMessage("<" + this.getDisplayName() + "> \u00c2§cNot enough room.");
            }
            return false;
        }
        return true;
    }
}
