
package me.cum.fusion.manager;

import me.cum.fusion.util.*;
import net.minecraftforge.common.*;

public class RotationManager2 implements Util
{
    boolean rotated;
    float yaw;
    float pitch;
    
    public RotationManager2() {
        this.rotated = false;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
        MinecraftForge.EVENT_BUS.register((Object)this);
    }
    
    public float getYaw() {
        return this.yaw;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public void setPitch(final float balls) {
        this.pitch = balls;
    }
    
    public void setYaw(final float sex) {
        this.yaw = sex;
    }
}
