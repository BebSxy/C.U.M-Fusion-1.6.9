
package me.cum.fusion.features.gui.alts;

import java.io.*;
import me.cum.fusion.mixin.mixins.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import java.util.*;
import java.util.function.*;

public class AltManagerGUI extends GuiScreen
{
    private GuiButton delete;
    private GuiScreen lastGui;
    private AltSlotList altList;
    private GuiTextField crackedNameField;
    
    public AltManagerGUI(final GuiScreen lastGui) {
        this.lastGui = lastGui;
    }
    
    public void initGui() {
        super.initGui();
        (this.crackedNameField = new GuiTextField(69, this.mc.fontRenderer, 4, 20, 95, 15)).setText(this.mc.getSession().getUsername());
        this.crackedNameField.setMaxStringLength(16);
        this.altList = new AltSlotList(this, this.mc, this.width, this.height, 40, this.height - 60, 36);
        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 52, 75, 20, "Add"));
        this.delete = new GuiButton(2, this.width / 2 + 1, this.height - 52, 75, 20, "Delete");
        this.buttonList.add(this.delete);
        this.buttonList.add(new GuiButton(3, this.width / 2 - 75, this.height - 30, 150, 20, "Back"));
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.altList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.mc.fontRenderer, "kisman.cc Alt Manager", this.width / 2, 15, -1);
        final String s = "Signed in as ";
        this.drawString(this.mc.fontRenderer, "Signed in as ", 4, 6, -5592406);
        this.drawString(this.mc.fontRenderer, this.mc.getSession().getUsername(), this.mc.fontRenderer.getStringWidth("Signed in as ") + 3, 6, -1);
        this.crackedNameField.drawTextBox();
        if (!this.crackedNameField.isFocused()) {
            this.crackedNameField.setText(this.mc.getSession().getUsername());
        }
        this.delete.enabled = this.altList.getVisibility().get();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        this.crackedNameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.altList.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    public void handleMouseInput() throws IOException {
        this.altList.handleMouseInput();
        super.handleMouseInput();
    }
    
    public void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.lastGui);
            return;
        }
        this.crackedNameField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 28) {
            ((ISession)this.mc.getSession()).setUsername(this.crackedNameField.getText());
            this.crackedNameField.setFocused(false);
        }
        super.keyTyped(typedChar, keyCode);
    }
    
    protected void actionPerformed(final GuiButton button) throws IOException {
        this.altList.actionPerformed(button);
        switch (button.id) {
            case 1: {
                this.mc.displayGuiScreen((GuiScreen)new AltCreatorGUI((GuiScreen)this));
                break;
            }
            case 2: {
                if (this.altList.getVisibility().get()) {
                    final AltEntry e = this.altList.getAlts().get(this.altList.getSelectedId());
                    this.altList.getAlts().remove(e);
                    AltManager.getAlts().remove(e);
                    break;
                }
                break;
            }
            case 3: {
                this.mc.displayGuiScreen(this.lastGui);
                break;
            }
        }
    }
    
    private static class AltSlotList extends GuiListExtended
    {
        private final List<AltEntry> alts;
        private int selectedId;
        
        public AltSlotList(final AltManagerGUI parentGui, final Minecraft mc, final int width, final int height, final int top, final int bottom, final int slotHeight) {
            super(mc, width, height, top, bottom, slotHeight);
            this.alts = new ArrayList<AltEntry>();
            this.selectedId = -1;
            this.alts.clear();
            AltManager.getAlts().forEach(alt -> this.alts.add(alt));
        }
        
        public AltEntry getListEntry(final int index) {
            return this.alts.get(index);
        }
        
        public int getListWidth() {
            return super.getListWidth() + 50;
        }
        
        protected void elementClicked(final int i, final boolean b, final int i1, final int i2) {
            this.selectElement(i);
        }
        
        protected int getSize() {
            return this.alts.size();
        }
        
        protected int getScrollBarX() {
            return super.getScrollBarX() + 20;
        }
        
        protected boolean isSelected(final int slotIndex) {
            return this.selectedId == slotIndex;
        }
        
        protected Supplier<Boolean> getVisibility() {
            return () -> this.selectedId > -1;
        }
        
        protected List<AltEntry> getAlts() {
            return this.alts;
        }
        
        protected int getSelectedId() {
            return this.selectedId;
        }
        
        private void selectElement(final int element) {
            this.selectedId = element;
            this.showSelectionBox = true;
            this.selectedElement = element;
        }
    }
}
