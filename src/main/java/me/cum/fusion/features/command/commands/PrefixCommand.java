
package me.cum.fusion.features.command.commands;

import me.cum.fusion.features.command.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.*;

public class PrefixCommand extends Command
{
    public PrefixCommand() {
        super("prefix", new String[] { "<char>" });
    }
    
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(ChatFormatting.GREEN + "Current prefix is " + Fusion.commandManager.getPrefix());
            return;
        }
        Fusion.commandManager.setPrefix(commands[0]);
        Command.sendMessage("Prefix changed to " + ChatFormatting.GRAY + commands[0]);
    }
}
