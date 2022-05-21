
package me.cum.fusion.features.gui2.components.items.buttons;

import me.cum.fusion.features.gui2.components.items.*;
import me.cum.fusion.*;
import me.cum.fusion.features.modules.client.*;
import me.cum.fusion.util.*;
import me.cum.fusion.features.gui2.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import me.cum.fusion.features.gui2.components.*;
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
        RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? Fusion.colorManager.getColorWithAlpha(Fusion.moduleManager.getModuleByClass(NewGUI.class).alpha.getValue()) : Fusion.colorManager.getColorWithAlpha(Fusion.moduleManager.getModuleByClass(NewGUI.class).hoverAlpha.getValue())) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
        Fusion.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - ClickGuiScreen.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
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
        Button.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
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
        for (final Component component : ClickGuiScreen.getClickGui().getComponents()) {
            if (!component.drag) {
                continue;
            }
            return false;
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
}
