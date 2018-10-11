package mcjty.lib.tooltips;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ITooltipExtras {

    // Get a list of items together with an optional error amount (will be printed in red).
    // If that amount is -1 it will not be printed
    List<Pair<ItemStack, Integer>> getItems(ItemStack stack);
}
