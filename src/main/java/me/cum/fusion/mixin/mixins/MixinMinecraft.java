
package me.cum.fusion.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.crash.*;
import org.spongepowered.asm.mixin.injection.*;
import org.lwjgl.input.*;
import me.cum.fusion.event.events.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.cum.fusion.*;

@Mixin({ Minecraft.class })
public abstract class MixinMinecraft
{
    @Inject(method = { "shutdownMinecraftApplet" }, at = { @At("HEAD") })
    private void stopClient(final CallbackInfo callbackInfo) {
        this.unload();
    }
    
    @Redirect(method = { "run" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReport(final Minecraft minecraft, final CrashReport crashReport) {
        this.unload();
    }
    
    @Inject(method = { "runTickKeyboard" }, at = { @At(value = "INVOKE", remap = false, target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0, shift = At.Shift.BEFORE) })
    private void onKeyboard(final CallbackInfo callbackInfo) {
        final int n;
        final int i = n = ((Keyboard.getEventKey() == 0) ? (Keyboard.getEventCharacter() + '\u0100') : Keyboard.getEventKey());
        if (Keyboard.getEventKeyState()) {
            final KeyEvent event = new KeyEvent(i);
            MinecraftForge.EVENT_BUS.post((Event)event);
        }
    }
    
    private void unload() {
        Fusion.LOGGER.info("Initiated client shutdown.");
        Fusion.onUnload();
        Fusion.LOGGER.info("Finished client shutdown.");
    }
}
