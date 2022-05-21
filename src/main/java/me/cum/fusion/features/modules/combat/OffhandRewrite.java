
package me.cum.fusion.features.modules.combat;

import me.cum.fusion.features.modules.*;
import java.util.concurrent.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.gameevent.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.*;
import me.cum.fusion.features.Notifications.*;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.common.eventhandler.*;
import org.lwjgl.input.*;
import me.cum.fusion.event.events.*;
import me.cum.fusion.features.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import java.util.function.*;
import net.minecraft.client.gui.*;
import me.cum.fusion.features.gui.*;
import net.minecraft.entity.*;
import net.minecraft.inventory.*;
import net.minecraft.client.gui.inventory.*;
import me.cum.fusion.features.modules.client.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import net.minecraft.entity.item.*;
import me.cum.fusion.util.*;
import java.util.*;

public class OffhandRewrite extends Module
{
    private static OffhandRewrite instance;
    private final Queue<InventoryUtil.Task> taskList;
    private final Timer timer;
    private final Timer secondTimer;
    private final Timer thirdtimer;
    public Mode2 currentMode;
    public int switchval;
    public int totems;
    public int crystals;
    public int gapples;
    public int lastTotemSlot;
    public int lastGappleSlot;
    public int lastCrystalSlot;
    public int lastObbySlot;
    public int lastWebSlot;
    public boolean holdingCrystal;
    public boolean holdingTotem;
    public boolean holdingGapple;
    public boolean didSwitchThisTick;
    public Setting<page> pageSetting;
    public Setting<Mode2> offhandmode;
    public Setting<Boolean> rightGap;
    public Setting<Boolean> swordgap;
    public Setting<Integer> maxSwitch;
    public Setting<Boolean> switchmode;
    public Setting<Bind> SwitchBind;
    public Setting<Float> switchHp;
    public Setting<Float> holeHP;
    public Setting<Boolean> armorCheck;
    public Setting<Integer> actions;
    public Setting<Boolean> crystalCheck;
    public Setting<Boolean> totemElytra;
    public Setting<Boolean> notfromhotbar;
    public Setting<Boolean> fallcheck;
    public Setting<Integer> falldistance;
    public Setting<Boolean> antiPing;
    public Setting<Integer> pingvalue;
    public Setting<Boolean> lagSwitch;
    public Setting<Boolean> debug;
    String s;
    private boolean second;
    private boolean switchedForHealthReason;
    
    public OffhandRewrite() {
        super("OffhandRewrite", "Allows you to switch up your Offhand.", Category.COMBAT, true, false, false);
        this.pageSetting = (Setting<page>)this.register(new Setting("Page", (T)page.MAIN));
        this.offhandmode = (Setting<Mode2>)this.register(new Setting("Offhand", (T)Mode2.TOTEMS, v -> this.pageSetting.getValue() == page.MAIN));
        this.rightGap = (Setting<Boolean>)this.register(new Setting("Right Click Gap", (T)true, v -> this.pageSetting.getValue() == page.MAIN));
        this.swordgap = (Setting<Boolean>)this.register(new Setting("Sword Gap", (T)false, v -> this.pageSetting.getValue() == page.MAIN));
        this.maxSwitch = (Setting<Integer>)this.register(new Setting("Max Switch", (T)10, (T)0, (T)10, v -> this.pageSetting.getValue() == page.MAIN));
        this.switchmode = (Setting<Boolean>)this.register(new Setting("KeyMode", (T)false, v -> this.pageSetting.getValue() == page.MAIN));
        this.SwitchBind = (Setting<Bind>)this.register(new Setting("SwitchKey", (T)new Bind(-1), v -> this.switchmode.getValue() && this.pageSetting.getValue() == page.MAIN));
        this.switchHp = (Setting<Float>)this.register(new Setting("SwitchHP", (T)16.5f, (T)0.1f, (T)36.0f, v -> this.pageSetting.getValue() == page.MAIN));
        this.holeHP = (Setting<Float>)this.register(new Setting("HoleHP", (T)8.0f, (T)0.1f, (T)36.0f, v -> this.pageSetting.getValue() == page.MAIN));
        this.armorCheck = (Setting<Boolean>)this.register(new Setting("ArmorCheck", (T)false, v -> this.pageSetting.getValue() == page.MAIN));
        this.actions = (Setting<Integer>)this.register(new Setting("Packets", (T)4, (T)1, (T)4, v -> this.pageSetting.getValue() == page.MAIN));
        this.crystalCheck = (Setting<Boolean>)this.register(new Setting("Crystal-Check", (T)true, v -> this.pageSetting.getValue() == page.MAIN));
        this.totemElytra = (Setting<Boolean>)this.register(new Setting("TotemElytra", (T)false, v -> this.pageSetting.getValue() == page.MISC));
        this.notfromhotbar = (Setting<Boolean>)this.register(new Setting("NoHotbar", (T)false, v -> this.pageSetting.getValue() == page.MISC));
        this.fallcheck = (Setting<Boolean>)this.register(new Setting("FallCheck", (T)true, v -> this.pageSetting.getValue() == page.MISC));
        this.falldistance = (Setting<Integer>)this.register(new Setting("FallDistance", (T)100, (T)1, (T)100, v -> this.fallcheck.getValue() && this.pageSetting.getValue() == page.MISC));
        this.antiPing = (Setting<Boolean>)this.register(new Setting("Ping Predict", (T)false, v -> this.pageSetting.getValue() == page.MISC));
        this.pingvalue = (Setting<Integer>)this.register(new Setting("Ping Value", (T)200, (T)0, (T)1000, v -> this.antiPing.getValue() && this.pageSetting.getValue() == page.MISC));
        this.lagSwitch = (Setting<Boolean>)this.register(new Setting("Anti Lag", (T)false, v -> this.pageSetting.getValue() == page.MISC));
        this.s = "OffhandRewrite";
        OffhandRewrite.instance = this;
        this.taskList = new ConcurrentLinkedQueue<InventoryUtil.Task>();
        this.timer = new Timer();
        this.secondTimer = new Timer();
        this.thirdtimer = new Timer();
        this.currentMode = Mode2.TOTEMS;
        this.switchval = this.maxSwitch.getValue();
        this.totems = 0;
        this.crystals = 0;
        this.gapples = 0;
        this.lastTotemSlot = -1;
        this.lastGappleSlot = -1;
        this.lastCrystalSlot = -1;
        this.lastObbySlot = -1;
        this.lastWebSlot = -1;
        this.holdingCrystal = false;
        this.holdingTotem = false;
        this.holdingGapple = false;
        this.didSwitchThisTick = false;
        this.second = false;
        this.switchedForHealthReason = false;
    }
    
    public static OffhandRewrite getInstance() {
        if (OffhandRewrite.instance == null) {
            OffhandRewrite.instance = new OffhandRewrite();
        }
        return OffhandRewrite.instance;
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final ProcessRightClickBlockEvent event) {
        if (event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandRewrite.mc.objectMouseOver != null && event.pos == OffhandRewrite.mc.objectMouseOver.getBlockPos()) {
            event.setCanceled(true);
            OffhandRewrite.mc.player.setActiveHand(EnumHand.OFF_HAND);
            OffhandRewrite.mc.playerController.processRightClick((EntityPlayer)OffhandRewrite.mc.player, (World)OffhandRewrite.mc.world, EnumHand.OFF_HAND);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && this.switchmode.getValue() && this.SwitchBind.getValue().getKey() == Keyboard.getEventKey()) {
            if (this.switchval < this.maxSwitch.getValue()) {
                final Mode2 newMode = (Mode2)EnumConverter.increaseEnum(this.currentMode);
                this.offhandmode.setValue(newMode);
                this.setMode(newMode);
                if (this.debug.getValue()) {
                    final TextComponentString textComponentString = new TextComponentString(Fusion.commandManager.getClientMessage() + " \u00c2§r\u00c2§aSwitched offhand to " + newMode.toString());
                    Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString, this.s.length() * 10);
                }
                this.doSwitch();
            }
            if (this.debug.getValue()) {
                new TextComponentString(Fusion.commandManager.getClientMessage() + " \u00c2§r\u00c2§aReached switch limit interval");
            }
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.timer.passedMs(50L)) {
            if (OffhandRewrite.mc.player != null && OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandRewrite.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown(1)) {
                OffhandRewrite.mc.player.setActiveHand(EnumHand.OFF_HAND);
                OffhandRewrite.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
            }
            this.switchval = 0;
        }
        else if (OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandRewrite.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            OffhandRewrite.mc.gameSettings.keyBindUseItem.pressed = false;
        }
        if (nullCheck()) {
            return;
        }
        this.doOffhand();
        if (this.secondTimer.passedMs(50L) && this.second) {
            this.second = false;
            this.timer.reset();
        }
        if (this.thirdtimer.passedDms(1000.0)) {
            this.switchval = 0;
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (!Feature.fullNullCheck() && OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffhandRewrite.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && OffhandRewrite.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                final CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
                if (packet2.getHand() == EnumHand.MAIN_HAND) {
                    if (this.timer.passedMs(50L)) {
                        OffhandRewrite.mc.player.setActiveHand(EnumHand.OFF_HAND);
                        OffhandRewrite.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                    }
                    event.setCanceled(true);
                }
            }
            else if (event.getPacket() instanceof CPacketPlayerTryUseItem && ((CPacketPlayerTryUseItem)event.getPacket()).getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
                event.setCanceled(true);
            }
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return "Crystal";
        }
        if (OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return "Totem";
        }
        if (OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            return "Gapple";
        }
        return null;
    }
    
    public void doOffhand() {
        this.didSwitchThisTick = false;
        this.holdingCrystal = (OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        this.holdingTotem = (OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING);
        this.holdingGapple = (OffhandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE);
        this.totems = OffhandRewrite.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (this.holdingTotem) {
            this.totems += OffhandRewrite.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        }
        this.crystals = OffhandRewrite.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        if (this.holdingCrystal) {
            this.crystals += OffhandRewrite.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        }
        this.gapples = OffhandRewrite.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        if (this.holdingGapple) {
            this.gapples += OffhandRewrite.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        }
        ++this.switchval;
        if (this.switchval < 6) {
            this.doSwitch();
        }
    }
    
    public void doSwitch() {
        this.currentMode = Mode2.TOTEMS;
        if (this.swordgap.getValue() && OffhandRewrite.mc.player.getHealth() > this.switchHp.getValue() && OffhandRewrite.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
            this.currentMode = Mode2.GAPPLES;
        }
        else if (this.rightGap.getValue() && OffhandRewrite.mc.player.getHealth() > this.switchHp.getValue() && OffhandRewrite.mc.gameSettings.keyBindUseItem.isKeyDown() && OffhandRewrite.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && !(OffhandRewrite.mc.currentScreen instanceof GuiContainer) && !(OffhandRewrite.mc.currentScreen instanceof GuiChat) && !(OffhandRewrite.mc.currentScreen instanceof NewGui2)) {
            this.currentMode = Mode2.GAPPLES;
        }
        else if (this.currentMode != Mode2.CRYSTALS && this.offhandmode.getValue() == Mode2.CRYSTALS && ((EntityUtil.isSafe((Entity)OffhandRewrite.mc.player) && EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.holeHP.getValue()) || EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.switchHp.getValue())) {
            this.currentMode = Mode2.CRYSTALS;
        }
        else if (this.currentMode != Mode2.GAPPLES && this.offhandmode.getValue() == Mode2.GAPPLES && ((EntityUtil.isSafe((Entity)OffhandRewrite.mc.player) && EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.holeHP.getValue()) || EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.switchHp.getValue())) {
            this.currentMode = Mode2.GAPPLES;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.crystals == 0) {
            if (this.gapples != 0) {
                this.setMode(Mode2.GAPPLES);
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && ((!EntityUtil.isSafe((Entity)OffhandRewrite.mc.player) && EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) <= this.switchHp.getValue()) || EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) <= this.holeHP.getValue())) {
            if (this.currentMode == Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && ((!EntityUtil.isSafe((Entity)OffhandRewrite.mc.player) && EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) <= this.switchHp.getValue()) || EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) <= this.holeHP.getValue())) {
            if (this.currentMode == Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (OffhandRewrite.mc.player.isAirBorne && this.totemElytra.getValue() && OffhandRewrite.mc.player.isElytraFlying() && OffhandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            this.setMode(Mode2.TOTEMS);
        }
        if (OffhandRewrite.mc.player.fallDistance > this.falldistance.getValue() && this.fallcheck.getValue()) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.switchedForHealthReason && ((EntityUtil.isSafe((Entity)OffhandRewrite.mc.player) && EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.holeHP.getValue()) || EntityUtil.getHealth((Entity)OffhandRewrite.mc.player, true) > this.switchHp.getValue())) {
            this.setMode(this.currentMode);
            this.switchedForHealthReason = false;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.armorCheck.getValue() && (OffhandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || OffhandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || OffhandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || OffhandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR)) {
            this.setMode(this.currentMode);
        }
        if (this.crystalCheck.getValue() && this.calcCrystal()) {
            if (this.debug.getValue()) {
                final TextComponentString textComponentString = new TextComponentString(Fusion.commandManager.getClientMessage() + " \u00c2§r\u00c2§aSwitched to totem because lethal crystal");
                Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString, this.s.length() * 10);
            }
            this.switchedForHealthReason = true;
            this.currentMode = Mode2.TOTEMS;
        }
        if (OffhandRewrite.mc.currentScreen instanceof GuiContainer && !(OffhandRewrite.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (this.antiPing.getValue() && (PingBypass.getInstance().isConnected() ? PingBypass.getInstance().getServerPing() : Fusion.serverManager.getPing()) > this.pingvalue.getValue()) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.lagSwitch.getValue() && Fusion.serverManager.isServerNotResponding()) {
            this.setMode(Mode2.TOTEMS);
        }
        final Item currentOffhandItem = OffhandRewrite.mc.player.getHeldItemOffhand().getItem();
        switch (this.currentMode) {
            case TOTEMS: {
                if (this.totems <= 0) {
                    break;
                }
                if (this.holdingTotem) {
                    break;
                }
                this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                this.putItemInOffhand(this.lastTotemSlot, lastSlot);
                break;
            }
            case GAPPLES: {
                if (this.gapples <= 0) {
                    break;
                }
                if (this.holdingGapple) {
                    break;
                }
                this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                this.putItemInOffhand(this.lastGappleSlot, lastSlot);
                break;
            }
            default: {
                if (this.crystals <= 0) {
                    break;
                }
                if (this.holdingCrystal) {
                    break;
                }
                this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
                break;
            }
        }
        for (int i = 0; i < this.actions.getValue(); ++i) {
            final InventoryUtil.Task task = this.taskList.poll();
            if (task != null) {
                task.run();
                if (task.isSwitching()) {
                    this.didSwitchThisTick = true;
                }
            }
        }
    }
    
    private int getLastSlot(final Item item, final int slotIn) {
        if (item == Items.END_CRYSTAL) {
            return this.lastCrystalSlot;
        }
        if (item == Items.GOLDEN_APPLE) {
            return this.lastGappleSlot;
        }
        if (item == Items.TOTEM_OF_UNDYING) {
            return this.lastTotemSlot;
        }
        if (InventoryUtil.isBlock(item, (Class<? extends Block>)BlockObsidian.class)) {
            return this.lastObbySlot;
        }
        if (InventoryUtil.isBlock(item, (Class<? extends Block>)BlockWeb.class)) {
            return this.lastWebSlot;
        }
        if (item == Items.AIR) {
            return -1;
        }
        return slotIn;
    }
    
    private void putItemInOffhand(final int slotIn, final int slotOut) {
        if (slotIn != -1 && this.taskList.isEmpty()) {
            this.taskList.add(new InventoryUtil.Task(slotIn));
            this.taskList.add(new InventoryUtil.Task(45));
            this.taskList.add(new InventoryUtil.Task(slotOut));
            this.taskList.add(new InventoryUtil.Task());
        }
    }
    
    public void setMode(final Mode2 mode) {
        this.currentMode = ((this.currentMode == mode) ? Mode2.TOTEMS : mode);
    }
    
    public boolean calcCrystal() {
        for (final Entity t : OffhandRewrite.mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && OffhandRewrite.mc.player.getDistanceSq(t.getPosition()) <= 36.0 && DamageUtil.calculateDamage(t, (Entity)OffhandRewrite.mc.player) >= OffhandRewrite.mc.player.getHealth()) {
                return true;
            }
        }
        return false;
    }
    
    public enum Mode2
    {
        TOTEMS, 
        GAPPLES, 
        CRYSTALS;
    }
    
    public enum page
    {
        MAIN, 
        MISC;
    }
}
