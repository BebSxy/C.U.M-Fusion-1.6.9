
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import net.minecraft.util.math.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.entity.player.*;
import me.cum.fusion.*;
import java.util.stream.*;
import net.minecraft.entity.*;
import me.cum.fusion.util.*;
import java.util.*;
import me.cum.fusion.features.command.*;
import me.cum.fusion.event.events.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class AutoCity extends Module
{
    private static final BlockPos[] surroundOffset;
    public Setting<Boolean> raytrace;
    public Setting<Integer> range;
    public Setting<Boolean> rotate;
    public Setting<Boolean> autodisable;
    public Setting<Integer> rotations;
    
    public AutoCity() {
        super("CityGod", "Automatically mines ur opponent out of their hole.", Category.COMBAT, true, false, false);
        this.raytrace = (Setting<Boolean>)this.register(new Setting("Raytrace", (T)false));
        this.range = (Setting<Integer>)this.register(new Setting("Range", (T)5, (T)1, (T)6));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", (T)true));
        this.autodisable = (Setting<Boolean>)this.register(new Setting("Auto Disable", (T)true));
        this.rotations = (Setting<Integer>)this.register(new Setting("Spoofs", (T)1, (T)1, (T)20));
    }
    
    public static ArrayList<PairUtil<EntityPlayer, ArrayList<BlockPos>>> GetPlayersReadyToBeCitied() {
        final ArrayList<PairUtil<EntityPlayer, ArrayList<BlockPos>>> arrayList = new ArrayList<PairUtil<EntityPlayer, ArrayList<BlockPos>>>();
        for (final EntityPlayer entity : Objects.requireNonNull(EntityUtil.getNearbyPlayers(6.0)).stream().filter(entityPlayer -> !Fusion.friendManager.isFriend(entityPlayer)).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList())) {
            final ArrayList<BlockPos> arrayList2 = new ArrayList<BlockPos>();
            for (int i = 0; i < 4; ++i) {
                final BlockPos blockPos = EntityUtil.GetPositionVectorBlockPos((Entity)entity, AutoCity.surroundOffset[i]);
                if (AutoCity.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) {
                    boolean bl = false;
                    switch (i) {
                        case 0: {
                            bl = BlockUtil.canPlaceCrystal(blockPos.north(2), true, false);
                            break;
                        }
                        case 1: {
                            bl = BlockUtil.canPlaceCrystal(blockPos.east(2), true, false);
                            break;
                        }
                        case 2: {
                            bl = BlockUtil.canPlaceCrystal(blockPos.south(2), true, false);
                            break;
                        }
                        case 3: {
                            bl = BlockUtil.canPlaceCrystal(blockPos.west(2), true, false);
                            break;
                        }
                    }
                    if (bl) {
                        arrayList2.add(blockPos);
                    }
                }
            }
            if (arrayList2.isEmpty()) {
                continue;
            }
            arrayList.add(new PairUtil<EntityPlayer, ArrayList<BlockPos>>(entity, arrayList2));
        }
        return arrayList;
    }
    
    @Override
    public void onEnable() {
        final ArrayList<PairUtil<EntityPlayer, ArrayList<BlockPos>>> arrayList = GetPlayersReadyToBeCitied();
        if (arrayList.isEmpty()) {
            Command.sendMessage("There is no one to city!");
            this.toggle();
            return;
        }
        EntityPlayer entityPlayer = null;
        BlockPos blockPos = null;
        double d = 50.0;
        for (final PairUtil<EntityPlayer, ArrayList<BlockPos>> pairUtil : arrayList) {
            for (final BlockPos blockPos2 : pairUtil.getSecond()) {
                if (blockPos == null) {
                    entityPlayer = pairUtil.getFirst();
                    blockPos = blockPos2;
                }
                else {
                    final double d2 = blockPos2.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    if (d2 >= d) {
                        continue;
                    }
                    d = d2;
                    blockPos = blockPos2;
                    entityPlayer = pairUtil.getFirst();
                }
            }
        }
        if (blockPos == null || entityPlayer == null) {
            Command.sendMessage("Couldn't find any blocks to mine!");
            this.toggle();
            return;
        }
        BlockUtil.SetCurrentBlock(blockPos);
        Command.sendMessage("Attempting to mine a block by your target: " + entityPlayer.getName());
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent updateWalkingPlayerEvent) {
        boolean bl = AutoCity.mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE;
        if (!bl) {
            for (int i = 0; i < 9; ++i) {
                final ItemStack itemStack = AutoCity.mc.player.inventory.getStackInSlot(i);
                if (!itemStack.isEmpty() && itemStack.getItem() == Items.DIAMOND_PICKAXE) {
                    bl = true;
                    AutoCity.mc.player.inventory.currentItem = i;
                    AutoCity.mc.playerController.updateController();
                    break;
                }
            }
        }
        if (!bl) {
            Command.sendMessage("No pickaxe!");
            this.toggle();
            return;
        }
        final BlockPos blockPos = BlockUtil.GetCurrBlock();
        if (blockPos == null) {
            if (this.autodisable.getValue()) {
                Command.sendMessage("Done!");
                this.toggle();
            }
            return;
        }
        if (this.rotate.getValue()) {
            Fusion.rotationManager.updateRotations();
            Fusion.rotationManager.lookAtPos(blockPos);
            updateWalkingPlayerEvent.setCanceled(true);
        }
        BlockUtil.Update(this.range.getValue(), this.raytrace.getValue());
    }
    
    static {
        surroundOffset = new BlockPos[] { new BlockPos(0, 0, 0), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0) };
    }
}
