
package me.cum.fusion.features.gui2.components.items.buttons;

import me.cum.fusion.features.modules.client.*;
import me.cum.fusion.*;
import me.cum.fusion.util.*;
import me.cum.fusion.features.gui2.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import me.cum.fusion.features.setting.*;

public class BindButton extends Button
{
    private final Setting setting;
    public boolean isListening;
    
    public BindButton(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        final int color = ColorUtil.toARGB(NewGUI.getInstance().red.getValue(), NewGUI.getInstance().green.getValue(), NewGUI.getInstance().blue.getValue(), 255);
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077) : (this.isHovering(mouseX, mouseY) ? Fusion.colorManager.getColorWithAlpha(Fusion.moduleManager.getModuleByClass(NewGUI.class).alpha.getValue()) : Fusion.colorManager.getColorWithAlpha(Fusion.moduleManager.getModuleByClass(NewGUI.class).hoverAlpha.getValue())));
        if (this.isListening) {
            Fusion.textManager.drawStringWithShadow("Press a Key...", this.x + 2.3f, this.y - 1.7f - ClickGuiScreen.getClickGui().getTextOffset(), -1);
        }
        else {
            Fusion.textManager.drawStringWithShadow(this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue().toString().toUpperCase(), this.x + 2.3f, this.y - 1.7f - ClickGuiScreen.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }
    
    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            BindButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    @Override
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            this.setting.setValue(bind);
            this.onMouseClick();
        }
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }
    
    @Override
    public boolean getState() {
        return !this.isListening;
    }
}
