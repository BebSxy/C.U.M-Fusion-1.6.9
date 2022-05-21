
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraft.init.*;
import net.minecraft.entity.*;
import net.minecraftforge.fml.common.eventhandler.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import me.cum.fusion.util.*;
import net.minecraft.util.*;

public class Surround extends Module
{
    private final Setting<Integer> delay;
    private final Setting<Integer> blocksPerTick;
    private final Setting<Boolean> helpingBlocks;
    private final Setting<Boolean> intelligent;
    private final Setting<Boolean> antiPedo;
    private final Setting<Boolean> floor;
    private final Setting<Integer> retryer;
    private final Setting<Integer> retryDelay;
    private final Map<BlockPos, Integer> retries;
    private final Timer timer;
    private final Timer retryTimer;
    private boolean didPlace;
    private int placements;
    private int obbySlot;
    double posY;
    
    public Surround() {
        super("Surround", "Surrounds you with obsidian", Category.COMBAT, true, false, false);
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", (T)50, (T)0, (T)250));
        this.blocksPerTick = (Setting<Integer>)this.register(new Setting("BPT", (T)8, (T)1, (T)20));
        this.helpingBlocks = (Setting<Boolean>)this.register(new Setting("HelpingBlocks", (T)true));
        this.intelligent = (Setting<Boolean>)this.register(new Setting("Intelligent", (T)false));
        this.antiPedo = (Setting<Boolean>)this.register(new Setting("Always Help", (T)false));
        this.floor = (Setting<Boolean>)this.register(new Setting("Floor", (T)false));
        this.retryer = (Setting<Integer>)this.register(new Setting("Retries", (T)4, (T)1, (T)15));
        this.retryDelay = (Setting<Integer>)this.register(new Setting("Retry Delay", (T)200, (T)1, (T)2500));
        this.retries = new HashMap<BlockPos, Integer>();
        this.timer = new Timer();
        this.retryTimer = new Timer();
        this.didPlace = false;
        this.placements = 0;
        this.obbySlot = -1;
    }
    
    @Override
    public void onEnable() {
        if (Surround.mc.player == null || Surround.mc.world == null) {
            this.setEnabled(false);
            return;
        }
        this.retries.clear();
        this.retryTimer.reset();
        this.posY = Surround.mc.player.posY;
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (this.check()) {
            return;
        }
        if (this.posY < Surround.mc.player.posY) {
            this.setEnabled(false);
            return;
        }
        boolean onEChest = Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST;
        if (Surround.mc.player.posY - (int)Surround.mc.player.posY < 0.7) {
            onEChest = false;
        }
        if (!isSafe((Entity)Surround.mc.player, onEChest ? 1 : 0, this.floor.getValue())) {
            this.placeBlocks(Surround.mc.player.getPositionVector(), getUnsafeBlockArray((Entity)Surround.mc.player, (int)(onEChest ? 1 : 0), this.floor.getValue()), this.helpingBlocks.getValue(), false);
        }
        else if (!isSafe((Entity)Surround.mc.player, onEChest ? 0 : -1, false) && this.antiPedo.getValue()) {
            this.placeBlocks(Surround.mc.player.getPositionVector(), getUnsafeBlockArray((Entity)Surround.mc.player, onEChest ? 0 : -1, false), false, false);
        }
        if (this.didPlace) {
            this.timer.reset();
        }
    }
    
    public static Vec3d[] getUnsafeBlockArray(final Entity entity, final int height, final boolean floor) {
        final List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
        final Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }
    
    public static boolean isSafe(final Entity entity, final int height, final boolean floor) {
        return getUnsafeBlocks(entity, height, floor).size() == 0;
    }
    
    public static List<Vec3d> getUnsafeBlocks(final Entity entity, final int height, final boolean floor) {
        return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }
    
    public static List<Vec3d> getUnsafeBlocksFromVec3d(final Vec3d pos, final int height, final boolean floor) {
        final List<Vec3d> vec3ds = new ArrayList<Vec3d>(floor ? 5 : 4);
        for (final Vec3d vector : getOffsets(height, floor)) {
            final Block block = Surround.mc.world.getBlockState(new BlockPos(pos).add(vector.x, vector.y, vector.z)).getBlock();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }
        return vec3ds;
    }
    
    public static Vec3d[] getOffsets(final int y, final boolean floor) {
        final List<Vec3d> offsets = getOffsetList(y, floor);
        final Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
    
    public static List<Vec3d> getOffsetList(final int y, final boolean floor) {
        final List<Vec3d> offsets = new ArrayList<Vec3d>(floor ? 5 : 4);
        offsets.add(new Vec3d(-1.0, (double)y, 0.0));
        offsets.add(new Vec3d(1.0, (double)y, 0.0));
        offsets.add(new Vec3d(0.0, (double)y, -1.0));
        offsets.add(new Vec3d(0.0, (double)y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, (double)(y - 1), 0.0));
        }
        return offsets;
    }
    
    private boolean placeBlocks(final Vec3d pos, final Vec3d[] vec3ds, final boolean hasHelpingBlocks, final boolean isHelping) {
        int helpings = 0;
        if (this.obbySlot == -1) {
            return false;
        }
        if (Surround.mc.player == null) {
            return false;
        }
        boolean switched = false;
        final int lastSlot = Surround.mc.player.inventory.currentItem;
        for (final Vec3d vec3d : vec3ds) {
            if (!switched) {
                if (Surround.mc.player.inventory.currentItem != this.obbySlot) {
                    InventoryUtil.switchToHotbarSlot(this.obbySlot, false);
                }
                switched = true;
            }
            boolean gotHelp = true;
            ++helpings;
            if (isHelping && !this.intelligent.getValue() && helpings > 1) {
                return false;
            }
            final BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            Label_0330: {
                switch (BlockUtil.isPositionPlaceable(position, true)) {
                    case 1: {
                        if (this.retries.get(position) == null || this.retries.get(position) < this.retryer.getValue()) {
                            this.placeBlock(position);
                            this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                            this.retryTimer.reset();
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if (hasHelpingBlocks) {
                            gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true);
                            break Label_0330;
                        }
                        break;
                    }
                    case 3: {
                        if (gotHelp) {
                            this.placeBlock(position);
                        }
                        if (isHelping) {
                            return true;
                        }
                        break;
                    }
                }
            }
        }
        if (switched && Surround.mc.player.inventory.currentItem != lastSlot) {
            InventoryUtil.switchToHotbarSlot(lastSlot, false);
        }
        return false;
    }
    
    private boolean check() {
        if (Surround.mc.player == null || Surround.mc.world == null) {
            return true;
        }
        this.didPlace = false;
        this.placements = 0;
        this.obbySlot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        if (this.retryTimer.passed(this.retryDelay.getValue())) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (this.obbySlot == -1) {
            this.obbySlot = InventoryUtil.getBlockInHotbar(Blocks.ENDER_CHEST);
            if (this.obbySlot == -1) {
                this.setEnabled(false);
                return true;
            }
        }
        return !this.timer.passed(this.delay.getValue());
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerTick.getValue()) {
            placeBlock2(pos);
            this.didPlace = true;
            ++this.placements;
        }
    }
    
    public static void placeBlock2(final BlockPos pos) {
        final Vec3d eyesPos = new Vec3d(Surround.mc.player.posX, Surround.mc.player.posY + Surround.mc.player.getEyeHeight(), Surround.mc.player.posZ);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (BlockUtil.canBeClicked(neighbor)) {
                final Vec3d hitVec = new Vec3d((Vec3i)neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= 18.0625) {
                    HoleFillUtil.faceVectorPacketInstant(hitVec);
                    Surround.mc.playerController.processRightClickBlock(Surround.mc.player, Surround.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    Surround.mc.player.swingArm(EnumHand.MAIN_HAND);
                    Surround.mc.rightClickDelayTimer = 4;
                    return;
                }
            }
        }
    }
}
