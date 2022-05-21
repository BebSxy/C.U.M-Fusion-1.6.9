
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;

public class MainMenu extends Module
{
    private static MainMenu INSTANCE;
    public Setting<Boolean> mainScreen;
    public Setting<Boolean> particles;
    
    public MainMenu() {
        super("MainMenuScreen", "Toggles MainMenuScreen", Category.CLIENT, true, false, false);
        this.mainScreen = (Setting<Boolean>)this.register(new Setting("MainScreen", (T)true));
        this.particles = (Setting<Boolean>)this.register(new Setting("Particles", (T)true));
        this.setInstance();
    }
    
    public static MainMenu getInstance() {
        if (MainMenu.INSTANCE == null) {
            MainMenu.INSTANCE = new MainMenu();
        }
        return MainMenu.INSTANCE;
    }
    
    private void setInstance() {
        MainMenu.INSTANCE = this;
    }
    
    static {
        MainMenu.INSTANCE = new MainMenu();
    }
}
