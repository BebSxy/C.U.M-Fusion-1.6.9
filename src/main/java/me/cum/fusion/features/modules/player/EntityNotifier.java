
package me.cum.fusion.features.modules.player;

import me.cum.fusion.features.modules.*;
import net.minecraft.entity.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.entity.monster.*;
import me.cum.fusion.features.command.*;
import net.minecraft.init.*;
import net.minecraft.entity.passive.*;
import java.util.*;

public class EntityNotifier extends Module
{
    private final Set<Entity> ghasts;
    private final Set<Entity> donkeys;
    private final Set<Entity> mules;
    private final Set<Entity> llamas;
    public Setting<Boolean> Chat;
    public Setting<Boolean> Sound;
    public Setting<Boolean> Ghasts;
    public Setting<Boolean> Donkeys;
    public Setting<Boolean> Mules;
    public Setting<Boolean> Llamas;
    
    public EntityNotifier() {
        super("EntityNotifier", "Helps you find certain things.", Module.Category.PLAYER, true, false, false);
        this.ghasts = new HashSet<Entity>();
        this.donkeys = new HashSet<Entity>();
        this.mules = new HashSet<Entity>();
        this.llamas = new HashSet<Entity>();
        this.Chat = (Setting<Boolean>)this.register(new Setting("Chat", (T)true));
        this.Sound = (Setting<Boolean>)this.register(new Setting("Sound", (T)true));
        this.Ghasts = (Setting<Boolean>)this.register(new Setting("Ghasts", (T)true));
        this.Donkeys = (Setting<Boolean>)this.register(new Setting("Donkeys", (T)true));
        this.Mules = (Setting<Boolean>)this.register(new Setting("Mules", (T)true));
        this.Llamas = (Setting<Boolean>)this.register(new Setting("Llamas", (T)true));
    }
    
    public void onEnable() {
        this.ghasts.clear();
        this.donkeys.clear();
        this.mules.clear();
        this.llamas.clear();
    }
    
    public void onUpdate() {
        if (this.Ghasts.getValue()) {
            for (final Entity entity : EntityNotifier.mc.world.getLoadedEntityList()) {
                if (entity instanceof EntityGhast) {
                    if (this.ghasts.contains(entity)) {
                        continue;
                    }
                    if (this.Chat.getValue()) {
                        Command.sendMessage("Ghast Detected at: " + Math.round((float)entity.getPosition().getX()) + "X, " + Math.round((float)entity.getPosition().getY()) + "Y, " + Math.round((float)entity.getPosition().getZ()) + "Z.");
                    }
                    this.ghasts.add(entity);
                    if (!this.Sound.getValue()) {
                        continue;
                    }
                    EntityNotifier.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
                }
            }
        }
        if (this.Donkeys.getValue()) {
            for (final Entity entity : EntityNotifier.mc.world.getLoadedEntityList()) {
                if (entity instanceof EntityDonkey) {
                    if (this.donkeys.contains(entity)) {
                        continue;
                    }
                    if (this.Chat.getValue()) {
                        Command.sendMessage("Donkey Detected at: " + Math.round((float)entity.getPosition().getX()) + "X, " + Math.round((float)entity.getPosition().getY()) + "Y, " + Math.round((float)entity.getPosition().getZ()) + "Z.");
                    }
                    this.donkeys.add(entity);
                    if (!this.Sound.getValue()) {
                        continue;
                    }
                    EntityNotifier.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
                }
            }
        }
        if (this.Mules.getValue()) {
            for (final Entity entity : EntityNotifier.mc.world.getLoadedEntityList()) {
                if (entity instanceof EntityMule) {
                    if (this.mules.contains(entity)) {
                        continue;
                    }
                    if (this.Chat.getValue()) {
                        Command.sendMessage("Mule Detected at: " + Math.round((float)entity.getPosition().getX()) + "X, " + Math.round((float)entity.getPosition().getY()) + "Y, " + Math.round((float)entity.getPosition().getZ()) + "Z.");
                    }
                    this.mules.add(entity);
                    if (!this.Sound.getValue()) {
                        continue;
                    }
                    EntityNotifier.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
                }
            }
        }
        if (this.Llamas.getValue()) {
            for (final Entity entity : EntityNotifier.mc.world.getLoadedEntityList()) {
                if (entity instanceof EntityLlama) {
                    if (this.llamas.contains(entity)) {
                        continue;
                    }
                    if (this.Chat.getValue()) {
                        Command.sendMessage("Llama Detected at: " + Math.round((float)entity.getPosition().getX()) + "X, " + Math.round((float)entity.getPosition().getY()) + "Y, " + Math.round((float)entity.getPosition().getZ()) + "Z.");
                    }
                    this.llamas.add(entity);
                    if (!this.Sound.getValue()) {
                        continue;
                    }
                    EntityNotifier.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
                }
            }
        }
    }
}
