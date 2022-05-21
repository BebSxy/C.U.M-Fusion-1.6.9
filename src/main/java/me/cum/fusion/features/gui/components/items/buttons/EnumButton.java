
package me.cum.fusion.features.gui.components.items.buttons;

import me.cum.fusion.features.setting.*;
import java.awt.*;
import me.cum.fusion.util.*;
import me.cum.fusion.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;

public class EnumButton extends Button
{
    public Setting setting;
    
    public EnumButton(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height, new Color(30, 30, 30, 180).hashCode());
        Fusion.textManager.drawStringWithShadow(this.setting.getName() + " " + ChatFormatting.RED + (this.setting.currentEnumName().equalsIgnoreCase("ABC") ? "ABC" : this.setting.currentEnumName()), this.x + 2.3f, this.y - 1.7f - NewGui2.getClickGui2().getTextOffset(), this.getState() ? -1 : -5592406);
    }
    
    public void update() {
        this.setHidden(this.setting.isVisible());
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            EnumButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    public int getHeight() {
        return 14;
    }
    
    public void toggle() {
        this.setting.increaseEnum();
    }
    
    public boolean getState() {
        return true;
    }
}
