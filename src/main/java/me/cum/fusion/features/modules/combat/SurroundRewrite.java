
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import me.cum.fusion.*;
import net.minecraft.init.*;
import me.cum.fusion.event.events.*;
import net.minecraftforge.fml.common.eventhandler.*;
import java.util.*;
import me.cum.fusion.util.*;
import me.cum.fusion.features.modules.player.*;
import net.minecraftforge.registries.*;
import net.minecraft.block.*;
import me.cum.fusion.features.command.*;
import net.minecraft.util.*;

public class SurroundRewrite extends Module
{
    private final Setting<Integer> delay;
    private final Setting<Integer> blocksPerTick;
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> raytrace;
    private final Setting<InventoryUtil.Switch> switchMode;
    private final Setting<Boolean> center;
    private final Setting<Boolean> helpingBlocks;
    private final Setting<Boolean> intelligent;
    private final Setting<Boolean> antiPedo;
    private final Setting<Integer> extender;
    private final Setting<Boolean> extendMove;
    private final Setting<MovementMode> movementMode;
    private final Setting<Double> speed;
    private final Setting<Integer> eventMode;
    private final Setting<Boolean> floor;
    private final Setting<Boolean> echests;
    private final Setting<Boolean> noGhost;
    private final Setting<Boolean> info;
    private final Setting<Integer> retryer;
    private final Timer timer;
    private final Timer retryTimer;
    private int isSafe;
    private BlockPos startPos;
    private boolean didPlace;
    private boolean switchedItem;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int placements;
    private final Set<Vec3d> extendingBlocks;
    private int extenders;
    public static boolean isPlacing;
    private int obbySlot;
    private boolean offHand;
    private final Map<BlockPos, Integer> retries;
    
    public SurroundRewrite() {
        super("S-Rewrite", "Surrounds you with Obsidian", Category.COMBAT, true, false, false);
        this.delay = (Setting<Integer>)this.register(new Setting("Delay/Place", (T)50, (T)0, (T)250));
        this.blocksPerTick = (Setting<Integer>)this.register(new Setting("Block/Place", (T)8, (T)1, (T)20));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", (T)true));
        this.raytrace = (Setting<Boolean>)this.register(new Setting("Raytrace", (T)false));
        this.switchMode = (Setting<InventoryUtil.Switch>)this.register(new Setting("Switch", (T)InventoryUtil.Switch.NORMAL));
        this.center = (Setting<Boolean>)this.register(new Setting("Center", (T)false));
        this.helpingBlocks = (Setting<Boolean>)this.register(new Setting("HelpingBlocks", (T)true));
        this.intelligent = (Setting<Boolean>)this.register(new Setting("Intelligent", (T)false, v -> this.helpingBlocks.getValue()));
        this.antiPedo = (Setting<Boolean>)this.register(new Setting("NoStupid", (T)false));
        this.extender = (Setting<Integer>)this.register(new Setting("Extend", (T)1, (T)1, (T)4));
        this.extendMove = (Setting<Boolean>)this.register(new Setting("MoveExtend", (T)false, v -> this.extender.getValue() > 1));
        this.movementMode = (Setting<MovementMode>)this.register(new Setting("Movement", (T)MovementMode.STATIC));
        this.speed = (Setting<Double>)this.register(new Setting("Speed", (T)10.0, (T)0.0, (T)30.0, v -> this.movementMode.getValue() == MovementMode.LIMIT || this.movementMode.getValue() == MovementMode.OFF, "Maximum Movement Speed"));
        this.eventMode = (Setting<Integer>)this.register(new Setting("Updates", (T)3, (T)1, (T)3));
        this.floor = (Setting<Boolean>)this.register(new Setting("Floor", (T)false));
        this.echests = (Setting<Boolean>)this.register(new Setting("Echests", (T)false));
        this.noGhost = (Setting<Boolean>)this.register(new Setting("Packet", (T)false));
        this.info = (Setting<Boolean>)this.register(new Setting("Info", (T)false));
        this.retryer = (Setting<Integer>)this.register(new Setting("Retries", (T)4, (T)1, (T)15));
        this.timer = new Timer();
        this.retryTimer = new Timer();
        this.didPlace = false;
        this.placements = 0;
        this.extendingBlocks = new HashSet<Vec3d>();
        this.extenders = 1;
        this.obbySlot = -1;
        this.offHand = false;
        this.retries = new HashMap<BlockPos, Integer>();
    }
    
    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
        }
        this.lastHotbarSlot = SurroundRewrite.mc.player.inventory.currentItem;
        this.startPos = EntityUtil.getRoundedBlockPos((Entity)SurroundRewrite.mc.player);
        if (this.center.getValue() && !Fusion.moduleManager.isModuleEnabled("Freecam")) {
            if (SurroundRewrite.mc.world.getBlockState(new BlockPos(SurroundRewrite.mc.player.getPositionVector())).getBlock() == Blocks.WEB) {
                Fusion.positionManager.setPositionPacket(SurroundRewrite.mc.player.posX, this.startPos.getY(), SurroundRewrite.mc.player.posZ, true, true, true);
            }
            else {
                Fusion.positionManager.setPositionPacket(this.startPos.getX() + 0.5, this.startPos.getY(), this.startPos.getZ() + 0.5, true, true, true);
            }
        }
        this.retries.clear();
        this.retryTimer.reset();
    }
    
    @Override
    public void onTick() {
        if (this.eventMode.getValue() == 3) {
            this.doFeetPlace();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.eventMode.getValue() == 2) {
            this.doFeetPlace();
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.eventMode.getValue() == 1) {
            this.doFeetPlace();
        }
    }
    
    @Override
    public void onDisable() {
        if (nullCheck()) {
            return;
        }
        SurroundRewrite.isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.switchItem(true);
    }
    
    @Override
    public String getDisplayInfo() {
        if (!this.info.getValue()) {
            return null;
        }
        switch (this.isSafe) {
            case 0: {
                return "\u00c2§cUnsafe";
            }
            case 1: {
                return "\u00c2§eSecure";
            }
            default: {
                return "\u00c2§aSecure";
            }
        }
    }
    
    private void doFeetPlace() {
        if (this.check()) {
            return;
        }
        if (!EntityUtil.isSafe((Entity)SurroundRewrite.mc.player, 0, this.floor.getValue())) {
            this.isSafe = 0;
            this.placeBlocks(SurroundRewrite.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray((Entity)SurroundRewrite.mc.player, 0, this.floor.getValue()), this.helpingBlocks.getValue(), false, false);
        }
        else if (!EntityUtil.isSafe((Entity)SurroundRewrite.mc.player, -1, false)) {
            this.isSafe = 1;
            if (this.antiPedo.getValue()) {
                this.placeBlocks(SurroundRewrite.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray((Entity)SurroundRewrite.mc.player, -1, false), false, false, true);
            }
        }
        else {
            this.isSafe = 2;
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
    }
    
    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < this.extender.getValue()) {
            final Vec3d[] array = new Vec3d[2];
            int i = 0;
            for (final Vec3d vec3d : this.extendingBlocks) {
                array[i] = vec3d;
                ++i;
            }
            final int placementsBefore = this.placements;
            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0, this.floor.getValue()), this.helpingBlocks.getValue(), false, true);
            }
            if (placementsBefore < this.placements) {
                this.extendingBlocks.clear();
            }
        }
        else if (this.extendingBlocks.size() > 2 || this.extenders >= this.extender.getValue()) {
            this.extendingBlocks.clear();
        }
    }
    
    private Vec3d areClose(final Vec3d[] vec3ds) {
        int matches = 0;
        for (final Vec3d vec3d : vec3ds) {
            for (final Vec3d pos : EntityUtil.getUnsafeBlockArray((Entity)SurroundRewrite.mc.player, 0, this.floor.getValue())) {
                if (vec3d.equals((Object)pos)) {
                    ++matches;
                }
            }
        }
        if (matches == 2) {
            return SurroundRewrite.mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        }
        return null;
    }
    
    private boolean placeBlocks(final Vec3d pos, final Vec3d[] vec3ds, final boolean hasHelpingBlocks, final boolean isHelping, final boolean isExtending) {
        int helpings = 0;
        boolean gotHelp = true;
        for (final Vec3d vec3d : vec3ds) {
            gotHelp = true;
            ++helpings;
            if (isHelping && !this.intelligent.getValue() && helpings > 1) {
                return false;
            }
            final BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            switch (BlockUtil.isPositionPlaceable(position, this.raytrace.getValue())) {
                case 1: {
                    if ((this.switchMode.getValue() == InventoryUtil.Switch.SILENT || (BlockTweaks.getINSTANCE().isOn() && BlockTweaks.getINSTANCE().noBlock.getValue())) && (this.retries.get(position) == null || this.retries.get(position) < this.retryer.getValue())) {
                        this.placeBlock(position);
                        this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                        this.retryTimer.reset();
                        break;
                    }
                    if ((this.extendMove.getValue() || Fusion.speedManager.getSpeedKpH() == 0.0) && !isExtending && this.extenders < this.extender.getValue()) {
                        this.placeBlocks(SurroundRewrite.mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d(SurroundRewrite.mc.player.getPositionVector().add(vec3d), 0, this.floor.getValue()), hasHelpingBlocks, false, true);
                        this.extendingBlocks.add(vec3d);
                        ++this.extenders;
                        break;
                    }
                    break;
                }
                case 2: {
                    if (hasHelpingBlocks) {
                        gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                        break;
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
        return false;
    }
    
    private boolean check() {
        if (nullCheck()) {
            return true;
        }
        this.offHand = InventoryUtil.isBlock(SurroundRewrite.mc.player.getHeldItemOffhand().getItem(), (Class<? extends Block>)BlockObsidian.class);
        SurroundRewrite.isPlacing = false;
        this.didPlace = false;
        this.extenders = 1;
        this.placements = 0;
        this.obbySlot = InventoryUtil.findHotbarBlock((Class<? extends IForgeRegistryEntry.Impl>)BlockObsidian.class);
        final int echestSlot = InventoryUtil.findHotbarBlock((Class<? extends IForgeRegistryEntry.Impl>)BlockEnderChest.class);
        if (this.isOff()) {
            return true;
        }
        if (this.retryTimer.passedMs(2500L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        this.switchItem(true);
        if (this.obbySlot == -1 && !this.offHand && (!this.echests.getValue() || echestSlot == -1)) {
            if (this.info.getValue()) {
                Command.sendMessage("<" + this.getDisplayName() + "> \u00c2§cYou are out of Obsidian.");
            }
            this.disable();
            return true;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        if (SurroundRewrite.mc.player.inventory.currentItem != this.lastHotbarSlot && SurroundRewrite.mc.player.inventory.currentItem != this.obbySlot && SurroundRewrite.mc.player.inventory.currentItem != echestSlot) {
            this.lastHotbarSlot = SurroundRewrite.mc.player.inventory.currentItem;
        }
        switch (this.movementMode.getValue()) {
            case STATIC: {
                if (!this.startPos.equals((Object)EntityUtil.getRoundedBlockPos((Entity)SurroundRewrite.mc.player))) {
                    this.disable();
                    return true;
                }
            }
            case LIMIT: {
                if (Fusion.speedManager.getSpeedKpH() > this.speed.getValue()) {
                    return true;
                }
                break;
            }
            case OFF: {
                if (Fusion.speedManager.getSpeedKpH() > this.speed.getValue()) {
                    this.disable();
                    return true;
                }
                break;
            }
        }
        return Fusion.moduleManager.isModuleEnabled("Freecam") || !this.timer.passedMs(this.delay.getValue()) || (this.switchMode.getValue() == InventoryUtil.Switch.NONE && SurroundRewrite.mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock((Class<? extends IForgeRegistryEntry.Impl>)BlockObsidian.class));
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerTick.getValue() && this.switchItem(false)) {
            SurroundRewrite.isPlacing = true;
            this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.noGhost.getValue(), this.isSneaking);
            this.didPlace = true;
            ++this.placements;
        }
    }
    
    private boolean switchItem(final boolean back) {
        if (this.offHand) {
            return true;
        }
        final boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, this.switchMode.getValue(), (Class<? extends IForgeRegistryEntry.Impl>)((this.obbySlot == -1) ? BlockEnderChest.class : BlockObsidian.class));
        this.switchedItem = value[0];
        return value[1];
    }
    
    static {
        SurroundRewrite.isPlacing = false;
    }
    
    public enum MovementMode
    {
        NONE, 
        STATIC, 
        LIMIT, 
        OFF;
    }
}
