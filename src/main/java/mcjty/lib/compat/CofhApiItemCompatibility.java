package mcjty.lib.compat;

import cofh.api.item.IToolHammer;
import mcjty.lib.container.WrenchUsage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class CofhApiItemCompatibility {
    public static boolean isToolHammer(Item item) {
        return item instanceof IToolHammer;
    }

    public static WrenchUsage getWrenchUsage(Item item, ItemStack itemStack, EntityPlayer player, BlockPos pos) {
        IToolHammer hammer = (IToolHammer) item;
        if (hammer.isUsable(itemStack, player, pos)) {
            hammer.toolUsed(itemStack, player, pos);
            return WrenchUsage.NORMAL;
        } else {
            return WrenchUsage.DISABLED;
        }
    }
}
