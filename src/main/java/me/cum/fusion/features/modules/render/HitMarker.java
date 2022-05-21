
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import net.minecraft.util.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraft.client.gui.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.*;
import java.awt.*;
import me.cum.fusion.util.*;

public final class HitMarker extends Module
{
    public final ResourceLocation image;
    public final ResourceLocation sounds;
    private int renderTicks;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> alpha;
    public Setting<Integer> thickness;
    public Setting<Double> time;
    public Setting<Boolean> sound;
    
    public HitMarker() {
        super("HitMarker", "", Module.Category.RENDER, false, false, false);
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)255, (T)0, (T)255));
        this.thickness = (Setting<Integer>)this.register(new Setting("Thickness", (T)2, (T)1, (T)6));
        this.time = (Setting<Double>)this.register(new Setting("Time", (T)20.0, (T)1.0, (T)50.0));
        this.sound = (Setting<Boolean>)this.register(new Setting("sound", (T)true));
        this.image = new ResourceLocation("hitmarker.png");
        this.sounds = new ResourceLocation("hit.ogg");
        this.renderTicks = 100;
    }
    
    public void onRender2D(final Render2DEvent event) {
        if (this.renderTicks < this.time.getValue()) {
            final ScaledResolution resolution = new ScaledResolution(HitMarker.mc);
            this.drawHitMarkers();
        }
    }
    
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }
    
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }
    
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent event) {
        if (!event.getEntity().equals((Object)HitMarker.mc.player)) {
            return;
        }
        this.renderTicks = 0;
    }
    
    @SubscribeEvent
    public void onTickClientTick(final TickEvent event) {
        ++this.renderTicks;
    }
    
    public void drawHitMarkers() {
        final ScaledResolution resolution = new ScaledResolution(HitMarker.mc);
        RenderUtil.drawLine(resolution.getScaledWidth() / 2.0f - 4.0f, resolution.getScaledHeight() / 2.0f - 4.0f, resolution.getScaledWidth() / 2.0f - 8.0f, resolution.getScaledHeight() / 2.0f - 8.0f, this.thickness.getValue(), new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB());
        RenderUtil.drawLine(resolution.getScaledWidth() / 2.0f + 4.0f, resolution.getScaledHeight() / 2.0f - 4.0f, resolution.getScaledWidth() / 2.0f + 8.0f, resolution.getScaledHeight() / 2.0f - 8.0f, this.thickness.getValue(), new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB());
        RenderUtil.drawLine(resolution.getScaledWidth() / 2.0f - 4.0f, resolution.getScaledHeight() / 2.0f + 4.0f, resolution.getScaledWidth() / 2.0f - 8.0f, resolution.getScaledHeight() / 2.0f + 8.0f, this.thickness.getValue(), new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB());
        RenderUtil.drawLine(resolution.getScaledWidth() / 2.0f + 4.0f, resolution.getScaledHeight() / 2.0f + 4.0f, resolution.getScaledWidth() / 2.0f + 8.0f, resolution.getScaledHeight() / 2.0f + 8.0f, this.thickness.getValue(), new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB());
    }
}
