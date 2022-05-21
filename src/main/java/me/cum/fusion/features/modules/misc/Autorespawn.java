
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraftforge.client.event.*;
import net.minecraft.client.gui.*;
import me.cum.fusion.features.command.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class Autorespawn extends Module
{
    public Setting<Boolean> antiDeathScreen;
    public Setting<Boolean> deathCoords;
    public Setting<Boolean> respawn;
    
    public Autorespawn() {
        super("AutoRespawn", "Respawns you when you die.", Category.MISC, true, false, false);
        this.antiDeathScreen = (Setting<Boolean>)this.register(new Setting("AntiDeathScreen", (T)true));
        this.deathCoords = (Setting<Boolean>)this.register(new Setting("DeathCoords", (T)false));
        this.respawn = (Setting<Boolean>)this.register(new Setting("Respawn", (T)true));
    }
    
    @SubscribeEvent
    public void onDisplayDeathScreen(final GuiOpenEvent event) {
        if (event.getGui() instanceof GuiGameOver) {
            if (this.deathCoords.getValue() && event.getGui() instanceof GuiGameOver) {
                Command.sendMessage(String.format("You died at x %d y %d z %d", (int)Autorespawn.mc.player.posX, (int)Autorespawn.mc.player.posY, (int)Autorespawn.mc.player.posZ));
            }
            if ((this.respawn.getValue() && Autorespawn.mc.player.getHealth() <= 0.0f) || (this.antiDeathScreen.getValue() && Autorespawn.mc.player.getHealth() > 0.0f)) {
                event.setCanceled(true);
                Autorespawn.mc.player.respawnPlayer();
            }
        }
    }
}
