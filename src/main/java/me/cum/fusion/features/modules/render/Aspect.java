
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class Aspect extends Module
{
    public Setting<Double> aspect;
    
    public Aspect() {
        super("AspectRatio", "Stretched res like fortnite", Module.Category.RENDER, true, false, false);
        this.aspect = (Setting<Double>)this.register(new Setting("Aspect", (T)(Aspect.mc.displayWidth / Aspect.mc.displayHeight + 0.0), (T)0.0, (T)3.0));
    }
    
    @SubscribeEvent
    public void onPerspectiveEvent(final PerspectiveEvent event) {
        event.setAspect(this.aspect.getValue().floatValue());
    }
}
