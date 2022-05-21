
package me.cum.fusion.features.modules;

import me.cum.fusion.features.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.features.modules.combat.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.modules.client.*;
import net.minecraft.util.text.*;
import net.minecraftforge.common.*;
import me.cum.fusion.event.events.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.cum.fusion.*;
import me.cum.fusion.features.command.*;

public abstract class Module extends Feature
{
    private static Module INSTANCE;
    private final String description;
    private final Category category;
    public Setting<Boolean> enabled;
    public Setting<Boolean> drawn;
    public Setting<Bind> bind;
    public Setting<String> displayName;
    public Setting<Object> colorSync;
    public boolean hasListener;
    public boolean alwaysListening;
    public boolean hidden;
    public float arrayListOffset;
    public float arrayListVOffset;
    public float offset;
    public float vOffset;
    public boolean sliding;
    protected AutoCrystal.switchTimer timer;
    
    public Module(final String name, final String description, final Category category, final boolean hasListener, final boolean hidden, final boolean alwaysListening) {
        super(name);
        this.enabled = (Setting<Boolean>)this.register(new Setting("Enabled", (T)false));
        this.drawn = (Setting<Boolean>)this.register(new Setting("Drawn", (T)true));
        this.bind = (Setting<Bind>)this.register(new Setting("Keybind", (T)new Bind(-1)));
        this.arrayListOffset = 0.0f;
        this.arrayListVOffset = 0.0f;
        this.displayName = (Setting<String>)this.register(new Setting("DisplayName", (T)name));
        this.description = description;
        this.category = category;
        this.hasListener = hasListener;
        this.hidden = hidden;
        this.alwaysListening = alwaysListening;
    }
    
    public boolean isSliding() {
        return this.sliding;
    }
    
    public void onEnable() {
    }
    
    public void onDisable() {
    }
    
    public void onToggle() {
    }
    
    public void onLoad() {
    }
    
    public void onTick() {
    }
    
    public void onThread() {
    }
    
    public void onLogin() {
    }
    
    public void onLogout() {
    }
    
    public void onUpdate() {
    }
    
    public void onRender2D(final Render2DEvent event) {
    }
    
    public void onRender3D(final Render3DEvent event) {
    }
    
    public void onUnload() {
    }
    
    public String getDisplayInfo() {
        return null;
    }
    
    public boolean isOn() {
        return this.enabled.getValue();
    }
    
    public boolean isOff() {
        return !this.enabled.getValue();
    }
    
    public void setEnabled(final boolean enabled) {
        if (enabled) {
            this.enable();
        }
        else {
            this.disable();
        }
    }
    
    public TextComponentString getNotifierOn() {
        if (ModuleTools.getInstance().isEnabled()) {
            switch (ModuleTools.getInstance().notifier.getValue()) {
                case FUTURE: {
                    return new TextComponentString(ChatFormatting.RED + "[Future] " + ChatFormatting.GRAY + this.getDisplayName() + " toggled " + ChatFormatting.GREEN + "on" + ChatFormatting.GRAY + ".");
                }
                case DOTGOD: {
                    return new TextComponentString(ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.DARK_AQUA + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.GREEN + "enabled.");
                }
                case PHOBOS: {
                    return new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.GREEN + " enabled.");
                }
                case TROLLGOD: {
                    return new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.DARK_PURPLE + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.GREEN + "enabled.");
                }
            }
        }
        return new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.GREEN + this.getDisplayName() + " toggled on.");
    }
    
    public TextComponentString getNotifierOff() {
        if (ModuleTools.getInstance().isEnabled()) {
            switch (ModuleTools.getInstance().notifier.getValue()) {
                case FUTURE: {
                    return new TextComponentString(ChatFormatting.RED + "[Future] " + ChatFormatting.GRAY + this.getDisplayName() + " toggled " + ChatFormatting.RED + "off" + ChatFormatting.GRAY + ".");
                }
                case DOTGOD: {
                    return new TextComponentString(ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.DARK_AQUA + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.RED + "disabled.");
                }
                case PHOBOS: {
                    return new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.RED + " disabled.");
                }
                case TROLLGOD: {
                    return new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.DARK_PURPLE + this.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.RED + "disabled.");
                }
            }
        }
        return new TextComponentString(HUD.getInstance().getCommandMessage() + ChatFormatting.RED + this.getDisplayName() + " toggled off.");
    }
    
    public void enable() {
        this.enabled.setValue(Boolean.TRUE);
        this.onToggle();
        this.onEnable();
        if (HUD.getInstance().notifyToggles.getValue()) {
            Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)this.getNotifierOn(), 1);
            if (this.isOn() && this.hasListener && !this.alwaysListening) {
                MinecraftForge.EVENT_BUS.register((Object)this);
            }
        }
    }
    
    public void disable() {
        if (this.hasListener && !this.alwaysListening) {
            MinecraftForge.EVENT_BUS.unregister((Object)this);
        }
        this.enabled.setValue(false);
        if (HUD.getInstance().notifyToggles.getValue()) {
            Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)this.getNotifierOff(), 1);
        }
        this.onToggle();
        this.onDisable();
    }
    
    public void toggle() {
        final ClientEvent event = new ClientEvent((int)(this.isEnabled() ? 0 : 1), (Feature)this);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (!event.isCanceled()) {
            this.setEnabled(!this.isEnabled());
        }
    }
    
    public String getDisplayName() {
        return this.displayName.getValue();
    }
    
    public void setDisplayName(final String name) {
        final Module module = Fusion.moduleManager.getModuleByDisplayName(name);
        final Module originalModule = Fusion.moduleManager.getModuleByName(name);
        if (module == null && originalModule == null) {
            Command.sendMessage(this.getDisplayName() + ", name: " + this.getName() + ", has been renamed to: " + name);
            this.displayName.setValue(name);
            return;
        }
        Command.sendMessage(ChatFormatting.RED + "A module of this name already exists.");
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public boolean isDrawn() {
        return this.drawn.getValue();
    }
    
    public void setDrawn(final boolean drawn) {
        this.drawn.setValue(drawn);
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public String getInfo() {
        return null;
    }
    
    public Bind getBind() {
        return this.bind.getValue();
    }
    
    public void setBind(final int key) {
        this.bind.setValue(new Bind(key));
    }
    
    public boolean listening() {
        return (this.hasListener && this.isOn()) || this.alwaysListening;
    }
    
    public String getFullArrayString() {
        return this.getDisplayName() + ChatFormatting.GRAY + ((this.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + this.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
    }
    
    public void update() {
    }
    
    public enum Category
    {
        COMBAT("Combat"), 
        MISC("Misc"), 
        RENDER("Render"), 
        MOVEMENT("Movement"), 
        PLAYER("Player"), 
        CLIENT("Client"), 
        TROLL("Troll");
        
        private final String name;
        
        private Category(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}
