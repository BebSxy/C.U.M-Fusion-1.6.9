
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.command.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import java.nio.*;
import me.cum.fusion.util.*;
import org.lwjgl.opengl.*;
import me.cum.fusion.*;
import java.io.*;

public class Icon extends Module
{
    private static Icon INSTANCE;
    
    public Icon() {
        super("Icon", "Toggle Icon", Category.CLIENT, true, false, false);
        this.setInstance();
    }
    
    public static Icon getInstance() {
        if (Icon.INSTANCE == null) {
            Icon.INSTANCE = new Icon();
        }
        return Icon.INSTANCE;
    }
    
    private void setInstance() {
        Icon.INSTANCE = this;
    }
    
    @Override
    public void onEnable() {
        this.setWindowsIcon();
        Command.sendMessage("Restart your game for this to take effect.");
    }
    
    @Override
    public void onDisable() {
        Icon.mc.setWindowIcon();
    }
    
    public static void setWindowIcon() {
        if (Util.getOSType() != Util.EnumOS.OSX) {
            try (final InputStream inputStream16x = Minecraft.class.getResourceAsStream("/assets/Fusion/icons/icon-16x.png");
                 final InputStream inputStream32x = Minecraft.class.getResourceAsStream("/assets/Fusion/icons/icon-32x.png")) {
                final ByteBuffer[] icons = { IconUtil.INSTANCE.readImageToBuffer(inputStream16x), IconUtil.INSTANCE.readImageToBuffer(inputStream32x) };
                Display.setIcon(icons);
            }
            catch (Exception e) {
                Fusion.LOGGER.error("Couldn't set Windows Icon", (Throwable)e);
            }
        }
    }
    
    private void setWindowsIcon() {
        setWindowIcon();
    }
    
    static {
        Icon.INSTANCE = new Icon();
    }
}
