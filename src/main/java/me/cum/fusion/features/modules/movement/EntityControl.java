
package me.cum.fusion.features.modules.movement;

import me.cum.fusion.features.modules.*;

public class EntityControl extends Module
{
    public static EntityControl INSTANCE;
    
    public EntityControl() {
        super("EntityControl", "Control entities with the force or some shit", Module.Category.MOVEMENT, false, false, false);
        EntityControl.INSTANCE = this;
    }
}
