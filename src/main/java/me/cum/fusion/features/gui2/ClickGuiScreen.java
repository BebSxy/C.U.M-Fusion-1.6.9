
package me.cum.fusion.features.gui2;

import net.minecraft.client.gui.*;
import me.cum.fusion.features.gui2.components.*;
import me.cum.fusion.*;
import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.gui2.components.items.buttons.*;
import me.cum.fusion.features.*;
import java.util.function.*;
import me.cum.fusion.features.gui2.components.items.*;
import java.util.*;
import org.lwjgl.input.*;
import java.io.*;

public class ClickGuiScreen extends GuiScreen
{
    private static ClickGuiScreen INSTANCE;
    private final ArrayList<Component> components;
    
    public ClickGuiScreen() {
        this.components = new ArrayList<Component>();
        this.setInstance();
        this.load();
    }
    
    public static ClickGuiScreen getInstance() {
        if (ClickGuiScreen.INSTANCE == null) {
            ClickGuiScreen.INSTANCE = new ClickGuiScreen();
        }
        return ClickGuiScreen.INSTANCE;
    }
    
    public static ClickGuiScreen getClickGui() {
        return getInstance();
    }
    
    private void setInstance() {
        ClickGuiScreen.INSTANCE = this;
    }
    
    private void load() {
        int x = -84;
        for (final Module.Category category : Fusion.moduleManager.getCategories()) {
            final ArrayList<Component> components2 = this.components;
            final String name = category.getName();
            x += 90;
            components2.add(new Component(name, x, 4, true) {
                @Override
                public void setupItems() {
                    ClickGuiScreen$1.counter1 = new int[] { 1 };
                    Fusion.moduleManager.getModulesByCategory(category).forEach(module -> {
                        if (!module.hidden) {
                            this.addButton(new ModuleButton(module));
                        }
                    });
                }
            });
        }
        this.components.forEach(components -> components.getItems().sort(Comparator.comparing((Function<? super Item, ? extends Comparable>)Feature::getName)));
    }
    
    public void updateModule(final Module module) {
        for (final Component component : this.components) {
            for (final Item item : component.getItems()) {
                if (!(item instanceof ModuleButton)) {
                    continue;
                }
                final ModuleButton button = (ModuleButton)item;
                final Module mod = button.getModule();
                if (module == null) {
                    continue;
                }
                if (!module.equals(mod)) {
                    continue;
                }
                button.initSettings();
            }
        }
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.checkMouseWheel();
        this.drawDefaultBackground();
        this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int clickedButton) {
        this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int releaseButton) {
        this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }
    
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    public final ArrayList<Component> getComponents() {
        return this.components;
    }
    
    public void checkMouseWheel() {
        final int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            this.components.forEach(component -> component.setY(component.getY() - 10));
        }
        else if (dWheel > 0) {
            this.components.forEach(component -> component.setY(component.getY() + 10));
        }
    }
    
    public int getTextOffset() {
        return -6;
    }
    
    public Component getComponentByName(final String name) {
        for (final Component component : this.components) {
            if (component.getName().equalsIgnoreCase(name)) {
                return component;
            }
        }
        return null;
    }
    
    public void keyTyped(final char typedChar, final int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }
    
    static {
        ClickGuiScreen.INSTANCE = new ClickGuiScreen();
    }
}
