
package me.cum.fusion.manager;

import me.cum.fusion.features.*;
import me.cum.fusion.features.command.*;
import me.cum.fusion.features.command.commands.*;
import com.mojang.realmsclient.gui.*;
import java.util.*;

public class CommandManager extends Feature
{
    private final ArrayList<Command> commands;
    private String clientMessage;
    private String prefix;
    
    public CommandManager() {
        super("Command");
        this.commands = new ArrayList<Command>();
        this.clientMessage = "<Fusion>";
        this.prefix = ".";
        this.commands.add((Command)new HistoryCommand());
        this.commands.add((Command)new BindCommand());
        this.commands.add((Command)new ModuleCommand());
        this.commands.add((Command)new PrefixCommand());
        this.commands.add((Command)new ConfigCommand());
        this.commands.add((Command)new FriendCommand());
        this.commands.add((Command)new HelpCommand());
        this.commands.add((Command)new ReloadCommand());
        this.commands.add((Command)new UnloadCommand());
        this.commands.add((Command)new ReloadSoundCommand());
        this.commands.add((Command)new QueueCommand());
        this.commands.add((Command)new RpsCommand());
        this.commands.add((Command)new OpenFolderCommand());
        this.commands.add((Command)new CoordsCommand());
        this.commands.add((Command)new PackCommand());
    }
    
    public static String[] removeElement(final String[] input, final int indexToDelete) {
        final LinkedList<String> result = new LinkedList<String>();
        for (int i = 0; i < input.length; ++i) {
            if (i != indexToDelete) {
                result.add(input[i]);
            }
        }
        return result.toArray(input);
    }
    
    private static String strip(final String str, final String key) {
        if (str.startsWith(key) && str.endsWith(key)) {
            return str.substring(key.length(), str.length() - key.length());
        }
        return str;
    }
    
    public void executeCommand(final String command) {
        final String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        final String name = parts[0].substring(1);
        final String[] args = removeElement(parts, 0);
        for (int i = 0; i < args.length; ++i) {
            if (args[i] != null) {
                args[i] = strip(args[i], "\"");
            }
        }
        for (final Command c : this.commands) {
            if (!c.getName().equalsIgnoreCase(name)) {
                continue;
            }
            c.execute(parts);
            return;
        }
        Command.sendMessage(ChatFormatting.GRAY + "Command not found, type 'help' for the commands list.");
    }
    
    public Command getCommandByName(final String name) {
        for (final Command command : this.commands) {
            if (!command.getName().equals(name)) {
                continue;
            }
            return command;
        }
        return null;
    }
    
    public ArrayList<Command> getCommands() {
        return this.commands;
    }
    
    public String getClientMessage() {
        return this.clientMessage;
    }
    
    public void setClientMessage(final String clientMessage) {
        this.clientMessage = clientMessage;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
}
