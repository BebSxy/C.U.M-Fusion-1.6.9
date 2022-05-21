
package me.cum.fusion.features.modules.player;

import me.cum.fusion.features.modules.*;
import me.cum.fusion.features.setting.*;
import me.cum.fusion.util.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;

public class FastPlace extends Module
{
    private final Setting<Boolean> all;
    private final Setting<Boolean> obby;
    private final Setting<Boolean> crystals;
    private final Setting<Boolean> exp;
    private final Setting<Boolean> PacketCrystal;
    private BlockPos mousePos;
    
    public FastPlace() {
        super("FastPlace", "Fast everything.", Module.Category.PLAYER, true, false, false);
        this.all = (Setting<Boolean>)this.register(new Setting("All", (T)false));
        this.obby = (Setting<Boolean>)this.register(new Setting("Obsidian", (T)Boolean.FALSE, v -> !this.all.getValue()));
        this.crystals = (Setting<Boolean>)this.register(new Setting("Crystals", (T)Boolean.FALSE, v -> !this.all.getValue()));
        this.exp = (Setting<Boolean>)this.register(new Setting("Experience", (T)Boolean.FALSE, v -> !this.all.getValue()));
        this.PacketCrystal = (Setting<Boolean>)this.register(new Setting("PacketCrystal", (T)false));
        this.mousePos = null;
    }
    
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (InventoryUtil.holdingItem(ItemExpBottle.class) && this.exp.getValue()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (InventoryUtil.holdingItem(BlockObsidian.class) && this.obby.getValue()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (this.all.getValue()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (InventoryUtil.holdingItem(ItemEndCrystal.class) && (this.crystals.getValue() || this.all.getValue())) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (this.PacketCrystal.getValue() && FastPlace.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            final boolean offhand = FastPlace.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
            if (offhand || FastPlace.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                final RayTraceResult result = FastPlace.mc.objectMouseOver;
                if (result == null) {
                    return;
                }
                switch (result.typeOfHit) {
                    case MISS: {
                        this.mousePos = null;
                        break;
                    }
                    case BLOCK: {
                        this.mousePos = FastPlace.mc.objectMouseOver.getBlockPos();
                        break;
                    }
                    case ENTITY: {
                        final Entity entity;
                        if (this.mousePos == null || (entity = result.entityHit) == null) {
                            break;
                        }
                        if (!this.mousePos.equals((Object)new BlockPos(entity.posX, entity.posY - 1.0, entity.posZ))) {
                            break;
                        }
                        FastPlace.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.mousePos, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                        break;
                    }
                }
            }
        }
    }
}
