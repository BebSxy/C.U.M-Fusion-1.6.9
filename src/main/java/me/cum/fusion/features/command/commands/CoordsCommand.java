
package me.cum.fusion.features.command.commands;

import me.cum.fusion.features.command.*;
import java.text.*;
import java.awt.*;
import java.awt.datatransfer.*;

public class CoordsCommand extends Command
{
    public CoordsCommand() {
        super("coords", new String[0]);
    }
    
    public void execute(final String[] commands) {
        final DecimalFormat format = new DecimalFormat("#");
        final StringSelection contents = new StringSelection(format.format(CoordsCommand.mc.player.posX) + ",  " + format.format(CoordsCommand.mc.player.posY) + ",  " + format.format(CoordsCommand.mc.player.posZ));
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(contents, null);
        Command.sendMessage("Saved Coordinates To You Clipboard!.");
    }
}
