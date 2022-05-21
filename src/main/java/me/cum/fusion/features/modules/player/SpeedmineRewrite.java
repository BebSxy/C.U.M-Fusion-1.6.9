
package me.cum.fusion.features.modules.player;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.block.state.*;
import me.cum.fusion.*;
import net.minecraft.network.*;
import net.minecraft.item.*;
import net.minecraftforge.registries.*;
import java.awt.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import java.util.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.cum.fusion.event.events.*;
import net.minecraft.block.*;
import me.cum.fusion.util.*;
import net.minecraft.util.*;
import net.minecraft.init.*;

public class SpeedmineRewrite extends Module
{
    private static SpeedmineRewrite INSTANCE;
    private final Setting<Float> range;
    private final Timer timer;
    public Setting<Boolean> tweaks;
    public Setting<Mode> mode;
    public Setting<Boolean> reset;
    public Setting<Float> damage;
    public Setting<Boolean> noBreakAnim;
    public Setting<Boolean> noDelay;
    public Setting<Boolean> noSwing;
    public Setting<Boolean> noTrace;
    public Setting<Boolean> allow;
    public Setting<Boolean> noGapTrace;
    public Setting<Boolean> pickaxe;
    public Setting<Boolean> doubleBreak;
    public Setting<Boolean> webSwitch;
    public Setting<Boolean> silentSwitch;
    public Setting<Boolean> render;
    public Setting<Boolean> box;
    private final Setting<Integer> boxAlpha;
    public Setting<Boolean> outline;
    private final Setting<Float> lineWidth;
    public BlockPos currentPos;
    public IBlockState currentBlockState;
    private boolean isMining;
    private BlockPos lastPos;
    private EnumFacing lastFacing;
    
    public SpeedmineRewrite() {
        super("FastBreak", "Speeds up mining.", Module.Category.PLAYER, true, false, false);
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)10.0f, (T)0.0f, (T)50.0f));
        this.timer = new Timer();
        this.tweaks = (Setting<Boolean>)this.register(new Setting("Tweaks", (T)true));
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", (T)Mode.PACKET, v -> this.tweaks.getValue()));
        this.reset = (Setting<Boolean>)this.register(new Setting("Reset", (T)true));
        this.damage = (Setting<Float>)this.register(new Setting("Damage", (T)0.7f, (T)0.0f, (T)1.0f, v -> this.mode.getValue() == Mode.DAMAGE && this.tweaks.getValue()));
        this.noBreakAnim = (Setting<Boolean>)this.register(new Setting("NoBreakAnim", (T)false));
        this.noDelay = (Setting<Boolean>)this.register(new Setting("NoDelay", (T)false));
        this.noSwing = (Setting<Boolean>)this.register(new Setting("NoSwing", (T)false));
        this.noTrace = (Setting<Boolean>)this.register(new Setting("NoTrace", (T)false));
        this.allow = (Setting<Boolean>)this.register(new Setting("AllowMultiTask", (T)false));
        this.noGapTrace = (Setting<Boolean>)this.register(new Setting("NoGapTrace", (T)false, v -> this.noTrace.getValue()));
        this.pickaxe = (Setting<Boolean>)this.register(new Setting("Pickaxe", (T)true, v -> this.noTrace.getValue()));
        this.doubleBreak = (Setting<Boolean>)this.register(new Setting("DoubleBreak", (T)false));
        this.webSwitch = (Setting<Boolean>)this.register(new Setting("WebSwitch", (T)false));
        this.silentSwitch = (Setting<Boolean>)this.register(new Setting("SilentSwitch", (T)true));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", (T)true));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", (T)true, v -> this.render.getValue()));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", (T)85, (T)0, (T)255, v -> this.box.getValue() && this.render.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", (T)true, v -> this.render.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", (T)1.0f, (T)0.1f, (T)5.0f, v -> this.outline.getValue() && this.render.getValue()));
        this.isMining = false;
        this.lastPos = null;
        this.lastFacing = null;
        this.setInstance();
    }
    
    public static SpeedmineRewrite getInstance() {
        if (SpeedmineRewrite.INSTANCE == null) {
            SpeedmineRewrite.INSTANCE = new SpeedmineRewrite();
        }
        return SpeedmineRewrite.INSTANCE;
    }
    
    private void setInstance() {
        SpeedmineRewrite.INSTANCE = this;
    }
    
    public void onTick() {
        if (this.currentPos != null) {
            if (SpeedmineRewrite.mc.player != null && SpeedmineRewrite.mc.player.getDistanceSq(this.currentPos) > MathUtil.square(this.range.getValue())) {
                this.currentPos = null;
                this.currentBlockState = null;
                return;
            }
            if (SpeedmineRewrite.mc.player != null && this.silentSwitch.getValue() && this.timer.passedMs((int)(2000.0f * Fusion.serverManager.getTpsFactor())) && this.getPickSlot() != -1) {
                SpeedmineRewrite.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.getPickSlot()));
            }
            if (!SpeedmineRewrite.mc.world.getBlockState(this.currentPos).equals(this.currentBlockState) || SpeedmineRewrite.mc.world.getBlockState(this.currentPos).getBlock() == Blocks.AIR) {
                this.currentPos = null;
                this.currentBlockState = null;
            }
            else if (this.webSwitch.getValue() && this.currentBlockState.getBlock() == Blocks.WEB && Speedmine.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                InventoryUtil.switchToHotbarSlot((Class<? extends IForgeRegistryEntry.Impl>)ItemSword.class, false);
            }
        }
    }
    
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (this.noDelay.getValue()) {
            SpeedmineRewrite.mc.playerController.blockHitDelay = 0;
        }
        if (this.isMining && this.lastPos != null && this.lastFacing != null && this.noBreakAnim.getValue()) {
            SpeedmineRewrite.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
        }
        if (this.reset.getValue() && SpeedmineRewrite.mc.gameSettings.keyBindUseItem.isKeyDown() && !this.allow.getValue()) {
            SpeedmineRewrite.mc.playerController.isHittingBlock = false;
        }
    }
    
    public void onRender3D(final Render3DEvent event) {
        if (this.render.getValue() && this.currentPos != null) {
            final Color color = new Color(this.timer.passedMs((int)(2000.0f * Fusion.serverManager.getTpsFactor())) ? 0 : 255, this.timer.passedMs((int)(2000.0f * Fusion.serverManager.getTpsFactor())) ? 255 : 0, 0, 255);
            RenderUtil.drawBoxESP(this.currentPos, color, false, color, this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getStage() == 0) {
            if (this.noSwing.getValue() && event.getPacket() instanceof CPacketAnimation) {
                event.setCanceled(true);
            }
            final CPacketPlayerDigging packet;
            if (this.noBreakAnim.getValue() && event.getPacket() instanceof CPacketPlayerDigging && (packet = (CPacketPlayerDigging)event.getPacket()) != null && packet.getPosition() != null) {
                try {
                    for (final Entity entity : SpeedmineRewrite.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(packet.getPosition()))) {
                        if (!(entity instanceof EntityEnderCrystal)) {
                            continue;
                        }
                        this.showAnimation();
                        return;
                    }
                }
                catch (Exception ex) {}
                if (packet.getAction().equals((Object)CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                    this.showAnimation(true, packet.getPosition(), packet.getFacing());
                }
                if (packet.getAction().equals((Object)CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                    this.showAnimation();
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onBlockEvent(final BlockEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getStage() == 3 && SpeedmineRewrite.mc.world.getBlockState(event.pos).getBlock() instanceof BlockEndPortalFrame) {
            SpeedmineRewrite.mc.world.getBlockState(event.pos).getBlock().setHardness(50.0f);
        }
        if (event.getStage() == 3 && this.reset.getValue() && Speedmine.mc.playerController.curBlockDamageMP > 0.1f) {
            SpeedmineRewrite.mc.playerController.isHittingBlock = true;
        }
        if (event.getStage() == 4 && this.tweaks.getValue()) {
            if (BlockUtil.canBreak(event.pos)) {
                if (this.reset.getValue()) {
                    SpeedmineRewrite.mc.playerController.isHittingBlock = false;
                }
                switch (this.mode.getValue()) {
                    case PACKET: {
                        if (this.currentPos == null) {
                            this.currentPos = event.pos;
                            this.currentBlockState = SpeedmineRewrite.mc.world.getBlockState(this.currentPos);
                            this.timer.reset();
                        }
                        SpeedmineRewrite.mc.player.swingArm(EnumHand.MAIN_HAND);
                        SpeedmineRewrite.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        SpeedmineRewrite.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        event.setCanceled(true);
                        break;
                    }
                    case DAMAGE: {
                        if (SpeedmineRewrite.mc.playerController.curBlockDamageMP < this.damage.getValue()) {
                            break;
                        }
                        SpeedmineRewrite.mc.playerController.curBlockDamageMP = 1.0f;
                        break;
                    }
                    case INSTANT: {
                        SpeedmineRewrite.mc.player.swingArm(EnumHand.MAIN_HAND);
                        SpeedmineRewrite.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        SpeedmineRewrite.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        SpeedmineRewrite.mc.playerController.onPlayerDestroyBlock(event.pos);
                        SpeedmineRewrite.mc.world.setBlockToAir(event.pos);
                        break;
                    }
                }
            }
            final BlockPos above;
            if (this.doubleBreak.getValue() && BlockUtil.canBreak(above = event.pos.add(0, 1, 0)) && Speedmine.mc.player.getDistance((double)above.getX(), (double)above.getY(), (double)above.getZ()) <= 5.0) {
                SpeedmineRewrite.mc.player.swingArm(EnumHand.MAIN_HAND);
                SpeedmineRewrite.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.facing));
                SpeedmineRewrite.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.facing));
                SpeedmineRewrite.mc.playerController.onPlayerDestroyBlock(above);
                SpeedmineRewrite.mc.world.setBlockToAir(above);
            }
        }
    }
    
    private int getPickSlot() {
        for (int i = 0; i < 9; ++i) {
            if (SpeedmineRewrite.mc.player.inventory.getStackInSlot(i).getItem() == Items.DIAMOND_PICKAXE) {
                return i;
            }
        }
        return -1;
    }
    
    private void showAnimation(final boolean isMining, final BlockPos lastPos, final EnumFacing lastFacing) {
        this.isMining = isMining;
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }
    
    public void showAnimation() {
        this.showAnimation(false, null, null);
    }
    
    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }
    
    static {
        SpeedmineRewrite.INSTANCE = new SpeedmineRewrite();
    }
    
    public enum Mode
    {
        PACKET, 
        DAMAGE, 
        INSTANT;
    }
}
