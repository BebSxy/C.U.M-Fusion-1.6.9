
package me.cum.fusion.mixin.mixins;

import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraft.crash.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import java.util.*;
import org.spongepowered.asm.mixin.*;
import me.cum.fusion.event.events.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ Entity.class })
public abstract class MixinEntity
{
    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;
    @Shadow
    public boolean onGround;
    @Shadow
    public boolean noClip;
    @Shadow
    public float prevDistanceWalkedModified;
    @Shadow
    public World world;
    @Shadow
    @Final
    private double[] pistonDeltas;
    @Shadow
    private long pistonDeltasGameTime;
    @Shadow
    protected boolean isInWeb;
    @Shadow
    public float stepHeight;
    @Shadow
    public boolean collidedHorizontally;
    @Shadow
    public boolean collidedVertically;
    @Shadow
    public boolean collided;
    @Shadow
    public float distanceWalkedModified;
    @Shadow
    public float distanceWalkedOnStepModified;
    @Shadow
    private int fire;
    @Shadow
    private int nextStepDistance;
    @Shadow
    private float nextFlap;
    @Shadow
    protected Random rand;
    
    @Shadow
    public abstract boolean isSprinting();
    
    @Shadow
    public abstract boolean isRiding();
    
    @Shadow
    public abstract boolean isSneaking();
    
    @Shadow
    public abstract void setEntityBoundingBox(final AxisAlignedBB p0);
    
    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();
    
    @Shadow
    public abstract void resetPositionToBB();
    
    @Shadow
    protected abstract void updateFallState(final double p0, final boolean p1, final IBlockState p2, final BlockPos p3);
    
    @Shadow
    protected abstract boolean canTriggerWalking();
    
    @Shadow
    public abstract boolean isInWater();
    
    @Shadow
    public abstract boolean isBeingRidden();
    
    @Shadow
    public abstract Entity getControllingPassenger();
    
    @Shadow
    public abstract void playSound(final SoundEvent p0, final float p1, final float p2);
    
    @Shadow
    protected abstract void doBlockCollisions();
    
    @Shadow
    public abstract boolean isWet();
    
    @Shadow
    protected abstract void playStepSound(final BlockPos p0, final Block p1);
    
    @Shadow
    protected abstract SoundEvent getSwimSound();
    
    @Shadow
    protected abstract float playFlySound(final float p0);
    
    @Shadow
    protected abstract boolean makeFlySound();
    
    @Shadow
    public abstract void addEntityCrashInfo(final CrashReportCategory p0);
    
    @Shadow
    protected abstract void dealFireDamage(final int p0);
    
    @Shadow
    public abstract void setFire(final int p0);
    
    @Shadow
    protected abstract int getFireImmuneTicks();
    
    @Shadow
    public abstract boolean isBurning();
    
    @Shadow
    public abstract int getMaxInPortalTime();
    
    @Overwrite
    public void move(final MoverType type, double x, double y, double z) {
        final Entity _this = (Entity)this;
        if (this.noClip) {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
            this.resetPositionToBB();
        }
        else {
            if (type == MoverType.PISTON) {
                final long i = this.world.getTotalWorldTime();
                if (i != this.pistonDeltasGameTime) {
                    Arrays.fill(this.pistonDeltas, 0.0);
                    this.pistonDeltasGameTime = i;
                }
                if (x != 0.0) {
                    final int j = EnumFacing.Axis.X.ordinal();
                    final double d0 = MathHelper.clamp(x + this.pistonDeltas[j], -0.51, 0.51);
                    x = d0 - this.pistonDeltas[j];
                    this.pistonDeltas[j] = d0;
                    if (Math.abs(x) <= 9.999999747378752E-6) {
                        return;
                    }
                }
                else if (y != 0.0) {
                    final int l4 = EnumFacing.Axis.Y.ordinal();
                    final double d2 = MathHelper.clamp(y + this.pistonDeltas[l4], -0.51, 0.51);
                    y = d2 - this.pistonDeltas[l4];
                    this.pistonDeltas[l4] = d2;
                    if (Math.abs(y) <= 9.999999747378752E-6) {
                        return;
                    }
                }
                else {
                    if (z == 0.0) {
                        return;
                    }
                    final int i2 = EnumFacing.Axis.Z.ordinal();
                    final double d3 = MathHelper.clamp(z + this.pistonDeltas[i2], -0.51, 0.51);
                    z = d3 - this.pistonDeltas[i2];
                    this.pistonDeltas[i2] = d3;
                    if (Math.abs(z) <= 9.999999747378752E-6) {
                        return;
                    }
                }
            }
            this.world.profiler.startSection("move");
            final double d4 = this.posX;
            final double d5 = this.posY;
            final double d6 = this.posZ;
            if (this.isInWeb) {
                this.isInWeb = false;
                x *= 0.25;
                y *= 0.05000000074505806;
                z *= 0.25;
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
            }
            final double d7 = x;
            final double d8 = y;
            final double d9 = z;
            if ((type == MoverType.SELF || type == MoverType.PLAYER) && this.onGround) {
                this.isSneaking();
            }
            final List<AxisAlignedBB> list1 = (List<AxisAlignedBB>)this.world.getCollisionBoxes(_this, this.getEntityBoundingBox().expand(x, y, z));
            final AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            if (y != 0.0) {
                final int k = list1.size();
                for (final AxisAlignedBB axisAlignedBB : list1) {
                    y = axisAlignedBB.calculateYOffset(this.getEntityBoundingBox(), y);
                }
                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
            }
            if (x != 0.0) {
                final int l5 = list1.size();
                for (final AxisAlignedBB axisAlignedBB : list1) {
                    x = axisAlignedBB.calculateXOffset(this.getEntityBoundingBox(), x);
                }
                if (x != 0.0) {
                    this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0, 0.0));
                }
            }
            if (z != 0.0) {
                final int i3 = list1.size();
                for (final AxisAlignedBB axisAlignedBB : list1) {
                    z = axisAlignedBB.calculateZOffset(this.getEntityBoundingBox(), z);
                }
                if (z != 0.0) {
                    this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, 0.0, z));
                }
            }
            final boolean bl;
            final boolean flag = bl = (this.onGround || (d8 != y && d8 < 0.0));
            if (this.stepHeight > 0.0f && flag && (d7 != x || d9 != z)) {
                final StepEvent preEvent = new StepEvent(0, _this);
                MinecraftForge.EVENT_BUS.post((Event)preEvent);
                final double d10 = x;
                final double d11 = y;
                final double d12 = z;
                final AxisAlignedBB axisalignedbb2 = this.getEntityBoundingBox();
                this.setEntityBoundingBox(axisalignedbb);
                y = preEvent.getHeight();
                final List<AxisAlignedBB> list2 = (List<AxisAlignedBB>)this.world.getCollisionBoxes(_this, this.getEntityBoundingBox().expand(d7, y, d9));
                AxisAlignedBB axisalignedbb3 = this.getEntityBoundingBox();
                final AxisAlignedBB axisalignedbb4 = axisalignedbb3.expand(d7, 0.0, d9);
                double d13 = y;
                final int k2 = list2.size();
                for (final AxisAlignedBB axisAlignedBB2 : list2) {
                    d13 = axisAlignedBB2.calculateYOffset(axisalignedbb4, d13);
                }
                axisalignedbb3 = axisalignedbb3.offset(0.0, d13, 0.0);
                double d14 = d7;
                final int i4 = list2.size();
                for (final AxisAlignedBB element : list2) {
                    d14 = element.calculateXOffset(axisalignedbb3, d14);
                }
                axisalignedbb3 = axisalignedbb3.offset(d14, 0.0, 0.0);
                double d15 = d9;
                final int k3 = list2.size();
                for (final AxisAlignedBB item : list2) {
                    d15 = item.calculateZOffset(axisalignedbb3, d15);
                }
                axisalignedbb3 = axisalignedbb3.offset(0.0, 0.0, d15);
                AxisAlignedBB axisalignedbb5 = this.getEntityBoundingBox();
                double d16 = y;
                final int i5 = list2.size();
                for (final AxisAlignedBB value : list2) {
                    d16 = value.calculateYOffset(axisalignedbb5, d16);
                }
                axisalignedbb5 = axisalignedbb5.offset(0.0, d16, 0.0);
                double d17 = d7;
                final int k4 = list2.size();
                for (final AxisAlignedBB bb : list2) {
                    d17 = bb.calculateXOffset(axisalignedbb5, d17);
                }
                axisalignedbb5 = axisalignedbb5.offset(d17, 0.0, 0.0);
                double d18 = d9;
                final int i6 = list2.size();
                for (final AxisAlignedBB alignedBB : list2) {
                    d18 = alignedBB.calculateZOffset(axisalignedbb5, d18);
                }
                axisalignedbb5 = axisalignedbb5.offset(0.0, 0.0, d18);
                final double d19 = d14 * d14 + d15 * d15;
                final double d20 = d17 * d17 + d18 * d18;
                if (d19 > d20) {
                    x = d14;
                    z = d15;
                    y = -d13;
                    this.setEntityBoundingBox(axisalignedbb3);
                }
                else {
                    x = d17;
                    z = d18;
                    y = -d16;
                    this.setEntityBoundingBox(axisalignedbb5);
                }
                final int k5 = list2.size();
                for (final AxisAlignedBB axisAlignedBB3 : list2) {
                    y = axisAlignedBB3.calculateYOffset(this.getEntityBoundingBox(), y);
                }
                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
                if (d10 * d10 + d12 * d12 >= x * x + z * z) {
                    x = d10;
                    y = d11;
                    z = d12;
                    this.setEntityBoundingBox(axisalignedbb2);
                }
                else {
                    final StepEvent postEvent = new StepEvent(1, _this);
                    MinecraftForge.EVENT_BUS.post((Event)postEvent);
                }
            }
            this.world.profiler.endSection();
            this.world.profiler.startSection("rest");
            this.resetPositionToBB();
            this.collidedHorizontally = (d7 != x || d9 != z);
            this.collidedVertically = (d8 != y);
            this.onGround = (this.collidedVertically && d8 < 0.0);
            this.collided = (this.collidedHorizontally || this.collidedVertically);
            final int j2 = MathHelper.floor(this.posX);
            final int i7 = MathHelper.floor(this.posY - 0.20000000298023224);
            final int k6 = MathHelper.floor(this.posZ);
            BlockPos blockpos = new BlockPos(j2, i7, k6);
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            final BlockPos blockpos2;
            final IBlockState iblockstate2;
            final Block block1;
            if (iblockstate.getMaterial() == Material.AIR && ((block1 = (iblockstate2 = this.world.getBlockState(blockpos2 = blockpos.down())).getBlock()) instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate)) {
                iblockstate = iblockstate2;
                blockpos = blockpos2;
            }
            this.updateFallState(y, this.onGround, iblockstate, blockpos);
            if (d7 != x) {
                this.motionX = 0.0;
            }
            if (d9 != z) {
                this.motionZ = 0.0;
            }
            final Block block2 = iblockstate.getBlock();
            if (d8 != y) {
                block2.onLanded(this.world, _this);
            }
            if (this.canTriggerWalking() && !this.isRiding()) {
                final double d21 = this.posX - d4;
                double d22 = this.posY - d5;
                final double d23 = this.posZ - d6;
                if (block2 != Blocks.LADDER) {
                    d22 = 0.0;
                }
                if (this.onGround) {
                    block2.onEntityWalk(this.world, blockpos, _this);
                }
                this.distanceWalkedModified += (float)(MathHelper.sqrt(d21 * d21 + d23 * d23) * 0.6);
                this.distanceWalkedOnStepModified += (float)(MathHelper.sqrt(d21 * d21 + d22 * d22 + d23 * d23) * 0.6);
                if (this.distanceWalkedOnStepModified > this.nextStepDistance && iblockstate.getMaterial() != Material.AIR) {
                    this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;
                    if (this.isInWater()) {
                        final Entity entity = (this.isBeingRidden() && this.getControllingPassenger() != null) ? this.getControllingPassenger() : _this;
                        final float f = (entity == _this) ? 0.35f : 0.4f;
                        float f2 = MathHelper.sqrt(entity.motionX * entity.motionX * 0.20000000298023224 + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ * 0.20000000298023224) * f;
                        if (f2 > 1.0f) {
                            f2 = 1.0f;
                        }
                        this.playSound(this.getSwimSound(), f2, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                    }
                    else {
                        this.playStepSound(blockpos, block2);
                    }
                }
                else if (this.distanceWalkedOnStepModified > this.nextFlap && this.makeFlySound() && iblockstate.getMaterial() == Material.AIR) {
                    this.nextFlap = this.playFlySound(this.distanceWalkedOnStepModified);
                }
            }
            try {
                this.doBlockCollisions();
            }
            catch (Throwable throwable) {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }
            final boolean flag2 = this.isWet();
            if (this.world.isFlammableWithin(this.getEntityBoundingBox().shrink(0.001))) {
                this.dealFireDamage(1);
                if (!flag2) {
                    ++this.fire;
                    if (this.fire == 0) {
                        this.setFire(8);
                    }
                }
            }
            else if (this.fire <= 0) {
                this.fire = -this.getFireImmuneTicks();
            }
            if (flag2 && this.isBurning()) {
                this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7f, 1.6f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                this.fire = -this.getFireImmuneTicks();
            }
            this.world.profiler.endSection();
        }
    }
    
    @Redirect(method = { "applyEntityCollision" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocityHook(final Entity entity, final double x, final double y, final double z) {
        final PushEvent event = new PushEvent(entity, x, y, z, true);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (!event.isCanceled()) {
            entity.motionX += event.x;
            entity.motionY += event.y;
            entity.motionZ += event.z;
            entity.isAirBorne = event.airbone;
        }
    }
}
