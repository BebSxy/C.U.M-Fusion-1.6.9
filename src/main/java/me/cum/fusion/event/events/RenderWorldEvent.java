
package me.cum.fusion.event.events;

import me.cum.fusion.event.*;

public class RenderWorldEvent extends EventStage
{
    private final float partialTicks;
    
    public RenderWorldEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }
    
    public float getPartialTicks() {
        return this.partialTicks;
    }
}
