
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

public class PenisESP extends Module
{
    private float spin;
    
    public PenisESP() {
        super("PenisEsp", "Draws something you don't have.", Module.Category.TROLL, false, false, false);
        this.spin = 0.0f;
    }
    
    public void onRender3D(final Render3DEvent render3DEvent) {
        this.spin += 5.0f;
        for (final Object e : PenisESP.mc.world.loadedEntityList) {
            if (!(e instanceof EntityPlayer)) {
                continue;
            }
            final RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            final EntityPlayer entityPlayer = (EntityPlayer)e;
            final double d = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * PenisESP.mc.timer.renderPartialTicks;
            final double d2 = d - renderManager.renderPosX;
            final double d3 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * PenisESP.mc.timer.renderPartialTicks;
            final double d4 = d3 - renderManager.renderPosY;
            final double d5 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * PenisESP.mc.timer.renderPartialTicks;
            final double d6 = d5 - renderManager.renderPosZ;
            GL11.glPushMatrix();
            RenderHelper.disableStandardItemLighting();
            this.esp(entityPlayer, d2, d4, d6);
            RenderHelper.enableStandardItemLighting();
            GL11.glPopMatrix();
        }
        if (this.spin >= 360.0f) {
            this.spin = 0.0f;
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
        GL11.glRotatef(-entityPlayer.rotationYaw + this.spin, 0.0f, entityPlayer.height, 0.0f);
        GL11.glTranslated(-d, -d2, -d3);
        GL11.glTranslated(d, d2 + entityPlayer.height / 2.0f - 0.22499999403953552, d3);
        GL11.glColor4f(ColorUtil.rainbow(50).getRed() / 255.0f, ColorUtil.rainbow(50).getGreen() / 255.0f, ColorUtil.rainbow(50).getBlue() / 255.0f, 1.0f);
        GL11.glRotated((double)(entityPlayer.isSneaking() ? 35 : 0), 1.0, 0.0, 0.0);
        GL11.glTranslated(0.0, 0.0, 0.07500000298023224);
        final Cylinder cylinder = new Cylinder();
        cylinder.setDrawStyle(100013);
        cylinder.draw(0.1f, 0.11f, 1.0f, 25, 20);
        GL11.glColor4f(ColorUtil.rainbow(50).getRed() / 255.0f, ColorUtil.rainbow(50).getGreen() / 255.0f, ColorUtil.rainbow(50).getBlue() / 255.0f, 1.0f);
        GL11.glTranslated(0.0, 0.0, -0.12500000298023223);
        GL11.glTranslated(-0.09000000074505805, 0.0, 0.0);
        final Sphere sphere = new Sphere();
        sphere.setDrawStyle(100013);
        sphere.draw(0.14f, 10, 20);
        GL11.glTranslated(0.16000000149011612, 0.0, 0.0);
        final Sphere sphere2 = new Sphere();
        sphere2.setDrawStyle(100013);
        sphere2.draw(0.14f, 10, 20);
        GL11.glColor4f(ColorUtil.rainbow(50).getRed() / 255.0f, ColorUtil.rainbow(50).getGreen() / 255.0f, ColorUtil.rainbow(50).getBlue() / 255.0f, 1.0f);
        GL11.glTranslated(-0.07000000074505806, 0.0, 1.089999952316284);
        final Sphere sphere3 = new Sphere();
        sphere3.setDrawStyle(100013);
        sphere3.draw(0.13f, 15, 20);
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
    }
}
