
package me.cum.fusion.features.command.commands;

import me.cum.fusion.features.command.*;
import me.cum.fusion.*;
import me.cum.fusion.manager.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.modules.misc.*;
import me.cum.fusion.util.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import java.util.*;

public class FriendCommand extends Command
{
    public FriendCommand() {
        super("friend", new String[] { "<add/del/name/clear>", "<name>" });
    }
    
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            if (Fusion.friendManager.getFriends().isEmpty()) {
                sendMessage("Friend list empty D:.");
            }
            else {
                final StringBuilder f = new StringBuilder("Friends: ");
                for (final FriendManager.Friend friend : Fusion.friendManager.getFriends()) {
                    try {
                        f.append(friend.getUsername()).append(", ");
                    }
                    catch (Exception ex) {}
                }
                sendMessage(f.toString());
            }
            return;
        }
        if (commands.length != 2) {
            if (commands.length >= 2) {
                final String s = commands[0];
                switch (s) {
                    case "add": {
                        Fusion.friendManager.addFriend(commands[1]);
                        sendMessage(ChatFormatting.GREEN + commands[1] + " has been friended");
                        if (FriendSettings.getInstance().notify.getValue()) {
                            Util.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("/w " + commands[1] + " I just added you to my friends list on C.U.M Fusion!"));
                        }
                    }
                    case "del": {
                        Fusion.friendManager.removeFriend(commands[1]);
                        if (FriendSettings.getInstance().notify.getValue()) {
                            Util.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("/w " + commands[1] + " I just removed you from my friends list on C.U.M Fusion!!"));
                        }
                        sendMessage(ChatFormatting.RED + commands[1] + " has been unfriended");
                    }
                    default: {
                        sendMessage("Unknown Command, try friend add/del (name)");
                        break;
                    }
                }
            }
            return;
        }
        if ("reset".equals(commands[0])) {
            Fusion.friendManager.onLoad();
            sendMessage("Friends got reset.");
            return;
        }
        sendMessage(commands[0] + (Fusion.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
    }
}
