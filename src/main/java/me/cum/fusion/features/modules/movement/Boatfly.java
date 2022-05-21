
package me.cum.fusion.features.modules.movement;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.entity.item.*;
import net.minecraft.util.math.*;
import net.minecraft.network.*;
import me.cum.fusion.event.events.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.play.client.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.network.play.server.*;

public class Boatfly extends Module
{
    public Setting<Double> speed;
    public Setting<Double> verticalSpeed;
    public Setting<Boolean> noKick;
    public Setting<Boolean> packet;
    public Setting<Integer> packets;
    public Setting<Integer> interact;
    public static Boatfly INSTANCE;
    private EntityBoat target;
    private int teleportID;
    
    public Boatfly() {
        super("BoatFly", "Boatfly for 2b", Module.Category.MOVEMENT, true, false, false);
        this.speed = (Setting<Double>)this.register(new Setting("Speed", (T)3.0, (T)1.0, (T)10.0));
        this.verticalSpeed = (Setting<Double>)this.register(new Setting("VerticalSpeed", (T)3.0, (T)1.0, (T)10.0));
        this.noKick = (Setting<Boolean>)this.register(new Setting("No-Kick", (T)true));
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", (T)true));
        this.packets = (Setting<Integer>)this.register(new Setting("Packets", (T)3, (T)1, (T)5, v -> this.packet.getValue()));
        this.interact = (Setting<Integer>)this.register(new Setting("Delay", (T)2, (T)1, (T)20));
        Boatfly.INSTANCE = this;
    }
    
    public void onUpdate() {
        if (Boatfly.mc.player == null) {
            return;
        }
        if (Boatfly.mc.world == null || Boatfly.mc.player.getRidingEntity() == null) {
            return;
        }
        if (Boatfly.mc.player.getRidingEntity() instanceof EntityBoat) {
            this.target = (EntityBoat)Boatfly.mc.player.ridingEntity;
        }
        Boatfly.mc.player.getRidingEntity().setNoGravity(true);
        Boatfly.mc.player.getRidingEntity().motionY = 0.0;
        if (Boatfly.mc.gameSettings.keyBindJump.isKeyDown()) {
            Boatfly.mc.player.getRidingEntity().onGround = false;
            Boatfly.mc.player.getRidingEntity().motionY = this.verticalSpeed.getValue() / 10.0;
        }
        if (Boatfly.mc.gameSettings.keyBindSprint.isKeyDown()) {
            Boatfly.mc.player.getRidingEntity().onGround = false;
            Boatfly.mc.player.getRidingEntity().motionY = -(this.verticalSpeed.getValue() / 10.0);
        }
        final double[] normalDir = this.directionSpeed(this.speed.getValue() / 2.0);
        if (Boatfly.mc.player.movementInput.moveStrafe != 0.0f || Boatfly.mc.player.movementInput.moveForward != 0.0f) {
            Boatfly.mc.player.getRidingEntity().motionX = normalDir[0];
            Boatfly.mc.player.getRidingEntity().motionZ = normalDir[1];
        }
        else {
            Boatfly.mc.player.getRidingEntity().motionX = 0.0;
            Boatfly.mc.player.getRidingEntity().motionZ = 0.0;
        }
        if (this.noKick.getValue()) {
            if (Boatfly.mc.gameSettings.keyBindJump.isKeyDown()) {
                if (Boatfly.mc.player.ticksExisted % 8 < 2) {
                    Boatfly.mc.player.getRidingEntity().motionY = -0.03999999910593033;
                }
            }
            else if (Boatfly.mc.player.ticksExisted % 8 < 4) {
                Boatfly.mc.player.getRidingEntity().motionY = -0.07999999821186066;
            }
        }
        this.handlePackets(Boatfly.mc.player.getRidingEntity().motionX, Boatfly.mc.player.getRidingEntity().motionY, Boatfly.mc.player.getRidingEntity().motionZ);
    }
    
    public void handlePackets(final double x, final double y, final double z) {
        if (this.packet.getValue()) {
            final Vec3d vec = new Vec3d(x, y, z);
            if (Boatfly.mc.player.getRidingEntity() == null) {
                return;
            }
            final Vec3d position = Boatfly.mc.player.getRidingEntity().getPositionVector().add(vec);
            Boatfly.mc.player.getRidingEntity().setPosition(position.x, position.y, position.z);
            Boatfly.mc.player.connection.sendPacket((Packet)new CPacketVehicleMove(Boatfly.mc.player.getRidingEntity()));
            for (int i = 0; i < this.packets.getValue(); ++i) {
                Boatfly.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID++));
            }
        }
    }
    
    @SubscribeEvent
    public void onSendPacket(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketVehicleMove && Boatfly.mc.player.isRiding() && Boatfly.mc.player.ticksExisted % this.interact.getValue() == 0) {
            Boatfly.mc.playerController.interactWithEntity((EntityPlayer)Boatfly.mc.player, Boatfly.mc.player.ridingEntity, EnumHand.OFF_HAND);
        }
        if ((event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketInput) && Boatfly.mc.player.isRiding()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void onReceivePacket(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketMoveVehicle && Boatfly.mc.player.isRiding()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.teleportID = ((SPacketPlayerPosLook)event.getPacket()).teleportId;
        }
    }
    
    private double[] directionSpeed(final double speed) {
        float forward = Boatfly.mc.player.movementInput.moveForward;
        float side = Boatfly.mc.player.movementInput.moveStrafe;
        float yaw = Boatfly.mc.player.prevRotationYaw + (Boatfly.mc.player.rotationYaw - Boatfly.mc.player.prevRotationYaw) * Boatfly.mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            }
            else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            }
            else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[] { posX, posZ };
    }
}
