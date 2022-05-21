
package me.cum.fusion.features.modules.troll;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.event.events.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import java.util.*;
import net.minecraft.client.renderer.entity.*;
import me.cum.fusion.util.*;
import org.lwjgl.util.glu.*;

public class AssESP extends Module
{
    public AssESP() {
        super("FuckEsp", "Want to have gay sex with your friend? Your solution is here!", Module.Category.TROLL, false, false, false);
    }
    
    public void onRender3D(final Render3DEvent render3DEvent) {
        for (final Object e : AssESP.mc.world.loadedEntityList) {
            if (!(e instanceof EntityPlayer)) {
                continue;
            }
            final RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            final EntityPlayer entityPlayer = (EntityPlayer)e;
            final double d = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * AssESP.mc.timer.renderPartialTicks;
            final double d2 = d - renderManager.renderPosX;
            final double d3 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * AssESP.mc.timer.renderPartialTicks;
            final double d4 = d3 - renderManager.renderPosY;
            final double d5 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * AssESP.mc.timer.renderPartialTicks;
            final double d6 = d5 - renderManager.renderPosZ;
            GL11.glPushMatrix();
            RenderHelper.disableStandardItemLighting();
            this.esp(entityPlayer, d2, d4, d6);
            RenderHelper.enableStandardItemLighting();
            GL11.glPopMatrix();
        }
    }
    
    public void esp(final EntityPlayer entityPlayer, final double d, final double d2, final double d3) {
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(true);
        GL11.glLineWidth(1.0f);
        GL11.glTranslated(d, d2, d3);
        GL11.glRotatef(-entityPlayer.rotationYaw, 0.0f, entityPlayer.height, 0.0f);
        GL11.glTranslated(-d, -d2, -d3);
        GL11.glTranslated(d, d2 + entityPlayer.height / 2.0f - 0.22499999403953552, d3);
        GL11.glColor4f(ColorUtil.rainbow(50).getRed() / 255.0f, ColorUtil.rainbow(50).getGreen() / 255.0f, ColorUtil.rainbow(50).getBlue() / 255.0f, 1.0f);
        GL11.glRotated((double)(entityPlayer.isSneaking() ? 35 : 0), 1.0, 0.0, 0.0);
        final Sphere sphere = new Sphere();
        GL11.glTranslated(-0.15, 0.0, -0.2);
        sphere.setDrawStyle(100013);
        sphere.draw(0.2f, 10, 20);
        GL11.glTranslated(0.3500000014901161, 0.0, 0.0);
        final Sphere sphere2 = new Sphere();
        sphere2.setDrawStyle(100013);
        sphere2.draw(0.2f, 15, 20);
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
    }
}
