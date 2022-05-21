
package me.cum.fusion.features.modules.movement;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.client.entity.*;
import me.cum.fusion.event.events.*;
import net.minecraft.block.material.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;

public class Step extends Module
{
    public Setting<Boolean> vanilla;
    public Setting<Integer> stepHeight;
    public Setting<Boolean> turnOff;
    public Setting<Boolean> reverse;
    private static Step instance;
    
    public Step() {
        super("Step", "Allows you to step up blocks", Module.Category.MOVEMENT, true, false, false);
        this.vanilla = (Setting<Boolean>)this.register(new Setting("Vanilla", (T)false));
        this.stepHeight = (Setting<Integer>)this.register(new Setting("Height", (T)2, (T)1, (T)2));
        this.turnOff = (Setting<Boolean>)this.register(new Setting("Disable", (T)false));
        this.reverse = (Setting<Boolean>)this.register(new Setting("Reverse", (T)false));
        Step.instance = this;
    }
    
    public static Step getInstance() {
        if (Step.instance == null) {
            Step.instance = new Step();
        }
        return Step.instance;
    }
    
    public void onUpdate() {
        if (this.reverse.getValue()) {
            if (Step.mc.player == null || Step.mc.player.isInWater() || Step.mc.player.isInLava()) {
                return;
            }
            if (Step.mc.player.onGround) {
                final EntityPlayerSP player;
                final EntityPlayerSP player = player = Step.mc.player;
                --player.motionY;
            }
        }
    }
    
    @SubscribeEvent
    public void onStep(final StepEvent event) {
        if (Step.mc.player.onGround && !Step.mc.player.isInsideOfMaterial(Material.WATER) && !Step.mc.player.isInsideOfMaterial(Material.LAVA) && Step.mc.player.collidedVertically && Step.mc.player.fallDistance == 0.0f && !Step.mc.gameSettings.keyBindJump.pressed && !Step.mc.player.isOnLadder()) {
            event.setHeight((float)this.stepHeight.getValue());
            final double rheight = Step.mc.player.getEntityBoundingBox().minY - Step.mc.player.posY;
            if (rheight >= 0.625) {
                if (!this.vanilla.getValue()) {
                    this.ncpStep(rheight);
                }
                if (this.turnOff.getValue()) {
                    this.disable();
                }
            }
        }
        else {
            event.setHeight(0.6f);
        }
    }
    
    private void ncpStep(final double height) {
        final double posX = Step.mc.player.posX;
        final double posZ = Step.mc.player.posZ;
        double y = Step.mc.player.posY;
        if (height >= 1.1) {
            if (height < 1.6) {
                final double[] array;
                final double[] offset = array = new double[] { 0.42, 0.33, 0.24, 0.083, -0.078 };
                for (final double off : array) {
                    Step.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, y += off, posZ, false));
                }
            }
            else if (height < 2.1) {
                final double[] array2;
                final double[] heights = array2 = new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869 };
                for (final double off : array2) {
                    Step.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, y + off, posZ, false));
                }
            }
            else {
                final double[] array3;
                final double[] heights = array3 = new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 };
                for (final double off : array3) {
                    Step.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, y + off, posZ, false));
                }
            }
        }
        else {
            double first = 0.42;
            double second = 0.75;
            if (height != 1.0) {
                first *= height;
                second *= height;
                if (first > 0.425) {
                    first = 0.425;
                }
                if (second > 0.78) {
                    second = 0.78;
                }
                if (second < 0.49) {
                    second = 0.49;
                }
            }
            Step.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, y + first, posZ, false));
            if (y + second < y + height) {
                Step.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, y + second, posZ, false));
            }
        }
    }
}
