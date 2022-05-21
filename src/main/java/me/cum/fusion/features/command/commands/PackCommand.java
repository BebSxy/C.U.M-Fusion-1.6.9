
package me.cum.fusion.features.command.commands;

import me.cum.fusion.features.command.*;
import java.awt.*;
import java.io.*;

public class PackCommand extends Command
{
    public PackCommand() {
        super("packs", new String[0]);
    }
    
    public void execute(final String[] commands) {
        try {
            Desktop.getDesktop().open(new File("resourcepacks/"));
            Command.sendMessage("Opened packs folder!");
        }
        catch (IOException e) {
            Command.sendMessage("Could not open packs folder!");
            e.printStackTrace();
        }
    }
}
