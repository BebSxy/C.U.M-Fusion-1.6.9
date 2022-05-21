
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class NoWeather extends Module
{
    public NoWeather() {
        super("NoWeather", "Makes the weather do shit.", Module.Category.RENDER, true, false, false);
    }
    
    @SubscribeEvent
    public void onUpdate(final TickEvent.ClientTickEvent event) {
        NoWeather.mc.world.setRainStrength(0.0f);
    }
}
