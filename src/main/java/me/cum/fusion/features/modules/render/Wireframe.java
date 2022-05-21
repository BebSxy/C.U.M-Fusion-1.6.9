
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class Wireframe extends Module
{
    private static Wireframe INSTANCE;
    public final Setting<Float> alpha;
    public final Setting<Float> lineWidth;
    public Setting<RenderMode> mode;
    public Setting<Boolean> players;
    public Setting<Boolean> playerModel;
    
    public Wireframe() {
        super("Wireframe", "Draws a wireframe esp around other players.", Module.Category.RENDER, false, false, false);
        this.alpha = (Setting<Float>)this.register(new Setting("PAlpha", (T)255.0f, (T)0.1f, (T)255.0f));
        this.lineWidth = (Setting<Float>)this.register(new Setting("PLineWidth", (T)1.0f, (T)0.1f, (T)3.0f));
        this.mode = (Setting<RenderMode>)this.register(new Setting("PMode", (T)RenderMode.SOLID));
        this.players = (Setting<Boolean>)this.register(new Setting("Players", (T)Boolean.FALSE));
        this.playerModel = (Setting<Boolean>)this.register(new Setting("PlayerModel", (T)Boolean.FALSE));
        this.setInstance();
    }
    
    public static Wireframe getINSTANCE() {
        if (Wireframe.INSTANCE == null) {
            Wireframe.INSTANCE = new Wireframe();
        }
        return Wireframe.INSTANCE;
    }
    
    private void setInstance() {
        Wireframe.INSTANCE = this;
    }
    
    @SubscribeEvent
    public void onRenderPlayerEvent(final RenderPlayerEvent.Pre event) {
        event.getEntityPlayer().hurtTime = 0;
    }
    
    static {
        Wireframe.INSTANCE = new Wireframe();
    }
    
    public enum RenderMode
    {
        SOLID, 
        WIREFRAME;
    }
}
