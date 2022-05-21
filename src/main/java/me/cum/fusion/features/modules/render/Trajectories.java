
package me.cum.fusion.features.modules.render;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.event.events.*;
import net.minecraft.entity.player.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import org.lwjgl.util.glu.*;
import net.minecraft.item.*;
import java.util.*;
import com.google.common.base.*;

public class Trajectories extends Module
{
    public Trajectories() {
        super("Trajectories", "Shows the way of projectiles.", Module.Category.RENDER, false, false, false);
    }
    
    public void onRender3D(final Render3DEvent event) {
        if (Trajectories.mc.world == null || Trajectories.mc.player == null) {
            return;
        }
        this.drawTrajectories((EntityPlayer)Trajectories.mc.player, event.getPartialTicks());
    }
    
    public void enableGL3D(final float lineWidth) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        Trajectories.mc.entityRenderer.disableLightmap();
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(lineWidth);
    }
    
    public void disableGL3D() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
    
    private void drawTrajectories(final EntityPlayer player, final float partialTicks) {
        float pow = 0.0f;
        final double renderPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double renderPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double renderPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        player.getHeldItem(EnumHand.MAIN_HAND);
        if (Trajectories.mc.gameSettings.thirdPersonView != 0 || (!(player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow) && !(player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFishingRod) && !(player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemEnderPearl) && !(player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemEgg) && !(player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSnowball) && !(player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemExpBottle))) {
            return;
        }
        GL11.glPushMatrix();
        final Item item = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
        double posX = renderPosX - MathHelper.cos(player.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
        double posY = renderPosY + player.getEyeHeight() - 0.1000000014901161;
        double posZ = renderPosZ - MathHelper.sin(player.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
        double motionX = -MathHelper.sin(player.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(player.rotationPitch / 180.0f * 3.1415927f) * ((item instanceof ItemBow) ? 1.0 : 0.4);
        double motionY = -MathHelper.sin(player.rotationPitch / 180.0f * 3.1415927f) * ((item instanceof ItemBow) ? 1.0 : 0.4);
        double motionZ = MathHelper.cos(player.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(player.rotationPitch / 180.0f * 3.1415927f) * ((item instanceof ItemBow) ? 1.0 : 0.4);
        final int var6 = 72000 - player.getItemInUseCount();
        float power = var6 / 20.0f;
        power = (power * power + power * 2.0f) / 3.0f;
        if (power > 1.0f) {
            power = 1.0f;
        }
        final float distance = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= distance;
        motionY /= distance;
        motionZ /= distance;
        final float f = (item instanceof ItemBow) ? (power * 2.0f) : ((item instanceof ItemFishingRod) ? 1.25f : (pow = ((player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE) ? 0.9f : 1.0f)));
        motionX *= pow * ((item instanceof ItemFishingRod) ? 0.75f : ((player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE) ? 0.75f : 1.5f));
        motionY *= pow * ((item instanceof ItemFishingRod) ? 0.75f : ((player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE) ? 0.75f : 1.5f));
        motionZ *= pow * ((item instanceof ItemFishingRod) ? 0.75f : ((player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE) ? 0.75f : 1.5f));
        this.enableGL3D(2.0f);
        if (power > 0.6f) {
            GlStateManager.color(0.0f, 1.0f, 0.0f, 1.0f);
        }
        else {
            GlStateManager.color(0.8f, 0.5f, 0.0f, 1.0f);
        }
        GL11.glEnable(2848);
        final float size = (float)((item instanceof ItemBow) ? 0.3 : 0.25);
        boolean hasLanded = false;
        Entity landingOnEntity = null;
        RayTraceResult landingPosition = null;
        while (!hasLanded && posY > 0.0) {
            final Vec3d present = new Vec3d(posX, posY, posZ);
            final Vec3d future = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            final RayTraceResult possibleLandingStrip = Trajectories.mc.world.rayTraceBlocks(present, future, false, true, false);
            if (possibleLandingStrip != null && possibleLandingStrip.typeOfHit != RayTraceResult.Type.MISS) {
                landingPosition = possibleLandingStrip;
                hasLanded = true;
            }
            final AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
            final List<Entity> entities = this.getEntitiesWithinAABB(arrowBox.offset(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
            for (final Entity entity : entities) {
                if (entity.canBeCollidedWith()) {
                    if (entity == player) {
                        continue;
                    }
                    final float var7 = 0.3f;
                    final AxisAlignedBB var8 = entity.getEntityBoundingBox().expand((double)var7, (double)var7, (double)var7);
                    final RayTraceResult possibleEntityLanding = var8.calculateIntercept(present, future);
                    if (possibleEntityLanding == null) {
                        continue;
                    }
                    hasLanded = true;
                    landingOnEntity = entity;
                    landingPosition = possibleEntityLanding;
                }
            }
            if (landingOnEntity != null) {
                GlStateManager.color(1.0f, 0.0f, 0.0f, 1.0f);
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            final float motionAdjustment = 0.99f;
            motionX *= motionAdjustment;
            motionY *= motionAdjustment;
            motionZ *= motionAdjustment;
            motionY -= ((item instanceof ItemBow) ? 0.05 : 0.03);
        }
        if (landingPosition != null && landingPosition.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.translate(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);
            final int side = landingPosition.sideHit.getIndex();
            if (side == 2) {
                GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            }
            else if (side == 3) {
                GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            }
            else if (side == 4) {
                GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            }
            else if (side == 5) {
                GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            }
            final Cylinder c = new Cylinder();
            GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
            c.setDrawStyle(100011);
            if (landingOnEntity != null) {
                GlStateManager.color(0.0f, 0.0f, 0.0f, 1.0f);
                GL11.glLineWidth(2.5f);
                c.draw(0.6f, 0.3f, 0.0f, 4, 1);
                GL11.glLineWidth(0.1f);
                GlStateManager.color(1.0f, 0.0f, 0.0f, 1.0f);
            }
            c.draw(0.6f, 0.3f, 0.0f, 4, 1);
        }
        this.disableGL3D();
        GL11.glPopMatrix();
    }
    
    private List<Entity> getEntitiesWithinAABB(final AxisAlignedBB bb) {
        final ArrayList<Entity> list = new ArrayList<Entity>();
        final int chunkMinX = MathHelper.floor((bb.minX - 2.0) / 16.0);
        final int chunkMaxX = MathHelper.floor((bb.maxX + 2.0) / 16.0);
        final int chunkMinZ = MathHelper.floor((bb.minZ - 2.0) / 16.0);
        final int chunkMaxZ = MathHelper.floor((bb.maxZ + 2.0) / 16.0);
        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                if (Trajectories.mc.world.getChunkProvider().getLoadedChunk(x, z) != null) {
                    Trajectories.mc.world.getChunk(x, z).getEntitiesWithinAABBForEntity((Entity)Trajectories.mc.player, bb, (List)list, (Predicate)null);
                }
            }
        }
        return list;
    }
}
