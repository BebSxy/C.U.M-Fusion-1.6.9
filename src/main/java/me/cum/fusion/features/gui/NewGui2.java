
package me.cum.fusion.features.gui;

import me.cum.fusion.features.gui.components.*;
import me.cum.fusion.features.gui.effect.*;
import me.cum.fusion.*;
import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.gui.components.items.buttons.*;
import java.util.*;
import me.cum.fusion.features.gui.components.items.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.gui.*;
import java.awt.*;
import me.cum.fusion.features.modules.client.*;
import org.lwjgl.input.*;
import java.io.*;

public class NewGui2 extends GuiScreen
{
    private static NewGui2 NewGui2;
    private static NewGui2 INSTANCE;
    private final ArrayList<Component> components;
    private ArrayList<Snow> _snowList;
    public ParticleSystem particleSystem;
    
    public NewGui2() {
        this.components = new ArrayList<Component>();
        this._snowList = new ArrayList<Snow>();
        this.setInstance();
        this.load();
    }
    
    public static NewGui2 getInstance() {
        if (me.cum.fusion.features.gui.NewGui2.INSTANCE == null) {
            me.cum.fusion.features.gui.NewGui2.INSTANCE = new NewGui2();
        }
        return me.cum.fusion.features.gui.NewGui2.INSTANCE;
    }
    
    public static NewGui2 getClickGui2() {
        return getInstance();
    }
    
    private void setInstance() {
        me.cum.fusion.features.gui.NewGui2.INSTANCE = this;
    }
    
    private void load() {
        final Random random = new Random();
        for (int i = 0; i < 100; ++i) {
            for (int y = 0; y < 3; ++y) {
                final Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2) + 1);
                this._snowList.add(snow);
            }
        }
        int x = -80;
        for (final Module.Category category : Fusion.moduleManager.getCategories()) {
            final ArrayList<Component> components2 = this.components;
            final String name = category.getName();
            x += 100;
            components2.add(new Component(name, x, 15, true) {
                public void setupItems() {
                    Fusion.moduleManager.getModulesByCategory(category).forEach(module -> {
                        if (!module.hidden) {
                            this.addButton((Button)new ModuleButton(module));
                        }
                    });
                }
            });
        }
        this.components.forEach(components -> components.getItems().sort((item1, item2) -> item1.getName().compareTo(item2.getName())));
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
                break;
            }
        }
    }
    
    public static void drawCompleteImage(final int posX, final int posY, final int width, final int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, (float)height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f((float)width, (float)height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f((float)width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (ClickGui.getInstance().dark.getValue()) {
            this.drawDefaultBackground();
        }
        final ScaledResolution res = new ScaledResolution(this.mc);
        this.checkMouseWheel();
        this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
        final ScaledResolution sr = new ScaledResolution(this.mc);
        if (ClickGui.getInstance().gradiant.getValue()) {
            this.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, ((boolean)ClickGui.getInstance().colorSync.getValue()) ? Colors.INSTANCE.getCurrentColor().getRGB() : new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue(), ClickGui.getInstance().alpha.getValue() / 2).getRGB());
        }
        if (!this._snowList.isEmpty() && ClickGui.getInstance().snowing.getValue()) {
            this._snowList.forEach(snow -> snow.Update(res));
        }
        if (this.particleSystem != null && Particles.getInstance().isEnabled()) {
            this.particleSystem.render(mouseX, mouseY);
        }
        else {
            this.particleSystem = new ParticleSystem(new ScaledResolution(this.mc));
        }
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
            if (!component.getName().equalsIgnoreCase(name)) {
                continue;
            }
            return component;
        }
        return null;
    }
    
    public void keyTyped(final char typedChar, final int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }
    
    public void updateScreen() {
        if (this.particleSystem != null) {
            this.particleSystem.update();
        }
    }
    
    static {
        me.cum.fusion.features.gui.NewGui2.INSTANCE = new NewGui2();
    }
}
