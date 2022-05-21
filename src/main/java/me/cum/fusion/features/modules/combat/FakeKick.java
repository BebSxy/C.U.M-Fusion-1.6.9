
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.client.network.*;
import net.minecraft.client.*;
import java.util.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.*;

public class FakeKick extends Module
{
    private final Setting<Boolean> healthDisplay;
    
    public FakeKick() {
        super("FakeKick", "Log with the press of a button", Category.COMBAT, true, false, false);
        this.healthDisplay = (Setting<Boolean>)this.register(new Setting("HealthDisplay", (T)false));
    }
    
    @Override
    public void onEnable() {
        if (this.healthDisplay.getValue()) {
            final float health = FakeKick.mc.player.getAbsorptionAmount() + FakeKick.mc.player.getHealth();
            Objects.requireNonNull(Minecraft.getMinecraft().getConnection()).handleDisconnect(new SPacketDisconnect((ITextComponent)new TextComponentString("Logged out with " + health + " health remaining.")));
            this.disable();
        }
        Objects.requireNonNull(Minecraft.getMinecraft().getConnection()).handleDisconnect(new SPacketDisconnect((ITextComponent)new TextComponentString("Internal Exception: java.lang.NullPointerException")));
        this.disable();
    }
}
