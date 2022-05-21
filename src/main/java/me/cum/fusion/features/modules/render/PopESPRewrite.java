
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import net.minecraft.client.entity.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;
import net.minecraft.entity.*;
import java.util.*;
import me.cum.fusion.event.events.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class PopESPRewrite extends Module
{
    private static PopESPRewrite INSTANCE;
    public EntityOtherPlayerMP fakeEntity;
    public Setting<Boolean> solidParent;
    public Setting<Boolean> solidSetting;
    public Setting<Float> red;
    public Setting<Float> green;
    public Setting<Float> blue;
    public Setting<Boolean> wireFrameParent;
    public Setting<Boolean> wireFrameSetting;
    public Setting<Float> wireRed;
    public Setting<Float> wireGreen;
    public Setting<Float> wireBlue;
    public Setting<Boolean> fadeParent;
    public Setting<Integer> startAlpha;
    public Setting<Integer> endAlpha;
    public Setting<Integer> fadeStep;
    public Setting<Boolean> yTravelParent;
    public Setting<Boolean> yTravel;
    public Setting<YTravelMode> yTravelMode;
    public Setting<Double> yTravelSpeed;
    public Setting<Boolean> miscParent;
    public Setting<Boolean> onDeath;
    public Setting<Boolean> clearListOnPop;
    public Setting<Boolean> clearListOnDeath;
    public Setting<Boolean> antiSelf;
    public HashMap<EntityPlayer, Integer> poppedPlayers;
    
    public PopESPRewrite() {
        super("PopESP New", "Pop Chams", Module.Category.RENDER, true, false, false);
        this.solidParent = (Setting<Boolean>)this.register(new Setting("Solid", (T)false));
        this.solidSetting = (Setting<Boolean>)this.register(new Setting("RenderSolid", (T)true, v -> this.solidParent.getValue()));
        this.red = (Setting<Float>)this.register(new Setting("SolidRed", (T)0.0f, (T)0.0f, (T)255.0f, v -> this.solidParent.getValue() && this.solidSetting.getValue()));
        this.green = (Setting<Float>)this.register(new Setting("SolidGreen", (T)255.0f, (T)0.0f, (T)255.0f, v -> this.solidParent.getValue() && this.solidSetting.getValue()));
        this.blue = (Setting<Float>)this.register(new Setting("SolidBlue", (T)0.0f, (T)0.0f, (T)255.0f, v -> this.solidParent.getValue() && this.solidSetting.getValue()));
        this.wireFrameParent = (Setting<Boolean>)this.register(new Setting("WireFrame", (T)false));
        this.wireFrameSetting = (Setting<Boolean>)this.register(new Setting("RenderWire", (T)true, v -> this.wireFrameParent.getValue()));
        this.wireRed = (Setting<Float>)this.register(new Setting("WireRed", (T)0.0f, (T)0.0f, (T)255.0f, v -> this.wireFrameParent.getValue() && this.wireFrameSetting.getValue()));
        this.wireGreen = (Setting<Float>)this.register(new Setting("WireGreen", (T)255.0f, (T)0.0f, (T)255.0f, v -> this.wireFrameParent.getValue() && this.wireFrameSetting.getValue()));
        this.wireBlue = (Setting<Float>)this.register(new Setting("WireBlue", (T)0.0f, (T)0.0f, (T)255.0f, v -> this.wireFrameParent.getValue() && this.wireFrameSetting.getValue()));
        this.fadeParent = (Setting<Boolean>)this.register(new Setting("Fade", (T)false));
        this.startAlpha = (Setting<Integer>)this.register(new Setting("StartAlpha", (T)255, (T)0, (T)255, v -> this.fadeParent.getValue()));
        this.endAlpha = (Setting<Integer>)this.register(new Setting("EndAlpha", (T)0, (T)0, (T)255, v -> this.fadeParent.getValue()));
        this.fadeStep = (Setting<Integer>)this.register(new Setting("FadeStep", (T)10, (T)10, (T)100, v -> this.fadeParent.getValue()));
        this.yTravelParent = (Setting<Boolean>)this.register(new Setting("YMovement", (T)false));
        this.yTravel = (Setting<Boolean>)this.register(new Setting("YTravel", (T)false, v -> this.yTravelParent.getValue()));
        this.yTravelMode = (Setting<YTravelMode>)this.register(new Setting("TravelMode", (T)YTravelMode.UP, v -> this.yTravelParent.getValue() && this.yTravel.getValue()));
        this.yTravelSpeed = (Setting<Double>)this.register(new Setting("TravelSpeed", (T)0.1, (T)0.0, (T)2.0, v -> this.yTravel.getValue()));
        this.miscParent = (Setting<Boolean>)this.register(new Setting("Misc", (T)false));
        this.onDeath = (Setting<Boolean>)this.register(new Setting("OnDeath", (T)false, v -> this.miscParent.getValue()));
        this.clearListOnPop = (Setting<Boolean>)this.register(new Setting("ClearListOnPop", (T)false, v -> this.miscParent.getValue()));
        this.clearListOnDeath = (Setting<Boolean>)this.register(new Setting("ClearListOnDeath", (T)false, v -> this.miscParent.getValue()));
        this.antiSelf = (Setting<Boolean>)this.register(new Setting("AntiSelf", (T)false, v -> this.miscParent.getValue()));
        this.poppedPlayers = new HashMap<EntityPlayer, Integer>();
        this.setInstance();
    }
    
    public static PopESPRewrite getInstance() {
        if (PopESPRewrite.INSTANCE == null) {
            PopESPRewrite.INSTANCE = new PopESPRewrite();
        }
        return PopESPRewrite.INSTANCE;
    }
    
    private void setInstance() {
        PopESPRewrite.INSTANCE = this;
    }
    
    public void onRender3D(final Render3DEvent event) {
        for (final Map.Entry<EntityPlayer, Integer> pop : this.poppedPlayers.entrySet()) {
            this.poppedPlayers.put(pop.getKey(), pop.getValue() - (this.fadeStep.getValue() + 10) / 20);
            if (pop.getValue() <= this.endAlpha.getValue()) {
                this.poppedPlayers.remove(pop.getKey());
                return;
            }
            if (getInstance().yTravel.getValue()) {
                if (getInstance().yTravelMode.getValue() == YTravelMode.UP) {
                    final EntityPlayer entityPlayer = pop.getKey();
                    entityPlayer.posY += getInstance().yTravelSpeed.getValue() / 20.0;
                }
                else if (getInstance().yTravelMode.getValue() == YTravelMode.DOWN) {
                    final EntityPlayer entityPlayer2 = pop.getKey();
                    entityPlayer2.posY -= getInstance().yTravelSpeed.getValue() / 20.0;
                }
            }
            if (this.wireFrameSetting.getValue()) {
                GlStateManager.pushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(1032, 6913);
                GL11.glDisable(3553);
                GL11.glDisable(2896);
                GL11.glDisable(2929);
                GL11.glEnable(2848);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glColor4f(this.wireRed.getValue() / 255.0f, this.wireGreen.getValue() / 255.0f, this.wireBlue.getValue() / 255.0f, pop.getValue() / 255.0f);
                this.renderEntityStatic((Entity)pop.getKey(), event.getPartialTicks(), false);
                GL11.glLineWidth(1.0f);
                GL11.glEnable(2896);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
            if (!this.solidSetting.getValue()) {
                continue;
            }
            GL11.glPushMatrix();
            GL11.glDepthRange(0.01, 1.0);
            GL11.glPushAttrib(-1);
            GL11.glEnable(3008);
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GL11.glLineWidth(1.0f);
            GL11.glColor4f(this.red.getValue() / 255.0f, this.green.getValue() / 255.0f, this.blue.getValue() / 255.0f, pop.getValue() / 255.0f);
            this.renderEntityStatic((Entity)pop.getKey(), event.getPartialTicks(), false);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3008);
            GL11.glEnable(3553);
            GL11.glDisable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopAttrib();
            GL11.glDepthRange(0.0, 1.0);
            GL11.glPopMatrix();
        }
    }
    
    @SubscribeEvent
    public void onPop(final TotemPopEvent event) {
        if (PopESP.mc.world.getEntityByID(event.getEntity().entityId) != null) {
            if (this.antiSelf.getValue() && event.getEntity().entityId == PopESP.mc.player.getEntityId()) {
                return;
            }
            final Entity entity = PopESP.mc.world.getEntityByID(event.getEntity().entityId);
            if (entity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer)entity;
                (this.fakeEntity = new EntityOtherPlayerMP((World)PopESP.mc.world, player.getGameProfile())).copyLocationAndAnglesFrom((Entity)player);
                this.fakeEntity.rotationYawHead = player.rotationYawHead;
                this.fakeEntity.prevRotationYawHead = player.rotationYawHead;
                this.fakeEntity.rotationYaw = player.rotationYaw;
                this.fakeEntity.prevRotationYaw = player.rotationYaw;
                this.fakeEntity.rotationPitch = player.rotationPitch;
                this.fakeEntity.prevRotationPitch = player.rotationPitch;
                this.fakeEntity.cameraYaw = this.fakeEntity.rotationYaw;
                this.fakeEntity.cameraPitch = this.fakeEntity.rotationPitch;
                if (this.clearListOnPop.getValue()) {
                    this.poppedPlayers.clear();
                }
                this.poppedPlayers.put((EntityPlayer)this.fakeEntity, this.startAlpha.getValue());
            }
        }
    }
    
    public void onDeath(final int entityId) {
        final Entity entity;
        if (this.onDeath.getValue() && PopESP.mc.world.getEntityByID(entityId) != null && (entity = PopESP.mc.world.getEntityByID(entityId)) instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)entity;
            (this.fakeEntity = new EntityOtherPlayerMP((World)PopESP.mc.world, player.getGameProfile())).copyLocationAndAnglesFrom((Entity)player);
            this.fakeEntity.rotationYawHead = player.rotationYawHead;
            this.fakeEntity.prevRotationYawHead = player.rotationYawHead;
            this.fakeEntity.rotationYaw = player.rotationYaw;
            this.fakeEntity.prevRotationYaw = player.rotationYaw;
            this.fakeEntity.rotationPitch = player.rotationPitch;
            this.fakeEntity.prevRotationPitch = player.rotationPitch;
            this.fakeEntity.cameraYaw = this.fakeEntity.rotationYaw;
            this.fakeEntity.cameraPitch = this.fakeEntity.rotationPitch;
            if (this.clearListOnDeath.getValue()) {
                this.poppedPlayers.clear();
            }
            this.poppedPlayers.put((EntityPlayer)this.fakeEntity, this.startAlpha.getValue());
        }
    }
    
    public void handlePopESP(final int entityId) {
        final Entity entity;
        if (PopESP.mc.world.getEntityByID(entityId) != null && (entity = PopESP.mc.world.getEntityByID(entityId)) instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)entity;
            (this.fakeEntity = new EntityOtherPlayerMP((World)PopESP.mc.world, player.getGameProfile())).copyLocationAndAnglesFrom((Entity)player);
            this.fakeEntity.rotationYawHead = player.rotationYawHead;
            this.fakeEntity.prevRotationYawHead = player.rotationYawHead;
            this.fakeEntity.rotationYaw = player.rotationYaw;
            this.fakeEntity.prevRotationYaw = player.rotationYaw;
            this.fakeEntity.rotationPitch = player.rotationPitch;
            this.fakeEntity.prevRotationPitch = player.rotationPitch;
            this.fakeEntity.cameraYaw = this.fakeEntity.rotationYaw;
            this.fakeEntity.cameraPitch = this.fakeEntity.rotationPitch;
            if (this.clearListOnDeath.getValue()) {
                this.poppedPlayers.clear();
            }
            this.poppedPlayers.put((EntityPlayer)this.fakeEntity, this.startAlpha.getValue());
        }
    }
    
    public void renderEntityStatic(final Entity entityIn, final float partialTicks, final boolean p_188388_3_) {
        if (entityIn.ticksExisted == 0) {
            entityIn.lastTickPosX = entityIn.posX;
            entityIn.lastTickPosY = entityIn.posY;
            entityIn.lastTickPosZ = entityIn.posZ;
        }
        final double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
        final double d2 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
        final double d3 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
        final float f = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks;
        final int i = entityIn.getBrightnessForRender();
        PopESPRewrite.mc.getRenderManager().renderEntity(entityIn, d0 - PopESP.mc.getRenderManager().viewerPosX, d2 - PopESP.mc.getRenderManager().viewerPosY, d3 - PopESP.mc.getRenderManager().viewerPosZ, f, partialTicks, p_188388_3_);
    }
    
    static {
        PopESPRewrite.INSTANCE = new PopESPRewrite();
    }
    
    public enum YTravelMode
    {
        UP, 
        DOWN;
    }
}
