
package me.cum.fusion.event;

import net.minecraftforge.fml.common.eventhandler.*;

public class EventStage extends Event
{
    private int stage;
    
    public EventStage() {
    }
    
    public EventStage(final int stage) {
        this.stage = stage;
    }
    
    public int getStage() {
        return this.stage;
    }
    
    public void setStage(final int stage) {
        this.stage = stage;
    }
}
