
package me.cum.fusion.features.modules.movement;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.network.*;
import net.minecraft.network.play.*;
import net.minecraft.client.network.*;
import java.util.*;
import net.minecraft.entity.*;
import me.cum.fusion.util.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;

public class PacketFlyNew extends Module
{
    private final Setting<types> type;
    private final Setting<modes> mode;
    private final ArrayList<Packet<INetHandlerPlayServer>> packets;
    public Setting<Integer> factorAmount;
    public Setting<Boolean> limit;
    private int teleportID;
    private int frequency;
    private boolean frequencyUp;
    private float rotationYaw;
    private float rotationPitch;
    
    public PacketFlyNew() {
        super("PacketFlyNew", "Uses packets to allow you to fly and move.", Module.Category.MOVEMENT, true, false, false);
        this.type = (Setting<types>)this.register(new Setting("Type", (T)types.DOWN));
        this.mode = (Setting<modes>)this.register(new Setting("Mode", (T)modes.FAST));
        this.factorAmount = (Setting<Integer>)this.register(new Setting("Factor", (T)1.0f, (T)0.1f, (T)10.0f));
        this.limit = (Setting<Boolean>)this.register(new Setting("Limit", (T)true));
        this.teleportID = -1;
        this.frequency = 1;
        this.frequencyUp = true;
        this.rotationYaw = -1.0f;
        this.rotationPitch = -1.0f;
        this.packets = new ArrayList<Packet<INetHandlerPlayServer>>();
    }
    
    public void sendOffsets(final double x, final double y, final double z) {
        CPacketPlayer.PositionRotation spoofPos = null;
        switch (this.type.getValue()) {
            case UP: {
                spoofPos = new CPacketPlayer.PositionRotation(x, y + 1337.0, z, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
                break;
            }
            case DOWN: {
                spoofPos = new CPacketPlayer.PositionRotation(x, y - 1337.0, z, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
                break;
            }
            case BOUNDED: {
                spoofPos = new CPacketPlayer.PositionRotation(x, 256.0, z, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
                break;
            }
            case CONCEAL: {
                spoofPos = new CPacketPlayer.PositionRotation(x + new Random().nextInt(2000000) - 1000000.0, y + new Random().nextInt(2000000) - 1000000.0, z + new Random().nextInt(2000000) - 1000000.0, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
                break;
            }
            case LIMITJITTER: {
                spoofPos = new CPacketPlayer.PositionRotation(x, y + new Random().nextInt(512) - 256.0, z, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
                break;
            }
            case PRESERVE: {
                spoofPos = new CPacketPlayer.PositionRotation(x + new Random().nextInt(2000000) - 1000000.0, y, z + new Random().nextInt(2000000) - 1000000.0, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
                break;
            }
        }
        if (spoofPos == null) {
            return;
        }
        this.packets.add((Packet<INetHandlerPlayServer>)spoofPos);
        Objects.requireNonNull(PacketFlyNew.mc.getConnection()).sendPacket((Packet)spoofPos);
    }
    
    public void onEnable() {
        this.packets.clear();
        this.rotationYaw = PacketFlyNew.mc.player.rotationYaw;
        this.rotationPitch = PacketFlyNew.mc.player.rotationPitch;
        this.sendOffsets(PacketFlyNew.mc.player.posX, PacketFlyNew.mc.player.posY, PacketFlyNew.mc.player.posZ);
    }
    
    private boolean isInsideBlock() {
        return !PacketFlyNew.mc.world.getCollisionBoxes((Entity)PacketFlyNew.mc.player, PacketFlyNew.mc.player.getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625)).isEmpty();
    }
    
    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        PacketFlyNew.mc.player.motionX = 0.0;
        PacketFlyNew.mc.player.motionY = 0.0;
        PacketFlyNew.mc.player.motionZ = 0.0;
        if (event.getStage() == 1) {
            event.setCanceled(true);
            return;
        }
        double motionY = 0.0;
        if (PacketFlyNew.mc.player.movementInput.jump) {
            if (this.isInsideBlock()) {
                motionY = 0.016;
            }
            else {
                motionY = 0.032;
            }
            if (!PacketFlyNew.mc.player.onGround && !this.isInsideBlock() && PacketFlyNew.mc.player.ticksExisted % 15 == 0) {
                motionY = -0.032;
            }
        }
        else if (PacketFlyNew.mc.player.movementInput.sneak) {
            if (this.isInsideBlock()) {
                motionY = -0.032;
            }
            else {
                motionY = -0.062;
            }
        }
        else if (!PacketFlyNew.mc.player.onGround && !this.isInsideBlock() && PacketFlyNew.mc.player.ticksExisted % 15 == 0) {
            motionY = -0.032;
        }
        int currentFactor = 1;
        int clock = 0;
        while (currentFactor <= (this.mode.getValue().equals(modes.FACTOR) ? this.factorAmount.getValue() : 1)) {
            if (clock++ > (this.limit.getValue() ? 5 : 1)) {
                double vSpeed;
                if (this.isInsideBlock()) {
                    if (PacketFlyNew.mc.player.movementInput.jump || PacketFlyNew.mc.player.movementInput.sneak) {
                        vSpeed = 0.016;
                    }
                    else {
                        vSpeed = 0.032;
                    }
                }
                else if (PacketFlyNew.mc.player.movementInput.jump) {
                    vSpeed = 0.0032;
                }
                else if (PacketFlyNew.mc.player.movementInput.sneak) {
                    vSpeed = 0.032;
                }
                else {
                    vSpeed = 0.062;
                }
                final double[] strafing = MathUtil.directionSpeed(vSpeed);
                final double motionX = strafing[0] * currentFactor;
                final double motionZ = strafing[1] * currentFactor;
                event.setX(motionX);
                event.setY(motionY);
                event.setZ(motionZ);
                this.doMovement(motionX, motionY, motionZ);
                ++currentFactor;
                if (motionX == 0.0 && motionY == 0.0 && motionZ == 0.0) {
                    return;
                }
                if (this.frequencyUp) {
                    ++this.frequency;
                    if (this.frequency != 3) {
                        continue;
                    }
                    this.frequencyUp = false;
                }
                else {
                    --this.frequency;
                    if (this.frequency != 1) {
                        continue;
                    }
                    this.frequencyUp = true;
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            return;
        }
        PacketFlyNew.mc.player.motionX = 0.0;
        PacketFlyNew.mc.player.motionY = 0.0;
        PacketFlyNew.mc.player.motionZ = 0.0;
        PacketFlyNew.mc.player.setVelocity(0.0, 0.0, 0.0);
    }
    
    @SubscribeEvent
    public void onPushOutOfBlocks(final PushEvent event) {
        if (event.getStage() == 1) {
            event.setCanceled(true);
        }
    }
    
    private void doMovement(final double x, final double y, final double z) {
        final CPacketPlayer.PositionRotation newPos = new CPacketPlayer.PositionRotation(PacketFlyNew.mc.player.posX + x, PacketFlyNew.mc.player.posY + y, PacketFlyNew.mc.player.posZ + z, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
        this.packets.add((Packet<INetHandlerPlayServer>)newPos);
        Objects.requireNonNull(PacketFlyNew.mc.getConnection()).sendPacket((Packet)newPos);
        for (int i = 0; i < this.frequency; ++i) {
            this.sendOffsets(PacketFlyNew.mc.player.posX, PacketFlyNew.mc.player.posY, PacketFlyNew.mc.player.posZ);
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.Rotation) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer packetPlayer = (CPacketPlayer)event.getPacket();
            if (this.packets.contains(packetPlayer)) {
                this.packets.remove(packetPlayer);
            }
            else {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (PacketFlyNew.mc.player == null || PacketFlyNew.mc.world == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            final SPacketPlayerPosLook flag = (SPacketPlayerPosLook)event.getPacket();
            this.teleportID = flag.getTeleportId();
            PacketFlyNew.mc.player.setPosition(flag.getX(), flag.getY(), flag.getZ());
            PacketFlyNew.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID++));
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            final SPacketEntityVelocity packet = (SPacketEntityVelocity)event.getPacket();
            if (packet.entityID == PacketFlyNew.mc.player.entityId) {
                packet.motionX = 0;
                packet.motionY = 0;
                packet.motionZ = 0;
            }
        }
    }
    
    private enum modes
    {
        FAST, 
        FACTOR;
    }
    
    private enum types
    {
        UP, 
        DOWN, 
        PRESERVE, 
        LIMITJITTER, 
        BOUNDED, 
        CONCEAL;
    }
}
