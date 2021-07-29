package mcjty.lib.tooltips;

import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ITooltipExtras {

    public static final int NOERROR = -1;   // Don't print error amount
    public static final int NOAMOUNT = -2;  // Don't show amount at all

    // Get a list of items together with an optional error amount (will be printed in red).
    // If that amount is NOERROR it will not be printed
    List<Pair<ItemStack, Integer>> getItems(ItemStack stack);
}
