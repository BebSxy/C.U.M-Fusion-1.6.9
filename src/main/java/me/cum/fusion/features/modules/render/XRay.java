
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import me.cum.fusion.features.command.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.block.*;
import java.util.*;

public class XRay extends Module
{
    private static XRay INSTANCE;
    public Setting<String> newBlock;
    public Setting<Boolean> showBlocks;
    
    public XRay() {
        super("XRay", "Lets you look through walls.", Module.Category.RENDER, false, false, true);
        this.newBlock = (Setting<String>)this.register(new Setting("diamond_ore", (T)"Add Block..."));
        this.showBlocks = (Setting<Boolean>)this.register(new Setting("ShowBlocks", (T)false));
        this.setInstance();
    }
    
    public static XRay getInstance() {
        if (XRay.INSTANCE == null) {
            XRay.INSTANCE = new XRay();
        }
        return XRay.INSTANCE;
    }
    
    private void setInstance() {
        XRay.INSTANCE = this;
    }
    
    public void onEnable() {
        XRay.mc.renderGlobal.loadRenderers();
    }
    
    public void onDisable() {
        XRay.mc.renderGlobal.loadRenderers();
    }
    
    @SubscribeEvent
    public void onSettingChange(final ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.newBlock) && !this.shouldRender(this.newBlock.getPlannedValue())) {
                this.register(new Setting(this.newBlock.getPlannedValue(), (T)Boolean.TRUE, v -> this.showBlocks.getValue()));
                Command.sendMessage("<Xray> Added new Block: " + this.newBlock.getPlannedValue());
                if (this.isOn()) {
                    XRay.mc.renderGlobal.loadRenderers();
                }
                event.setCanceled(true);
            }
            else {
                final Setting<? extends Boolean> setting = (Setting<? extends Boolean>)event.getSetting();
                if (setting.equals(this.enabled) || setting.equals(this.drawn) || setting.equals(this.bind) || setting.equals(this.newBlock) || setting.equals(this.showBlocks)) {
                    return;
                }
                if (setting.getValue() != null && !(boolean)setting.getPlannedValue()) {
                    this.unregister((Setting)setting);
                    if (this.isOn()) {
                        XRay.mc.renderGlobal.loadRenderers();
                    }
                    event.setCanceled(true);
                }
            }
        }
    }
    
    public boolean shouldRender(final Block block) {
        return this.shouldRender(block.getLocalizedName());
    }
    
    public boolean shouldRender(final String name) {
        for (final Setting setting : this.getSettings()) {
            if (!name.equalsIgnoreCase(setting.getName())) {
                continue;
            }
            return true;
        }
        return false;
    }
    
    static {
        XRay.INSTANCE = new XRay();
    }
}
