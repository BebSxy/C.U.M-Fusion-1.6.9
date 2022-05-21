
package me.cum.fusion.event.events;

import net.minecraftforge.fml.relauncher.*;
import net.minecraft.network.*;
import io.netty.buffer.*;

@SideOnly(Side.CLIENT)
public interface PacketListener
{
    Packet<?> packetReceived(final EnumPacketDirection p0, final int p1, final Packet<?> p2, final ByteBuf p3);
}
