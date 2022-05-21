
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.cum.fusion.event.events.*;
import java.awt.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.block.*;
import me.cum.fusion.util.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.item.*;
import net.minecraftforge.registries.*;

public class AutoBuilder extends Module
{
    private final Setting<Settings> settings;
    private final Setting<Mode> mode;
    private final Setting<Direction> stairDirection;
    private final Setting<Integer> width;
    private final Setting<Boolean> dynamic;
    private final Setting<Boolean> setPos;
    private final Setting<Float> range;
    private final Setting<Integer> blocksPerTick;
    private final Setting<Integer> placeDelay;
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> altRotate;
    private final Setting<Boolean> ground;
    private final Setting<Boolean> noMove;
    private final Setting<Boolean> packet;
    private final Setting<Boolean> render;
    private final Setting<Boolean> colorSync;
    private final Setting<Boolean> box;
    private final Setting<Integer> bRed;
    private final Setting<Integer> bGreen;
    private final Setting<Integer> bBlue;
    private final Setting<Integer> bAlpha;
    private final Setting<Boolean> outline;
    private final Setting<Integer> oRed;
    private final Setting<Integer> oGreen;
    private final Setting<Integer> oBlue;
    private final Setting<Integer> oAlpha;
    private final Setting<Float> lineWidth;
    private final Setting<Boolean> keepPos;
    private final Setting<Updates> updates;
    private final Setting<Switch> switchMode;
    private final Setting<Boolean> allBlocks;
    private final Timer timer;
    private final List<BlockPos> placepositions;
    private BlockPos startPos;
    private int blocksThisTick;
    private int lastSlot;
    private int blockSlot;
    
    public AutoBuilder() {
        super("AutoBuilder", "Auto Builds.", Category.PLAYER, true, false, false);
        this.settings = (Setting<Settings>)this.register(new Setting("Settings", (T)Settings.PATTERN));
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", (T)Mode.STAIRS, v -> this.settings.getValue() == Settings.PATTERN));
        this.stairDirection = (Setting<Direction>)this.register(new Setting("Direction", (T)Direction.NORTH, v -> this.mode.getValue() == Mode.STAIRS && this.settings.getValue() == Settings.PATTERN));
        this.width = (Setting<Integer>)this.register(new Setting("StairWidth", (T)40, (T)1, (T)100, v -> this.mode.getValue() == Mode.STAIRS && this.settings.getValue() == Settings.PATTERN));
        this.dynamic = (Setting<Boolean>)this.register(new Setting("Dynamic", (T)true, v -> this.mode.getValue() == Mode.FLAT && this.settings.getValue() == Settings.PATTERN));
        this.setPos = (Setting<Boolean>)this.register(new Setting("ResetPos", (T)false, v -> this.settings.getValue() == Settings.PATTERN && (this.mode.getValue() == Mode.STAIRS || (this.mode.getValue() == Mode.FLAT && !this.dynamic.getValue()))));
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)4.0f, (T)1.0f, (T)6.0f, v -> this.settings.getValue() == Settings.PLACE));
        this.blocksPerTick = (Setting<Integer>)this.register(new Setting("Blocks/Tick", (T)3, (T)1, (T)8, v -> this.settings.getValue() == Settings.PLACE));
        this.placeDelay = (Setting<Integer>)this.register(new Setting("PlaceDelay", (T)150, (T)0, (T)500, v -> this.settings.getValue() == Settings.PLACE));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", (T)true, v -> this.settings.getValue() == Settings.PLACE));
        this.altRotate = (Setting<Boolean>)this.register(new Setting("AltRotate", (T)false, v -> this.rotate.getValue() && this.settings.getValue() == Settings.PLACE));
        this.ground = (Setting<Boolean>)this.register(new Setting("NoJump", (T)true, v -> this.settings.getValue() == Settings.PLACE));
        this.noMove = (Setting<Boolean>)this.register(new Setting("NoMove", (T)true, v -> this.settings.getValue() == Settings.PLACE));
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", (T)true, v -> this.settings.getValue() == Settings.PLACE));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", (T)true, v -> this.settings.getValue() == Settings.RENDER));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("CSync", (T)false, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue()));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", (T)true, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue()));
        this.bRed = (Setting<Integer>)this.register(new Setting("BoxRed", (T)150, (T)0, (T)255, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() && this.box.getValue()));
        this.bGreen = (Setting<Integer>)this.register(new Setting("BoxGreen", (T)0, (T)0, (T)255, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() && this.box.getValue()));
        this.bBlue = (Setting<Integer>)this.register(new Setting("BoxBlue", (T)150, (T)0, (T)255, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() && this.box.getValue()));
        this.bAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", (T)40, (T)0, (T)255, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() && this.box.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", (T)true, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue()));
        this.oRed = (Setting<Integer>)this.register(new Setting("OutlineRed", (T)255, (T)0, (T)255, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.oGreen = (Setting<Integer>)this.register(new Setting("OutlineGreen", (T)50, (T)0, (T)255, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.oBlue = (Setting<Integer>)this.register(new Setting("OutlineBlue", (T)255, (T)0, (T)255, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.oAlpha = (Setting<Integer>)this.register(new Setting("OutlineAlpha", (T)255, (T)0, (T)255, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", (T)1.5f, (T)0.1f, (T)5.0f, v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.keepPos = (Setting<Boolean>)this.register(new Setting("KeepOldPos", (T)false, v -> this.settings.getValue() == Settings.MISC));
        this.updates = (Setting<Updates>)this.register(new Setting("Update", (T)Updates.TICK, v -> this.settings.getValue() == Settings.MISC));
        this.switchMode = (Setting<Switch>)this.register(new Setting("Switch", (T)Switch.SILENT, v -> this.settings.getValue() == Settings.MISC));
        this.allBlocks = (Setting<Boolean>)this.register(new Setting("AllBlocks", (T)true, v -> this.settings.getValue() == Settings.MISC));
        this.timer = new Timer();
        this.placepositions = new ArrayList<BlockPos>();
    }
    
    @Override
    public void onTick() {
        if (this.updates.getValue() == Updates.TICK) {
            this.doAutoBuilder();
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.updates.getValue() == Updates.UPDATE) {
            this.doAutoBuilder();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (this.updates.getValue() == Updates.WALKING && event.getStage() != 1) {
            this.doAutoBuilder();
        }
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (this.placepositions == null || !this.render.getValue()) {
            return;
        }
        final Color outline = new Color(this.oRed.getValue(), this.oGreen.getValue(), this.oBlue.getValue(), this.oAlpha.getValue());
        final Color box = new Color(this.bRed.getValue(), this.bGreen.getValue(), this.bBlue.getValue(), this.bAlpha.getValue());
        this.placepositions.forEach(pos -> RenderUtil.drawSexyBoxPhobosIsRetardedFuckYouESP(new AxisAlignedBB(pos), box, outline, this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.colorSync.getValue(), 1.0f, 1.0f, 1.0f));
    }
    
    @Override
    public void onEnable() {
        this.placepositions.clear();
        if (!this.keepPos.getValue() || this.startPos == null) {
            this.startPos = new BlockPos(AutoBuilder.mc.player.posX, Math.ceil(AutoBuilder.mc.player.posY), AutoBuilder.mc.player.posZ).down();
        }
        this.blocksThisTick = 0;
        this.lastSlot = AutoBuilder.mc.player.inventory.currentItem;
        this.timer.reset();
    }
    
    private void doAutoBuilder() {
        if (!this.check()) {
            return;
        }
        for (final BlockPos pos : this.placepositions) {
            if (this.blocksThisTick >= this.blocksPerTick.getValue()) {
                this.doSwitch(true);
                return;
            }
            final int canPlace = BlockUtil.isPositionPlaceable(pos, false, true);
            if (canPlace == 3) {
                BlockUtil.placeBlockNotRetarded(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                ++this.blocksThisTick;
            }
            else {
                if (canPlace != 2) {
                    continue;
                }
                if (this.mode.getValue() != Mode.STAIRS) {
                    continue;
                }
                if (BlockUtil.isPositionPlaceable(pos.down(), false, true) == 3) {
                    BlockUtil.placeBlockNotRetarded(pos.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                    ++this.blocksThisTick;
                }
                else {
                    switch (this.stairDirection.getValue()) {
                        case SOUTH: {
                            if (BlockUtil.isPositionPlaceable(pos.south(), false, true) == 3) {
                                BlockUtil.placeBlockNotRetarded(pos.south(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                                ++this.blocksThisTick;
                                continue;
                            }
                            continue;
                        }
                        case WEST: {
                            if (BlockUtil.isPositionPlaceable(pos.west(), false, true) == 3) {
                                BlockUtil.placeBlockNotRetarded(pos.west(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                                ++this.blocksThisTick;
                                continue;
                            }
                            continue;
                        }
                        case NORTH: {
                            if (BlockUtil.isPositionPlaceable(pos.north(), false, true) == 3) {
                                BlockUtil.placeBlockNotRetarded(pos.north(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                                ++this.blocksThisTick;
                                continue;
                            }
                            continue;
                        }
                        case EAST: {
                            if (BlockUtil.isPositionPlaceable(pos.east(), false, true) == 3) {
                                BlockUtil.placeBlockNotRetarded(pos.east(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                                ++this.blocksThisTick;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                }
            }
        }
        this.doSwitch(true);
    }
    
    private boolean doSwitch(final boolean back) {
        final Item i = AutoBuilder.mc.player.getHeldItemMainhand().getItem();
        switch (this.switchMode.getValue()) {
            case NONE: {
                return i instanceof ItemBlock && (this.allBlocks.getValue() || ((ItemBlock)i).getBlock() instanceof BlockObsidian);
            }
            case NORMAL: {
                if (!back) {
                    InventoryUtil.switchToHotbarSlot(this.blockSlot, false);
                    break;
                }
                break;
            }
            case SILENT: {
                if (i instanceof ItemBlock) {
                    if (this.allBlocks.getValue()) {
                        break;
                    }
                    if (((ItemBlock)i).getBlock() instanceof BlockObsidian) {
                        break;
                    }
                }
                if (this.lastSlot == -1) {
                    break;
                }
                if (back) {
                    AutoBuilder.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.lastSlot));
                    break;
                }
                AutoBuilder.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.blockSlot));
                break;
            }
        }
        return true;
    }
    
    private boolean check() {
        if (this.setPos.getValue()) {
            this.startPos = new BlockPos(AutoBuilder.mc.player.posX, Math.ceil(AutoBuilder.mc.player.posY), AutoBuilder.mc.player.posZ).down();
            this.setPos.setValue(false);
        }
        this.getPositions();
        if (this.placepositions.isEmpty()) {
            return false;
        }
        if (!this.timer.passedMs(this.placeDelay.getValue())) {
            return false;
        }
        this.timer.reset();
        this.blocksThisTick = 0;
        this.lastSlot = AutoBuilder.mc.player.inventory.currentItem;
        this.blockSlot = (this.allBlocks.getValue() ? InventoryUtil.findAnyBlock() : InventoryUtil.findHotbarBlock((Class<? extends IForgeRegistryEntry.Impl>)BlockObsidian.class));
        return (!this.ground.getValue() || AutoBuilder.mc.player.onGround) && this.blockSlot != -1 && (!this.noMove.getValue() || (AutoBuilder.mc.player.moveForward == 0.0f && AutoBuilder.mc.player.moveStrafing == 0.0f)) && this.doSwitch(false);
    }
    
    private void getPositions() {
        if (this.startPos == null) {
            return;
        }
        this.placepositions.clear();
        for (final BlockPos pos : BlockUtil.getSphere(new BlockPos(AutoBuilder.mc.player.posX, Math.ceil(AutoBuilder.mc.player.posY), AutoBuilder.mc.player.posZ).up(), this.range.getValue(), this.range.getValue().intValue(), false, true, 0)) {
            if (this.placepositions.contains(pos)) {
                continue;
            }
            if (!AutoBuilder.mc.world.isAirBlock(pos)) {
                continue;
            }
            if (this.mode.getValue() == Mode.STAIRS) {
                switch (this.stairDirection.getValue()) {
                    case NORTH: {
                        if (this.startPos.getZ() - pos.getZ() == pos.getY() - this.startPos.getY() && Math.abs(pos.getX() - this.startPos.getX()) < this.width.getValue() / 2) {
                            this.placepositions.add(pos);
                            continue;
                        }
                        continue;
                    }
                    case EAST: {
                        if (pos.getX() - this.startPos.getX() == pos.getY() - this.startPos.getY() && Math.abs(pos.getZ() - this.startPos.getZ()) < this.width.getValue() / 2) {
                            this.placepositions.add(pos);
                            continue;
                        }
                        continue;
                    }
                    case SOUTH: {
                        if (pos.getZ() - this.startPos.getZ() == pos.getY() - this.startPos.getY() && Math.abs(this.startPos.getX() - pos.getX()) < this.width.getValue() / 2) {
                            this.placepositions.add(pos);
                            continue;
                        }
                        continue;
                    }
                    case WEST: {
                        if (this.startPos.getX() - pos.getX() == pos.getY() - this.startPos.getY() && Math.abs(this.startPos.getZ() - pos.getZ()) < this.width.getValue() / 2) {
                            this.placepositions.add(pos);
                            continue;
                        }
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
            else {
                if (this.mode.getValue() != Mode.FLAT) {
                    continue;
                }
                if (pos.getY() != (this.dynamic.getValue() ? (Math.ceil(AutoBuilder.mc.player.posY) - 1.0) : this.startPos.getY())) {
                    continue;
                }
                this.placepositions.add(pos);
            }
        }
    }
    
    public enum Mode
    {
        STAIRS, 
        FLAT;
    }
    
    public enum Switch
    {
        NONE, 
        NORMAL, 
        SILENT;
    }
    
    public enum Updates
    {
        TICK, 
        UPDATE, 
        WALKING;
    }
    
    public enum Direction
    {
        WEST, 
        SOUTH, 
        EAST, 
        NORTH;
    }
    
    public enum Settings
    {
        MISC, 
        PATTERN, 
        PLACE, 
        RENDER;
    }
}
