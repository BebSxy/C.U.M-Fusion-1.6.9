
package me.cum.fusion.features.modules.player;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.util.math.*;
import java.awt.*;
import net.minecraft.network.play.client.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.network.*;
import me.cum.fusion.event.events.*;
import me.cum.fusion.util.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.block.*;

public class InstantMine extends Module
{
    private final Timer breakTimer;
    public Setting<Integer> delay;
    public Setting<Boolean> picOnly;
    public Setting<Boolean> render;
    public Setting<Boolean> box;
    private final Setting<Integer> boxAlpha;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Boolean> outline;
    public final Setting<Float> lineWidth;
    private BlockPos renderBlock;
    private BlockPos lastBlock;
    private boolean packetCancel;
    private EnumFacing direction;
    
    public InstantMine() {
        super("InstantMine", "Instantly mine blocks placed in the same spot.", Module.Category.PLAYER, true, false, false);
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", (T)65, (T)0, (T)500));
        this.picOnly = (Setting<Boolean>)this.register(new Setting("PicOnly", (T)true));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", (T)false));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", (T)true, v -> this.render.getValue()));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", (T)85, (T)0, (T)255, v -> this.box.getValue() && this.render.getValue()));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)125, (T)0, (T)255, v -> this.render.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)0, (T)0, (T)255, v -> this.render.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255, v -> this.render.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", (T)true, v -> this.render.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", (T)1.0f, (T)0.1f, (T)5.0f, v -> this.outline.getValue() && this.render.getValue()));
        this.packetCancel = false;
        this.breakTimer = new Timer();
    }
    
    public void onRender3D(final Render3DEvent event) {
        if (this.render.getValue() && this.renderBlock != null) {
            final Color color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.boxAlpha.getValue());
            RenderUtil.drawBoxESP(this.renderBlock, color, false, color, this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            final CPacketPlayerDigging digPacket = (CPacketPlayerDigging)event.getPacket();
            if (digPacket.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK && this.packetCancel) {
                event.setCanceled(true);
            }
        }
    }
    
    public void onTick() {
        if (this.renderBlock != null) {
            if (this.breakTimer.passedMs(this.delay.getValue())) {
                this.breakTimer.reset();
                if (this.picOnly.getValue() && InstantMine.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() != Items.DIAMOND_PICKAXE) {
                    return;
                }
                InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.renderBlock, this.direction));
                return;
            }
        }
        try {
            InstantMine.mc.playerController.blockHitDelay = 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SubscribeEvent
    public void onBlockEvent(final BlockEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getStage() == 3 && InstantMine.mc.playerController.curBlockDamageMP > 0.1f) {
            InstantMine.mc.playerController.isHittingBlock = true;
        }
        if (event.getStage() == 4 && BlockUtil.canBreak(event.pos)) {
            InstantMine.mc.playerController.isHittingBlock = false;
            if (this.canBreak(event.pos)) {
                if (this.lastBlock == null || event.pos.getX() != this.lastBlock.getX() || event.pos.getY() != this.lastBlock.getY() || event.pos.getZ() != this.lastBlock.getZ()) {
                    this.packetCancel = false;
                    InstantMine.mc.player.swingArm(EnumHand.MAIN_HAND);
                    InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                    this.packetCancel = true;
                }
                else {
                    this.packetCancel = true;
                }
                InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                this.renderBlock = event.pos;
                this.lastBlock = event.pos;
                this.direction = event.facing;
                event.setCanceled(true);
            }
        }
    }
    
    private boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = InstantMine.mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)InstantMine.mc.world, pos) != -1.0f;
    }
}
