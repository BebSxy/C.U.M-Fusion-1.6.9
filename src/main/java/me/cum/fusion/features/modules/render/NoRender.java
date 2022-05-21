
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class NoRender extends Module
{
    private static NoRender INSTANCE;
    public Setting<Boolean> blocks;
    public Setting<NoArmor> noArmor;
    public Setting<Skylight> skylight;
    public Setting<Boolean> advancements;
    public Setting<Boolean> hurtCam;
    public Setting<Boolean> fire;
    public Setting<Boolean> explosion;
    
    public NoRender() {
        super("NoRender", "Allows you to stop rendering stuff", Module.Category.RENDER, true, false, false);
        this.blocks = (Setting<Boolean>)this.register(new Setting("Blocks", (T)Boolean.FALSE, "Blocks"));
        this.noArmor = (Setting<NoArmor>)this.register(new Setting("NoArmor", (T)NoArmor.NONE, "Doesnt Render Armor on players."));
        this.skylight = (Setting<Skylight>)this.register(new Setting("Skylight", (T)Skylight.NONE));
        this.advancements = (Setting<Boolean>)this.register(new Setting("Advancements", (T)false));
        this.hurtCam = (Setting<Boolean>)this.register(new Setting("NoHurtCam", (T)false));
        this.fire = (Setting<Boolean>)this.register(new Setting("Fire", (T)Boolean.FALSE, "Removes the portal overlay."));
        this.explosion = (Setting<Boolean>)this.register(new Setting("Explosions", (T)false, "Removes explosions"));
        this.setInstance();
    }
    
    public static NoRender getInstance() {
        if (NoRender.INSTANCE == null) {
            NoRender.INSTANCE = new NoRender();
        }
        return NoRender.INSTANCE;
    }
    
    private void setInstance() {
        NoRender.INSTANCE = this;
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketExplosion && this.explosion.getValue()) {
            event.setCanceled(true);
        }
    }
    
    static {
        NoRender.INSTANCE = new NoRender();
        NoRender.INSTANCE = new NoRender();
    }
    
    public enum Skylight
    {
        NONE, 
        WORLD, 
        ENTITY, 
        ALL;
    }
    
    public enum NoArmor
    {
        NONE, 
        ALL, 
        HELMET;
    }
}
