
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.entity.*;
import me.cum.fusion.*;
import me.cum.fusion.features.command.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.entity.item.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.*;
import me.cum.fusion.util.*;
import java.util.*;
import net.minecraft.block.state.*;

public class ShulkerAura extends Module
{
    public final List<Block> blackList;
    public final List<Block> shulkerList;
    private int oldSlot;
    private int shulkerSlot;
    private int crystalSlot;
    private int waitTicks;
    private boolean doShulker;
    private boolean doCrystal;
    private boolean openShulker;
    private boolean detonate;
    private boolean finishedDetonate;
    private BlockPos shulkerSpot;
    private BlockPos crystalSpot;
    private direction spoofDirection;
    private EntityPlayer target;
    private final Setting<Boolean> debug;
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> antiWeakness;
    private final Setting<Integer> detonateDelay;
    private final Setting<Integer> endDelay;
    private final Setting<Integer> restartDelay;
    
    public ShulkerAura() {
        super("ShulkerAura", "Uses shulkers to push crystals into enemies", Category.COMBAT, true, false, false);
        this.blackList = Arrays.asList(Blocks.ENDER_CHEST, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, (Block)Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
        this.shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
        this.debug = (Setting<Boolean>)this.register(new Setting("Debug", (T)false));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotation Lock", (T)false));
        this.antiWeakness = (Setting<Boolean>)this.register(new Setting("Anti Weakness", (T)false));
        this.detonateDelay = (Setting<Integer>)this.register(new Setting("Detonate Delay", (T)4, (T)1, (T)10));
        this.endDelay = (Setting<Integer>)this.register(new Setting("Await Delay", (T)4, (T)1, (T)10));
        this.restartDelay = (Setting<Integer>)this.register(new Setting("Attempt Delay", (T)4, (T)1, (T)10));
    }
    
    @Override
    public void onEnable() {
        this.spoofDirection = direction.NORTH;
        this.target = null;
        this.oldSlot = -1;
        this.doShulker = false;
        this.doCrystal = false;
        this.openShulker = false;
        this.detonate = false;
        this.finishedDetonate = false;
        this.crystalSlot = -1;
        this.shulkerSlot = -1;
        this.waitTicks = 0;
    }
    
    private EntityPlayer getTarget() {
        EntityPlayer temp = null;
        for (final EntityPlayer player : ShulkerAura.mc.world.playerEntities) {
            if (player != null && player != ShulkerAura.mc.player && player.getHealth() > 0.0f && ShulkerAura.mc.player.getDistance((Entity)player) < 5.0f && !Fusion.friendManager.isFriend(player.getName())) {
                temp = player;
            }
        }
        if (temp != null && this.debug.getValue()) {
            Command.sendMessage("Target Set: " + temp.getName());
        }
        return temp;
    }
    
    @Override
    public void onUpdate() {
        if (this.doShulker) {
            if (ShulkerAura.mc.player.inventory.currentItem != this.shulkerSlot) {
                if (this.debug.getValue()) {
                    Command.sendMessage("Swapping to slot " + this.shulkerSlot);
                }
                ShulkerAura.mc.player.inventory.currentItem = this.shulkerSlot;
                return;
            }
            this.placeBlock(this.shulkerSpot);
            ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.oldSlot));
            this.doShulker = false;
        }
        else if (this.doCrystal) {
            if (ShulkerAura.mc.player.inventory.currentItem != this.crystalSlot) {
                if (this.debug.getValue()) {
                    Command.sendMessage("Swapping to slot " + this.crystalSlot);
                }
                ShulkerAura.mc.player.inventory.currentItem = this.crystalSlot;
                return;
            }
            this.placeCrystal(this.crystalSpot);
            ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.oldSlot));
            this.doCrystal = false;
            this.openShulker = true;
        }
        else {
            if (this.openShulker) {
                ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.shulkerSpot, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                if (ShulkerAura.mc.currentScreen instanceof GuiShulkerBox) {
                    if (this.debug.getValue()) {
                        Command.sendMessage("Closing Shulker");
                    }
                    ShulkerAura.mc.player.closeScreenAndDropStack();
                    this.openShulker = false;
                    this.detonate = true;
                    this.waitTicks = 0;
                }
                return;
            }
            if (this.detonate) {
                if (this.waitTicks++ > this.detonateDelay.getValue()) {
                    if (this.waitTicks - this.detonateDelay.getValue() > this.restartDelay.getValue()) {
                        if (this.debug.getValue()) {
                            Command.sendMessage("Re-Attempting");
                        }
                        this.detonate = false;
                        this.doCrystal = true;
                    }
                    for (final Entity e : ShulkerAura.mc.world.loadedEntityList) {
                        if (e instanceof EntityEnderCrystal && !e.isDead && ShulkerAura.mc.player.getDistance(e) < 5.0f) {
                            ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(e));
                            ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                            this.detonate = false;
                            this.finishedDetonate = true;
                            this.waitTicks = 0;
                        }
                    }
                }
                return;
            }
            if (this.finishedDetonate) {
                if (this.waitTicks++ > this.endDelay.getValue()) {
                    this.finishedDetonate = false;
                }
                return;
            }
            this.target = this.getTarget();
            this.oldSlot = ShulkerAura.mc.player.inventory.currentItem;
            if (this.target != null) {
                Vec3d offset1 = this.target.getPositionVector().add(1.0, 0.0, 0.0);
                Vec3d offset2 = this.target.getPositionVector().add(2.0, 0.0, 0.0);
                Vec3d offset3 = this.target.getPositionVector().add(3.0, 0.0, 0.0);
                Vec3d offset4 = this.target.getPositionVector().add(1.0, 1.0, 0.0);
                Vec3d offset5 = this.target.getPositionVector().add(2.0, 1.0, 0.0);
                Vec3d offset6 = this.target.getPositionVector().add(3.0, 1.0, 0.0);
                if (ShulkerAura.mc.world.getBlockState(new BlockPos(offset1)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset2)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset3)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset4)).getBlock() == Blocks.AIR && (ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock() == Blocks.AIR || this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) && ShulkerAura.mc.world.getBlockState(new BlockPos(offset6)).getBlock() != Blocks.AIR) {
                    if (this.debug.getValue()) {
                        Command.sendMessage("Target is vulnerable!");
                    }
                    if (this.debug.getValue()) {
                        Command.sendMessage("Method 1");
                    }
                    this.spoofDirection = direction.EAST;
                    this.shulkerSlot = -1;
                    this.crystalSlot = -1;
                    if (this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                        if (this.debug.getValue()) {
                            Command.sendMessage("Shulker already in place.");
                        }
                        this.shulkerSlot = 1337;
                    }
                    else {
                        for (final Block b : this.shulkerList) {
                            if (this.findHotbarBlock(b) != -1) {
                                this.shulkerSlot = this.findHotbarBlock(b);
                                break;
                            }
                        }
                    }
                    this.crystalSlot = this.findHotbarItem(Items.END_CRYSTAL);
                    if (this.shulkerSlot != -1 && this.crystalSlot != -1) {
                        this.shulkerSpot = new BlockPos(offset5);
                        this.crystalSpot = new BlockPos(offset1);
                        if (!this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                            this.doShulker = true;
                        }
                        this.doCrystal = true;
                        return;
                    }
                }
                offset1 = this.target.getPositionVector().add(-1.0, 0.0, 0.0);
                offset2 = this.target.getPositionVector().add(-2.0, 0.0, 0.0);
                offset3 = this.target.getPositionVector().add(-3.0, 0.0, 0.0);
                offset4 = this.target.getPositionVector().add(-1.0, 1.0, 0.0);
                offset5 = this.target.getPositionVector().add(-2.0, 1.0, 0.0);
                offset6 = this.target.getPositionVector().add(-3.0, 1.0, 0.0);
                if (ShulkerAura.mc.world.getBlockState(new BlockPos(offset1)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset2)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset3)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset4)).getBlock() == Blocks.AIR && (ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock() == Blocks.AIR || this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) && ShulkerAura.mc.world.getBlockState(new BlockPos(offset6)).getBlock() != Blocks.AIR) {
                    if (this.debug.getValue()) {
                        Command.sendMessage("Target is vulnerable!");
                    }
                    if (this.debug.getValue()) {
                        Command.sendMessage("Method 2");
                    }
                    this.spoofDirection = direction.WEST;
                    this.shulkerSlot = -1;
                    this.crystalSlot = -1;
                    if (this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                        if (this.debug.getValue()) {
                            Command.sendMessage("Shulker already in place.");
                        }
                        this.shulkerSlot = 1337;
                    }
                    else {
                        for (final Block b : this.shulkerList) {
                            if (this.findHotbarBlock(b) != -1) {
                                this.shulkerSlot = this.findHotbarBlock(b);
                                break;
                            }
                        }
                    }
                    this.crystalSlot = this.findHotbarItem(Items.END_CRYSTAL);
                    if (this.shulkerSlot != -1 && this.crystalSlot != -1) {
                        this.shulkerSpot = new BlockPos(offset5);
                        this.crystalSpot = new BlockPos(offset1);
                        if (!this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                            this.doShulker = true;
                        }
                        this.doCrystal = true;
                        return;
                    }
                }
                offset1 = this.target.getPositionVector().add(0.0, 0.0, 1.0);
                offset2 = this.target.getPositionVector().add(0.0, 0.0, 2.0);
                offset3 = this.target.getPositionVector().add(0.0, 0.0, 3.0);
                offset4 = this.target.getPositionVector().add(0.0, 1.0, 1.0);
                offset5 = this.target.getPositionVector().add(0.0, 1.0, 2.0);
                offset6 = this.target.getPositionVector().add(0.0, 1.0, 3.0);
                if (ShulkerAura.mc.world.getBlockState(new BlockPos(offset1)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset2)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset3)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset4)).getBlock() == Blocks.AIR && (ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock() == Blocks.AIR || this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) && ShulkerAura.mc.world.getBlockState(new BlockPos(offset6)).getBlock() != Blocks.AIR) {
                    if (this.debug.getValue()) {
                        Command.sendMessage("Target is vulnerable!");
                    }
                    if (this.debug.getValue()) {
                        Command.sendMessage("Method 3");
                    }
                    this.spoofDirection = direction.SOUTH;
                    this.shulkerSlot = -1;
                    this.crystalSlot = -1;
                    if (this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                        if (this.debug.getValue()) {
                            Command.sendMessage("Shulker already in place.");
                        }
                        this.shulkerSlot = 1337;
                    }
                    else {
                        for (final Block b : this.shulkerList) {
                            if (this.findHotbarBlock(b) != -1) {
                                this.shulkerSlot = this.findHotbarBlock(b);
                                break;
                            }
                        }
                    }
                    this.crystalSlot = this.findHotbarItem(Items.END_CRYSTAL);
                    if (this.shulkerSlot != -1 && this.crystalSlot != -1) {
                        this.shulkerSpot = new BlockPos(offset5);
                        this.crystalSpot = new BlockPos(offset1);
                        if (!this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                            this.doShulker = true;
                        }
                        this.doCrystal = true;
                        return;
                    }
                }
                offset1 = this.target.getPositionVector().add(0.0, 0.0, -1.0);
                offset2 = this.target.getPositionVector().add(0.0, 0.0, -2.0);
                offset3 = this.target.getPositionVector().add(0.0, 0.0, -3.0);
                offset4 = this.target.getPositionVector().add(0.0, 1.0, -1.0);
                offset5 = this.target.getPositionVector().add(0.0, 1.0, -2.0);
                offset6 = this.target.getPositionVector().add(0.0, 1.0, -3.0);
                if (ShulkerAura.mc.world.getBlockState(new BlockPos(offset1)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset2)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset3)).getBlock() != Blocks.AIR && ShulkerAura.mc.world.getBlockState(new BlockPos(offset4)).getBlock() == Blocks.AIR && (ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock() == Blocks.AIR || this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) && ShulkerAura.mc.world.getBlockState(new BlockPos(offset6)).getBlock() != Blocks.AIR) {
                    if (this.debug.getValue()) {
                        Command.sendMessage("Target is vulnerable!");
                    }
                    if (this.debug.getValue()) {
                        Command.sendMessage("Method 4");
                    }
                    this.spoofDirection = direction.NORTH;
                    this.shulkerSlot = -1;
                    this.crystalSlot = -1;
                    if (this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                        if (this.debug.getValue()) {
                            Command.sendMessage("Shulker already in place.");
                        }
                        this.shulkerSlot = 1337;
                    }
                    else {
                        for (final Block b : this.shulkerList) {
                            if (this.findHotbarBlock(b) != -1) {
                                this.shulkerSlot = this.findHotbarBlock(b);
                                break;
                            }
                        }
                    }
                    this.crystalSlot = this.findHotbarItem(Items.END_CRYSTAL);
                    if (this.shulkerSlot != -1 && this.crystalSlot != -1) {
                        this.shulkerSpot = new BlockPos(offset5);
                        this.crystalSpot = new BlockPos(offset1);
                        if (!this.shulkerList.contains(ShulkerAura.mc.world.getBlockState(new BlockPos(offset5)).getBlock())) {
                            this.doShulker = true;
                        }
                        this.doCrystal = true;
                    }
                }
            }
        }
    }
    
    public int findHotbarItem(final Item itemIn) {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);
            final Item item;
            if (stack != ItemStack.EMPTY && (item = stack.getItem()) == itemIn) {
                return i;
            }
        }
        return -1;
    }
    
    public int findHotbarBlock(final Block blockIn) {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);
            final Block block;
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock && (block = ((ItemBlock)stack.getItem()).getBlock()) == blockIn) {
                return i;
            }
        }
        return -1;
    }
    
    private void placeCrystal(final BlockPos pos) {
        if (this.debug.getValue()) {
            Command.sendMessage("Debug " + pos);
        }
        ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.spoofDirection == direction.NORTH) {
            if (this.rotate.getValue()) {
                ShulkerAura.mc.player.rotationYaw = 180.0f;
            }
            ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(180.0f, 0.0f, ShulkerAura.mc.player.onGround));
        }
        else if (this.spoofDirection == direction.SOUTH) {
            if (this.rotate.getValue()) {
                ShulkerAura.mc.player.rotationYaw = 0.0f;
            }
            ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(0.0f, 0.0f, ShulkerAura.mc.player.onGround));
        }
        else if (this.spoofDirection == direction.WEST) {
            if (this.rotate.getValue()) {
                ShulkerAura.mc.player.rotationYaw = 90.0f;
            }
            ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(90.0f, 0.0f, ShulkerAura.mc.player.onGround));
        }
        else if (this.spoofDirection == direction.EAST) {
            if (this.rotate.getValue()) {
                ShulkerAura.mc.player.rotationYaw = -90.0f;
            }
            ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(-90.0f, 0.0f, ShulkerAura.mc.player.onGround));
        }
        final boolean isSneaking = this.placeBlock(pos, EnumHand.MAIN_HAND, false, true, ShulkerAura.mc.player.isSneaking());
        if (isSneaking) {
            ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)ShulkerAura.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }
    
    public boolean placeBlock(final BlockPos pos, final EnumHand hand, final boolean rotate, final boolean packet, final boolean isSneaking) {
        boolean sneaking = false;
        final EnumFacing side = this.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = ShulkerAura.mc.world.getBlockState(neighbour).getBlock();
        if (!ShulkerAura.mc.player.isSneaking() && (this.blackList.contains(neighbourBlock) || this.shulkerList.contains(neighbourBlock))) {
            ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)ShulkerAura.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            ShulkerAura.mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            RotationUtil.faceVector(hitVec, true);
        }
        this.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
        ShulkerAura.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }
    
    public EnumFacing getFirstFacing(final BlockPos pos) {
        final Iterator<EnumFacing> iterator = this.getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
    
    public List<EnumFacing> getPossibleSides(final BlockPos pos) {
        final ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
        final List<EnumFacing> directions = new ArrayList<EnumFacing>();
        directions.add(EnumFacing.NORTH);
        directions.add(EnumFacing.SOUTH);
        directions.add(EnumFacing.EAST);
        directions.add(EnumFacing.WEST);
        for (final EnumFacing side : directions) {
            final BlockPos neighbour = pos.offset(side);
            if (ShulkerAura.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(ShulkerAura.mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState;
                if ((blockState = ShulkerAura.mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) {
                    continue;
                }
                facings.add(side);
            }
        }
        return facings;
    }
    
    public void rightClickBlock(final BlockPos pos, final Vec3d vec, final EnumHand hand, final EnumFacing direction, final boolean packet) {
        if (packet) {
            final float f = (float)(vec.x - pos.getX());
            final float f2 = (float)(vec.y - pos.getY());
            final float f3 = (float)(vec.z - pos.getZ());
            ShulkerAura.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
        }
        else {
            ShulkerAura.mc.playerController.processRightClickBlock(ShulkerAura.mc.player, ShulkerAura.mc.world, pos, direction, vec, hand);
        }
        ShulkerAura.mc.player.swingArm(EnumHand.MAIN_HAND);
        ShulkerAura.mc.rightClickDelayTimer = 4;
    }
    
    private enum direction
    {
        NORTH, 
        SOUTH, 
        EAST, 
        WEST;
    }
}
