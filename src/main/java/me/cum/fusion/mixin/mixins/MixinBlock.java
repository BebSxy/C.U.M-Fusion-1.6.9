
package me.cum.fusion.mixin.mixins;

import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.world.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.cum.fusion.features.modules.render.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ Block.class })
public abstract class MixinBlock
{
    @Shadow
    @Deprecated
    public abstract float getBlockHardness(final IBlockState p0, final World p1, final BlockPos p2);
    
    @Inject(method = { "isFullCube" }, at = { @At("HEAD") }, cancellable = true)
    public void isFullCubeHook(final IBlockState blockState, final CallbackInfoReturnable<Boolean> info) {
        try {
            if (XRay.getInstance().isOn()) {
                info.setReturnValue((Object)XRay.getInstance().shouldRender((Block)Block.class.cast(this)));
                info.cancel();
            }
        }
        catch (Exception ex) {}
    }
}
