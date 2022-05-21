
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import net.minecraft.client.network.*;
import net.minecraft.scoreboard.*;
import me.cum.fusion.*;
import com.mojang.realmsclient.gui.*;

public class TabFriends extends Module
{
    public static TabFriends INSTANCE;
    
    public TabFriends() {
        super("TabFriends", "TabModify", Category.CLIENT, true, false, false);
        TabFriends.INSTANCE = this;
    }
    
    public static String getPlayerName(final NetworkPlayerInfo networkPlayerInfoIn) {
        final String string;
        final String dname = string = ((networkPlayerInfoIn.getDisplayName() != null) ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName((Team)networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName()));
        return Fusion.friendManager.isFriend(dname) ? (ChatFormatting.BOLD + ChatFormatting.AQUA.toString() + dname) : dname;
    }
}
