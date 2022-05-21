
package me.cum.fusion.features.gui2.components.items.buttons;

import me.cum.fusion.features.setting.*;
import me.cum.fusion.*;
import me.cum.fusion.features.modules.client.*;
import me.cum.fusion.util.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.gui2.*;
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
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? Fusion.colorManager.getColorWithAlpha(Fusion.moduleManager.getModuleByClass(NewGUI.class).alpha.getValue()) : Fusion.colorManager.getColorWithAlpha(Fusion.moduleManager.getModuleByClass(NewGUI.class).hoverAlpha.getValue())) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
        Fusion.textManager.drawStringWithShadow(this.setting.getName() + " " + ChatFormatting.GRAY + (this.setting.currentEnumName().equalsIgnoreCase("ABC") ? "ABC" : this.setting.currentEnumName()), this.x + 2.3f, this.y - 1.7f - ClickGuiScreen.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }
    
    public void update() {
        this.setHidden(!this.setting.isVisible());
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
