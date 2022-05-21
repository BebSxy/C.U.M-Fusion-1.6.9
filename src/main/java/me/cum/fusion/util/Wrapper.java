
package me.cum.fusion.util;

import me.cum.fusion.manager.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.world.*;

public class Wrapper
{
    public static FileManager fileManager;
    
    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }
    
    public static EntityPlayerSP getPlayer() {
        return getMinecraft().player;
    }
    
    public static World getWorld() {
        return (World)getMinecraft().world;
    }
    
    public static FileManager getFileManager() {
        if (Wrapper.fileManager == null) {
            Wrapper.fileManager = new FileManager();
        }
        return Wrapper.fileManager;
    }
}
