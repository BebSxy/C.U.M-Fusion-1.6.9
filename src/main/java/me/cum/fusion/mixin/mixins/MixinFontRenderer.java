
package me.cum.fusion.mixin.mixins;

import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.*;
import me.cum.fusion.features.modules.client.*;
import net.minecraft.client.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ FontRenderer.class })
public abstract class MixinFontRenderer
{
    @Shadow
    protected abstract int renderString(final String p0, final float p1, final float p2, final int p3, final boolean p4);
    
    @Shadow
    protected abstract void renderStringAtPos(final String p0, final boolean p1);
    
    @Redirect(method = { "renderString(Ljava/lang/String;FFIZ)I" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderStringAtPos(Ljava/lang/String;Z)V"))
    public void renderStringAtPosHook(final FontRenderer renderer, final String text, final boolean shadow) {
        if (NickHider.getInstance().isOn()) {
            this.renderStringAtPos(text.replace(Minecraft.getMinecraft().getSession().getUsername(), NickHider.getInstance().NameString.getValueAsString()), shadow);
        }
        else {
            this.renderStringAtPos(text, shadow);
        }
    }
}
