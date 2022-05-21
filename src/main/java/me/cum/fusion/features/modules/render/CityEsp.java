
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import java.util.*;
import me.cum.fusion.event.events.*;
import java.awt.*;
import me.cum.fusion.util.*;

public class CityEsp extends Module
{
    public Setting<Boolean> end_crystal;
    public Setting<Integer> range;
    public Setting<Boolean> render;
    public Setting<Boolean> colorSync;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> alpha;
    List<BlockPos> blocks;
    
    public CityEsp() {
        super("CityEsp", "citi e es pee", Module.Category.RENDER, true, false, false);
        this.end_crystal = (Setting<Boolean>)this.register(new Setting("EndCrystal", (T)true));
        this.range = (Setting<Integer>)this.register(new Setting("Range", (T)6, (T)0, (T)12));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", (T)true));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("ColorSync", (T)true, v -> this.render.getValue()));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)1, (T)255, v -> this.render.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)1, (T)255, v -> this.render.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)1, (T)255, v -> this.render.getValue()));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)125, (T)1, (T)255, v -> this.render.getValue()));
        this.blocks = new ArrayList<BlockPos>();
    }
    
    public void onEnable() {
        this.blocks.clear();
    }
    
    public void onUpdate() {
        this.blocks.clear();
        for (final EntityPlayer player : CityEsp.mc.world.playerEntities) {
            if (CityEsp.mc.player.getDistance((Entity)player) <= this.range.getValue()) {
                if (CityEsp.mc.player == player) {
                    continue;
                }
                final BlockPos p = EntityUtil.is_cityable(player, this.end_crystal.getValue());
                if (p == null) {
                    continue;
                }
                this.blocks.add(p);
            }
        }
    }
    
    public void onRender3D(final Render3DEvent event) {
        for (final BlockPos pos : this.blocks) {
            RenderUtil.drawBox(pos, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()));
        }
    }
    
    public void onDisable() {
        this.blocks.clear();
    }
}
