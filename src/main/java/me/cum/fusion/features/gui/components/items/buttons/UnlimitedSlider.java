
package me.cum.fusion.features.gui.components.items.buttons;

import me.cum.fusion.features.setting.*;
import me.cum.fusion.*;
import me.cum.fusion.features.modules.client.*;
import me.cum.fusion.util.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;

public class UnlimitedSlider extends Button
{
    public Setting setting;
    
    public UnlimitedSlider(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.isHovering(mouseX, mouseY) ? Fusion.colorManager.getColorWithAlpha(Fusion.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue()) : Fusion.colorManager.getColorWithAlpha(Fusion.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()));
        Fusion.textManager.drawStringWithShadow(" - " + this.setting.getName() + " " + ChatFormatting.RED + this.setting.getValue() + ChatFormatting.WHITE + " +", this.x + 2.3f, this.y - 1.7f - NewGui2.getClickGui2().getTextOffset(), this.getState() ? -1 : -5592406);
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            UnlimitedSlider.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.isRight(mouseX)) {
                if (this.setting.getValue() instanceof Double) {
                    this.setting.setValue(this.setting.getValue() + 1.0);
                }
                else if (this.setting.getValue() instanceof Float) {
                    this.setting.setValue(this.setting.getValue() + 1.0f);
                }
                else if (this.setting.getValue() instanceof Integer) {
                    this.setting.setValue(this.setting.getValue() + 1);
                }
            }
            else if (this.setting.getValue() instanceof Double) {
                this.setting.setValue(this.setting.getValue() - 1.0);
            }
            else if (this.setting.getValue() instanceof Float) {
                this.setting.setValue(this.setting.getValue() - 1.0f);
            }
            else if (this.setting.getValue() instanceof Integer) {
                this.setting.setValue(this.setting.getValue() - 1);
            }
        }
    }
    
    public void update() {
        this.setHidden(this.setting.isVisible());
    }
    
    public int getHeight() {
        return 14;
    }
    
    public void toggle() {
    }
    
    public boolean getState() {
        return true;
    }
    
    public boolean isRight(final int x) {
        return x > this.x + (this.width + 7.4f) / 2.0f;
    }
}
