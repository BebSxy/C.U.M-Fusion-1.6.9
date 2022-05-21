
package me.cum.fusion.manager;

import me.cum.fusion.features.*;

public class TimerManager extends Feature
{
    private float timer;
    
    public TimerManager() {
        this.timer = 1.0f;
    }
    
    public void init() {
    }
    
    public void unload() {
        this.timer = 1.0f;
    }
    
    public void update() {
    }
    
    public void setTimer(final float timer) {
        if (timer > 0.0f) {
            this.timer = timer;
        }
    }
    
    public float getTimer() {
        return this.timer;
    }
    
    public void reset() {
        this.timer = 1.0f;
    }
}
