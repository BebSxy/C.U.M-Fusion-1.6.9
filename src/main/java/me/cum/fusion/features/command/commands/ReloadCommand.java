
package me.cum.fusion.features.command.commands;

import me.cum.fusion.features.command.*;
import me.cum.fusion.*;

public class ReloadCommand extends Command
{
    public ReloadCommand() {
        super("reload", new String[0]);
    }
    
    public void execute(final String[] commands) {
        Fusion.reload();
    }
}
