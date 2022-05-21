
package me.cum.fusion.features.modules.misc;

import me.cum.fusion.features.modules.*;
import java.util.*;
import net.minecraft.entity.player.*;
import me.cum.fusion.features.modules.client.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.command.*;

public class PopCounter extends Module
{
    public static HashMap<String, Integer> TotemPopContainer;
    private static PopCounter INSTANCE;
    
    public PopCounter() {
        super("PopCounter", "Counts other players totem pops.", Category.MISC, true, false, false);
        this.setInstance();
    }
    
    public static PopCounter getInstance() {
        if (PopCounter.INSTANCE == null) {
            PopCounter.INSTANCE = new PopCounter();
        }
        return PopCounter.INSTANCE;
    }
    
    private void setInstance() {
        PopCounter.INSTANCE = this;
    }
    
    @Override
    public void onEnable() {
        PopCounter.TotemPopContainer.clear();
    }
    
    public String death1(final EntityPlayer player) {
        final int l_Count = PopCounter.TotemPopContainer.get(player.getName());
        PopCounter.TotemPopContainer.remove(player.getName());
        if (l_Count == 1) {
            if (!ModuleTools.getInstance().isEnabled()) {
                return HUD.getInstance().getCommandMessage() + ChatFormatting.WHITE + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totem!";
            }
            switch (ModuleTools.getInstance().popNotifier.getValue()) {
                case FUTURE: {
                    return ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.getName() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " totem.";
                }
                case PHOBOS: {
                    return ChatFormatting.GOLD + player.getName() + ChatFormatting.RED + " died after popping " + ChatFormatting.GOLD + l_Count + ChatFormatting.RED + " totem.";
                }
                case DOTGOD: {
                    return ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.LIGHT_PURPLE + " time!";
                }
                case NONE: {
                    return HUD.getInstance().getCommandMessage() + ChatFormatting.WHITE + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totem!";
                }
            }
        }
        else {
            if (!ModuleTools.getInstance().isEnabled()) {
                return HUD.getInstance().getCommandMessage() + ChatFormatting.WHITE + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totems!";
            }
            switch (ModuleTools.getInstance().popNotifier.getValue()) {
                case FUTURE: {
                    return ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.getName() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " totems.";
                }
                case PHOBOS: {
                    return ChatFormatting.GOLD + player.getName() + ChatFormatting.RED + " died after popping " + ChatFormatting.GOLD + l_Count + ChatFormatting.RED + " totems.";
                }
                case DOTGOD: {
                    return ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.LIGHT_PURPLE + " times!";
                }
                case NONE: {
                    return HUD.getInstance().getCommandMessage() + ChatFormatting.WHITE + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totems!";
                }
            }
        }
        return null;
    }
    
    public void onDeath(final EntityPlayer player) {
        if (fullNullCheck()) {
            return;
        }
        if (getInstance().isDisabled()) {
            return;
        }
        if (PopCounter.mc.player.equals((Object)player)) {
            return;
        }
        if (PopCounter.TotemPopContainer.containsKey(player.getName())) {
            Command.sendSilentMessage(this.death1(player));
        }
    }
    
    public String pop(final EntityPlayer player) {
        int l_Count = 1;
        if (PopCounter.TotemPopContainer.containsKey(player.getName())) {
            l_Count = PopCounter.TotemPopContainer.get(player.getName());
            PopCounter.TotemPopContainer.put(player.getName(), ++l_Count);
        }
        else {
            PopCounter.TotemPopContainer.put(player.getName(), l_Count);
        }
        if (l_Count == 1) {
            if (!ModuleTools.getInstance().isEnabled()) {
                return HUD.getInstance().getCommandMessage() + ChatFormatting.WHITE + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totem.";
            }
            switch (ModuleTools.getInstance().popNotifier.getValue()) {
                case FUTURE: {
                    return ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.getName() + ChatFormatting.GRAY + " just popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " totem.";
                }
                case PHOBOS: {
                    return ChatFormatting.GOLD + player.getName() + ChatFormatting.RED + " popped " + ChatFormatting.GOLD + l_Count + ChatFormatting.RED + " totem.";
                }
                case DOTGOD: {
                    return ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.getName() + " has popped " + ChatFormatting.RED + l_Count + ChatFormatting.LIGHT_PURPLE + " time in total!";
                }
                case NONE: {
                    return HUD.getInstance().getCommandMessage() + ChatFormatting.WHITE + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totem.";
                }
            }
        }
        else {
            if (!ModuleTools.getInstance().isEnabled()) {
                return HUD.getInstance().getCommandMessage() + ChatFormatting.WHITE + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totems.";
            }
            switch (ModuleTools.getInstance().popNotifier.getValue()) {
                case FUTURE: {
                    return ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.getName() + ChatFormatting.GRAY + " just popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " totems.";
                }
                case PHOBOS: {
                    return ChatFormatting.GOLD + player.getName() + ChatFormatting.RED + " popped " + ChatFormatting.GOLD + l_Count + ChatFormatting.RED + " totems.";
                }
                case DOTGOD: {
                    return ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.getName() + " has popped " + ChatFormatting.RED + l_Count + ChatFormatting.LIGHT_PURPLE + " times in total!";
                }
                case NONE: {
                    return ChatFormatting.WHITE + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.WHITE + " Totems.";
                }
            }
        }
        return "";
    }
    
    public void onTotemPop(final EntityPlayer player) {
        if (fullNullCheck()) {
            return;
        }
        if (getInstance().isDisabled()) {
            return;
        }
        if (PopCounter.mc.player.equals((Object)player)) {
            return;
        }
        Command.sendSilentMessage(this.pop(player));
    }
    
    static {
        PopCounter.TotemPopContainer = new HashMap<String, Integer>();
        PopCounter.INSTANCE = new PopCounter();
    }
}
