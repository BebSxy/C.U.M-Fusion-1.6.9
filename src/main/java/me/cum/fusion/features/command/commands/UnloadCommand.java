
package me.cum.fusion.features.command.commands;

import me.cum.fusion.features.command.*;
import me.cum.fusion.*;

public class UnloadCommand extends Command
{
    public UnloadCommand() {
        super("unload", new String[0]);
    }
    
    public void execute(final String[] commands) {
        Fusion.unload(true);
    }
}
