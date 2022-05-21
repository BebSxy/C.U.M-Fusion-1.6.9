
package me.cum.fusion.features.command.commands;

import me.cum.fusion.features.command.*;
import me.cum.fusion.*;
import me.cum.fusion.features.modules.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.setting.*;
import com.google.gson.*;
import me.cum.fusion.manager.*;
import me.cum.fusion.features.*;
import java.util.*;

public class ModuleCommand extends Command
{
    public ModuleCommand() {
        super("module", new String[] { "<module>", "<set/reset>", "<setting>", "<value>" });
    }
    
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            sendMessage("Modules: ");
            for (final Module.Category category : Fusion.moduleManager.getCategories()) {
                final StringBuilder modules = new StringBuilder(category.getName() + ": ");
                for (final Module module1 : Fusion.moduleManager.getModulesByCategory(category)) {
                    modules.append(module1.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED).append(module1.getName()).append(ChatFormatting.WHITE).append(", ");
                }
                sendMessage(modules.toString());
            }
            return;
        }
        Module module2 = Fusion.moduleManager.getModuleByDisplayName(commands[0]);
        if (module2 == null) {
            module2 = Fusion.moduleManager.getModuleByName(commands[0]);
            if (module2 == null) {
                sendMessage("This module doesnt exist.");
                return;
            }
            sendMessage(" This is the original name of the module. Its current name is: " + module2.getDisplayName());
        }
        else {
            if (commands.length == 2) {
                sendMessage(module2.getDisplayName() + " : " + module2.getDescription());
                for (final Setting setting2 : module2.getSettings()) {
                    sendMessage(setting2.getName() + " : " + setting2.getValue() + ", " + setting2.getDescription());
                }
                return;
            }
            if (commands.length == 3) {
                if (commands[1].equalsIgnoreCase("set")) {
                    sendMessage("Please specify a setting.");
                }
                else if (commands[1].equalsIgnoreCase("reset")) {
                    for (final Setting setting3 : module2.getSettings()) {
                        setting3.setValue(setting3.getDefaultValue());
                    }
                }
                else {
                    sendMessage("This command doesnt exist.");
                }
                return;
            }
            if (commands.length == 4) {
                sendMessage("Please specify a value.");
                return;
            }
            final Setting setting4;
            if (commands.length == 5 && (setting4 = module2.getSettingByName(commands[2])) != null) {
                final JsonParser jp = new JsonParser();
                if (setting4.getType().equalsIgnoreCase("String")) {
                    setting4.setValue(commands[3]);
                    sendMessage(ChatFormatting.DARK_GRAY + module2.getName() + " " + setting4.getName() + " has been set to " + commands[3] + ".");
                    return;
                }
                try {
                    if (setting4.getName().equalsIgnoreCase("Enabled")) {
                        if (commands[3].equalsIgnoreCase("true")) {
                            module2.enable();
                        }
                        if (commands[3].equalsIgnoreCase("false")) {
                            module2.disable();
                        }
                    }
                    ConfigManager.setValueFromJson(module2, setting4, jp.parse(commands[3]));
                }
                catch (Exception e) {
                    sendMessage("Bad Value! This setting requires a: " + setting4.getType() + " value.");
                    return;
                }
                sendMessage(ChatFormatting.GRAY + module2.getName() + " " + setting4.getName() + " has been set to " + commands[3] + ".");
            }
        }
    }
}
