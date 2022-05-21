
package me.cum.fusion.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ Session.class })
public interface ISession
{
    @Accessor("username")
    void setUsername(final String p0);
}
