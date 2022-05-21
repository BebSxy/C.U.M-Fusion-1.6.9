
package me.cum.fusion.features.gui.alts;

import net.minecraft.client.gui.*;
import java.io.*;

public class AltCreatorGUI extends GuiScreen
{
    private GuiScreen lastGui;
    private GuiTextField passField;
    private GuiTextField emailField;
    
    public AltCreatorGUI(final GuiScreen lastGui) {
        this.lastGui = lastGui;
    }
    
    public void initGui() {
        this.emailField = new GuiTextField(1, this.mc.fontRenderer, this.width / 2 - 100, this.height / 2 - 100, 200, 15);
        this.passField = new GuiTextField(2, this.mc.fontRenderer, this.width / 2 - 100, this.height / 2 - 80, 200, 15);
        this.buttonList.add(new GuiButton(3, this.width / 2 - 51, this.height / 2 - 60, 50, 20, "Add"));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 1, this.height / 2 - 60, 50, 20, "Cancel"));
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.mc.fontRenderer, "Add Alt Account", this.width / 2, 10, -1);
        this.emailField.drawTextBox();
        this.passField.drawTextBox();
        if (this.emailField.getText().equals("") && !this.emailField.isFocused()) {
            this.fontRenderer.drawStringWithShadow("Email", (float)(this.width / 2 - 97), (float)(this.height / 2 - 96), -6710887);
        }
        if (this.passField.getText().equals("") && !this.passField.isFocused()) {
            this.fontRenderer.drawStringWithShadow("Password", (float)(this.width / 2 - 97), (float)(this.height / 2 - 76), -6710887);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    public void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.lastGui);
            return;
        }
        this.emailField.textboxKeyTyped(typedChar, keyCode);
        this.passField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 28) {
            this.emailField.setFocused(false);
            this.passField.setFocused(false);
        }
        super.keyTyped(typedChar, keyCode);
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        this.emailField.mouseClicked(mouseX, mouseY, mouseButton);
        this.passField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    protected void actionPerformed(final GuiButton button) throws IOException {
        switch (button.id) {
            case 3: {
                final AltEntry alt = new AltEntry(this.emailField.getText(), this.passField.getText());
                AltManager.getAlts().add(alt);
                this.mc.displayGuiScreen(this.lastGui);
                break;
            }
            case 4: {
                this.mc.displayGuiScreen(this.lastGui);
                break;
            }
        }
    }
}
