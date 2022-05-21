
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraft.util.math.*;
import net.minecraft.init.*;
import me.cum.fusion.features.modules.client.*;
import java.awt.*;
import me.cum.fusion.util.*;

public class HoleESP extends Module
{
    public Setting<Boolean> renderOwn;
    public Setting<Boolean> fov;
    public Setting<Boolean> rainbow;
    private final Setting<Integer> range;
    private final Setting<Integer> rangeY;
    public Setting<Boolean> box;
    public Setting<Boolean> gradientBox;
    public Setting<Boolean> invertGradientBox;
    public Setting<Boolean> outline;
    public Setting<Boolean> gradientOutline;
    public Setting<Boolean> invertGradientOutline;
    public Setting<Double> height;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    private final Setting<Integer> boxAlpha;
    private final Setting<Float> lineWidth;
    public Setting<Boolean> safeColor;
    public Setting<Boolean> doubleHoles;
    private final Setting<Integer> safeRed;
    private final Setting<Integer> safeGreen;
    private final Setting<Integer> safeBlue;
    private final Setting<Integer> safeAlpha;
    public Setting<Boolean> customOutline;
    private final Setting<Integer> cRed;
    private final Setting<Integer> cGreen;
    private final Setting<Integer> cBlue;
    private final Setting<Integer> cAlpha;
    private final Setting<Integer> safecRed;
    private final Setting<Integer> safecGreen;
    private final Setting<Integer> safecBlue;
    private final Setting<Integer> safecAlpha;
    private static HoleESP INSTANCE;
    
    public HoleESP() {
        super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
        this.renderOwn = (Setting<Boolean>)this.register(new Setting("RenderOwn", (T)true));
        this.fov = (Setting<Boolean>)this.register(new Setting("InFov", (T)false));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)true));
        this.range = (Setting<Integer>)this.register(new Setting("RangeX", (T)10, (T)0, (T)10));
        this.rangeY = (Setting<Integer>)this.register(new Setting("RangeY", (T)10, (T)0, (T)10));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", (T)true));
        this.gradientBox = (Setting<Boolean>)this.register(new Setting("Gradient", (T)Boolean.TRUE, v -> this.box.getValue()));
        this.invertGradientBox = (Setting<Boolean>)this.register(new Setting("ReverseGradient", (T)Boolean.FALSE, v -> this.gradientBox.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", (T)true));
        this.gradientOutline = (Setting<Boolean>)this.register(new Setting("GradientOutline", (T)Boolean.FALSE, v -> this.outline.getValue()));
        this.invertGradientOutline = (Setting<Boolean>)this.register(new Setting("ReverseOutline", (T)Boolean.FALSE, v -> this.gradientOutline.getValue()));
        this.height = (Setting<Double>)this.register(new Setting("Height", (T)0.0, (T)(-2.0), (T)2.0));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)0, (T)0, (T)255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)0, (T)0, (T)255));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)255, (T)0, (T)255));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", (T)125, (T)0, (T)255, v -> this.box.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", (T)1.0f, (T)0.1f, (T)5.0f, v -> this.outline.getValue()));
        this.safeColor = (Setting<Boolean>)this.register(new Setting("BedrockColor", (T)false));
        this.doubleHoles = (Setting<Boolean>)this.register(new Setting("DoubleHoles", (T)true));
        this.safeRed = (Setting<Integer>)this.register(new Setting("BedrockRed", (T)0, (T)0, (T)255, v -> this.safeColor.getValue()));
        this.safeGreen = (Setting<Integer>)this.register(new Setting("BedrockGreen", (T)255, (T)0, (T)255, v -> this.safeColor.getValue()));
        this.safeBlue = (Setting<Integer>)this.register(new Setting("BedrockBlue", (T)0, (T)0, (T)255, v -> this.safeColor.getValue()));
        this.safeAlpha = (Setting<Integer>)this.register(new Setting("BedrockAlpha", (T)255, (T)0, (T)255, v -> this.safeColor.getValue()));
        this.customOutline = (Setting<Boolean>)this.register(new Setting("CustomLine", (T)Boolean.FALSE, v -> this.outline.getValue()));
        this.cRed = (Setting<Integer>)this.register(new Setting("OL-Red", (T)0, (T)0, (T)255, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.cGreen = (Setting<Integer>)this.register(new Setting("OL-Green", (T)0, (T)0, (T)255, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.cBlue = (Setting<Integer>)this.register(new Setting("OL-Blue", (T)255, (T)0, (T)255, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.cAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", (T)255, (T)0, (T)255, v -> this.customOutline.getValue() && this.outline.getValue()));
        this.safecRed = (Setting<Integer>)this.register(new Setting("OL-SafeRed", (T)0, (T)0, (T)255, v -> this.customOutline.getValue() && this.outline.getValue() && this.safeColor.getValue()));
        this.safecGreen = (Setting<Integer>)this.register(new Setting("OL-SafeGreen", (T)255, (T)0, (T)255, v -> this.customOutline.getValue() && this.outline.getValue() && this.safeColor.getValue()));
        this.safecBlue = (Setting<Integer>)this.register(new Setting("OL-SafeBlue", (T)0, (T)0, (T)255, v -> this.customOutline.getValue() && this.outline.getValue() && this.safeColor.getValue()));
        this.safecAlpha = (Setting<Integer>)this.register(new Setting("OL-SafeAlpha", (T)255, (T)0, (T)255, v -> this.customOutline.getValue() && this.outline.getValue() && this.safeColor.getValue()));
        this.setInstance();
    }
    
    private void setInstance() {
        HoleESP.INSTANCE = this;
    }
    
    public static HoleESP getInstance() {
        if (HoleESP.INSTANCE == null) {
            HoleESP.INSTANCE = new HoleESP();
        }
        return HoleESP.INSTANCE;
    }
    
    public void onRender3D(final Render3DEvent event) {
        assert HoleESP.mc.renderViewEntity != null;
        final Vec3i playerPos = new Vec3i(HoleESP.mc.renderViewEntity.posX, HoleESP.mc.renderViewEntity.posY, HoleESP.mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - this.range.getValue(); x < playerPos.getX() + this.range.getValue(); ++x) {
            for (int z = playerPos.getZ() - this.range.getValue(); z < playerPos.getZ() + this.range.getValue(); ++z) {
                for (int y = playerPos.getY() + this.rangeY.getValue(); y > playerPos.getY() - this.rangeY.getValue(); --y) {
                    final BlockPos pos = new BlockPos(x, y, z);
                    if (HoleESP.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && HoleESP.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && HoleESP.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && (!pos.equals((Object)new BlockPos(HoleESP.mc.player.posX, HoleESP.mc.player.posY, HoleESP.mc.player.posZ)) || this.renderOwn.getValue())) {
                        if (BlockUtil.isPosInFov(pos) || !this.fov.getValue()) {
                            if (this.doubleHoles.getValue()) {
                                final int currentAlpha = 0;
                                if (HoleESP.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
                                    RenderUtil.drawBoxESP(pos, ((boolean)this.rainbow.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.safeRed.getValue(), this.safeGreen.getValue(), this.safeBlue.getValue(), this.safeAlpha.getValue()), this.customOutline.getValue(), new Color(this.safecRed.getValue(), this.safecGreen.getValue(), this.safecBlue.getValue(), this.safecAlpha.getValue()), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), currentAlpha);
                                }
                                else if (!BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.down()).getBlock()) && !BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.east()).getBlock()) && !BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.west()).getBlock()) && !BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.south()).getBlock())) {
                                    if (!BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.north()).getBlock())) {
                                        RenderUtil.drawBoxESP(pos, ((boolean)this.rainbow.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), currentAlpha);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    static {
        HoleESP.INSTANCE = new HoleESP();
    }
}
