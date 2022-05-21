
package me.cum.fusion.util;

import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import java.util.stream.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.item.*;
import com.google.common.util.concurrent.*;
import java.util.concurrent.atomic.*;
import me.cum.fusion.features.command.*;
import me.cum.fusion.*;
import java.util.function.*;
import net.minecraft.world.*;
import net.minecraft.network.play.client.*;
import net.minecraft.block.material.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import java.util.*;

public class BlockUtil implements Util
{
    public static final List<Block> blackList;
    public static final List<Block> shulkerList;
    public static final List<Block> unSafeBlocks;
    public static List<Block> unSolidBlocks;
    private static BlockPos _currBlock;
    private static boolean _started;
    
    public static void placeBlockScaffold(final BlockPos pos) {
        final Vec3d eyesPos = new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            final Vec3d hitVec;
            if (canBeClicked(neighbor) && eyesPos.squareDistanceTo(hitVec = new Vec3d((Vec3i)neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5))) <= 18.0625) {
                HoleFillUtil.faceVectorPacketInstant(hitVec);
                HoleFillUtil.processRightClickBlock(neighbor, side2, hitVec);
                BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
                BlockUtil.mc.rightClickDelayTimer = 4;
                return;
            }
        }
    }
    
    public static boolean placeBlockNotRetarded(final BlockPos pos, final EnumHand hand, final boolean rotate, final boolean packet, final boolean altRotate) {
        final EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        if (!BlockUtil.mc.player.isSneaking() && (BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock))) {
            BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            BlockUtil.mc.player.setSneaking(true);
        }
        if (rotate) {
            RotationUtil.faceVector(altRotate ? new Vec3d((Vec3i)pos) : hitVec, true);
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        return true;
    }
    
    public static List<BlockPos> possiblePlacePositions(final float placeRange, final boolean specialEntityCheck, final boolean oneDot15) {
        final NonNullList positions = NonNullList.create();
        positions.addAll((Collection)getSphere(EntityUtil.getPlayerPos((EntityPlayer)BlockUtil.mc.player), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, specialEntityCheck, oneDot15)).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        return (List<BlockPos>)positions;
    }
    
    public static void placeCrystalOnBlock(final BlockPos pos, final EnumHand hand, final boolean swing, final boolean exactHand, final boolean silent) {
        final RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        final EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        final int old = BlockUtil.mc.player.inventory.currentItem;
        final int crystal = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
        if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != BlockUtil.mc.player.inventory.currentItem) {
            BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(crystal));
        }
        BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
        if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != BlockUtil.mc.player.inventory.currentItem) {
            BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(old));
        }
        if (swing) {
            BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND));
        }
    }
    
    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean specialEntityCheck, final boolean oneDot15) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if ((!oneDot15 && BlockUtil.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) || BlockUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
                return false;
            }
            for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost))) {
                if (!entity.isDead) {
                    if (specialEntityCheck && entity instanceof EntityEnderCrystal) {
                        continue;
                    }
                    return false;
                }
            }
            if (!oneDot15) {
                for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost2))) {
                    if (!entity.isDead) {
                        if (specialEntityCheck && entity instanceof EntityEnderCrystal) {
                            continue;
                        }
                        return false;
                    }
                }
            }
        }
        catch (Exception ignored) {
            return false;
        }
        return true;
    }
    
    public static List<BlockPos> getBlockSphere(final float breakRange, final Class clazz) {
        final NonNullList positions = NonNullList.create();
        positions.addAll((Collection)getSphere(EntityUtil.getPlayerPos((EntityPlayer)BlockUtil.mc.player), breakRange, (int)breakRange, false, true, 0).stream().filter(pos -> clazz.isInstance(BlockUtil.mc.world.getBlockState(pos).getBlock())).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        return (List<BlockPos>)positions;
    }
    
    public static List<EnumFacing> getPossibleSides(final BlockPos pos) {
        final ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (BlockUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState;
                if (!(blockState = BlockUtil.mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }
    
    public static EnumFacing getFacing(final BlockPos pos) {
        for (final EnumFacing facing : EnumFacing.values()) {
            final RayTraceResult rayTraceResult = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX() + 0.5 + facing.getDirectionVec().getX() / 2.0, pos.getY() + 0.5 + facing.getDirectionVec().getY() / 2.0, pos.getZ() + 0.5 + facing.getDirectionVec().getZ() / 2.0), false, true, false);
            if (rayTraceResult == null || (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK && rayTraceResult.getBlockPos().equals((Object)pos))) {
                return facing;
            }
        }
        if (pos.getY() > BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight()) {
            return EnumFacing.DOWN;
        }
        return EnumFacing.UP;
    }
    
    public static EnumFacing getFirstFacing(final BlockPos pos) {
        final Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
    
    public static EnumFacing getRayTraceFacing(final BlockPos pos) {
        final RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getX() - 0.5, pos.getX() + 0.5));
        if (result == null || result.sideHit == null) {
            return EnumFacing.UP;
        }
        return result.sideHit;
    }
    
    public static int isPositionPlaceable(final BlockPos pos, final boolean rayTrace) {
        return isPositionPlaceable(pos, rayTrace, true);
    }
    
    public static int isPositionPlaceable(final BlockPos pos, final boolean rayTrace, final boolean entityCheck) {
        final Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
            return 0;
        }
        if (!rayTracePlaceCheck(pos, rayTrace, 0.0f)) {
            return -1;
        }
        if (entityCheck) {
            for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(pos))) {
                if (!(entity instanceof EntityItem)) {
                    if (entity instanceof EntityXPOrb) {
                        continue;
                    }
                    return 1;
                }
            }
        }
        for (final EnumFacing side : getPossibleSides(pos)) {
            if (!canBeClicked(pos.offset(side))) {
                continue;
            }
            return 3;
        }
        return 2;
    }
    
    public static void rightClickBlock(final BlockPos pos, final Vec3d vec, final EnumHand hand, final EnumFacing direction, final boolean packet) {
        if (packet) {
            final float f = (float)(vec.x - pos.getX());
            final float f2 = (float)(vec.y - pos.getY());
            final float f3 = (float)(vec.z - pos.getZ());
            BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
        }
        else {
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, direction, vec, hand);
        }
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.rightClickDelayTimer = 4;
    }
    
    public static void rightClickBlockLegit(final BlockPos pos, final float range, final boolean rotate, final EnumHand hand, final AtomicDouble Yaw, final AtomicDouble Pitch, final AtomicBoolean rotating) {
        final Vec3d eyesPos = RotationUtil.getEyesPos();
        final Vec3d posVec = new Vec3d((Vec3i)pos).add(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        for (final EnumFacing side : EnumFacing.values()) {
            final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
            final double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
            if (distanceSqHitVec <= MathUtil.square(range) && distanceSqHitVec < distanceSqPosVec && BlockUtil.mc.world.rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                if (rotate) {
                    final float[] rotations = RotationUtil.getLegitRotations(hitVec);
                    Yaw.set((double)rotations[0]);
                    Pitch.set((double)rotations[1]);
                    rotating.set(true);
                }
                BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, side, hitVec, hand);
                BlockUtil.mc.player.swingArm(hand);
                BlockUtil.mc.rightClickDelayTimer = 4;
                break;
            }
        }
    }
    
    public static boolean placeBlock(final BlockPos pos, final EnumHand hand, final boolean rotate, final boolean packet, final boolean isSneaking) {
        boolean sneaking = false;
        final EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        if (!BlockUtil.mc.player.isSneaking() && (BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock))) {
            BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            BlockUtil.mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            RotationUtil.faceVector(hitVec, true);
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }
    
    public static boolean placeBlockSmartRotate(final BlockPos pos, final EnumHand hand, final boolean rotate, final boolean packet, final boolean isSneaking) {
        boolean sneaking = false;
        final EnumFacing side = getFirstFacing(pos);
        Command.sendMessage(Objects.requireNonNull(side).toString());
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        if (!BlockUtil.mc.player.isSneaking() && (BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock))) {
            BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }
        if (rotate) {
            Fusion.rotationManager.lookAtVec3d(hitVec);
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }
    
    public static void placeBlockStopSneaking(final BlockPos pos, final EnumHand hand, final boolean rotate, final boolean packet, final boolean isSneaking) {
        final boolean sneaking = placeBlockSmartRotate(pos, hand, rotate, packet, isSneaking);
        if (!isSneaking && sneaking) {
            BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }
    
    public static Vec3d[] getHelpingBlocks(final Vec3d vec3d) {
        return new Vec3d[] { new Vec3d(vec3d.x, vec3d.y - 1.0, vec3d.z), new Vec3d((vec3d.x != 0.0) ? (vec3d.x * 2.0) : vec3d.x, vec3d.y, (vec3d.x != 0.0) ? vec3d.z : (vec3d.z * 2.0)), new Vec3d((vec3d.x == 0.0) ? (vec3d.x + 1.0) : vec3d.x, vec3d.y, (vec3d.x == 0.0) ? vec3d.z : (vec3d.z + 1.0)), new Vec3d((vec3d.x == 0.0) ? (vec3d.x - 1.0) : vec3d.x, vec3d.y, (vec3d.x == 0.0) ? vec3d.z : (vec3d.z - 1.0)), new Vec3d(vec3d.x, vec3d.y + 1.0, vec3d.z) };
    }
    
    public static List<BlockPos> possiblePlacePositions(final float placeRange) {
        final NonNullList positions = NonNullList.create();
        positions.addAll((Collection)getSphere(EntityUtil.getPlayerPos((EntityPlayer)BlockUtil.mc.player), placeRange, (int)placeRange, false, true, 0).stream().filter((Predicate<? super Object>)BlockUtil::canPlaceCrystal).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        return (List<BlockPos>)positions;
    }
    
    public static List<BlockPos> getSphere(final BlockPos pos, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = pos.getX();
        final int cy = pos.getY();
        final int cz = pos.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                int y = sphere ? (cy - (int)r) : cy;
                while (true) {
                    final float f = (float)y;
                    final float f2 = sphere ? (cy + r) : ((float)(cy + h));
                    if (f >= f2) {
                        break;
                    }
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
            }
        }
        return circleblocks;
    }
    
    public static List<BlockPos> getDisc(final BlockPos pos, final float r) {
        final ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = pos.getX();
        final int cy = pos.getY();
        final int cz = pos.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                if (dist < r * r) {
                    final BlockPos position = new BlockPos(x, cy, z);
                    circleblocks.add(position);
                }
            }
        }
        return circleblocks;
    }
    
    public static boolean canPlaceCrystal(final BlockPos blockPos) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            return (BlockUtil.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || BlockUtil.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && BlockUtil.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && BlockUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost)).isEmpty() && BlockUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static List<BlockPos> possiblePlacePositions(final float placeRange, final boolean specialEntityCheck) {
        final NonNullList positions = NonNullList.create();
        positions.addAll((Collection)getSphere(EntityUtil.getPlayerPos((EntityPlayer)BlockUtil.mc.player), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, specialEntityCheck)).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        return (List<BlockPos>)positions;
    }
    
    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean specialEntityCheck) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if (BlockUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || BlockUtil.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                return false;
            }
            if (!specialEntityCheck) {
                return BlockUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost)).isEmpty() && BlockUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost2)).isEmpty();
            }
            for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost))) {
                if (entity instanceof EntityEnderCrystal) {
                    continue;
                }
                return false;
            }
            for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost2))) {
                if (entity instanceof EntityEnderCrystal) {
                    continue;
                }
                return false;
            }
        }
        catch (Exception ignored) {
            return false;
        }
        return true;
    }
    
    public static boolean canBeClicked(final BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }
    
    private static Block getBlock(final BlockPos pos) {
        return getState(pos).getBlock();
    }
    
    private static IBlockState getState(final BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos);
    }
    
    public static boolean isBlockAboveEntitySolid(final Entity entity) {
        if (entity != null) {
            final BlockPos pos = new BlockPos(entity.posX, entity.posY + 2.0, entity.posZ);
            return isBlockSolid(pos);
        }
        return false;
    }
    
    public static void SetCurrentBlock(final BlockPos block) {
        BlockUtil._currBlock = block;
        BlockUtil._started = false;
    }
    
    public static BlockPos GetCurrBlock() {
        return BlockUtil._currBlock;
    }
    
    public static void debugPos(final String message, final BlockPos pos) {
        Command.sendMessage(message + pos.getX() + "x, " + pos.getY() + "y, " + pos.getZ() + "z");
    }
    
    public static void placeCrystalOnBlock(final BlockPos pos, final EnumHand hand) {
        final RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        final EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
    }
    
    public static BlockPos[] toBlockPos(final Vec3d[] vec3ds) {
        final BlockPos[] list = new BlockPos[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }
        return list;
    }
    
    public static Vec3d posToVec3d(final BlockPos pos) {
        return new Vec3d((Vec3i)pos);
    }
    
    public static BlockPos vec3dToPos(final Vec3d vec3d) {
        return new BlockPos(vec3d);
    }
    
    public static Boolean isPosInFov(final BlockPos pos) {
        final int dirnumber = RotationUtil.getDirection4D();
        if (dirnumber == 0 && pos.getZ() - BlockUtil.mc.player.getPositionVector().z < 0.0) {
            return false;
        }
        if (dirnumber == 1 && pos.getX() - BlockUtil.mc.player.getPositionVector().x > 0.0) {
            return false;
        }
        if (dirnumber == 2 && pos.getZ() - BlockUtil.mc.player.getPositionVector().z > 0.0) {
            return false;
        }
        return dirnumber != 3 || pos.getX() - BlockUtil.mc.player.getPositionVector().x >= 0.0;
    }
    
    public static boolean isBlockBelowEntitySolid(final Entity entity) {
        if (entity != null) {
            final BlockPos pos = new BlockPos(entity.posX, entity.posY - 1.0, entity.posZ);
            return isBlockSolid(pos);
        }
        return false;
    }
    
    public static boolean isBlockSolid(final BlockPos pos) {
        return !isBlockUnSolid(pos);
    }
    
    public static boolean isBlockUnSolid(final BlockPos pos) {
        return isBlockUnSolid(BlockUtil.mc.world.getBlockState(pos).getBlock());
    }
    
    public static boolean isBlockUnSolid(final Block block) {
        return BlockUtil.unSolidBlocks.contains(block);
    }
    
    public static boolean isBlockUnSafe(final Block block) {
        return !BlockUtil.unSafeBlocks.contains(block);
    }
    
    public static Vec3d[] convertVec3ds(final Vec3d vec3d, final Vec3d[] input) {
        final Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }
    
    public static Vec3d[] convertVec3ds(final EntityPlayer entity, final Vec3d[] input) {
        return convertVec3ds(entity.getPositionVector(), input);
    }
    
    public static boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = BlockUtil.mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)BlockUtil.mc.world, pos) != -1.0f;
    }
    
    private static boolean IsDoneBreaking(final IBlockState blockState) {
        return blockState.getBlock() == Blocks.BEDROCK || blockState.getBlock() == Blocks.AIR || blockState.getBlock() instanceof BlockLiquid;
    }
    
    public static boolean Update(final float range, final boolean rayTrace) {
        if (BlockUtil._currBlock == null) {
            return false;
        }
        final IBlockState state = BlockUtil.mc.world.getBlockState(BlockUtil._currBlock);
        if (!IsDoneBreaking(state) && BlockUtil.mc.player.getDistanceSq(BlockUtil._currBlock) <= Math.pow(range, range)) {
            BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
            EnumFacing facing = EnumFacing.UP;
            if (rayTrace) {
                final RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(BlockUtil._currBlock.getX() + 0.5, BlockUtil._currBlock.getY() - 0.5, BlockUtil._currBlock.getZ() + 0.5));
                if (result != null && result.sideHit != null) {
                    facing = result.sideHit;
                }
            }
            if (!BlockUtil._started) {
                BlockUtil._started = true;
                BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, BlockUtil._currBlock, facing));
                BlockUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, BlockUtil._currBlock, facing));
            }
            else {
                BlockUtil.mc.playerController.onPlayerDamageBlock(BlockUtil._currBlock, facing);
            }
            return true;
        }
        BlockUtil._currBlock = null;
        return false;
    }
    
    public static boolean isValidBlock(final BlockPos pos) {
        final Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
        return !(block instanceof BlockLiquid) && block.getMaterial((IBlockState)null) != Material.AIR;
    }
    
    public static boolean isScaffoldPos(final BlockPos pos) {
        return BlockUtil.mc.world.isAirBlock(pos) || BlockUtil.mc.world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER || BlockUtil.mc.world.getBlockState(pos).getBlock() == Blocks.TALLGRASS || BlockUtil.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid;
    }
    
    public static boolean isBothHole(final BlockPos blockPos) {
        for (final BlockPos pos : getTouchingBlocks(blockPos)) {
            final IBlockState touchingState = BlockUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || (touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN)) {
                return false;
            }
        }
        return true;
    }
    
    public static BlockPos[] getTouchingBlocks(final BlockPos blockPos) {
        return new BlockPos[] { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() };
    }
    
    public static boolean rayTracePlaceCheck(final BlockPos pos, final boolean shouldCheck, final float height) {
        return !shouldCheck || BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d((double)pos.getX(), (double)(pos.getY() + height), (double)pos.getZ()), false, true, false) == null;
    }
    
    public static boolean rayTracePlaceCheck(final BlockPos pos, final boolean shouldCheck) {
        return rayTracePlaceCheck(pos, shouldCheck, 1.0f);
    }
    
    public static boolean rayTracePlaceCheck(final BlockPos pos) {
        return rayTracePlaceCheck(pos, true);
    }
    
    public static BlockPos GetLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(BlockUtil.mc.player.posX), Math.floor(BlockUtil.mc.player.posY), Math.floor(BlockUtil.mc.player.posZ));
    }
    
    public static ValidResult valid(final BlockPos pos) {
        if (!BlockUtil.mc.world.checkNoEntityCollision(new AxisAlignedBB(pos))) {
            return ValidResult.NoEntityCollision;
        }
        if (!checkForNeighbours(pos)) {
            return ValidResult.NoNeighbors;
        }
        final IBlockState l_State = BlockUtil.mc.world.getBlockState(pos);
        if (l_State.getBlock() == Blocks.AIR) {
            final BlockPos[] array;
            final BlockPos[] l_Blocks = array = new BlockPos[] { pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down() };
            for (final BlockPos l_Pos : array) {
                final IBlockState l_State2 = BlockUtil.mc.world.getBlockState(l_Pos);
                if (l_State2.getBlock() != Blocks.AIR) {
                    for (final EnumFacing side : EnumFacing.values()) {
                        final BlockPos neighbor = pos.offset(side);
                        if (BlockUtil.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbor), false)) {
                            return ValidResult.Ok;
                        }
                    }
                }
            }
            return ValidResult.NoNeighbors;
        }
        return ValidResult.AlreadyBlockThere;
    }
    
    public static boolean checkForNeighbours(final BlockPos blockPos) {
        if (!hasNeighbour(blockPos)) {
            for (final EnumFacing side : EnumFacing.values()) {
                final BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    private static boolean hasNeighbour(final BlockPos blockPos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = blockPos.offset(side);
            if (!BlockUtil.mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
                return true;
            }
        }
        return false;
    }
    
    public static int findObiInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = BlockUtil.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock)stack.getItem()).getBlock();
                if (block instanceof BlockEnderChest) {
                    return i;
                }
                if (block instanceof BlockObsidian) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(BlockUtil.mc.player.posX), Math.floor(BlockUtil.mc.player.posY), Math.floor(BlockUtil.mc.player.posZ));
    }
    
    static {
        blackList = Arrays.asList(Blocks.ENDER_CHEST, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, (Block)Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
        shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
        unSafeBlocks = Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL);
        BlockUtil.unSolidBlocks = Arrays.asList((Block)Blocks.FLOWING_LAVA, Blocks.FLOWER_POT, Blocks.SNOW, Blocks.CARPET, Blocks.END_ROD, (Block)Blocks.SKULL, Blocks.FLOWER_POT, Blocks.TRIPWIRE, (Block)Blocks.TRIPWIRE_HOOK, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.LADDER, (Block)Blocks.UNPOWERED_COMPARATOR, (Block)Blocks.POWERED_COMPARATOR, (Block)Blocks.UNPOWERED_REPEATER, (Block)Blocks.POWERED_REPEATER, Blocks.UNLIT_REDSTONE_TORCH, Blocks.REDSTONE_TORCH, (Block)Blocks.REDSTONE_WIRE, Blocks.AIR, (Block)Blocks.PORTAL, Blocks.END_PORTAL, (Block)Blocks.WATER, (Block)Blocks.FLOWING_WATER, (Block)Blocks.LAVA, (Block)Blocks.FLOWING_LAVA, Blocks.SAPLING, (Block)Blocks.RED_FLOWER, (Block)Blocks.YELLOW_FLOWER, (Block)Blocks.BROWN_MUSHROOM, (Block)Blocks.RED_MUSHROOM, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, (Block)Blocks.REEDS, Blocks.PUMPKIN_STEM, Blocks.MELON_STEM, Blocks.WATERLILY, Blocks.NETHER_WART, Blocks.COCOA, Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, (Block)Blocks.TALLGRASS, (Block)Blocks.DEADBUSH, Blocks.VINE, (Block)Blocks.FIRE, Blocks.RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.TORCH);
    }
    
    public enum ValidResult
    {
        NoEntityCollision, 
        AlreadyBlockThere, 
        NoNeighbors, 
        Ok;
    }
}
