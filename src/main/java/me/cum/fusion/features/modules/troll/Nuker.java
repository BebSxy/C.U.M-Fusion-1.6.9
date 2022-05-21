
package me.cum.fusion.features.modules.troll;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.cum.fusion.event.events.*;
import me.cum.fusion.*;
import net.minecraft.util.*;
import me.cum.fusion.util.*;
import net.minecraft.init.*;
import java.util.*;
import net.minecraft.util.math.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.block.*;

public class Nuker extends Module
{
    public Setting<Boolean> rotate;
    public Setting<Float> distance;
    public Setting<Integer> blockPerTick;
    public Setting<Integer> delay;
    public Setting<Boolean> nuke;
    public Setting<Mode> mode;
    public Setting<Boolean> antiRegear;
    public Setting<Boolean> hopperNuker;
    private Setting<Boolean> autoSwitch;
    private int oldSlot;
    private boolean isMining;
    private final Timer timer;
    private Block selected;
    
    public Nuker() {
        super("Nuker", "Mines many blocks", Module.Category.TROLL, true, false, false);
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", (T)false));
        this.distance = (Setting<Float>)this.register(new Setting("Range", (T)6.0f, (T)0.1f, (T)10.0f));
        this.blockPerTick = (Setting<Integer>)this.register(new Setting("Blocks/Attack", (T)50, (T)1, (T)100));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay/Attack", (T)50, (T)1, (T)1000));
        this.nuke = (Setting<Boolean>)this.register(new Setting("Nuke", (T)false));
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", (T)Mode.NUKE, v -> this.nuke.getValue()));
        this.antiRegear = (Setting<Boolean>)this.register(new Setting("AntiRegear", (T)false));
        this.hopperNuker = (Setting<Boolean>)this.register(new Setting("HopperAura", (T)false));
        this.autoSwitch = (Setting<Boolean>)this.register(new Setting("AutoSwitch", (T)false));
        this.oldSlot = -1;
        this.isMining = false;
        this.timer = new Timer();
    }
    
    public void onToggle() {
        this.selected = null;
    }
    
    @SubscribeEvent
    public void onClickBlock(final BlockEvent event) {
        final Block block;
        if (event.getStage() == 3 && (this.mode.getValue() == Mode.SELECTION || this.mode.getValue() == Mode.NUKE) && (block = Nuker.mc.world.getBlockState(event.pos).getBlock()) != null && block != this.selected) {
            this.selected = block;
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0) {
            if (this.nuke.getValue()) {
                BlockPos pos = null;
                switch (this.mode.getValue()) {
                    case SELECTION:
                    case NUKE: {
                        pos = this.getClosestBlockSelection();
                        break;
                    }
                    case ALL: {
                        pos = this.getClosestBlockAll();
                        break;
                    }
                }
                if (pos != null) {
                    if (this.mode.getValue() == Mode.SELECTION || this.mode.getValue() == Mode.ALL) {
                        if (this.rotate.getValue()) {
                            final float[] angle = MathUtil.calcAngle(Nuker.mc.player.getPositionEyes(Nuker.mc.getRenderPartialTicks()), new Vec3d((double)(pos.getX() + 0.5f), (double)(pos.getY() + 0.5f), (double)(pos.getZ() + 0.5f)));
                            Fusion.rotationManager.setPlayerRotations(angle[0], angle[1]);
                        }
                        if (this.canBreak(pos)) {
                            Nuker.mc.playerController.onPlayerDamageBlock(pos, Nuker.mc.player.getHorizontalFacing());
                            Nuker.mc.player.swingArm(EnumHand.MAIN_HAND);
                        }
                    }
                    else {
                        for (int i = 0; i < this.blockPerTick.getValue(); ++i) {
                            pos = this.getClosestBlockSelection();
                            if (pos != null) {
                                if (this.rotate.getValue()) {
                                    final float[] angle2 = MathUtil.calcAngle(Nuker.mc.player.getPositionEyes(Nuker.mc.getRenderPartialTicks()), new Vec3d((double)(pos.getX() + 0.5f), (double)(pos.getY() + 0.5f), (double)(pos.getZ() + 0.5f)));
                                    Fusion.rotationManager.setPlayerRotations(angle2[0], angle2[1]);
                                }
                                if (this.timer.passedMs(this.delay.getValue())) {
                                    Nuker.mc.playerController.onPlayerDamageBlock(pos, Nuker.mc.player.getHorizontalFacing());
                                    Nuker.mc.player.swingArm(EnumHand.MAIN_HAND);
                                    this.timer.reset();
                                }
                            }
                        }
                    }
                }
            }
            if (this.antiRegear.getValue()) {
                this.breakBlocks(BlockUtil.shulkerList);
            }
            if (this.hopperNuker.getValue()) {
                final ArrayList<Block> blocklist = new ArrayList<Block>();
                blocklist.add((Block)Blocks.HOPPER);
                this.breakBlocks(blocklist);
            }
        }
    }
    
    public void breakBlocks(final List<Block> blocks) {
        final BlockPos pos = this.getNearestBlock(blocks);
        if (pos != null) {
            if (!this.isMining) {
                this.oldSlot = Nuker.mc.player.inventory.currentItem;
                this.isMining = true;
            }
            if (this.rotate.getValue()) {
                final float[] angle = MathUtil.calcAngle(Nuker.mc.player.getPositionEyes(Nuker.mc.getRenderPartialTicks()), new Vec3d((double)(pos.getX() + 0.5f), (double)(pos.getY() + 0.5f), (double)(pos.getZ() + 0.5f)));
                Fusion.rotationManager.setPlayerRotations(angle[0], angle[1]);
            }
            if (this.canBreak(pos)) {
                if (this.autoSwitch.getValue()) {
                    int newSlot = -1;
                    for (int i = 0; i < 9; ++i) {
                        final ItemStack stack = Nuker.mc.player.inventory.getStackInSlot(i);
                        if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemPickaxe) {
                            newSlot = i;
                            break;
                        }
                    }
                    if (newSlot != -1) {
                        Nuker.mc.player.inventory.currentItem = newSlot;
                    }
                }
                Nuker.mc.playerController.onPlayerDamageBlock(pos, Nuker.mc.player.getHorizontalFacing());
                Nuker.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }
        else if (this.autoSwitch.getValue() && this.oldSlot != -1) {
            Nuker.mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
            this.isMining = false;
        }
    }
    
    private boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = Nuker.mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)Nuker.mc.world, pos) != -1.0f;
    }
    
    private BlockPos getNearestBlock(final List<Block> blocks) {
        double maxDist = MathUtil.square(this.distance.getValue());
        BlockPos ret = null;
        for (double x = maxDist; x >= -maxDist; --x) {
            for (double y = maxDist; y >= -maxDist; --y) {
                for (double z = maxDist; z >= -maxDist; --z) {
                    final BlockPos pos = new BlockPos(Nuker.mc.player.posX + x, Nuker.mc.player.posY + y, Nuker.mc.player.posZ + z);
                    final double dist = Nuker.mc.player.getDistanceSq((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                    if (dist <= maxDist && blocks.contains(Nuker.mc.world.getBlockState(pos).getBlock())) {
                        if (this.canBreak(pos)) {
                            maxDist = dist;
                            ret = pos;
                        }
                    }
                }
            }
        }
        return ret;
    }
    
    private BlockPos getClosestBlockAll() {
        float maxDist = this.distance.getValue();
        BlockPos ret = null;
        for (float x = maxDist; x >= -maxDist; --x) {
            for (float y = maxDist; y >= -maxDist; --y) {
                for (float z = maxDist; z >= -maxDist; --z) {
                    final BlockPos pos = new BlockPos(Nuker.mc.player.posX + x, Nuker.mc.player.posY + y, Nuker.mc.player.posZ + z);
                    final double dist = Nuker.mc.player.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                    if (dist <= maxDist && Nuker.mc.world.getBlockState(pos).getBlock() != Blocks.AIR && !(Nuker.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) && this.canBreak(pos)) {
                        if (pos.getY() >= Nuker.mc.player.posY) {
                            maxDist = (float)dist;
                            ret = pos;
                        }
                    }
                }
            }
        }
        return ret;
    }
    
    private BlockPos getClosestBlockSelection() {
        float maxDist = this.distance.getValue();
        BlockPos ret = null;
        for (float x = maxDist; x >= -maxDist; --x) {
            for (float y = maxDist; y >= -maxDist; --y) {
                for (float z = maxDist; z >= -maxDist; --z) {
                    final BlockPos pos = new BlockPos(Nuker.mc.player.posX + x, Nuker.mc.player.posY + y, Nuker.mc.player.posZ + z);
                    final double dist = Nuker.mc.player.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                    if (dist <= maxDist && Nuker.mc.world.getBlockState(pos).getBlock() != Blocks.AIR && !(Nuker.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) && Nuker.mc.world.getBlockState(pos).getBlock() == this.selected && this.canBreak(pos)) {
                        if (pos.getY() >= Nuker.mc.player.posY) {
                            maxDist = (float)dist;
                            ret = pos;
                        }
                    }
                }
            }
        }
        return ret;
    }
    
    public enum Mode
    {
        SELECTION, 
        ALL, 
        NUKE;
    }
}
