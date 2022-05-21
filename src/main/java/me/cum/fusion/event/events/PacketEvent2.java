
package me.cum.fusion.event.events;

import me.cum.fusion.event.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class PacketEvent2 extends EventStage
{
    private final Packet<?> packet;
    
    public PacketEvent2(final int stage, final Packet<?> packet) {
        super(stage);
        this.packet = packet;
    }
    
    public <T extends Packet<?>> T getPacket() {
        return (T)this.packet;
    }
    
    @Cancelable
    public static class Send extends PacketEvent2
    {
        public Send(final int stage, final Packet<?> packet) {
            super(stage, packet);
        }
    }
    
    @Cancelable
    public static class Receive extends PacketEvent2
    {
        public Receive(final int stage, final Packet<?> packet) {
            super(stage, packet);
        }
    }
}
