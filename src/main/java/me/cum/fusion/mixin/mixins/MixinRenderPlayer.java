
package me.cum.fusion.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.client.*;
import me.cum.fusion.features.modules.render.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import me.cum.fusion.features.modules.client.*;
import me.cum.fusion.util.*;
import java.awt.*;

@Mixin({ RenderPlayer.class })
public class MixinRenderPlayer
{
    @Inject(method = { "renderEntityName" }, at = { @At("HEAD") }, cancellable = true)
    public void renderEntityNameHook(final AbstractClientPlayer entityIn, final double x, final double y, final double z, final String name, final double distanceSq, final CallbackInfo info) {
        if (Nametags.getInstance().isOn()) {
            info.cancel();
        }
    }
    
    @Inject(method = { "renderRightArm" }, at = { @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPlayer;swingProgress:F", opcode = 181) }, cancellable = true)
    public void renderRightArmBegin(final AbstractClientPlayer clientPlayer, final CallbackInfo ci) {
        if (clientPlayer == Minecraft.getMinecraft().player && HandChams.INSTANCE.isEnabled()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            GL11.glEnable(10754);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
            if (HandChams.INSTANCE.rainbow.getValue()) {
                final Color rainbowColor = HandChams.INSTANCE.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color(RenderUtil.getRainbow((int)HandChams.INSTANCE.speed.getValue() * 100, 0, (int)HandChams.INSTANCE.saturation.getValue() / 100.0f, (int)HandChams.INSTANCE.brightness.getValue() / 100.0f));
                GL11.glColor4f(rainbowColor.getRed() / 255.0f, rainbowColor.getGreen() / 255.0f, rainbowColor.getBlue() / 255.0f, (int)HandChams.INSTANCE.alpha.getValue() / 255.0f);
            }
            else {
                final Color color = HandChams.INSTANCE.colorSync.getValue() ? new Color(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getBlue(), Colors.INSTANCE.getCurrentColor().getGreen(), (int)HandChams.INSTANCE.alpha.getValue()) : new Color((int)HandChams.INSTANCE.red.getValue(), (int)HandChams.INSTANCE.green.getValue(), (int)HandChams.INSTANCE.blue.getValue(), (int)HandChams.INSTANCE.alpha.getValue());
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            }
        }
    }
    
    @Inject(method = { "renderRightArm" }, at = { @At("RETURN") }, cancellable = true)
    public void renderRightArmReturn(final AbstractClientPlayer clientPlayer, final CallbackInfo ci) {
        if (clientPlayer == Minecraft.getMinecraft().player && HandChams.INSTANCE.isEnabled()) {
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
        }
    }
    
    @Inject(method = { "renderLeftArm" }, at = { @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPlayer;swingProgress:F", opcode = 181) }, cancellable = true)
    public void renderLeftArmBegin(final AbstractClientPlayer clientPlayer, final CallbackInfo ci) {
        if (clientPlayer == Minecraft.getMinecraft().player && HandChams.INSTANCE.isEnabled()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            GL11.glEnable(10754);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
            if (HandChams.INSTANCE.rainbow.getValue()) {
                final Color rainbowColor = HandChams.INSTANCE.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color(RenderUtil.getRainbow((int)HandChams.INSTANCE.speed.getValue() * 100, 0, (int)HandChams.INSTANCE.saturation.getValue() / 100.0f, (int)HandChams.INSTANCE.brightness.getValue() / 100.0f));
                GL11.glColor4f(rainbowColor.getRed() / 255.0f, rainbowColor.getGreen() / 255.0f, rainbowColor.getBlue() / 255.0f, (int)HandChams.INSTANCE.alpha.getValue() / 255.0f);
            }
            else {
                final Color color = HandChams.INSTANCE.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color(RenderUtil.getRainbow((int)HandChams.INSTANCE.speed.getValue() * 100, 0, (int)HandChams.INSTANCE.saturation.getValue() / 100.0f, (int)HandChams.INSTANCE.brightness.getValue() / 100.0f));
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, (int)HandChams.INSTANCE.alpha.getValue() / 255.0f);
            }
        }
    }
    
    @Inject(method = { "renderLeftArm" }, at = { @At("RETURN") }, cancellable = true)
    public void renderLeftArmReturn(final AbstractClientPlayer clientPlayer, final CallbackInfo ci) {
        if (clientPlayer == Minecraft.getMinecraft().player && HandChams.INSTANCE.isEnabled()) {
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
        }
    }
}
