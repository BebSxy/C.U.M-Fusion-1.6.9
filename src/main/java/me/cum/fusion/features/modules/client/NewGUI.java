
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.client.settings.*;
import java.awt.*;
import me.cum.fusion.util.*;
import me.cum.fusion.features.gui2.*;
import net.minecraft.client.gui.*;
import me.cum.fusion.*;

public class NewGUI extends Module
{
    private static NewGUI INSTANCE;
    public Setting<Boolean> customFov;
    public Setting<Float> fov;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> hoverAlpha;
    public Setting<Integer> topRed;
    public Setting<Integer> topGreen;
    public Setting<Integer> topBlue;
    public Setting<Integer> alpha;
    public Setting<Boolean> rainbow;
    public Setting<rainbowMode> rainbowModeHud;
    public Setting<rainbowModeArray> rainbowModeA;
    public Setting<Integer> rainbowHue;
    public Setting<Float> rainbowBrightness;
    public Setting<Float> rainbowSaturation;
    public Setting<Boolean> colorSync;
    public float hue;
    
    public NewGUI() {
        super("TestGUI", "Opens the TestGUI", Category.CLIENT, true, true, false);
        this.customFov = (Setting<Boolean>)this.register(new Setting("CustomFov", (T)false));
        this.fov = (Setting<Float>)this.register(new Setting("Fov", (T)150.0f, (T)(-180.0f), (T)180.0f));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255));
        this.hoverAlpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)180, (T)0, (T)255));
        this.topRed = (Setting<Integer>)this.register(new Setting("SecondRed", (T)0, (T)0, (T)255));
        this.topGreen = (Setting<Integer>)this.register(new Setting("SecondGreen", (T)0, (T)0, (T)255));
        this.topBlue = (Setting<Integer>)this.register(new Setting("SecondBlue", (T)150, (T)0, (T)255));
        this.alpha = (Setting<Integer>)this.register(new Setting("HoverAlpha", (T)240, (T)0, (T)255));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)false));
        this.rainbowModeHud = (Setting<rainbowMode>)this.register(new Setting("HRainbowMode", (T)rainbowMode.Static, v -> this.rainbow.getValue()));
        this.rainbowModeA = (Setting<rainbowModeArray>)this.register(new Setting("ARainbowMode", (T)rainbowModeArray.Static, v -> this.rainbow.getValue()));
        this.rainbowHue = (Setting<Integer>)this.register(new Setting("Delay", (T)240, (T)0, (T)600, v -> this.rainbow.getValue()));
        this.rainbowBrightness = (Setting<Float>)this.register(new Setting("Brightness ", (T)150.0f, (T)1.0f, (T)255.0f, v -> this.rainbow.getValue()));
        this.rainbowSaturation = (Setting<Float>)this.register(new Setting("Saturation", (T)150.0f, (T)1.0f, (T)255.0f, v -> this.rainbow.getValue()));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", (T)false));
        this.setInstance();
    }
    
    public static NewGUI getInstance() {
        if (NewGUI.INSTANCE == null) {
            NewGUI.INSTANCE = new NewGUI();
        }
        return NewGUI.INSTANCE;
    }
    
    private void setInstance() {
        NewGUI.INSTANCE = this;
    }
    
    @Override
    public void onUpdate() {
        if (this.customFov.getValue()) {
            NewGUI.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, (float)this.fov.getValue());
        }
    }
    
    public Color getCurrentColor() {
        if (this.rainbow.getValue()) {
            return Color.getHSBColor(this.hue, this.rainbowSaturation.getValue().intValue() / 255.0f, this.rainbowBrightness.getValue().intValue() / 255.0f);
        }
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }
    
    public int getCurrentColorHex() {
        if (this.rainbow.getValue()) {
            return Color.HSBtoRGB(this.hue, this.rainbowSaturation.getValue().intValue() / 255.0f, this.rainbowBrightness.getValue().intValue() / 255.0f);
        }
        return ColorUtil.toARGB(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }
    
    @Override
    public void onEnable() {
        NewGUI.mc.displayGuiScreen((GuiScreen)ClickGuiScreen.getClickGui());
    }
    
    @Override
    public void onLoad() {
        Fusion.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
    }
    
    @Override
    public void onTick() {
        if (!(NewGUI.mc.currentScreen instanceof ClickGuiScreen)) {
            this.disable();
        }
    }
    
    static {
        NewGUI.INSTANCE = new NewGUI();
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
