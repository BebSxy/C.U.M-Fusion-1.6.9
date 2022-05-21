
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import me.cum.fusion.event.events.*;
import me.cum.fusion.features.modules.client.*;
import java.awt.*;
import me.cum.fusion.util.*;
import me.cum.fusion.*;
import net.minecraft.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.*;

public class HoleFiller extends Module
{
    private final Setting<Double> range;
    private final Setting<Boolean> smart;
    private final Setting<Double> smartRange;
    private final Setting<Boolean> rainbow;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    private final Setting<Integer> outlineAlpha;
    private BlockPos render;
    private EntityPlayer closestTarget;
    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;
    private static HoleFiller INSTANCE;
    
    public HoleFiller() {
        super("HoleFiller", "Fills holes around you.", Category.COMBAT, true, false, true);
        this.range = (Setting<Double>)this.register(new Setting("Range", (T)4.5, (T)0.1, (T)6.0));
        this.smart = (Setting<Boolean>)this.register(new Setting("Smart", (T)false));
        this.smartRange = (Setting<Double>)this.register(new Setting("Smart Range", (T)4.0, (T)0.1, (T)6.0));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)false));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.outlineAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.setInstance();
    }
    
    public static HoleFiller getInstance() {
        if (HoleFiller.INSTANCE == null) {
            HoleFiller.INSTANCE = new HoleFiller();
        }
        return HoleFiller.INSTANCE;
    }
    
    private void setInstance() {
        HoleFiller.INSTANCE = this;
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        final Packet<? extends INetHandler> packet = (Packet<? extends INetHandler>)event.getPacket();
        if (packet instanceof CPacketPlayer && HoleFiller.isSpoofingAngles) {
            ((CPacketPlayer)packet).yaw = (float)HoleFiller.yaw;
            ((CPacketPlayer)packet).pitch = (float)HoleFiller.pitch;
        }
    }
    
    @Override
    public void onUpdate() {
        if (HoleFiller.mc.world == null) {
            return;
        }
        if (this.smart.getValue()) {
            this.findClosestTarget();
        }
        final List<BlockPos> blocks = this.findCrystalBlocks();
        BlockPos q = null;
        int obsidianSlot = (HoleFiller.mc.player.getHeldItemMainhand().getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) ? HoleFiller.mc.player.inventory.currentItem : -1;
        if (obsidianSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (HoleFiller.mc.player.inventory.getStackInSlot(l).getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) {
                    obsidianSlot = l;
                    break;
                }
            }
        }
        if (obsidianSlot == -1) {
            return;
        }
        for (final BlockPos blockPos : blocks) {
            if (!HoleFiller.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(blockPos)).isEmpty()) {
                continue;
            }
            if (this.smart.getValue() && this.isInRange(blockPos)) {
                q = blockPos;
            }
            else {
                q = blockPos;
            }
        }
        this.render = q;
        if (q != null && HoleFiller.mc.player.onGround) {
            final int oldSlot = HoleFiller.mc.player.inventory.currentItem;
            if (HoleFiller.mc.player.inventory.currentItem != obsidianSlot) {
                HoleFiller.mc.player.inventory.currentItem = obsidianSlot;
            }
            this.lookAtPacket(q.getX() + 0.5, q.getY() - 0.5, q.getZ() + 0.5, (EntityPlayer)HoleFiller.mc.player);
            BlockUtil.placeBlockScaffold(this.render);
            HoleFiller.mc.player.swingArm(EnumHand.MAIN_HAND);
            HoleFiller.mc.player.inventory.currentItem = oldSlot;
            resetRotation();
        }
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (this.render != null) {
            RenderUtil.drawBoxESP(this.render, ((boolean)this.rainbow.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.outlineAlpha.getValue()), 3.5f, true, true, this.alpha.getValue());
        }
    }
    
    private void lookAtPacket(final double px, final double py, final double pz, final EntityPlayer me) {
        final double[] v = EntityUtil.calculateLookAt(px, py, pz, me);
        setYawAndPitch((float)v[0], (float)v[1]);
    }
    
    public boolean IsHole(final BlockPos blockPos) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 0, 0);
        final BlockPos boost3 = blockPos.add(0, 0, -1);
        final BlockPos boost4 = blockPos.add(1, 0, 0);
        final BlockPos boost5 = blockPos.add(-1, 0, 0);
        final BlockPos boost6 = blockPos.add(0, 0, 1);
        final BlockPos boost7 = blockPos.add(0, 2, 0);
        final BlockPos boost8 = blockPos.add(0.5, 0.5, 0.5);
        final BlockPos boost9 = blockPos.add(0, -1, 0);
        return HoleFiller.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && HoleFiller.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && HoleFiller.mc.world.getBlockState(boost7).getBlock() == Blocks.AIR && (HoleFiller.mc.world.getBlockState(boost3).getBlock() == Blocks.OBSIDIAN || HoleFiller.mc.world.getBlockState(boost3).getBlock() == Blocks.BEDROCK) && (HoleFiller.mc.world.getBlockState(boost4).getBlock() == Blocks.OBSIDIAN || HoleFiller.mc.world.getBlockState(boost4).getBlock() == Blocks.BEDROCK) && (HoleFiller.mc.world.getBlockState(boost5).getBlock() == Blocks.OBSIDIAN || HoleFiller.mc.world.getBlockState(boost5).getBlock() == Blocks.BEDROCK) && (HoleFiller.mc.world.getBlockState(boost6).getBlock() == Blocks.OBSIDIAN || HoleFiller.mc.world.getBlockState(boost6).getBlock() == Blocks.BEDROCK) && HoleFiller.mc.world.getBlockState(boost8).getBlock() == Blocks.AIR && (HoleFiller.mc.world.getBlockState(boost9).getBlock() == Blocks.OBSIDIAN || HoleFiller.mc.world.getBlockState(boost9).getBlock() == Blocks.BEDROCK);
    }
    
    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(HoleFiller.mc.player.posX), Math.floor(HoleFiller.mc.player.posY), Math.floor(HoleFiller.mc.player.posZ));
    }
    
    public BlockPos getClosestTargetPos() {
        if (this.closestTarget != null) {
            return new BlockPos(Math.floor(this.closestTarget.posX), Math.floor(this.closestTarget.posY), Math.floor(this.closestTarget.posZ));
        }
        return null;
    }
    
    private void findClosestTarget() {
        final List<EntityPlayer> playerList = (List<EntityPlayer>)HoleFiller.mc.world.playerEntities;
        this.closestTarget = null;
        for (final EntityPlayer target : playerList) {
            if (target != HoleFiller.mc.player && !Fusion.friendManager.isFriend(target.getName()) && EntityUtil.isLiving((Entity)target)) {
                if (target.getHealth() <= 0.0f) {
                    continue;
                }
                if (this.closestTarget == null) {
                    this.closestTarget = target;
                }
                else {
                    if (HoleFiller.mc.player.getDistance((Entity)target) >= HoleFiller.mc.player.getDistance((Entity)this.closestTarget)) {
                        continue;
                    }
                    this.closestTarget = target;
                }
            }
        }
    }
    
    private boolean isInRange(final BlockPos blockPos) {
        final NonNullList<BlockPos> positions = (NonNullList<BlockPos>)NonNullList.create();
        positions.addAll((Collection)this.getSphere(getPlayerPos(), this.range.getValue().floatValue(), this.range.getValue().intValue(), false, true, 0).stream().filter((Predicate<? super Object>)this::IsHole).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        return positions.contains((Object)blockPos);
    }
    
    private List<BlockPos> findCrystalBlocks() {
        final NonNullList<BlockPos> positions = (NonNullList<BlockPos>)NonNullList.create();
        if (this.smart.getValue() && this.closestTarget != null) {
            positions.addAll((Collection)this.getSphere(this.getClosestTargetPos(), this.smartRange.getValue().floatValue(), this.range.getValue().intValue(), false, true, 0).stream().filter((Predicate<? super Object>)this::IsHole).filter((Predicate<? super Object>)this::isInRange).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        }
        else if (!this.smart.getValue()) {
            positions.addAll((Collection)this.getSphere(getPlayerPos(), this.range.getValue().floatValue(), this.range.getValue().intValue(), false, true, 0).stream().filter((Predicate<? super Object>)this::IsHole).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        }
        return (List<BlockPos>)positions;
    }
    
    public List<BlockPos> getSphere(final BlockPos loc, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.getX();
        final int cy = loc.getY();
        final int cz = loc.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                int y = sphere ? (cy - (int)r) : cy;
                while (true) {
                    final float f = (float)y;
                    final float f2 = sphere ? (cy + r) : ((float)(cy + h));
                    if (f >= f2) {
                        break;
                    }
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
            }
        }
        return circleblocks;
    }
    
    private static void setYawAndPitch(final float yaw1, final float pitch1) {
        HoleFiller.yaw = yaw1;
        HoleFiller.pitch = pitch1;
        HoleFiller.isSpoofingAngles = true;
    }
    
    private static void resetRotation() {
        if (HoleFiller.isSpoofingAngles) {
            HoleFiller.yaw = HoleFiller.mc.player.rotationYaw;
            HoleFiller.pitch = HoleFiller.mc.player.rotationPitch;
            HoleFiller.isSpoofingAngles = false;
        }
    }
    
    @Override
    public void onDisable() {
        this.closestTarget = null;
        this.render = null;
        resetRotation();
        super.onDisable();
    }
    
    static {
        HoleFiller.INSTANCE = new HoleFiller();
    }
}
