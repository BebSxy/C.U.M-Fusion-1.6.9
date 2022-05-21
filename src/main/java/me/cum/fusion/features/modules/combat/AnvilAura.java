
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.client.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraftforge.registries.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import me.cum.fusion.util.*;
import java.util.*;
import net.minecraft.util.math.*;

public class AnvilAura extends Module
{
    public Setting<Float> range;
    public Setting<Boolean> rotate;
    public Setting<Boolean> packet;
    public Setting<Boolean> switcher;
    public Setting<Integer> rotations;
    private float yaw;
    private float pitch;
    private boolean rotating;
    private int rotationPacketsSpoofed;
    private BlockPos placeTarget;
    
    public AnvilAura() {
        super("AnvilAura", "WIP.", Category.COMBAT, true, false, false);
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)6.0f, (T)0.0f, (T)10.0f));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", (T)true));
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", (T)true));
        this.switcher = (Setting<Boolean>)this.register(new Setting("Switch", (T)true));
        this.rotations = (Setting<Integer>)this.register(new Setting("Spoofs", (T)1, (T)1, (T)20));
    }
    
    @Override
    public void onTick() {
        this.doAnvilAura();
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getStage() == 0 && this.rotate.getValue() && this.rotating) {
            if (event.getPacket() instanceof CPacketPlayer) {
                final CPacketPlayer packet = (CPacketPlayer)event.getPacket();
                packet.yaw = this.yaw;
                packet.pitch = this.pitch;
            }
            ++this.rotationPacketsSpoofed;
            if (this.rotationPacketsSpoofed >= this.rotations.getValue()) {
                this.rotating = false;
                this.rotationPacketsSpoofed = 0;
            }
        }
    }
    
    public void doAnvilAura() {
        final EntityPlayer finalTarget = this.getTarget();
        if (finalTarget != null) {
            this.placeTarget = this.getTargetPos((Entity)finalTarget);
        }
        if (this.placeTarget != null && finalTarget != null) {
            this.placeAnvil(this.placeTarget);
        }
    }
    
    public void placeAnvil(final BlockPos pos) {
        if (this.rotate.getValue()) {
            this.rotateToPos(pos);
        }
        if (this.switcher.getValue() && !this.isHoldingAnvil()) {
            this.doSwitch();
        }
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, this.packet.getValue(), AnvilAura.mc.player.isSneaking());
    }
    
    public boolean isHoldingAnvil() {
        InventoryUtil.findHotbarBlock((Class<? extends IForgeRegistryEntry.Impl>)BlockObsidian.class);
        return (AnvilAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && ((ItemBlock)AnvilAura.mc.player.getHeldItemMainhand().getItem()).getBlock() instanceof BlockAnvil) || (AnvilAura.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock)AnvilAura.mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockAnvil);
    }
    
    public void doSwitch() {
        int obbySlot = InventoryUtil.findHotbarBlock((Class<? extends IForgeRegistryEntry.Impl>)BlockObsidian.class);
        if (obbySlot == -1) {
            for (int l = 0; l < 9; ++l) {
                final ItemStack stack = AnvilAura.mc.player.inventory.getStackInSlot(l);
                final Block block = ((ItemBlock)stack.getItem()).getBlock();
                if (block instanceof BlockObsidian) {
                    obbySlot = l;
                }
            }
        }
        if (obbySlot != -1) {
            AnvilAura.mc.player.inventory.currentItem = obbySlot;
        }
    }
    
    public EntityPlayer getTarget() {
        double shortestDistance = -1.0;
        EntityPlayer target = null;
        for (final EntityPlayer player : AnvilAura.mc.world.playerEntities) {
            if (!this.getPlaceableBlocksAboveEntity((Entity)player).isEmpty()) {
                if (shortestDistance != -1.0 && AnvilAura.mc.player.getDistanceSq((Entity)player) >= MathUtil.square(shortestDistance)) {
                    continue;
                }
                shortestDistance = AnvilAura.mc.player.getDistance((Entity)player);
                target = player;
            }
        }
        return target;
    }
    
    public BlockPos getTargetPos(final Entity target) {
        double distance = -1.0;
        BlockPos finalPos = null;
        for (final BlockPos pos : this.getPlaceableBlocksAboveEntity(target)) {
            if (distance != -1.0 && AnvilAura.mc.player.getDistanceSq(pos) >= MathUtil.square(distance)) {
                continue;
            }
            finalPos = pos;
            distance = AnvilAura.mc.player.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        }
        return finalPos;
    }
    
    public List<BlockPos> getPlaceableBlocksAboveEntity(final Entity target) {
        new BlockPos(Math.floor(AnvilAura.mc.player.posX), Math.floor(AnvilAura.mc.player.posY), Math.floor(AnvilAura.mc.player.posZ));
        final ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        BlockPos pos;
        for (int i = (int)Math.floor(AnvilAura.mc.player.posY + 2.0); i <= 256 && BlockUtil.isPositionPlaceable(pos = new BlockPos(Math.floor(AnvilAura.mc.player.posX), (double)i, Math.floor(AnvilAura.mc.player.posZ)), false) != 0 && BlockUtil.isPositionPlaceable(pos, false) != -1 && BlockUtil.isPositionPlaceable(pos, false) != 2; ++i) {
            positions.add(pos);
        }
        return positions;
    }
    
    private void rotateToPos(final BlockPos pos) {
        if (this.rotate.getValue()) {
            final float[] angle = MathUtil.calcAngle(AnvilAura.mc.player.getPositionEyes(AnvilAura.mc.getRenderPartialTicks()), new Vec3d((double)(pos.getX() + 0.5f), (double)(pos.getY() - 0.5f), (double)(pos.getZ() + 0.5f)));
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }
    }
}
