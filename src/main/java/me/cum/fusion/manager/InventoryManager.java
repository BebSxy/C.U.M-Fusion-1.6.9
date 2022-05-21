
package me.cum.fusion.manager;

import me.cum.fusion.util.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;

public class InventoryManager implements Util
{
    public int currentPlayerItem;
    private int recoverySlot;
    
    public InventoryManager() {
        this.recoverySlot = -1;
    }
    
    public void update() {
        if (this.recoverySlot != -1) {
            InventoryManager.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange((this.recoverySlot == 8) ? 7 : (this.recoverySlot + 1)));
            InventoryManager.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.recoverySlot));
            InventoryManager.mc.player.inventory.currentItem = this.recoverySlot;
            final int i = InventoryManager.mc.player.inventory.currentItem;
            if (i != this.currentPlayerItem) {
                this.currentPlayerItem = i;
                InventoryManager.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.currentPlayerItem));
            }
            this.recoverySlot = -1;
        }
    }
    
    public void recoverSilent(final int slot) {
        this.recoverySlot = slot;
    }
}
