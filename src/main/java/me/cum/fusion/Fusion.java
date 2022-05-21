
package me.cum.fusion;

import net.minecraftforge.fml.common.*;
import me.cum.fusion.manager.*;
import me.cum.fusion.features.gui.custom.*;
import me.cum.fusion.features.modules.client.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import java.nio.*;
import me.cum.fusion.util.*;
import org.lwjgl.opengl.*;
import java.io.*;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.*;

@Mod(modid = "cum", name = "C.U.M Fusion", version = "1.6.9")
public class Fusion
{
    public static final String MODID = "cum";
    public static final String MODNAME = "C.U.M Fusion";
    public static final String MODVER = "1.6.9";
    public static final Logger LOGGER;
    public static TotemPopManager totemPopManager;
    public static TimerManager timerManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static PacketManager packetManager;
    public static ColorManager colorManager;
    public static HoleManager holeManager;
    public static InventoryManager inventoryManager;
    public static NotificationManager notificationManager;
    public static PotionManager potionManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static SpeedManager speedManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    public static RotationManager2 rotationManagerNew;
    public static ThreadManager threadManager;
    public static GuiCustomMainScreen customMainScreen;
    @Mod.Instance
    public static Fusion INSTANCE;
    private static boolean unloaded;
    
    public static void load() {
        Fusion.LOGGER.info("\n\nLoading C.U.M Fusion");
        Fusion.unloaded = false;
        if (Fusion.reloadManager != null) {
            Fusion.reloadManager.unload();
            Fusion.reloadManager = null;
        }
        Fusion.totemPopManager = new TotemPopManager();
        Fusion.timerManager = new TimerManager();
        Fusion.textManager = new TextManager();
        Fusion.commandManager = new CommandManager();
        Fusion.friendManager = new FriendManager();
        Fusion.moduleManager = new ModuleManager();
        Fusion.rotationManager = new RotationManager();
        Fusion.packetManager = new PacketManager();
        Fusion.eventManager = new EventManager();
        Fusion.speedManager = new SpeedManager();
        Fusion.potionManager = new PotionManager();
        Fusion.inventoryManager = new InventoryManager();
        Fusion.notificationManager = new NotificationManager();
        Fusion.serverManager = new ServerManager();
        Fusion.fileManager = new FileManager();
        Fusion.colorManager = new ColorManager();
        Fusion.positionManager = new PositionManager();
        Fusion.configManager = new ConfigManager();
        Fusion.holeManager = new HoleManager();
        Fusion.rotationManagerNew = new RotationManager2();
        Fusion.threadManager = new ThreadManager();
        Fusion.LOGGER.info("Managers loaded.");
        Fusion.moduleManager.init();
        Fusion.LOGGER.info("Modules loaded.");
        Fusion.configManager.init();
        Fusion.eventManager.init();
        Fusion.LOGGER.info("EventManager loaded.");
        Fusion.textManager.init(true);
        Fusion.moduleManager.onLoad();
        if (Fusion.moduleManager.getModuleByClass(RPC.class).isEnabled()) {
            DiscordPresence.start();
        }
        Fusion.LOGGER.info("C.U.M Fusion successfully loaded!\n");
    }
    
    public static void unload(final boolean unload) {
        Fusion.LOGGER.info("\n\nUnloading C.U.M Fusion");
        if (unload) {
            (Fusion.reloadManager = new ReloadManager()).init((Fusion.commandManager != null) ? Fusion.commandManager.getPrefix() : ".");
        }
        onUnload();
        Fusion.timerManager = null;
        Fusion.eventManager = null;
        Fusion.friendManager = null;
        Fusion.speedManager = null;
        Fusion.holeManager = null;
        Fusion.positionManager = null;
        Fusion.rotationManager = null;
        Fusion.configManager = null;
        Fusion.commandManager = null;
        Fusion.colorManager = null;
        Fusion.serverManager = null;
        Fusion.fileManager = null;
        Fusion.potionManager = null;
        Fusion.inventoryManager = null;
        Fusion.notificationManager = null;
        Fusion.moduleManager = null;
        Fusion.rotationManagerNew = null;
        Fusion.textManager = null;
        Fusion.threadManager = null;
        Fusion.LOGGER.info("C.U.M Fusion unloaded!\n");
    }
    
    public static void reload() {
        unload(false);
        load();
    }
    
    public static void onUnload() {
        if (!Fusion.unloaded) {
            Fusion.eventManager.onUnload();
            Fusion.moduleManager.onUnload();
            Fusion.configManager.saveConfig(Fusion.configManager.config.replaceFirst("fusion/", ""));
            Fusion.moduleManager.onUnloadPost();
            Fusion.unloaded = true;
        }
    }
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        Fusion.LOGGER.info("Cum at ass of Joseph");
        Fusion.LOGGER.info("Perry phobos 1.3.1 so shit client, cum fusion on top).");
        Fusion.LOGGER.info("Fuck yu");
    }
    
    public static void setWindowIcon() {
        if (Util.getOSType() != Util.EnumOS.OSX) {
            try (final InputStream inputStream16x = Minecraft.class.getResourceAsStream("/assets/Fusion/icons/icon-16x.png");
                 final InputStream inputStream32x = Minecraft.class.getResourceAsStream("/assets/Fusion/icons/icon-32x.png")) {
                final ByteBuffer[] icons = { IconUtil.INSTANCE.readImageToBuffer(inputStream16x), IconUtil.INSTANCE.readImageToBuffer(inputStream32x) };
                Display.setIcon((ByteBuffer[])icons);
            }
            catch (Exception e) {
                Fusion.LOGGER.error("Couldn't set Windows Icon", (Throwable)e);
            }
        }
    }
    
    private void setWindowsIcon() {
        setWindowIcon();
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        Fusion.customMainScreen = new GuiCustomMainScreen();
        Display.setTitle("C.U.M Fusion v1.6.9");
        load();
    }
    
    static {
        LOGGER = LogManager.getLogger("fusion");
        Fusion.unloaded = false;
    }
}
