
package me.cum.fusion.util;

import net.minecraft.client.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import net.minecraft.network.play.client.*;
import java.util.*;

public class BlockInteractHelper
{
    public static final List<Block> blackList;
    public static final List<Block> shulkerList;
    private static final Minecraft mc;
    
    public static PlaceResult place(final BlockPos pos, final float p_Distance, final boolean p_Rotate, final boolean p_UseSlabRule) {
        final IBlockState l_State = BlockInteractHelper.mc.world.getBlockState(pos);
        final boolean l_Replaceable = l_State.getMaterial().isReplaceable();
        final boolean l_IsSlabAtBlock = l_State.getBlock() instanceof BlockSlab;
        if (!l_Replaceable && !l_IsSlabAtBlock) {
            return PlaceResult.NotReplaceable;
        }
        if (!checkForNeighbours(pos)) {
            return PlaceResult.Neighbors;
        }
        if (p_UseSlabRule && l_IsSlabAtBlock && !l_State.isFullCube()) {
            return PlaceResult.CantPlace;
        }
        final Vec3d eyesPos = new Vec3d(BlockInteractHelper.mc.player.posX, BlockInteractHelper.mc.player.posY + BlockInteractHelper.mc.player.getEyeHeight(), BlockInteractHelper.mc.player.posZ);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (BlockInteractHelper.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(BlockInteractHelper.mc.world.getBlockState(neighbor), false)) {
                final Vec3d hitVec = new Vec3d((Vec3i)neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.distanceTo(hitVec) <= p_Distance) {
                    final Block neighborPos = BlockInteractHelper.mc.world.getBlockState(neighbor).getBlock();
                    final boolean activated = neighborPos.onBlockActivated((World)BlockInteractHelper.mc.world, pos, BlockInteractHelper.mc.world.getBlockState(pos), (EntityPlayer)BlockInteractHelper.mc.player, EnumHand.MAIN_HAND, side, 0.0f, 0.0f, 0.0f);
                    if (BlockInteractHelper.blackList.contains(neighborPos) || BlockInteractHelper.shulkerList.contains(neighborPos) || activated) {
                        BlockInteractHelper.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BlockInteractHelper.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    }
                    if (p_Rotate) {
                        faceVectorPacketInstant(hitVec);
                    }
                    final EnumActionResult l_Result2 = BlockInteractHelper.mc.playerController.processRightClickBlock(BlockInteractHelper.mc.player, BlockInteractHelper.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    if (l_Result2 != EnumActionResult.FAIL) {
                        BlockInteractHelper.mc.player.swingArm(EnumHand.MAIN_HAND);
                        if (activated) {
                            BlockInteractHelper.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BlockInteractHelper.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                        }
                        return PlaceResult.Placed;
                    }
                }
            }
        }
        return PlaceResult.CantPlace;
    }
    
    public static ValidResult valid(final BlockPos pos) {
        if (!BlockInteractHelper.mc.world.checkNoEntityCollision(new AxisAlignedBB(pos))) {
            return ValidResult.NoEntityCollision;
        }
        if (!checkForNeighbours(pos)) {
            return ValidResult.NoNeighbors;
        }
        final IBlockState l_State = BlockInteractHelper.mc.world.getBlockState(pos);
        if (l_State.getBlock() == Blocks.AIR) {
            final BlockPos[] array;
            final BlockPos[] l_Blocks = array = new BlockPos[] { pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down() };
            for (final BlockPos l_Pos : array) {
                final IBlockState l_State2 = BlockInteractHelper.mc.world.getBlockState(l_Pos);
                if (l_State2.getBlock() != Blocks.AIR) {
                    for (final EnumFacing side : EnumFacing.values()) {
                        final BlockPos neighbor = pos.offset(side);
                        if (BlockInteractHelper.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(BlockInteractHelper.mc.world.getBlockState(neighbor), false)) {
                            return ValidResult.Ok;
                        }
                    }
                }
            }
            return ValidResult.NoNeighbors;
        }
        return ValidResult.AlreadyBlockThere;
    }
    
    public static float[] getLegitRotations(final Vec3d vec) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.x - eyesPos.x;
        final double diffY = vec.y - eyesPos.y;
        final double diffZ = vec.z - eyesPos.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { BlockInteractHelper.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - BlockInteractHelper.mc.player.rotationYaw), BlockInteractHelper.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - BlockInteractHelper.mc.player.rotationPitch) };
    }
    
    private static Vec3d getEyesPos() {
        return new Vec3d(BlockInteractHelper.mc.player.posX, BlockInteractHelper.mc.player.posY + BlockInteractHelper.mc.player.getEyeHeight(), BlockInteractHelper.mc.player.posZ);
    }
    
    public static void faceVectorPacketInstant(final Vec3d vec) {
        final float[] rotations = getLegitRotations(vec);
        BlockInteractHelper.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], rotations[1], BlockInteractHelper.mc.player.onGround));
    }
    
    public static boolean canBeClicked(final BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }
    
    private static Block getBlock(final BlockPos pos) {
        return getState(pos).getBlock();
    }
    
    private static IBlockState getState(final BlockPos pos) {
        return BlockInteractHelper.mc.world.getBlockState(pos);
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
            if (!BlockInteractHelper.mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
                return true;
            }
        }
        return false;
    }
    
    public static List<BlockPos> getSphere(final BlockPos loc, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.getX();
        final int cy = loc.getY();
        final int cz = loc.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                int y = sphere ? (cy - (int)r) : cy;
                while (true) {
                    final float f = sphere ? (cy + r) : ((float)(cy + h));
                    if (y >= f) {
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
    
    static {
        blackList = Arrays.asList(Blocks.ENDER_CHEST, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, (Block)Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER);
        shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
        mc = Minecraft.getMinecraft();
    }
    
    public enum ValidResult
    {
        NoEntityCollision, 
        AlreadyBlockThere, 
        NoNeighbors, 
        Ok;
    }
    
    public enum PlaceResult
    {
        NotReplaceable, 
        Neighbors, 
        CantPlace, 
        Placed;
    }
}
