
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.entity.player.*;
import me.cum.fusion.*;
import net.minecraft.entity.*;
import me.cum.fusion.util.*;
import java.util.*;

public class Killaura extends Module
{
    public static Entity target;
    private final Timer timer;
    public Setting<Float> range;
    public Setting<Boolean> delay;
    public Setting<Boolean> rotate;
    public Setting<Boolean> onlySharp;
    public Setting<Float> raytrace;
    public Setting<Boolean> players;
    public Setting<Boolean> mobs;
    public Setting<Boolean> animals;
    public Setting<Boolean> vehicles;
    public Setting<Boolean> projectiles;
    public Setting<Boolean> tps;
    public Setting<Boolean> packet;
    
    public Killaura() {
        super("Killaura", "Kills aura.", Category.COMBAT, true, false, false);
        this.timer = new Timer();
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)6.0f, (T)0.1f, (T)7.0f));
        this.delay = (Setting<Boolean>)this.register(new Setting("HitDelay", (T)Boolean.TRUE));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", (T)Boolean.TRUE));
        this.onlySharp = (Setting<Boolean>)this.register(new Setting("SwordOnly", (T)Boolean.TRUE));
        this.raytrace = (Setting<Float>)this.register(new Setting("Raytrace", (T)6.0f, (T)0.1f, (T)7.0f, "Wall Range."));
        this.players = (Setting<Boolean>)this.register(new Setting("Players", (T)Boolean.TRUE));
        this.mobs = (Setting<Boolean>)this.register(new Setting("Mobs", (T)Boolean.FALSE));
        this.animals = (Setting<Boolean>)this.register(new Setting("Animals", (T)Boolean.FALSE));
        this.vehicles = (Setting<Boolean>)this.register(new Setting("Entities", (T)Boolean.FALSE));
        this.projectiles = (Setting<Boolean>)this.register(new Setting("Projectiles", (T)Boolean.FALSE));
        this.tps = (Setting<Boolean>)this.register(new Setting("TpsSync", (T)Boolean.TRUE));
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", (T)Boolean.FALSE));
    }
    
    @Override
    public void onTick() {
        if (!this.rotate.getValue()) {
            this.doKillaura();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotate.getValue()) {
            this.doKillaura();
        }
    }
    
    private void doKillaura() {
        if (this.onlySharp.getValue() && !EntityUtil.holdingWeapon((EntityPlayer)Util.mc.player)) {
            Killaura.target = null;
            return;
        }
        final int wait = this.delay.getValue() ? ((int)(DamageUtil.getCooldownByWeapon((EntityPlayer)Util.mc.player) * (this.tps.getValue() ? Fusion.serverManager.getTpsFactor() : 1.0f))) : 0;
        if (!this.timer.passedMs(wait)) {
            return;
        }
        Killaura.target = this.getTarget();
        if (Killaura.target == null) {
            return;
        }
        if (this.rotate.getValue()) {
            Fusion.rotationManager.lookAtEntity(Killaura.target);
        }
        EntityUtil.attackEntity(Killaura.target, this.packet.getValue(), true);
        this.timer.reset();
    }
    
    private Entity getTarget() {
        Entity target = null;
        double distance = this.range.getValue();
        double maxHealth = 36.0;
        for (final EntityPlayer entity : Util.mc.world.playerEntities) {
            if ((this.players.getValue() && entity instanceof EntityPlayer) || (this.animals.getValue() && EntityUtil.isPassive((Entity)entity)) || (this.mobs.getValue() && !EntityUtil.isMobAggressive((Entity)entity)) || (this.vehicles.getValue() && EntityUtil.isVehicle((Entity)entity)) || (this.projectiles.getValue() && EntityUtil.isProjectile((Entity)entity))) {
                if (entity instanceof EntityLivingBase && EntityUtil.isntValid((Entity)entity, distance)) {
                    continue;
                }
                if (!Util.mc.player.canEntityBeSeen((Entity)entity) && !EntityUtil.canEntityFeetBeSeen((Entity)entity) && Util.mc.player.getDistanceSq((Entity)entity) > MathUtil.square(this.raytrace.getValue())) {
                    continue;
                }
                if (target == null) {
                    target = (Entity)entity;
                    distance = Util.mc.player.getDistanceSq((Entity)entity);
                    maxHealth = EntityUtil.getHealth((Entity)entity);
                }
                else {
                    if (DamageUtil.isArmorLow(entity, 18)) {
                        target = (Entity)entity;
                        break;
                    }
                    if (Util.mc.player.getDistanceSq((Entity)entity) < distance) {
                        target = (Entity)entity;
                        distance = Util.mc.player.getDistanceSq((Entity)entity);
                        maxHealth = EntityUtil.getHealth((Entity)entity);
                    }
                    if (EntityUtil.getHealth((Entity)entity) >= maxHealth) {
                        continue;
                    }
                    target = (Entity)entity;
                    distance = Util.mc.player.getDistanceSq((Entity)entity);
                    maxHealth = EntityUtil.getHealth((Entity)entity);
                }
            }
        }
        return target;
    }
    
    @Override
    public String getDisplayInfo() {
        if (Killaura.target instanceof EntityPlayer) {
            return Killaura.target.getName();
        }
        return null;
    }
}
