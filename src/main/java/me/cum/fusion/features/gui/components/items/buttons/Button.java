
package me.cum.fusion.features.gui.components.items.buttons;

import me.cum.fusion.features.gui.components.items.*;
import me.cum.fusion.*;
import java.awt.*;
import me.cum.fusion.features.gui.*;
import me.cum.fusion.util.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import me.cum.fusion.features.gui.components.*;
import java.util.*;

public class Button extends Item
{
    private boolean state;
    
    public Button(final String name) {
        super(name);
        this.height = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (Fusion.moduleManager.isModuleEnabled(this.getName())) {
            RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height + 0.5f, new Color(0, 206, 236, 244).hashCode());
            Fusion.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - NewGui2.getClickGui2().getTextOffset(), new Color(255, 255, 255, 255).hashCode());
        }
        else {
            if (this.isHovering(mouseX, mouseY)) {
                RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height + 0.5f, new Color(30, 30, 30, 127).hashCode());
            }
            else {
                RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height + 0.5f, new Color(30, 30, 30, 100).hashCode());
            }
            Fusion.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - NewGui2.getClickGui2().getTextOffset(), new Color(255, 255, 255).hashCode());
        }
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }
    
    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        Util.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
    
    public void toggle() {
    }
    
    public boolean getState() {
        return this.state;
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    public boolean isHovering(final int mouseX, final int mouseY) {
        for (final Component component : NewGui2.getClickGui2().getComponents()) {
            if (!component.drag) {
                continue;
            }
            return false;
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
}
