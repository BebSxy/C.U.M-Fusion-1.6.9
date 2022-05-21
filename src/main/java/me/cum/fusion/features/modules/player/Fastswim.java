
package me.cum.fusion.features.modules.player;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class Fastswim extends Module
{
    public Setting<Double> waterHorizontal;
    public Setting<Double> waterVertical;
    public Setting<Double> lavaHorizontal;
    public Setting<Double> lavaVertical;
    
    public Fastswim() {
        super("FastSwim", "Swim fast", Module.Category.MOVEMENT, true, false, false);
        this.waterHorizontal = (Setting<Double>)this.register(new Setting("WaterHorizontal", (T)3.0, (T)1.0, (T)20.0));
        this.waterVertical = (Setting<Double>)this.register(new Setting("WaterVertical", (T)3.0, (T)1.0, (T)20.0));
        this.lavaHorizontal = (Setting<Double>)this.register(new Setting("LavaHorizontal", (T)4.0, (T)1.0, (T)20.0));
        this.lavaVertical = (Setting<Double>)this.register(new Setting("LavaVertical", (T)4.0, (T)1.0, (T)20.0));
    }
    
    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (Fastswim.mc.player.isInLava() && !Fastswim.mc.player.onGround) {
            event.setX(event.getX() * this.lavaHorizontal.getValue());
            event.setZ(event.getZ() * this.lavaHorizontal.getValue());
            event.setY(event.getY() * this.lavaVertical.getValue());
        }
        else if (Fastswim.mc.player.isInWater() && !Fastswim.mc.player.onGround) {
            event.setX(event.getX() * this.waterHorizontal.getValue());
            event.setZ(event.getZ() * this.waterHorizontal.getValue());
            event.setY(event.getY() * this.waterVertical.getValue());
        }
    }
    
    public void update() {
    }
}
