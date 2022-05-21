
package me.cum.fusion.event.events;

import me.cum.fusion.event.*;

public class Render3DEvent extends EventStage
{
    private final float partialTicks;
    
    public Render3DEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }
    
    public float getPartialTicks() {
        return this.partialTicks;
    }
}
