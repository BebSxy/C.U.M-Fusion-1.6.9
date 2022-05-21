
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.command.*;
import net.minecraft.network.play.client.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.entity.item.*;
import java.util.*;
import me.cum.fusion.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

public class AutoPortal extends Module
{
    public Setting<Boolean> rotate;
    private final Setting<Integer> tick_for_place;
    Vec3d[] targets;
    int new_slot;
    int old_slot;
    int y_level;
    int tick_runs;
    int blocks_placed;
    int offset_step;
    boolean sneak;
    
    public AutoPortal() {
        super("AutoPortal", "Auto nether portal.", Category.MISC, true, false, false);
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", (T)true));
        this.tick_for_place = (Setting<Integer>)this.register(new Setting("BPT", (T)2, (T)1, (T)8));
        this.targets = new Vec3d[] { new Vec3d(1.0, 1.0, 0.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(1.0, 1.0, 2.0), new Vec3d(1.0, 1.0, 3.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(1.0, 4.0, 0.0), new Vec3d(1.0, 5.0, 0.0), new Vec3d(1.0, 5.0, 1.0), new Vec3d(1.0, 5.0, 2.0), new Vec3d(1.0, 5.0, 3.0), new Vec3d(1.0, 4.0, 3.0), new Vec3d(1.0, 3.0, 3.0), new Vec3d(1.0, 2.0, 3.0) };
        this.new_slot = 0;
        this.old_slot = 0;
        this.y_level = 0;
        this.tick_runs = 0;
        this.blocks_placed = 0;
        this.offset_step = 0;
        this.sneak = false;
    }
    
    @Override
    public void onEnable() {
        if (AutoPortal.mc.player != null) {
            this.old_slot = AutoPortal.mc.player.inventory.currentItem;
            this.new_slot = this.find_in_hotbar();
            if (this.new_slot == -1) {
                Command.sendMessage(ChatFormatting.RED + "Cannot find obi in hotbar!");
                this.toggle();
            }
            this.y_level = (int)Math.round(AutoPortal.mc.player.posY);
        }
    }
    
    @Override
    public void onDisable() {
        if (AutoPortal.mc.player != null) {
            if (this.new_slot != this.old_slot && this.old_slot != -1) {
                AutoPortal.mc.player.inventory.currentItem = this.old_slot;
            }
            if (this.sneak) {
                AutoPortal.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoPortal.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.sneak = false;
            }
            this.old_slot = -1;
            this.new_slot = -1;
        }
    }
    
    @Override
    public void onUpdate() {
        if (AutoPortal.mc.player != null) {
            this.blocks_placed = 0;
            while (this.blocks_placed < this.tick_for_place.getValue()) {
                if (this.offset_step >= this.targets.length) {
                    this.offset_step = 0;
                    break;
                }
                final BlockPos offsetPos = new BlockPos(this.targets[this.offset_step]);
                final BlockPos targetPos = new BlockPos(AutoPortal.mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ()).down();
                boolean try_to_place = true;
                if (!AutoPortal.mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                    try_to_place = false;
                }
                for (final Entity entity : AutoPortal.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(targetPos))) {
                    if (!(entity instanceof EntityItem)) {
                        if (entity instanceof EntityXPOrb) {
                            continue;
                        }
                        try_to_place = false;
                        break;
                    }
                }
                if (try_to_place && this.place_blocks(targetPos)) {
                    ++this.blocks_placed;
                }
                ++this.offset_step;
            }
            if (this.blocks_placed > 0 && this.new_slot != this.old_slot) {
                AutoPortal.mc.player.inventory.currentItem = this.old_slot;
            }
            ++this.tick_runs;
        }
    }
    
    private boolean place_blocks(final BlockPos pos) {
        if (!AutoPortal.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        }
        if (!BlockInteractHelper.checkForNeighbours(pos)) {
            return false;
        }
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (BlockInteractHelper.canBeClicked(neighbor)) {
                AutoPortal.mc.player.inventory.currentItem = this.new_slot;
                final Block neighborPos;
                if (BlockInteractHelper.blackList.contains(neighborPos = AutoPortal.mc.world.getBlockState(neighbor).getBlock())) {
                    AutoPortal.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoPortal.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    this.sneak = true;
                }
                final Vec3d hitVec = new Vec3d((Vec3i)neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (this.rotate.getValue()) {
                    BlockInteractHelper.faceVectorPacketInstant(hitVec);
                }
                AutoPortal.mc.playerController.processRightClickBlock(AutoPortal.mc.player, AutoPortal.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                AutoPortal.mc.player.swingArm(EnumHand.MAIN_HAND);
                return true;
            }
        }
        return false;
    }
    
    private int find_in_hotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AutoPortal.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock)stack.getItem()).getBlock();
                if (block instanceof BlockObsidian) {
                    return i;
                }
            }
        }
        return -1;
    }
}
