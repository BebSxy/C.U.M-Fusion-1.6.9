
package me.cum.fusion.features.modules.troll;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.event.events.*;
import net.minecraft.util.math.*;
import me.cum.fusion.util.*;
import me.cum.fusion.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.network.*;
import java.util.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.init.*;

public class Lawnmower extends Module
{
    public Setting<Integer> playerRange;
    public Setting<Integer> playerHeight;
    public Setting<Boolean> rotate;
    
    public Lawnmower() {
        super("LawnMower", "haha funny meme", Module.Category.TROLL, true, false, false);
        this.playerRange = new Setting<Integer>("Range", 2, 0, 6);
        this.playerHeight = new Setting<Integer>("Height", 2, 0, 6);
        this.rotate = new Setting<Boolean>("Rotate", true);
        this.register((Setting)this.playerRange);
        this.register((Setting)this.playerHeight);
        this.register((Setting)this.rotate);
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        for (final BlockPos pos : BlockUtil.getSphere(Lawnmower.mc.player.getPosition(), this.playerRange.getValue(), this.playerHeight.getValue(), false, true, 0)) {
            if (!this.check(pos)) {
                continue;
            }
            if (pos == null) {
                continue;
            }
            if (this.rotate.getValue()) {
                final float[] angle = MathUtil.calcAngle(Lawnmower.mc.player.getPositionEyes(Lawnmower.mc.getRenderPartialTicks()), new Vec3d((double)(float)pos.getX(), (double)(float)pos.getY(), (double)(float)pos.getZ()));
                Fusion.rotationManager.setPlayerRotations(angle[0], angle[1]);
            }
            Lawnmower.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
        }
    }
    
    boolean check(final BlockPos pos) {
        return Lawnmower.mc.world.getBlockState(pos).getBlock() == Blocks.TALLGRASS || Lawnmower.mc.world.getBlockState(pos).getBlock() == Blocks.DOUBLE_PLANT;
    }
}
