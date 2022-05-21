
package me.cum.fusion.event.events;

import me.cum.fusion.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;
import net.minecraft.item.*;

@Cancelable
public class ProcessRightClickBlockEvent extends EventStage
{
    public BlockPos pos;
    public EnumHand hand;
    public ItemStack stack;
    
    public ProcessRightClickBlockEvent(final BlockPos pos, final EnumHand hand, final ItemStack stack) {
        this.pos = pos;
        this.hand = hand;
        this.stack = stack;
    }
}
