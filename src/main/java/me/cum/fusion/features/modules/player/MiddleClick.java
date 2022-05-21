
package me.cum.fusion.features.modules.player;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.features.*;
import org.lwjgl.input.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import me.cum.fusion.*;
import me.cum.fusion.features.modules.misc.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import com.mojang.realmsclient.gui.*;
import me.cum.fusion.features.command.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import me.cum.fusion.util.*;
import net.minecraftforge.registries.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class MiddleClick extends Module
{
    private boolean clicked;
    private boolean clickedbutton;
    private final Setting<Boolean> friend;
    private final Setting<Boolean> pearl;
    private final Setting<Mode> mode;
    
    public MiddleClick() {
        super("MiddleClick", "Stuff for middle clicking", Module.Category.PLAYER, true, false, false);
        this.clicked = false;
        this.clickedbutton = false;
        this.friend = (Setting<Boolean>)this.register(new Setting("Friend", (T)false));
        this.pearl = (Setting<Boolean>)this.register(new Setting("Pearl", (T)false));
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", (T)Mode.MiddleClick, v -> this.pearl.getValue()));
    }
    
    public void onEnable() {
        if (this.pearl.getValue() && !Feature.fullNullCheck() && this.mode.getValue() == Mode.Toggle) {
            this.throwPearl();
            this.disable();
        }
    }
    
    public void onUpdate() {
        if (this.friend.getValue()) {
            if (Mouse.isButtonDown(2)) {
                if (!this.clicked && Util.mc.currentScreen == null) {
                    this.onClick();
                }
                this.clicked = true;
            }
            else {
                this.clicked = false;
            }
        }
    }
    
    public void onTick() {
        if (this.pearl.getValue() && this.mode.getValue() == Mode.MiddleClick) {
            if (Mouse.isButtonDown(2)) {
                if (!this.clickedbutton) {
                    this.throwPearl();
                }
                this.clickedbutton = true;
            }
            else {
                this.clickedbutton = false;
            }
        }
    }
    
    private void onClick() {
        if (this.friend.getValue()) {
            final RayTraceResult result = Util.mc.objectMouseOver;
            final Entity entity;
            if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
                if (Fusion.friendManager.isFriend(entity.getName())) {
                    Fusion.friendManager.removeFriend(entity.getName());
                    if (FriendSettings.getInstance().notify.getValue()) {
                        Util.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("/w " + entity.getName() + " I just removed you from my friends list on C.U.M Fusion!"));
                    }
                    Command.sendMessage(ChatFormatting.RED + entity.getName() + ChatFormatting.RED + " has been unfriended.");
                }
                else {
                    Fusion.friendManager.addFriend(entity.getName());
                    if (FriendSettings.getInstance().notify.getValue()) {
                        Util.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("/w " + entity.getName() + " I just added you to my friends list on C.U.M Fusion!"));
                    }
                    Command.sendMessage(ChatFormatting.GREEN + entity.getName() + ChatFormatting.GREEN + " has been friended.");
                }
            }
            this.clicked = true;
        }
    }
    
    private void throwPearl() {
        if (this.pearl.getValue()) {
            final int pearlSlot = InventoryUtil.findHotbarBlock((Class<? extends IForgeRegistryEntry.Impl>)ItemEnderPearl.class);
            final RayTraceResult result;
            final Entity entity;
            if ((result = Util.mc.objectMouseOver) != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
                return;
            }
            final boolean bl;
            final boolean offhand = bl = (Util.mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL);
            if (pearlSlot != -1 || offhand) {
                final int oldslot = Util.mc.player.inventory.currentItem;
                if (!offhand) {
                    InventoryUtil.switchToHotbarSlot(pearlSlot, false);
                }
                Util.mc.playerController.processRightClick((EntityPlayer)Util.mc.player, (World)Util.mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                if (!offhand) {
                    InventoryUtil.switchToHotbarSlot(oldslot, false);
                }
            }
        }
    }
    
    public enum Mode
    {
        Toggle, 
        MiddleClick;
    }
}
