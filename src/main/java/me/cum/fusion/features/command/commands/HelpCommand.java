
package me.cum.fusion.features.command.commands;

import me.cum.fusion.features.command.*;
import me.cum.fusion.*;
import com.mojang.realmsclient.gui.*;
import java.util.*;

public class HelpCommand extends Command
{
    public HelpCommand() {
        super("help");
    }
    
    public void execute(final String[] commands) {
        sendMessage("Commands: ");
        for (final Command command : Fusion.commandManager.getCommands()) {
            sendMessage(ChatFormatting.GRAY + Fusion.commandManager.getPrefix() + command.getName());
        }
    }
}
