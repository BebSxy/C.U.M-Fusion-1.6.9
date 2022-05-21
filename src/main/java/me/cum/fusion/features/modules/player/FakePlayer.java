
package me.cum.fusion.features.modules.player;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.client.entity.*;
import net.minecraft.enchantment.*;
import java.util.*;
import com.mojang.authlib.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;

public class FakePlayer extends Module
{
    public Setting<Boolean> hollow;
    private EntityOtherPlayerMP otherPlayer;
    
    public FakePlayer() {
        super("FakePlayer", "Spawns fake player", Module.Category.PLAYER, false, false, false);
        this.hollow = (Setting<Boolean>)this.register(new Setting("Move", (T)false));
    }
    
    public void onTick() {
        if (this.otherPlayer != null) {
            final Random random = new Random();
            this.otherPlayer.moveForward = FakePlayer.mc.player.moveForward + random.nextInt(5) / 10.0f;
            this.otherPlayer.moveStrafing = FakePlayer.mc.player.moveStrafing + random.nextInt(5) / 10.0f;
            if (this.hollow.getValue()) {
                this.travel(this.otherPlayer.moveStrafing, this.otherPlayer.moveVertical, this.otherPlayer.moveForward);
            }
        }
    }
    
    public void travel(final float strafe, final float vertical, final float forward) {
        final double d0 = this.otherPlayer.posY;
        float f1 = 0.8f;
        float f2 = 0.02f;
        float f3 = (float)EnchantmentHelper.getDepthStriderModifier((EntityLivingBase)this.otherPlayer);
        if (f3 > 3.0f) {
            f3 = 3.0f;
        }
        if (!this.otherPlayer.onGround) {
            f3 *= 0.5f;
        }
        if (f3 > 0.0f) {
            f1 += (0.54600006f - f1) * f3 / 3.0f;
            f2 += (this.otherPlayer.getAIMoveSpeed() - f2) * f3 / 4.0f;
        }
        this.otherPlayer.moveRelative(strafe, vertical, forward, f2);
        this.otherPlayer.move(MoverType.SELF, this.otherPlayer.motionX, this.otherPlayer.motionY, this.otherPlayer.motionZ);
        final EntityOtherPlayerMP otherPlayer = this.otherPlayer;
        otherPlayer.motionX *= f1;
        final EntityOtherPlayerMP otherPlayer2 = this.otherPlayer;
        otherPlayer2.motionY *= 0.800000011920929;
        final EntityOtherPlayerMP otherPlayer3 = this.otherPlayer;
        otherPlayer3.motionZ *= f1;
        if (!this.otherPlayer.hasNoGravity()) {
            final EntityOtherPlayerMP otherPlayer4 = this.otherPlayer;
            otherPlayer4.motionY -= 0.02;
        }
        if (this.otherPlayer.collidedHorizontally && this.otherPlayer.isOffsetPositionInLiquid(this.otherPlayer.motionX, this.otherPlayer.motionY + 0.6000000238418579 - this.otherPlayer.posY + d0, this.otherPlayer.motionZ)) {
            this.otherPlayer.motionY = 0.30000001192092896;
        }
    }
    
    public void onEnable() {
        if (FakePlayer.mc.world == null || FakePlayer.mc.player == null) {
            this.toggle();
            return;
        }
        if (this.otherPlayer == null) {
            (this.otherPlayer = new EntityOtherPlayerMP((World)FakePlayer.mc.world, new GameProfile(UUID.randomUUID(), "TestDummy"))).copyLocationAndAnglesFrom((Entity)FakePlayer.mc.player);
            this.otherPlayer.inventory.copyInventory(FakePlayer.mc.player.inventory);
        }
        FakePlayer.mc.world.spawnEntity((Entity)this.otherPlayer);
    }
    
    public void onDisable() {
        if (this.otherPlayer != null) {
            FakePlayer.mc.world.removeEntity((Entity)this.otherPlayer);
            this.otherPlayer = null;
        }
    }
}
