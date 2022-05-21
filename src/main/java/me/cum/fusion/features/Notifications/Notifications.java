
package me.cum.fusion.features.Notifications;

import net.minecraft.client.*;
import me.cum.fusion.features.modules.client.*;
import me.cum.fusion.*;
import net.minecraft.client.gui.*;
import me.cum.fusion.util.*;

public class Notifications
{
    public static Minecraft mc;
    private final String text;
    private final long disableTime;
    private final float width;
    private final Timer timer;
    
    public Notifications(final String text, final long disableTime) {
        this.timer = new Timer();
        this.text = text;
        this.disableTime = disableTime;
        this.width = (float)Fusion.moduleManager.getModuleByClass(HUD.class).renderer.getStringWidth(text);
        this.timer.reset();
    }
    
    public void onDraw(final int y) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        if (this.timer.passedMs(this.disableTime)) {
            Fusion.notificationManager.getNotifications().remove(this);
        }
        RenderUtil.drawRect(scaledResolution.getScaledWidth() - 4 - this.width, (float)y, (float)(scaledResolution.getScaledWidth() - 2), (float)(y + Fusion.moduleManager.getModuleByClass(HUD.class).renderer.getFontHeight() + 3), 1962934272);
        Fusion.moduleManager.getModuleByClass(HUD.class).renderer.drawString(this.text, scaledResolution.getScaledWidth() - this.width - 3.0f, (float)(y + 2), -1, true);
    }
}
