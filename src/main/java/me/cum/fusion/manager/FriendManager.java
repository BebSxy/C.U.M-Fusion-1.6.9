
package me.cum.fusion.manager;

import me.cum.fusion.features.*;
import net.minecraft.entity.player.*;
import me.cum.fusion.features.setting.*;
import java.util.function.*;
import me.cum.fusion.util.*;
import java.util.*;

public class FriendManager extends Feature
{
    private List<Friend> friends;
    
    public FriendManager() {
        super("Friends");
        this.friends = new ArrayList<Friend>();
    }
    
    public boolean isFriend(final String name) {
        this.cleanFriends();
        return this.friends.stream().anyMatch(friend -> friend.username.equalsIgnoreCase(name));
    }
    
    public boolean isFriend(final EntityPlayer player) {
        return this.isFriend(player.getName());
    }
    
    public void addFriend(final String name) {
        final Friend friend = this.getFriendByName(name);
        if (friend != null) {
            this.friends.add(friend);
        }
        this.cleanFriends();
    }
    
    public void removeFriend(final String name) {
        this.cleanFriends();
        for (final Friend friend : this.friends) {
            if (!friend.getUsername().equalsIgnoreCase(name)) {
                continue;
            }
            this.friends.remove(friend);
            break;
        }
    }
    
    public void onLoad() {
        this.friends = new ArrayList<Friend>();
        this.clearSettings();
    }
    
    public void saveFriends() {
        this.clearSettings();
        this.cleanFriends();
        for (final Friend friend : this.friends) {
            this.register(new Setting(friend.getUuid().toString(), (Object)friend.getUsername()));
        }
    }
    
    public void cleanFriends() {
        this.friends.stream().filter(Objects::nonNull).filter(friend -> friend.getUsername() != null);
    }
    
    public List<Friend> getFriends() {
        this.cleanFriends();
        return this.friends;
    }
    
    public Friend getFriendByName(final String input) {
        final UUID uuid = PlayerUtil.getUUIDFromName(input);
        if (uuid != null) {
            return new Friend(input, uuid);
        }
        return null;
    }
    
    public void addFriend(final Friend friend) {
        this.friends.add(friend);
    }
    
    public static class Friend
    {
        private final String username;
        private final UUID uuid;
        
        public Friend(final String username, final UUID uuid) {
            this.username = username;
            this.uuid = uuid;
        }
        
        public String getUsername() {
            return this.username;
        }
        
        public UUID getUuid() {
            return this.uuid;
        }
    }
}
