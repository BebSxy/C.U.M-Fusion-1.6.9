
package me.cum.fusion.features.modules.player;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraftforge.event.world.*;
import me.cum.fusion.features.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.event.entity.player.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.client.*;
import net.minecraft.world.*;
import me.cum.fusion.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.enchantment.*;
import me.cum.fusion.util.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;

public class BlockTweaks extends Module
{
    public Setting<Boolean> autoTool;
    public Setting<Boolean> autoWeapon;
    public Setting<Boolean> noFriendAttack;
    public Setting<Boolean> noBlock;
    public Setting<Boolean> noGhost;
    public Setting<Boolean> destroy;
    private static BlockTweaks INSTANCE;
    private int lastHotbarSlot;
    private int currentTargetSlot;
    private boolean switched;
    
    public BlockTweaks() {
        super("BlockTweaks", "Some tweaks for blocks.", Module.Category.PLAYER, true, false, false);
        this.autoTool = (Setting<Boolean>)this.register(new Setting("AutoTool", (T)false));
        this.autoWeapon = (Setting<Boolean>)this.register(new Setting("AutoWeapon", (T)false));
        this.noFriendAttack = (Setting<Boolean>)this.register(new Setting("NoFriendAttack", (T)false));
        this.noBlock = (Setting<Boolean>)this.register(new Setting("NoHitboxBlock", (T)true));
        this.noGhost = (Setting<Boolean>)this.register(new Setting("NoGlitchBlocks", (T)false));
        this.destroy = (Setting<Boolean>)this.register(new Setting("Destroy", (T)false, v -> this.noGhost.getValue()));
        this.lastHotbarSlot = -1;
        this.currentTargetSlot = -1;
        this.switched = false;
        this.setInstance();
    }
    
    private void setInstance() {
        BlockTweaks.INSTANCE = this;
    }
    
    public static BlockTweaks getINSTANCE() {
        if (BlockTweaks.INSTANCE == null) {
            BlockTweaks.INSTANCE = new BlockTweaks();
        }
        return BlockTweaks.INSTANCE;
    }
    
    public void onDisable() {
        if (this.switched) {
            this.equip(this.lastHotbarSlot, false);
        }
        this.lastHotbarSlot = -1;
        this.currentTargetSlot = -1;
    }
    
    @SubscribeEvent
    public void onBreak(final BlockEvent.BreakEvent event) {
        if (Feature.fullNullCheck() || !this.noGhost.getValue() || !this.destroy.getValue()) {
            return;
        }
        if (!(BlockTweaks.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
            final BlockPos pos = BlockTweaks.mc.player.getPosition();
            this.removeGlitchBlocks(pos);
        }
    }
    
    @SubscribeEvent
    public void onBlockInteract(final PlayerInteractEvent.LeftClickBlock event) {
        if (this.autoTool.getValue() && (Speedmine.getInstance().mode.getValue() != Speedmine.Mode.PACKET || PacketMine.getInstance().isOff() || !PacketMine.getInstance().tweaks.getValue()) && !Feature.fullNullCheck() && event.getPos() != null) {
            this.equipBestTool(BlockTweaks.mc.world.getBlockState(event.getPos()));
        }
    }
    
    @SubscribeEvent
    public void onAttack(final AttackEntityEvent event) {
        if (this.autoWeapon.getValue() && !Feature.fullNullCheck() && event.getTarget() != null) {
            this.equipBestWeapon(event.getTarget());
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (fullNullCheck()) {
            return;
        }
        if (this.noFriendAttack.getValue() && event.getPacket() instanceof CPacketUseEntity) {
            final CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
            final Entity entity = packet.getEntityFromWorld((World)BlockTweaks.mc.world);
            if (entity != null && Fusion.friendManager.isFriend(entity.getName())) {
                event.setCanceled(true);
            }
        }
    }
    
    public void onUpdate() {
        if (!Feature.fullNullCheck()) {
            if (BlockTweaks.mc.player.inventory.currentItem != this.lastHotbarSlot && BlockTweaks.mc.player.inventory.currentItem != this.currentTargetSlot) {
                this.lastHotbarSlot = BlockTweaks.mc.player.inventory.currentItem;
            }
            if (!BlockTweaks.mc.gameSettings.keyBindAttack.isKeyDown() && this.switched) {
                this.equip(this.lastHotbarSlot, false);
            }
        }
    }
    
    private void removeGlitchBlocks(final BlockPos pos) {
        for (int dx = -4; dx <= 4; ++dx) {
            for (int dy = -4; dy <= 4; ++dy) {
                for (int dz = -4; dz <= 4; ++dz) {
                    final BlockPos blockPos = new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    if (BlockTweaks.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)) {
                        BlockTweaks.mc.playerController.processRightClickBlock(BlockTweaks.mc.player, BlockTweaks.mc.world, blockPos, EnumFacing.DOWN, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
                    }
                }
            }
        }
    }
    
    private void equipBestTool(final IBlockState blockState) {
        int bestSlot = -1;
        double max = 0.0;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = BlockTweaks.mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                float speed = stack.getDestroySpeed(blockState);
                if (speed > 1.0f) {
                    final int eff;
                    speed += (float)(((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0) ? (Math.pow(eff, 2.0) + 1.0) : 0.0);
                    if (speed > max) {
                        max = speed;
                        bestSlot = i;
                    }
                }
            }
        }
        this.equip(bestSlot, true);
    }
    
    public void equipBestWeapon(final Entity entity) {
        int bestSlot = -1;
        double maxDamage = 0.0;
        EnumCreatureAttribute creatureAttribute = EnumCreatureAttribute.UNDEFINED;
        if (EntityUtil.isLiving(entity)) {
            final EntityLivingBase base = (EntityLivingBase)entity;
            creatureAttribute = base.getCreatureAttribute();
        }
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = BlockTweaks.mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemTool) {
                    final double damage = 0.0;
                    if (0.0 > maxDamage) {
                        maxDamage = 0.0;
                        bestSlot = i;
                    }
                }
                else if (stack.getItem() instanceof ItemSword) {
                    final double damage = ((ItemSword)stack.getItem()).getAttackDamage() + (double)EnchantmentHelper.getModifierForCreature(stack, creatureAttribute);
                    if (damage > maxDamage) {
                        maxDamage = damage;
                        bestSlot = i;
                    }
                }
            }
        }
        this.equip(bestSlot, true);
    }
    
    private void equip(final int slot, final boolean equipTool) {
        if (slot != -1) {
            if (slot != BlockTweaks.mc.player.inventory.currentItem) {
                this.lastHotbarSlot = BlockTweaks.mc.player.inventory.currentItem;
            }
            this.currentTargetSlot = slot;
            BlockTweaks.mc.player.inventory.currentItem = slot;
            this.switched = equipTool;
        }
    }
    
    static {
        BlockTweaks.INSTANCE = new BlockTweaks();
    }
}
