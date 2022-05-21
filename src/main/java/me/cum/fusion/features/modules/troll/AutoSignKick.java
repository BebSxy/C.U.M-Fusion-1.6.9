
package me.cum.fusion.features.modules.troll;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.tileentity.*;
import me.cum.fusion.features.command.*;
import net.minecraftforge.registries.*;
import java.util.*;
import net.minecraft.util.math.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import me.cum.fusion.util.*;

public class AutoSignKick extends Module
{
    Setting<Integer> switchDelay;
    Setting<Integer> placeDelay;
    Setting<Integer> mineDelay;
    Setting<Integer> range;
    Timer placeTimer;
    Timer switchTimer;
    Timer mineTimer;
    private boolean hadBreak;
    
    public AutoSignKick() {
        super("AutoSignKick", "stop being lazy lmao", Module.Category.TROLL, true, false, false);
        this.switchDelay = (Setting<Integer>)this.register(new Setting("SwitchDelay", (T)100, (T)0, (T)5000));
        this.placeDelay = (Setting<Integer>)this.register(new Setting("PlaceDelay", (T)100, (T)0, (T)5000));
        this.mineDelay = (Setting<Integer>)this.register(new Setting("MineDelay", (T)100, (T)0, (T)5000));
        this.range = (Setting<Integer>)this.register(new Setting("Range", (T)2, (T)1, (T)20));
        this.placeTimer = new Timer();
        this.switchTimer = new Timer();
        this.mineTimer = new Timer();
    }
    
    public void onEnable() {
        this.hadBreak = false;
    }
    
    public void onDisable() {
        this.hadBreak = false;
    }
    
    public void onUpdate() {
        for (final TileEntity tileEntity : AutoSignKick.mc.world.loadedTileEntityList) {
            if (!(tileEntity instanceof TileEntitySign)) {
                continue;
            }
            if (AutoSignKick.mc.player.getDistanceSq(tileEntity.getPos()) > MathUtil.square(this.range.getValue())) {
                continue;
            }
            Command.sendMessage("Sign located at X: " + tileEntity.getPos().getX() + ", Y: " + tileEntity.getPos().getY() + ", Z: " + tileEntity.getPos().getZ());
            final BlockPos posTile = tileEntity.getPos();
            if (this.hadBreak) {
                continue;
            }
            this.axeSwitch();
            this.mineBlock(posTile);
            InventoryUtil.switchToHotbarSlot((Class<? extends IForgeRegistryEntry.Impl>)ItemSign.class, false);
            Command.sendMessage("Changed to sign hotbar.");
            this.switchTimer.reset();
            this.place(posTile);
            Command.sendMessage("Done!");
            this.disable();
            this.hadBreak = true;
        }
    }
    
    private void axeSwitch() {
        if (this.switchTimer.passedMs(this.switchDelay.getValue() * 3L)) {
            InventoryUtil.switchToHotbarSlot((Class<? extends IForgeRegistryEntry.Impl>)ItemAxe.class, false);
            Command.sendMessage("Switched to Axe");
            this.switchTimer.reset();
        }
    }
    
    private void mineBlock(final BlockPos pos) {
        if (this.mineTimer.passedMs(this.mineDelay.getValue() * 3L)) {
            AutoSignKick.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
            Command.sendMessage("Mined");
            this.mineTimer.reset();
        }
    }
    
    private void place(final BlockPos pos) {
        if (this.placeTimer.passedMs(this.placeDelay.getValue() * 3L)) {
            BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, true, false);
            Command.sendMessage("Placed sign!");
            this.placeTimer.reset();
        }
    }
}
