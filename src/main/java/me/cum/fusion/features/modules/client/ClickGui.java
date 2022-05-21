
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.features.gui.*;
import net.minecraft.client.settings.*;
import java.awt.*;
import me.cum.fusion.util.*;
import me.cum.fusion.event.events.*;
import me.cum.fusion.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.command.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.client.gui.*;

public class ClickGui extends Module
{
    private static ClickGui INSTANCE;
    public Setting<String> prefix;
    public Setting<Boolean> customFov;
    public Setting<Boolean> colorSync;
    public Setting<Boolean> dark;
    public Setting<Boolean> snowing;
    public Setting<Boolean> gradiant;
    public Setting<Boolean> rainbowRolling;
    public Setting<Boolean> outline;
    public Setting<Float> fov;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> hoverAlpha;
    public Setting<Integer> alpha;
    public Setting<Boolean> rainbow;
    public Setting<rainbowMode> rainbowModeHud;
    public Setting<rainbowModeArray> rainbowModeA;
    public Setting<Integer> rainbowHue;
    public Setting<Float> rainbowBrightness;
    public Setting<Float> rainbowSaturation;
    public float hue;
    private NewGui2 click;
    
    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Category.CLIENT, true, false, false);
        this.prefix = (Setting<String>)this.register(new Setting("Prefix", (T)"."));
        this.customFov = (Setting<Boolean>)this.register(new Setting("CustomFov", (T)false));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", (T)false));
        this.dark = (Setting<Boolean>)this.register(new Setting("Darken", (T)false));
        this.snowing = (Setting<Boolean>)this.register(new Setting("Snowing", (T)true));
        this.gradiant = (Setting<Boolean>)this.register(new Setting("gradiant", (T)true));
        this.rainbowRolling = (Setting<Boolean>)this.register(new Setting("Rolling rainbow", (T)true));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", (T)true));
        this.fov = (Setting<Float>)this.register(new Setting("Fov", (T)150.0f, (T)(-180.0f), (T)180.0f));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)253, (T)0, (T)255));
        this.hoverAlpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)136, (T)0, (T)255));
        this.alpha = (Setting<Integer>)this.register(new Setting("HoverAlpha", (T)241, (T)0, (T)255));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)true));
        this.rainbowModeHud = (Setting<rainbowMode>)this.register(new Setting("HRainbowMode", (T)rainbowMode.Static, v -> this.rainbow.getValue()));
        this.rainbowModeA = (Setting<rainbowModeArray>)this.register(new Setting("ARainbowMode", (T)rainbowModeArray.Static, v -> this.rainbow.getValue()));
        this.rainbowHue = (Setting<Integer>)this.register(new Setting("Delay", (T)7, (T)0, (T)600, v -> this.rainbow.getValue()));
        this.rainbowBrightness = (Setting<Float>)this.register(new Setting("Brightness ", (T)150.0f, (T)1.0f, (T)255.0f, v -> this.rainbow.getValue()));
        this.rainbowSaturation = (Setting<Float>)this.register(new Setting("Saturation", (T)150.0f, (T)1.0f, (T)255.0f, v -> this.rainbow.getValue()));
        this.setInstance();
    }
    
    public static ClickGui getInstance() {
        if (ClickGui.INSTANCE == null) {
            ClickGui.INSTANCE = new ClickGui();
        }
        return ClickGui.INSTANCE;
    }
    
    private void setInstance() {
        ClickGui.INSTANCE = this;
    }
    
    @Override
    public void onUpdate() {
        if (this.customFov.getValue()) {
            ClickGui.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, (float)this.fov.getValue());
        }
    }
    
    public int getCurrentColorHex() {
        if (this.rainbow.getValue()) {
            return Color.HSBtoRGB(this.hue, this.rainbowSaturation.getValue().intValue() / 255.0f, this.rainbowBrightness.getValue().intValue() / 255.0f);
        }
        return ColorUtil.toARGB(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }
    
    public Color getCurrentColor() {
        if (this.rainbow.getValue()) {
            return Color.getHSBColor(this.hue, this.rainbowSaturation.getValue().intValue() / 255.0f, this.rainbowBrightness.getValue().intValue() / 255.0f);
        }
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }
    
    @SubscribeEvent
    public void onSettingChange(final ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                Fusion.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + Fusion.commandManager.getPrefix());
            }
            Fusion.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.hoverAlpha.getPlannedValue());
        }
    }
    
    @Override
    public void onEnable() {
        ClickGui.mc.displayGuiScreen((GuiScreen)NewGui2.getClickGui2());
    }
    
    @Override
    public void onLoad() {
        if (this.colorSync.getValue()) {
            Fusion.colorManager.setColor(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), this.hoverAlpha.getValue());
        }
        else {
            Fusion.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
            Fusion.commandManager.setPrefix(this.prefix.getValue());
        }
    }
    
    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof NewGui2)) {
            this.disable();
            if (ClickGui.mc.entityRenderer.getShaderGroup() != null) {
                ClickGui.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
        }
    }
    
    @Override
    public void onDisable() {
        if (ClickGui.mc.currentScreen instanceof NewGui2) {
            ClickGui.mc.displayGuiScreen((GuiScreen)null);
        }
    }
    
    static {
        ClickGui.INSTANCE = new ClickGui();
    }
    
    public enum rainbowModeArray
    {
        Static, 
        Up;
    }
    
    public enum rainbowMode
    {
        Static, 
        Sideway;
    }
}
