
package me.cum.fusion.mixin.mixins;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import me.cum.fusion.features.modules.render.*;
import net.minecraft.client.renderer.*;
import me.cum.fusion.event.events.*;
import org.lwjgl.opengl.*;
import me.cum.fusion.features.modules.client.*;
import java.awt.*;
import me.cum.fusion.util.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ RenderEnderCrystal.class })
public class MixinRenderEnderCrystal
{
    @Shadow
    @Final
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;
    private static final ResourceLocation glint;
    
    @Redirect(method = { "doRender" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(final ModelBase model, final Entity entity, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        if (Chams.INSTANCE.isEnabled()) {
            GlStateManager.scale((float)Chams.INSTANCE.scale.getValue(), (float)Chams.INSTANCE.scale.getValue(), (float)Chams.INSTANCE.scale.getValue());
        }
        if (Chams.INSTANCE.isEnabled() && (boolean)Chams.INSTANCE.wireframe.getValue()) {
            final RenderEntityModelEvent event = new RenderEntityModelEvent(0, model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            Chams.INSTANCE.onRenderModel(event);
        }
        if (Chams.INSTANCE.isEnabled() && (boolean)Chams.INSTANCE.chams.getValue()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            if (Chams.INSTANCE.rainbow.getValue()) {
                final Color rainbowColor1 = Chams.INSTANCE.rainbow.getValue() ? ColorUtil.rainbow((int)ClickGui.getInstance().rainbowHue.getValue()) : new Color(RenderUtil.getRainbow(20000, 0, 100.0f, 100.0f));
                final Color rainbowColor2 = EntityUtil.getColor(entity, rainbowColor1.getRed(), rainbowColor1.getGreen(), rainbowColor1.getBlue(), (int)Chams.INSTANCE.alpha.getValue(), true);
                if (Chams.INSTANCE.throughWalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(rainbowColor2.getRed() / 255.0f, rainbowColor2.getGreen() / 255.0f, rainbowColor2.getBlue() / 255.0f, (int)Chams.INSTANCE.alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (Chams.INSTANCE.throughWalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
            }
            else if ((boolean)Chams.INSTANCE.xqz.getValue() && (boolean)Chams.INSTANCE.throughWalls.getValue()) {
                final Color hiddenColor = EntityUtil.getColor(entity, (int)Chams.INSTANCE.hiddenRed.getValue(), (int)Chams.INSTANCE.hiddenGreen.getValue(), (int)Chams.INSTANCE.hiddenBlue.getValue(), (int)Chams.INSTANCE.hiddenAlpha.getValue(), true);
                final Color color;
                final Color visibleColor = color = EntityUtil.getColor(entity, (int)Chams.INSTANCE.red.getValue(), (int)Chams.INSTANCE.green.getValue(), (int)Chams.INSTANCE.blue.getValue(), (int)Chams.INSTANCE.alpha.getValue(), true);
                if (Chams.INSTANCE.throughWalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(hiddenColor.getRed() / 255.0f, hiddenColor.getGreen() / 255.0f, hiddenColor.getBlue() / 255.0f, (int)Chams.INSTANCE.alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (Chams.INSTANCE.throughWalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
                GL11.glColor4f(visibleColor.getRed() / 255.0f, visibleColor.getGreen() / 255.0f, visibleColor.getBlue() / 255.0f, (int)Chams.INSTANCE.alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
            else {
                final Color color2;
                final Color visibleColor = color2 = (Chams.INSTANCE.rainbow.getValue() ? ColorUtil.rainbow((int)ClickGui.getInstance().rainbowHue.getValue()) : EntityUtil.getColor(entity, (int)Chams.INSTANCE.red.getValue(), (int)Chams.INSTANCE.green.getValue(), (int)Chams.INSTANCE.blue.getValue(), (int)Chams.INSTANCE.alpha.getValue(), true));
                if (Chams.INSTANCE.throughWalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(visibleColor.getRed() / 255.0f, visibleColor.getGreen() / 255.0f, visibleColor.getBlue() / 255.0f, (int)Chams.INSTANCE.alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (Chams.INSTANCE.throughWalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
            }
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
            if (Chams.INSTANCE.glint.getValue()) {
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GlStateManager.enableAlpha();
                GlStateManager.color(1.0f, 0.0f, 0.0f, 0.13f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GlStateManager.disableAlpha();
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
            }
        }
        else {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        if (Chams.INSTANCE.isEnabled()) {
            GlStateManager.scale((float)Chams.INSTANCE.scale.getValue(), (float)Chams.INSTANCE.scale.getValue(), (float)Chams.INSTANCE.scale.getValue());
        }
    }
    
    static {
        glint = new ResourceLocation("textures/glint");
    }
}
