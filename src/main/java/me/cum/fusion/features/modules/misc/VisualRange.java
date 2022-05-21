
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.entity.player.*;
import me.cum.fusion.*;
import me.cum.fusion.features.command.*;
import net.minecraft.init.*;
import me.cum.fusion.util.*;
import net.minecraft.entity.*;
import java.util.*;

public class VisualRange extends Module
{
    public Setting<Boolean> VisualRangeSound;
    public Setting<Boolean> coords;
    public Setting<Boolean> leaving;
    private List<EntityPlayer> knownPlayers;
    
    public VisualRange() {
        super("VisualRange", "Visual range", Category.CLIENT, true, false, false);
        this.VisualRangeSound = (Setting<Boolean>)this.register(new Setting("Sound", (T)true));
        this.coords = (Setting<Boolean>)this.register(new Setting("Coords", (T)true));
        this.leaving = (Setting<Boolean>)this.register(new Setting("leaving", (T)true));
        this.knownPlayers = new ArrayList<EntityPlayer>();
    }
    
    @Override
    public void onEnable() {
        final List<String> people = new ArrayList<String>();
        this.knownPlayers = new ArrayList<EntityPlayer>();
    }
    
    @Override
    public void onUpdate() {
        final ArrayList<EntityPlayer> tickPlayerList = new ArrayList<EntityPlayer>(VisualRange.mc.world.playerEntities);
        if (tickPlayerList.size() > 0) {
            for (final EntityPlayer player : tickPlayerList) {
                if (!player.getName().equals(VisualRange.mc.player.getName())) {
                    if (this.knownPlayers.contains(player)) {
                        continue;
                    }
                    this.knownPlayers.add(player);
                    if (Fusion.friendManager.isFriend(player)) {
                        Command.sendMessage("Player §a" + player.getName() + "§r entered your visual range" + (this.coords.getValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"));
                    }
                    else {
                        Command.sendMessage("Player §c" + player.getName() + "§r entered your visual range" + (this.coords.getValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"));
                    }
                    if (this.VisualRangeSound.getValue()) {
                        VisualRange.mc.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                    }
                    return;
                }
            }
        }
        if (this.knownPlayers.size() > 0) {
            for (final EntityPlayer player : this.knownPlayers) {
                if (tickPlayerList.contains(player)) {
                    continue;
                }
                this.knownPlayers.remove(player);
                if (this.leaving.getValue()) {
                    if (Fusion.friendManager.isFriend(player)) {
                        Command.sendMessage("Player §a" + player.getName() + "§r left your visual range" + (this.coords.getValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"));
                    }
                    else {
                        Command.sendMessage("Player §c" + player.getName() + "§r left your visual range" + (this.coords.getValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"));
                    }
                }
                return;
            }
        }
        if (Util.mc.world == null | Util.mc.player == null) {
            return;
        }
        final List<String> peoplenew = new ArrayList<String>();
        final List<EntityPlayer> playerEntities = (List<EntityPlayer>)Util.mc.world.playerEntities;
        for (final Entity e : playerEntities) {
            if (e.getName().equals(Util.mc.player.getName())) {
                continue;
            }
            peoplenew.add(e.getName());
        }
    }
}
