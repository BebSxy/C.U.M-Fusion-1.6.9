
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import me.cum.fusion.*;
import me.cum.fusion.util.*;

public class CSGOWatermark extends Module
{
    Timer delayTimer;
    public Setting<Integer> X;
    public Setting<Integer> Y;
    public Setting<Integer> delay;
    public Setting<Integer> saturation;
    public Setting<Integer> brightness;
    public float hue;
    public int red;
    public int green;
    public int blue;
    
    public CSGOWatermark() {
        super("CSGOWatermark", "noat em cee actually makes something", Category.CLIENT, true, false, false);
        this.delayTimer = new Timer();
        this.X = (Setting<Integer>)this.register(new Setting("WatermarkX", (T)0, (T)0, (T)300));
        this.Y = (Setting<Integer>)this.register(new Setting("WatermarkY", (T)0, (T)0, (T)300));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", (T)240, (T)0, (T)600));
        this.saturation = (Setting<Integer>)this.register(new Setting("Saturation", (T)127, (T)1, (T)255));
        this.brightness = (Setting<Integer>)this.register(new Setting("Brightness", (T)100, (T)0, (T)255));
        this.red = 1;
        this.green = 1;
        this.blue = 1;
    }
    
    @Override
    public void onRender2D(final Render2DEvent event) {
        this.drawCsgoWatermark();
    }
    
    public void drawCsgoWatermark() {
        final int padding = 5;
        final String message = "C.U.M Fusion v1.6.9 | " + CSGOWatermark.mc.player.getName() + " | " + Fusion.serverManager.getPing() + "ms";
        final int textWidth = Fusion.textManager.getStringWidth(message);
        final int textHeight = CSGOWatermark.mc.fontRenderer.FONT_HEIGHT;
        RenderUtil.drawRectangleCorrectly(this.X.getValue() - 4, this.Y.getValue() - 4, textWidth + 16, textHeight + 12, ColorUtil.toRGBA(22, 22, 22, 255));
        RenderUtil.drawRectangleCorrectly(this.X.getValue(), this.Y.getValue(), textWidth + 4, textHeight + 4, ColorUtil.toRGBA(0, 0, 0, 255));
        RenderUtil.drawRectangleCorrectly(this.X.getValue(), this.Y.getValue(), textWidth + 8, textHeight + 4, ColorUtil.toRGBA(0, 0, 0, 255));
        RenderUtil.drawRectangleCorrectly(this.X.getValue(), this.Y.getValue(), textWidth + 8, 1, ColorUtil.rainbow(this.delay.getValue()).hashCode());
        Fusion.textManager.drawString(message, (float)(this.X.getValue() + 3), (float)(this.Y.getValue() + 3), ColorUtil.toRGBA(255, 255, 255, 255), false);
    }
}
