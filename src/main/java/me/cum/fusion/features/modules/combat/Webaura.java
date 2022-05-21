
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import me.cum.fusion.event.events.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.util.math.*;
import me.cum.fusion.util.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraftforge.registries.*;
import me.cum.fusion.features.command.*;
import me.cum.fusion.*;
import net.minecraft.util.*;

public class Webaura extends Module
{
    private final Setting<Integer> delay;
    private final Setting<Integer> blocksPerPlace;
    private final Setting<Double> targetRange;
    private final Setting<Double> range;
    private final Setting<TargetMode> targetMode;
    private final Setting<InventoryUtil.Switch> switchMode;
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> raytrace;
    private final Setting<Double> speed;
    private final Setting<Boolean> upperBody;
    private final Setting<Boolean> lowerbody;
    private final Setting<Boolean> ylower;
    private final Setting<Boolean> antiSelf;
    private final Setting<Integer> eventMode;
    private final Setting<Boolean> freecam;
    private final Setting<Boolean> info;
    private final Setting<Boolean> disable;
    private final Setting<Boolean> packet;
    private final Timer timer;
    private boolean didPlace;
    private boolean switchedItem;
    public EntityPlayer target;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements;
    public static boolean isPlacing;
    private boolean smartRotate;
    private BlockPos startPos;
    
    public Webaura() {
        super("Webaura", "Traps other players in webs", Category.COMBAT, true, false, false);
        this.delay = (Setting<Integer>)this.register(new Setting("Delay/Place", (T)50, (T)0, (T)250));
        this.blocksPerPlace = (Setting<Integer>)this.register(new Setting("Block/Place", (T)8, (T)1, (T)30));
        this.targetRange = (Setting<Double>)this.register(new Setting("TargetRange", (T)10.0, (T)0.0, (T)20.0));
        this.range = (Setting<Double>)this.register(new Setting("PlaceRange", (T)6.0, (T)0.0, (T)10.0));
        this.targetMode = (Setting<TargetMode>)this.register(new Setting("Target", (T)TargetMode.CLOSEST));
        this.switchMode = (Setting<InventoryUtil.Switch>)this.register(new Setting("Switch", (T)InventoryUtil.Switch.NORMAL));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", (T)true));
        this.raytrace = (Setting<Boolean>)this.register(new Setting("Raytrace", (T)false));
        this.speed = (Setting<Double>)this.register(new Setting("Speed", (T)30.0, (T)0.0, (T)30.0));
        this.upperBody = (Setting<Boolean>)this.register(new Setting("Upper", (T)false));
        this.lowerbody = (Setting<Boolean>)this.register(new Setting("Lower", (T)true));
        this.ylower = (Setting<Boolean>)this.register(new Setting("Y-1", (T)false));
        this.antiSelf = (Setting<Boolean>)this.register(new Setting("AntiSelf", (T)false));
        this.eventMode = (Setting<Integer>)this.register(new Setting("Updates", (T)3, (T)1, (T)3));
        this.freecam = (Setting<Boolean>)this.register(new Setting("Freecam", (T)false));
        this.info = (Setting<Boolean>)this.register(new Setting("Info", (T)false));
        this.disable = (Setting<Boolean>)this.register(new Setting("TSelfMove", (T)false));
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", (T)false));
        this.timer = new Timer();
        this.didPlace = false;
        this.placements = 0;
        this.smartRotate = false;
        this.startPos = null;
    }
    
    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        this.startPos = EntityUtil.getRoundedBlockPos((Entity)Webaura.mc.player);
        this.lastHotbarSlot = Webaura.mc.player.inventory.currentItem;
    }
    
    @Override
    public void onTick() {
        if (this.eventMode.getValue() == 3) {
            this.smartRotate = false;
            this.doTrap();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.eventMode.getValue() == 2) {
            this.smartRotate = (this.rotate.getValue() && this.blocksPerPlace.getValue() == 1);
            this.doTrap();
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.eventMode.getValue() == 1) {
            this.smartRotate = false;
            this.doTrap();
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.info.getValue() && this.target != null) {
            return this.target.getName();
        }
        return null;
    }
    
    @Override
    public void onDisable() {
        Webaura.isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.switchItem(true);
    }
    
    private void doTrap() {
        if (this.check()) {
            return;
        }
        this.doWebTrap();
        if (this.didPlace) {
            this.timer.reset();
        }
    }
    
    private void doWebTrap() {
        final List<Vec3d> placeTargets = this.getPlacements();
        this.placeList(placeTargets);
    }
    
    private List<Vec3d> getPlacements() {
        final ArrayList<Vec3d> list = new ArrayList<Vec3d>();
        final Vec3d baseVec = this.target.getPositionVector();
        if (this.ylower.getValue()) {
            list.add(baseVec.add(0.0, -1.0, 0.0));
        }
        if (this.lowerbody.getValue()) {
            list.add(baseVec);
        }
        if (this.upperBody.getValue()) {
            list.add(baseVec.add(0.0, 1.0, 0.0));
        }
        return list;
    }
    
    private void placeList(final List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(Webaura.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), Webaura.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (final Vec3d vec3d3 : list) {
            final BlockPos position = new BlockPos(vec3d3);
            final int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValue());
            if (placeability == 3 || placeability == 1) {
                if (this.antiSelf.getValue() && MathUtil.areVec3dsAligned(Webaura.mc.player.getPositionVector(), vec3d3)) {
                    continue;
                }
                this.placeBlock(position);
            }
        }
    }
    
    private boolean check() {
        Webaura.isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        final int obbySlot = InventoryUtil.findHotbarBlock((Class<? extends IForgeRegistryEntry.Impl>)BlockWeb.class);
        if (this.isOff()) {
            return true;
        }
        if (this.disable.getValue() && !this.startPos.equals((Object)EntityUtil.getRoundedBlockPos((Entity)Webaura.mc.player))) {
            this.disable();
            return true;
        }
        if (obbySlot == -1) {
            if (this.switchMode.getValue() != InventoryUtil.Switch.NONE) {
                if (this.info.getValue()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> §cYou are out of Webs.");
                }
                this.disable();
            }
            return true;
        }
        if (Webaura.mc.player.inventory.currentItem != this.lastHotbarSlot && Webaura.mc.player.inventory.currentItem != obbySlot) {
            this.lastHotbarSlot = Webaura.mc.player.inventory.currentItem;
        }
        this.switchItem(true);
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.target = this.getTarget(this.targetRange.getValue(), this.targetMode.getValue() == TargetMode.UNTRAPPED);
        return this.target == null || (Fusion.moduleManager.isModuleEnabled("Freecam") && !this.freecam.getValue()) || !this.timer.passedMs(this.delay.getValue()) || (this.switchMode.getValue() == InventoryUtil.Switch.NONE && Webaura.mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock((Class<? extends IForgeRegistryEntry.Impl>)BlockWeb.class));
    }
    
    private EntityPlayer getTarget(final double range, final boolean trapped) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (final EntityPlayer player : Webaura.mc.world.playerEntities) {
            if (!EntityUtil.isntValid((Entity)player, range) && (!trapped || !player.isInWeb) && (!EntityUtil.getRoundedBlockPos((Entity)Webaura.mc.player).equals((Object)EntityUtil.getRoundedBlockPos((Entity)player)) || !this.antiSelf.getValue())) {
                if (Fusion.speedManager.getPlayerSpeed(player) > this.speed.getValue()) {
                    continue;
                }
                if (target == null) {
                    target = player;
                    distance = Webaura.mc.player.getDistanceSq((Entity)player);
                }
                else {
                    if (Webaura.mc.player.getDistanceSq((Entity)player) >= distance) {
                        continue;
                    }
                    target = player;
                    distance = Webaura.mc.player.getDistanceSq((Entity)player);
                }
            }
        }
        return target;
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerPlace.getValue() && Webaura.mc.player.getDistanceSq(pos) <= MathUtil.square(this.range.getValue()) && this.switchItem(false)) {
            Webaura.isPlacing = true;
            this.isSneaking = (this.smartRotate ? BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, this.packet.getValue(), this.isSneaking) : BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking));
            this.didPlace = true;
            ++this.placements;
        }
    }
    
    private boolean switchItem(final boolean back) {
        final boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, this.switchMode.getValue(), (Class<? extends IForgeRegistryEntry.Impl>)BlockWeb.class);
        this.switchedItem = value[0];
        return value[1];
    }
    
    static {
        Webaura.isPlacing = false;
    }
    
    public enum TargetMode
    {
        CLOSEST, 
        UNTRAPPED;
    }
}
