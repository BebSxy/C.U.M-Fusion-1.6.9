
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import java.util.*;
import me.cum.fusion.event.events.*;
import me.cum.fusion.features.modules.client.*;
import me.cum.fusion.util.*;
import java.awt.*;

public class BurrowESP extends Module
{
    private static BurrowESP INSTANCE;
    public Setting<Integer> range;
    public Setting<Boolean> self;
    public Setting<Boolean> text;
    public Setting<String> textString;
    public Setting<Boolean> rainbow;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> alpha;
    public Setting<Integer> outlineAlpha;
    private final List<BlockPos> posList;
    private final RenderUtil renderUtil;
    
    public BurrowESP() {
        super("BurrowESP", "BURROWESP", Module.Category.RENDER, true, false, false);
        this.range = (Setting<Integer>)this.register(new Setting("Range", (T)20, (T)5, (T)50));
        this.self = (Setting<Boolean>)this.register(new Setting("Self", (T)true));
        this.text = (Setting<Boolean>)this.register(new Setting("Text", (T)true));
        this.textString = (Setting<String>)this.register(new Setting("TextString", (T)"BURROW", v -> this.text.getValue()));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)false));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue()));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)0, (T)0, (T)255));
        this.outlineAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", (T)0, (T)0, (T)255));
        this.posList = new ArrayList<BlockPos>();
        this.renderUtil = new RenderUtil();
        this.setInstance();
    }
    
    public static BurrowESP getInstance() {
        if (BurrowESP.INSTANCE == null) {
            BurrowESP.INSTANCE = new BurrowESP();
        }
        return BurrowESP.INSTANCE;
    }
    
    private void setInstance() {
        BurrowESP.INSTANCE = this;
    }
    
    public void onTick() {
        this.posList.clear();
        for (final EntityPlayer player : BurrowESP.mc.world.playerEntities) {
            final BlockPos blockPos = new BlockPos(Math.floor(player.posX), Math.floor(player.posY + 0.2), Math.floor(player.posZ));
            if ((BurrowESP.mc.world.getBlockState(blockPos).getBlock() == Blocks.ENDER_CHEST || BurrowESP.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && blockPos.distanceSq(BurrowESP.mc.player.posX, BurrowESP.mc.player.posY, BurrowESP.mc.player.posZ) <= this.range.getValue() && (blockPos.distanceSq(BurrowESP.mc.player.posX, BurrowESP.mc.player.posY, BurrowESP.mc.player.posZ) > 1.5 || this.self.getValue())) {
                this.posList.add(blockPos);
            }
        }
    }
    
    public void onRender3D(final Render3DEvent event) {
        for (final BlockPos blockPos : this.posList) {
            final String s = this.textString.getValue().toUpperCase();
            if (this.text.getValue()) {
                this.renderUtil.drawText(blockPos, s, ((boolean)this.rainbow.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.outlineAlpha.getValue()));
            }
            RenderUtil.drawBoxESP(blockPos, ((boolean)this.rainbow.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.outlineAlpha.getValue()), 1.5f, true, true, this.alpha.getValue());
        }
    }
    
    static {
        BurrowESP.INSTANCE = new BurrowESP();
    }
}
