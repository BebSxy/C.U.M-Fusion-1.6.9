
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import java.awt.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.player.*;
import com.mojang.authlib.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.cum.fusion.event.events.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;

public class PopESP extends Module
{
    public static Color color;
    public static Color outlineColor;
    public static EntityOtherPlayerMP player;
    public static EntityPlayer entity;
    public long startTime;
    public static float opacity;
    public static long time;
    public static long duration;
    public static float startAlpha;
    
    public PopESP() {
        super("PopESP BETA", "Nice popchams", Module.Category.RENDER, true, true, false);
    }
    
    @SubscribeEvent
    public void onPopped(final TotemPopEvent event) {
        if (PopESP.mc.player != null && PopESP.mc.world != null) {
            final EntityPlayer entity = event.getEntity();
            if (entity != null && entity != PopESP.mc.player) {
                final GameProfile profile = new GameProfile(PopESP.mc.player.getUniqueID(), "BrianGamer");
                (PopESP.player = new EntityOtherPlayerMP((World)PopESP.mc.world, profile)).copyLocationAndAnglesFrom((Entity)entity);
                PopESP.player.rotationYaw = entity.rotationYaw;
                PopESP.player.rotationYawHead = entity.rotationYawHead;
                PopESP.player.rotationPitch = entity.rotationPitch;
                PopESP.player.prevRotationPitch = entity.prevRotationPitch;
                PopESP.player.prevRotationYaw = entity.prevRotationYaw;
                PopESP.player.renderYawOffset = entity.renderYawOffset;
                this.startTime = System.currentTimeMillis();
            }
        }
    }
    
    public void onRender3D(final Render3DEvent eventRender3D) {
        if (PopESP.mc.player == null || PopESP.mc.world == null) {
            return;
        }
        PopESP.color = new Color(255, 255, 255, 210);
        PopESP.outlineColor = new Color(255, 255, 255, 255);
        PopESP.opacity = Float.intBitsToFloat(Float.floatToIntBits(1.6358529E38f) ^ 0x7EF622C3);
        PopESP.time = System.currentTimeMillis();
        PopESP.duration = PopESP.time - this.startTime;
        PopESP.startAlpha = 195.0f / Float.intBitsToFloat(Float.floatToIntBits(0.0119778095f) ^ 0x7F3B3E93);
        if (PopESP.player != null) {
            if (PopESP.entity != null) {
                if (PopESP.duration < 25000L) {
                    PopESP.opacity = PopESP.startAlpha - PopESP.duration / 25000.0f;
                }
                if (PopESP.duration < 2000L) {
                    GL11.glPushMatrix();
                    GlStateManager.translate(Float.intBitsToFloat(Float.floatToIntBits(1.240196E38f) ^ 0x7EBA9A9D), PopESP.duration / 1500.0f, Float.intBitsToFloat(Float.floatToIntBits(3.0414126E38f) ^ 0x7F64CF7A));
                }
                PopESP.mc.renderManager.renderEntityStatic((Entity)PopESP.player, Float.intBitsToFloat(Float.floatToIntBits(6.159893f) ^ 0x7F451DD8), false);
                GlStateManager.translate(Float.intBitsToFloat(Float.floatToIntBits(3.0715237E38f) ^ 0x7F671365), Float.intBitsToFloat(Float.floatToIntBits(1.9152719E37f) ^ 0x7D668ADF), Float.intBitsToFloat(Float.floatToIntBits(1.9703683E38f) ^ 0x7F143BEA));
                GL11.glPopMatrix();
            }
        }
    }
}
