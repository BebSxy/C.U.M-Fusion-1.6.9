
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import java.util.concurrent.*;
import me.cum.fusion.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.item.*;
import me.cum.fusion.event.events.*;
import me.cum.fusion.util.*;
import java.awt.*;
import net.minecraft.util.math.*;
import java.util.stream.*;
import java.util.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.enchantment.*;
import net.minecraft.init.*;

public class TooBeeCrystalAura extends Module
{
    private final Timer placeTimer;
    private final Timer breakTimer;
    private final Timer preditTimer;
    private final Timer manualTimer;
    private final Setting<Integer> attackFactor;
    public Setting<Boolean> place;
    public Setting<Integer> placeDelay;
    public Setting<Float> placeRange;
    public Setting<Boolean> explode;
    public Setting<Boolean> packetBreak;
    public Setting<Boolean> sequential;
    public Setting<Boolean> predicts;
    public Setting<Integer> amountOfThreads;
    public Setting<Boolean> rotate;
    public Setting<Boolean> yawStep;
    public Setting<Integer> yawSteps;
    public Setting<Sync> sync;
    public Setting<Boolean> cityPredict;
    public Setting<Integer> breakDelay;
    public Setting<Float> breakRange;
    public Setting<Float> breakWallRange;
    public Setting<Boolean> opPlace;
    Setting<InventoryUtil.Switch> switchMode;
    public Setting<Boolean> suicide;
    public Setting<Boolean> ignoreUseAmount;
    public Setting<Integer> wasteAmount;
    public Setting<Boolean> facePlaceSword;
    public Setting<Float> targetRange;
    public Setting<Float> minDamage;
    public Setting<Float> facePlace;
    public Setting<Float> breakMaxSelfDamage;
    public Setting<Float> breakMinDmg;
    public Setting<Float> minArmor;
    public Setting<SwingMode> swingMode;
    public Setting<Boolean> render;
    public Setting<Boolean> renderDmg;
    Setting<Boolean> fastPop;
    EntityEnderCrystal crystal;
    private final Map<EntityPlayer, Timer> totemPops;
    private EntityLivingBase target;
    private BlockPos pos;
    private int hotBarSlot;
    Multithread multiThread;
    private boolean armor;
    private boolean armorTarget;
    private int crystalCount;
    private int predictWait;
    private int predictPackets;
    private boolean packetCalc;
    private float yaw;
    private EntityLivingBase realTarget;
    private int predict;
    private float pitch;
    private boolean rotating;
    
    public TooBeeCrystalAura() {
        super("FusionAC beta", "This is a beta module", Category.COMBAT, true, false, false);
        this.placeTimer = new Timer();
        this.breakTimer = new Timer();
        this.preditTimer = new Timer();
        this.manualTimer = new Timer();
        this.attackFactor = (Setting<Integer>)this.register(new Setting("PredictDelay", (T)0, (T)0, (T)200));
        this.place = (Setting<Boolean>)this.register(new Setting("Place", (T)true));
        this.placeDelay = (Setting<Integer>)this.register(new Setting("PlaceDelay", (T)0, (T)0, (T)500));
        this.placeRange = (Setting<Float>)this.register(new Setting("PlaceRange", (T)4.0f, (T)0.1f, (T)7.0f));
        this.explode = (Setting<Boolean>)this.register(new Setting("Break", (T)true));
        this.packetBreak = (Setting<Boolean>)this.register(new Setting("PacketBreak", (T)true));
        this.sequential = (Setting<Boolean>)this.register(new Setting("Sequential", (T)true));
        this.predicts = (Setting<Boolean>)this.register(new Setting("Predict", (T)true));
        this.amountOfThreads = (Setting<Integer>)this.register(new Setting("Threads", (T)1, (T)1, (T)10));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", (T)true));
        this.yawStep = (Setting<Boolean>)this.register(new Setting("YawStep", (T)true, v -> this.rotate.getValue()));
        this.yawSteps = (Setting<Integer>)this.register(new Setting("Step", (T)7, (T)0, (T)32, v -> this.rotate.getValue() && this.yawStep.getValue()));
        this.sync = (Setting<Sync>)this.register(new Setting("Sync", (T)Sync.Sound));
        this.cityPredict = (Setting<Boolean>)this.register(new Setting("CityPredict", (T)true));
        this.breakDelay = (Setting<Integer>)this.register(new Setting("BreakDelay", (T)0, (T)0, (T)500));
        this.breakRange = (Setting<Float>)this.register(new Setting("BreakRange", (T)4.0f, (T)0.1f, (T)7.0f));
        this.breakWallRange = (Setting<Float>)this.register(new Setting("BreakWallRange", (T)4.0f, (T)0.1f, (T)7.0f));
        this.opPlace = (Setting<Boolean>)this.register(new Setting("1.13 Place", (T)true));
        this.switchMode = (Setting<InventoryUtil.Switch>)this.register(new Setting("Switch", (T)InventoryUtil.Switch.SILENT));
        this.suicide = (Setting<Boolean>)this.register(new Setting("AntiSuicide", (T)true));
        this.ignoreUseAmount = (Setting<Boolean>)this.register(new Setting("IgnoreUseAmount", (T)true));
        this.wasteAmount = (Setting<Integer>)this.register(new Setting("UseAmount", (T)4, (T)1, (T)5));
        this.facePlaceSword = (Setting<Boolean>)this.register(new Setting("FacePlaceSword", (T)true));
        this.targetRange = (Setting<Float>)this.register(new Setting("TargetRange", (T)4.0f, (T)1.0f, (T)12.0f));
        this.minDamage = (Setting<Float>)this.register(new Setting("MinDamage", (T)4.0f, (T)0.1f, (T)20.0f));
        this.facePlace = (Setting<Float>)this.register(new Setting("FacePlaceHP", (T)4.0f, (T)0.0f, (T)36.0f));
        this.breakMaxSelfDamage = (Setting<Float>)this.register(new Setting("BreakMaxSelf", (T)4.0f, (T)0.1f, (T)12.0f));
        this.breakMinDmg = (Setting<Float>)this.register(new Setting("BreakMinDmg", (T)4.0f, (T)0.1f, (T)7.0f));
        this.minArmor = (Setting<Float>)this.register(new Setting("MinArmor", (T)4.0f, (T)0.1f, (T)80.0f));
        this.swingMode = (Setting<SwingMode>)this.register(new Setting("Swing", (T)SwingMode.None));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", (T)true));
        this.renderDmg = (Setting<Boolean>)this.register(new Setting("RenderDmg", (T)true));
        this.fastPop = (Setting<Boolean>)this.register(new Setting("FastPop", (T)false));
        this.totemPops = new ConcurrentHashMap<EntityPlayer, Timer>();
        this.multiThread = new Multithread();
        this.yaw = 0.0f;
        this.pitch = 0.0f;
        this.rotating = false;
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
    
    @Override
    public void onEnable() {
        this.placeTimer.reset();
        this.breakTimer.reset();
        this.predictWait = 0;
        this.hotBarSlot = -1;
        this.pos = null;
        this.crystal = null;
        this.predict = 0;
        this.predictPackets = 1;
        this.target = null;
        this.packetCalc = false;
        this.realTarget = null;
        this.armor = false;
        this.armorTarget = false;
        this.totemPops.clear();
    }
    
    @Override
    public void onDisable() {
        this.rotating = false;
        this.totemPops.clear();
    }
    
    @SubscribeEvent
    public void onBlockEvent(final BlockEvent event) {
        if (this.cityPredict.getValue() && this.getTarget() != null && event.pos == EntityUtil.is_cityable(this.getTarget(), this.opPlace.getValue())) {
            placeCrystalOnBlock(event.pos.down(), (TooBeeCrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, false, false, this.switchMode.getValue() == InventoryUtil.Switch.SILENT);
        }
    }
    
    @SubscribeEvent
    public void onPlayerWalkingUpdated(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0) {
            if (this.rotate.getValue()) {
                this.onCrystal();
            }
            if (this.rotate.getValue() && this.crystal != null) {
                final float[] angle = calculateAngles(this.crystal.getPositionVector());
                for (int i = 0; i <= this.yawSteps.getValue(); ++i) {
                    Fusion.rotationManagerNew.setYaw(angle[0] / i);
                    Fusion.rotationManagerNew.setPitch(angle[1] / i);
                }
            }
        }
    }
    
    private boolean isDoublePopable(final EntityPlayer player, final float damage) {
        final double health = player.getHealth();
        if (this.fastPop.getValue() && health <= 1.0 && damage > health + 0.5 && damage <= 4.0) {
            final Timer timer = this.totemPops.get(player);
            return timer == null || timer.passed(500L);
        }
        return false;
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void listenSentPackets(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && this.rotate.getValue() && this.yawStep.getValue() && this.pos != null) {
            for (float step = this.yawSteps.getValue(); step > 0.0f; --step) {
                Fusion.rotationManager.setYaw(this.yaw / step);
                Fusion.rotationManager.setPitch(this.pitch / step);
            }
        }
        if (event.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && ((CPacketUseEntity)event.getPacket()).getEntityFromWorld((World)TooBeeCrystalAura.mc.world) instanceof EntityEnderCrystal && this.sync.getValue() == Sync.Attack) {
            Objects.requireNonNull(((CPacketUseEntity)event.getPacket()).getEntityFromWorld((World)TooBeeCrystalAura.mc.world)).setDead();
        }
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (!this.rotate.getValue() && event.phase == TickEvent.Phase.START) {
            this.pos = this.calculatePosition();
            this.crystal = this.calculateCrystal();
            for (int i = this.amountOfThreads.getValue(); i >= 1; --i) {
                final Thread thr;
                TooBeeCrystalAura.mc.addScheduledTask(() -> {
                    thr = new Thread(this::onCrystal);
                    thr.setDaemon(true);
                    thr.start();
                    return;
                });
            }
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.realTarget != null) {
            return this.realTarget.getName();
        }
        return null;
    }
    
    public void onCrystal() {
        if (TooBeeCrystalAura.mc.world == null || TooBeeCrystalAura.mc.player == null) {
            return;
        }
        this.realTarget = null;
        this.manualBreaker();
        this.crystalCount = 0;
        if (!this.ignoreUseAmount.getValue()) {
            for (final Entity crystal : TooBeeCrystalAura.mc.world.loadedEntityList) {
                if (crystal instanceof EntityEnderCrystal) {
                    if (!this.IsValidCrystal(crystal)) {
                        continue;
                    }
                    boolean count = false;
                    final double damage = this.calculateDamage(this.target.getPosition().getX() + 0.5, this.target.getPosition().getY() + 1.0, this.target.getPosition().getZ() + 0.5, (Entity)this.target);
                    if (damage >= this.minDamage.getValue()) {
                        count = true;
                    }
                    if (!count) {
                        continue;
                    }
                    ++this.crystalCount;
                }
            }
        }
        this.hotBarSlot = -1;
        if (TooBeeCrystalAura.mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
            int crystalSlot = (TooBeeCrystalAura.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? TooBeeCrystalAura.mc.player.inventory.currentItem : -1;
            if (crystalSlot == -1) {
                for (int l = 0; l < 9; ++l) {
                    if (TooBeeCrystalAura.mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                        crystalSlot = l;
                        this.hotBarSlot = l;
                        break;
                    }
                }
            }
            if (crystalSlot == -1) {
                this.pos = null;
                this.target = null;
                return;
            }
        }
        if (TooBeeCrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && TooBeeCrystalAura.mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
            this.pos = null;
            this.target = null;
            return;
        }
        if (this.target == null) {
            this.target = (EntityLivingBase)this.getTarget();
        }
        if (this.target == null) {
            this.crystal = null;
            return;
        }
        if (this.target.getDistance((Entity)TooBeeCrystalAura.mc.player) > 12.0f) {
            this.crystal = null;
            this.target = null;
        }
        if (this.crystal != null && this.explode.getValue() && this.breakTimer.passedMs(this.breakDelay.getValue())) {
            this.breakTimer.reset();
            if (this.packetBreak.getValue()) {
                new Thread(() -> TooBeeCrystalAura.mc.player.connection.sendPacket((Packet)new CPacketUseEntity((Entity)this.crystal))).start();
                if (this.sync.getValue() == Sync.Instant) {
                    this.crystal.setDead();
                }
                if (this.sequential.getValue()) {
                    final BlockPos crystalPos = new BlockPos(Math.floor(this.crystal.posX), Math.floor(this.crystal.posY - 1.0), Math.floor(this.crystal.posZ));
                    new Thread(() -> placeCrystalOnBlock(crystalPos, (TooBeeCrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, false, false, this.switchMode.getValue() == InventoryUtil.Switch.SILENT)).start();
                }
            }
            else {
                new Thread(() -> TooBeeCrystalAura.mc.playerController.attackEntity((EntityPlayer)TooBeeCrystalAura.mc.player, (Entity)this.crystal)).start();
                if (this.sync.getValue() == Sync.Instant) {
                    this.crystal.setDead();
                }
                if (this.sequential.getValue()) {
                    final BlockPos crystalPos = new BlockPos(Math.floor(this.crystal.posX), Math.floor(this.crystal.posY - 1.0), Math.floor(this.crystal.posZ));
                    final BlockPos crystalPos2;
                    new Thread(() -> placeCrystalOnBlock(crystalPos2, (TooBeeCrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, false, false, this.switchMode.getValue() == InventoryUtil.Switch.SILENT)).start();
                }
            }
            if (this.swingMode.getValue() == SwingMode.MainHand) {
                TooBeeCrystalAura.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            else if (this.swingMode.getValue() == SwingMode.OffHand) {
                TooBeeCrystalAura.mc.player.swingArm(EnumHand.OFF_HAND);
            }
        }
        if (this.placeTimer.passedMs(this.placeDelay.getValue()) && this.place.getValue() && this.pos != null) {
            this.placeTimer.reset();
            if (!this.ignoreUseAmount.getValue()) {
                final int crystalLimit = this.wasteAmount.getValue();
                if (this.crystalCount >= crystalLimit) {
                    return;
                }
                if (this.crystalCount < crystalLimit && this.pos != null) {
                    new Thread(() -> placeCrystalOnBlock(this.pos, (TooBeeCrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, false, false, this.switchMode.getValue() == InventoryUtil.Switch.SILENT)).start();
                }
            }
            else if (this.pos != null) {
                new Thread(() -> placeCrystalOnBlock(this.pos, (TooBeeCrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, false, false, this.switchMode.getValue() == InventoryUtil.Switch.SILENT)).start();
            }
        }
    }
    
    public EntityEnderCrystal calculateCrystal() {
        final EntityEnderCrystal idealCrystal = (EntityEnderCrystal)TooBeeCrystalAura.mc.world.getLoadedEntityList().stream().filter(entity -> entity instanceof EntityEnderCrystal).filter(entity -> TooBeeCrystalAura.mc.player.canEntityBeSeen(entity)).map(entity -> entity).min(Comparator.comparing(c -> TooBeeCrystalAura.mc.player.getDistance(c) < this.breakRange.getValue())).orElse(null);
        return idealCrystal;
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
    
    boolean rayTracePlaceCheck(final BlockPos pos, final boolean shouldCheck, final float height) {
        return !shouldCheck || (TooBeeCrystalAura.mc.world.rayTraceBlocks(new Vec3d(TooBeeCrystalAura.mc.player.posX, TooBeeCrystalAura.mc.player.posY + TooBeeCrystalAura.mc.player.getEyeHeight(), TooBeeCrystalAura.mc.player.posZ), new Vec3d((double)pos.getX(), (double)(pos.getY() + height), (double)pos.getZ()), false, true, false) == null && (calculateAngles(new Vec3d((double)pos.getX(), (double)(pos.getY() + height), (double)pos.getZ()))[0] <= 90.0f || calculateAngles(new Vec3d((double)pos.getX(), (double)(pos.getY() + height), (double)pos.getZ()))[1] <= 90.0f));
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPacketReceive(final PacketEvent.Receive event) {
        final SPacketSpawnObject packet;
        if (event.getPacket() instanceof SPacketSpawnObject && (packet = (SPacketSpawnObject)event.getPacket()).getType() == 51 && this.predicts.getValue() && this.preditTimer.passedMs(this.attackFactor.getValue()) && this.predicts.getValue() && this.explode.getValue() && this.packetBreak.getValue() && this.target != null) {
            final CPacketUseEntity predict = new CPacketUseEntity();
            predict.entityId = packet.getEntityID();
            predict.action = CPacketUseEntity.Action.ATTACK;
            TooBeeCrystalAura.mc.player.connection.sendPacket((Packet)predict);
        }
        if (event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect)event.getPacket()).getCategory() == SoundCategory.BLOCKS && ((SPacketSoundEffect)event.getPacket()).getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            for (final Entity crystal : TooBeeCrystalAura.mc.world.loadedEntityList) {
                if (crystal instanceof EntityEnderCrystal && crystal.getDistance(((SPacketSoundEffect)event.getPacket()).getX(), ((SPacketSoundEffect)event.getPacket()).getY(), ((SPacketSoundEffect)event.getPacket()).getZ()) <= 6.0 && this.sync.getValue() == Sync.Sound) {
                    crystal.setDead();
                }
            }
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet2 = (SPacketEntityStatus)event.getPacket();
            if (packet2.getOpCode() == 35 && packet2.getEntity((World)AutoCrystal.mc.world) instanceof EntityPlayer) {
                this.totemPops.put((EntityPlayer)packet2.getEntity((World)AutoCrystal.mc.world), new Timer().reset());
            }
        }
    }
    
    public BlockPos calculatePosition() {
        double damage = 0.5;
        for (final BlockPos blockPos : this.placePostions(this.placeRange.getValue())) {
            final double targetRange;
            if (blockPos != null && this.target != null && TooBeeCrystalAura.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(blockPos)).isEmpty() && (targetRange = this.target.getDistance((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ())) <= this.targetRange.getValue() && !this.target.isDead) {
                if (this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0f) {
                    continue;
                }
                final double targetDmg = this.calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5, (Entity)this.target);
                if (this.isDoublePopable((EntityPlayer)this.target, (float)targetDmg)) {
                    this.pos = blockPos;
                    damage = targetDmg;
                }
                this.armor = false;
                for (final ItemStack is : this.target.getArmorInventoryList()) {
                    final float green = (is.getMaxDamage() - (float)is.getItemDamage()) / is.getMaxDamage();
                    final float red = 1.0f - green;
                    final int dmg = 100 - (int)(red * 100.0f);
                    if (dmg > this.minArmor.getValue()) {
                        continue;
                    }
                    this.armor = true;
                }
                Label_0514: {
                    if (targetDmg < this.minDamage.getValue()) {
                        if (this.facePlaceSword.getValue()) {
                            if (this.target.getAbsorptionAmount() + this.target.getHealth() <= this.facePlace.getValue()) {
                                break Label_0514;
                            }
                        }
                        else if (!(TooBeeCrystalAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && this.target.getAbsorptionAmount() + this.target.getHealth() <= this.facePlace.getValue()) {
                            break Label_0514;
                        }
                        if (this.facePlaceSword.getValue()) {
                            if (!this.armor) {
                                continue;
                            }
                        }
                        else if (TooBeeCrystalAura.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || !this.armor) {
                            continue;
                        }
                    }
                }
                final double selfDmg;
                if ((selfDmg = this.calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5, (Entity)TooBeeCrystalAura.mc.player)) >= TooBeeCrystalAura.mc.player.getHealth() + TooBeeCrystalAura.mc.player.getAbsorptionAmount() && selfDmg >= targetDmg && targetDmg < this.target.getHealth() + this.target.getAbsorptionAmount()) {
                    continue;
                }
                if (damage >= targetDmg) {
                    continue;
                }
                this.pos = blockPos;
                damage = targetDmg;
            }
        }
        if (damage == 0.5) {
            this.pos = null;
            this.target = null;
            this.realTarget = null;
        }
        return this.pos;
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (this.pos != null && this.render.getValue() && this.target != null) {
            final AxisAlignedBB bb = TooBeeCrystalAura.mc.world.getBlockState(this.pos).getSelectedBoundingBox((World)TooBeeCrystalAura.mc.world, this.pos);
            final Vec3d interp = EntityUtil.interpolateEntity((Entity)RenderUtil.mc.player, TooBeeCrystalAura.mc.getRenderPartialTicks());
            for (final EnumFacing face : EnumFacing.values()) {
                RenderUtil.drawGradientPlaneBB(bb.grow(0.0020000000949949026).offset(-interp.x, -interp.y, -interp.z), face, new Color(ColorUtil.rainbow(50).getRed(), ColorUtil.rainbow(50).getGreen(), ColorUtil.rainbow(50).getBlue(), 127), ColorUtil.invert(new Color(ColorUtil.rainbow(50).getRed(), ColorUtil.rainbow(50).getGreen(), ColorUtil.rainbow(50).getBlue(), 127)), 2.0);
            }
            RenderUtil.drawGradientBlockOutline(bb.grow(0.0020000000949949026).offset(-interp.x, -interp.y, -interp.z), ColorUtil.invert(new Color(ColorUtil.rainbow(50).getRed(), ColorUtil.rainbow(50).getGreen(), ColorUtil.rainbow(50).getBlue(), 255)), new Color(ColorUtil.rainbow(50).getRed(), ColorUtil.rainbow(50).getGreen(), ColorUtil.rainbow(50).getBlue(), 255), 2.0f);
            if (this.renderDmg.getValue()) {
                final double renderDamage = this.calculateDamage(this.pos.getX() + 0.5, this.pos.getY() + 1.0, this.pos.getZ() + 0.5, (Entity)this.target);
                RenderUtil.drawText(this.pos, ((Math.floor(renderDamage) == renderDamage) ? Integer.valueOf((int)renderDamage) : String.format("%.1f", renderDamage)) + "");
            }
        }
    }
    
    public static float[] calculateAngles(final Vec3d vector) {
        final float yaw = (float)(Math.toDegrees(Math.atan2(vector.z, vector.x)) - 90.0);
        final float pitch = (float)Math.toDegrees(-Math.atan2(vector.y, Math.hypot(vector.x, vector.z)));
        return new float[] { MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch) };
    }
    
    boolean isBlind(final BlockPos pos) {
        return TooBeeCrystalAura.mc.world.rayTraceBlocks(new Vec3d(TooBeeCrystalAura.mc.player.posX, TooBeeCrystalAura.mc.player.posY + TooBeeCrystalAura.mc.player.getEyeHeight(), TooBeeCrystalAura.mc.player.posZ), new Vec3d(pos.getX() + 0.5, (double)(pos.getY() + 1), pos.getZ() + 0.5), false, true, false) == null;
    }
    
    private boolean IsValidCrystal(final Entity p_Entity) {
        if (p_Entity == null) {
            return false;
        }
        if (!(p_Entity instanceof EntityEnderCrystal)) {
            return false;
        }
        if (this.target == null) {
            return false;
        }
        if (p_Entity.getDistance((Entity)TooBeeCrystalAura.mc.player) > this.breakRange.getValue()) {
            return false;
        }
        if (!TooBeeCrystalAura.mc.player.canEntityBeSeen(p_Entity) && p_Entity.getDistance((Entity)TooBeeCrystalAura.mc.player) > this.breakWallRange.getValue()) {
            return false;
        }
        if (this.target.isDead || this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0f) {
            return false;
        }
        final double targetDmg = this.calculateDamage(p_Entity.getPosition().getX() + 0.5, p_Entity.getPosition().getY() + 1.0, p_Entity.getPosition().getZ() + 0.5, (Entity)this.target);
        if (EntityUtil.isInHole((Entity)TooBeeCrystalAura.mc.player) && targetDmg >= 1.0) {
            return true;
        }
        final double selfDmg = this.calculateDamage(p_Entity.getPosition().getX() + 0.5, p_Entity.getPosition().getY() + 1.0, p_Entity.getPosition().getZ() + 0.5, (Entity)TooBeeCrystalAura.mc.player);
        if (!this.suicide.getValue() && selfDmg < TooBeeCrystalAura.mc.player.getHealth() + TooBeeCrystalAura.mc.player.getAbsorptionAmount() && targetDmg >= this.target.getAbsorptionAmount() + this.target.getHealth()) {
            return true;
        }
        this.armorTarget = false;
        for (final ItemStack is : this.target.getArmorInventoryList()) {
            final float green = (is.getMaxDamage() - (float)is.getItemDamage()) / is.getMaxDamage();
            final float red = 1.0f - green;
            final int dmg = 100 - (int)(red * 100.0f);
            if (dmg > this.minArmor.getValue()) {
                continue;
            }
            this.armorTarget = true;
        }
        return (targetDmg >= this.breakMinDmg.getValue() && selfDmg <= this.breakMaxSelfDamage.getValue()) || (EntityUtil.isInHole((Entity)this.target) && this.target.getHealth() + this.target.getAbsorptionAmount() <= this.facePlace.getValue());
    }
    
    EntityPlayer getTarget() {
        EntityPlayer closestPlayer = null;
        for (final EntityPlayer entity : TooBeeCrystalAura.mc.world.playerEntities) {
            if (TooBeeCrystalAura.mc.player != null && !TooBeeCrystalAura.mc.player.isDead && !entity.isDead && entity != TooBeeCrystalAura.mc.player && !Fusion.friendManager.isFriend(entity.getName())) {
                if (entity.getDistance((Entity)TooBeeCrystalAura.mc.player) > 12.0f) {
                    continue;
                }
                this.armorTarget = false;
                for (final ItemStack is : entity.getArmorInventoryList()) {
                    final float green = (is.getMaxDamage() - (float)is.getItemDamage()) / is.getMaxDamage();
                    final float red = 1.0f - green;
                    final int dmg = 100 - (int)(red * 100.0f);
                    if (dmg > this.minArmor.getValue()) {
                        continue;
                    }
                    this.armorTarget = true;
                }
                if (EntityUtil.isInHole((Entity)entity) && entity.getAbsorptionAmount() + entity.getHealth() > this.facePlace.getValue() && !this.armorTarget && this.minDamage.getValue() > 2.2f) {
                    continue;
                }
                if (closestPlayer == null) {
                    closestPlayer = entity;
                }
                else {
                    if (closestPlayer.getDistance((Entity)TooBeeCrystalAura.mc.player) <= entity.getDistance((Entity)TooBeeCrystalAura.mc.player)) {
                        continue;
                    }
                    closestPlayer = entity;
                }
            }
        }
        return closestPlayer;
    }
    
    private void manualBreaker() {
        final RayTraceResult result;
        if (this.manualTimer.passedMs(200L) && TooBeeCrystalAura.mc.gameSettings.keyBindUseItem.isKeyDown() && TooBeeCrystalAura.mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && TooBeeCrystalAura.mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && TooBeeCrystalAura.mc.player.inventory.getCurrentItem().getItem() != Items.BOW && TooBeeCrystalAura.mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE && (result = TooBeeCrystalAura.mc.objectMouseOver) != null) {
            if (result.typeOfHit.equals((Object)RayTraceResult.Type.ENTITY)) {
                final Entity entity = result.entityHit;
                if (entity instanceof EntityEnderCrystal) {
                    if (this.packetBreak.getValue()) {
                        TooBeeCrystalAura.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity));
                    }
                    else {
                        TooBeeCrystalAura.mc.playerController.attackEntity((EntityPlayer)TooBeeCrystalAura.mc.player, entity);
                    }
                    this.manualTimer.reset();
                }
            }
            else if (result.typeOfHit.equals((Object)RayTraceResult.Type.BLOCK)) {
                final BlockPos mousePos = new BlockPos((double)TooBeeCrystalAura.mc.objectMouseOver.getBlockPos().getX(), TooBeeCrystalAura.mc.objectMouseOver.getBlockPos().getY() + 1.0, (double)TooBeeCrystalAura.mc.objectMouseOver.getBlockPos().getZ());
                for (final Entity target : TooBeeCrystalAura.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(mousePos))) {
                    if (!(target instanceof EntityEnderCrystal)) {
                        continue;
                    }
                    if (this.packetBreak.getValue()) {
                        TooBeeCrystalAura.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(target));
                    }
                    else {
                        TooBeeCrystalAura.mc.playerController.attackEntity((EntityPlayer)TooBeeCrystalAura.mc.player, target);
                    }
                    this.manualTimer.reset();
                }
            }
        }
    }
    
    private boolean canSeePos(final BlockPos pos) {
        return TooBeeCrystalAura.mc.world.rayTraceBlocks(new Vec3d(TooBeeCrystalAura.mc.player.posX, TooBeeCrystalAura.mc.player.posY + TooBeeCrystalAura.mc.player.getEyeHeight(), TooBeeCrystalAura.mc.player.posZ), new Vec3d((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), false, true, false) == null;
    }
    
    private NonNullList<BlockPos> placePostions(final float placeRange) {
        final NonNullList positions = NonNullList.create();
        positions.addAll((Collection)getSphere(new BlockPos(Math.floor(TooBeeCrystalAura.mc.player.posX), Math.floor(TooBeeCrystalAura.mc.player.posY), Math.floor(TooBeeCrystalAura.mc.player.posZ)), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> this.canPlaceCrystal(pos, true)).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        return (NonNullList<BlockPos>)positions;
    }
    
    private boolean canPlaceCrystal(final BlockPos blockPos, final boolean specialEntityCheck) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (!this.opPlace.getValue()) {
                if (TooBeeCrystalAura.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && TooBeeCrystalAura.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }
                if (TooBeeCrystalAura.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || TooBeeCrystalAura.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                    return false;
                }
                if (!specialEntityCheck) {
                    return TooBeeCrystalAura.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost)).isEmpty() && TooBeeCrystalAura.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost2)).isEmpty();
                }
                for (final Entity entity : TooBeeCrystalAura.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal) {
                        continue;
                    }
                    return false;
                }
                for (final Entity entity : TooBeeCrystalAura.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost2))) {
                    if (entity instanceof EntityEnderCrystal) {
                        continue;
                    }
                    return false;
                }
            }
            else {
                if (TooBeeCrystalAura.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && TooBeeCrystalAura.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }
                if (TooBeeCrystalAura.mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
                    return false;
                }
                if (!specialEntityCheck) {
                    return TooBeeCrystalAura.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost)).isEmpty();
                }
                for (final Entity entity : TooBeeCrystalAura.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal) {
                        continue;
                    }
                    return false;
                }
            }
        }
        catch (Exception ignored) {
            return false;
        }
        return true;
    }
    
    private float calculateDamage(final double posX, final double posY, final double posZ, final Entity entity) {
        final float doubleExplosionSize = 12.0f;
        final double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        }
        catch (Exception ex) {}
        final double v = (1.0 - distancedsize) * blockDensity;
        final float damage = (float)(int)((v * v + v) / 2.0 * 7.0 * 12.0 + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = this.getBlastReduction((EntityLivingBase)entity, this.getDamageMultiplied(damage), new Explosion((World)TooBeeCrystalAura.mc.world, (Entity)null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finald;
    }
    
    private float getBlastReduction(final EntityLivingBase entity, final float damageI, final Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer)entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float)ep.getTotalArmorValue(), (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            }
            catch (Exception ex) {}
            final float f = MathHelper.clamp((float)k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float)entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }
    
    private float getDamageMultiplied(final float damage) {
        final int diff = TooBeeCrystalAura.mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }
    
    public enum SwingMode
    {
        MainHand, 
        OffHand, 
        None;
    }
    
    public enum Sync
    {
        None, 
        Instant, 
        Attack, 
        Sound;
    }
}
