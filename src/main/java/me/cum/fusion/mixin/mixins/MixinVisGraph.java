
package me.cum.fusion.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.chunk.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.cum.fusion.features.modules.render.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ VisGraph.class })
public class MixinVisGraph
{
    @Inject(method = { "setOpaqueCube" }, at = { @At("HEAD") }, cancellable = true)
    public void setOpaqueCubeHook(final BlockPos pos, final CallbackInfo info) {
        try {
            if (XRay.getInstance().isOn()) {
                info.cancel();
            }
        }
        catch (Exception ex) {}
    }
}
