
package me.cum.fusion.features.command.commands;

import me.cum.fusion.features.command.*;
import java.awt.*;
import java.io.*;

public class OpenFolderCommand extends Command
{
    public OpenFolderCommand() {
        super("openfolder", new String[0]);
    }
    
    public void execute(final String[] commands) {
        try {
            Desktop.getDesktop().open(new File("fusion/"));
            Command.sendMessage("Opened config folder!");
        }
        catch (IOException e) {
            Command.sendMessage("Could not open config folder!");
            e.printStackTrace();
        }
    }
}
