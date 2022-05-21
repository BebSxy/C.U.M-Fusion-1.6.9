
package me.cum.fusion.mixin;

import net.minecraftforge.fml.relauncher.*;
import me.cum.fusion.*;
import org.spongepowered.asm.launch.*;
import org.spongepowered.asm.mixin.*;
import java.util.*;

public class CumLoader implements IFMLLoadingPlugin
{
    public CumLoader() {
        Fusion.LOGGER.info("\n\nLoading mixins by JPDA123");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.cum.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        Fusion.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }
    
    public String[] getASMTransformerClass() {
        return new String[0];
    }
    
    public String getModContainerClass() {
        return null;
    }
    
    public String getSetupClass() {
        return null;
    }
    
    public void injectData(final Map<String, Object> data) {
        data.get("runtimeDeobfuscationEnabled");
    }
    
    public String getAccessTransformerClass() {
        return null;
    }
}
