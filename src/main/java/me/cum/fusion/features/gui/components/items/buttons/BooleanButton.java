
package me.cum.fusion.features.gui.components.items.buttons;

import me.cum.fusion.features.setting.*;
import me.cum.fusion.features.modules.client.*;
import java.awt.*;
import me.cum.fusion.util.*;
import me.cum.fusion.*;
import me.cum.fusion.features.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;

public class BooleanButton extends Button
{
    private final Setting setting;
    int color;
    
    public BooleanButton(final Setting setting) {
        super(setting.getName());
        this.color = new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().blue.getValue(), ClickGui.getInstance().green.getValue()).getRGB();
        this.setting = setting;
        this.width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height, new Color(30, 30, 30, 180).hashCode());
        Fusion.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - NewGui2.getClickGui2().getTextOffset(), this.getState() ? -1 : -5592406);
    }
    
    @Override
    public void update() {
        this.setHidden(this.setting.isVisible());
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            BooleanButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public void toggle() {
        this.setting.setValue(!this.setting.getValue());
    }
    
    @Override
    public boolean getState() {
        return this.setting.getValue();
    }
}
