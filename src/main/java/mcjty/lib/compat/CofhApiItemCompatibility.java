package mcjty.lib.compat;

import mcjty.lib.varia.WrenchUsage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;

public class CofhApiItemCompatibility {
    public static boolean isToolHammer(Item item) {
        return false;
        // @todo 1.14 return item instanceof IToolHammer;
    }

    public static WrenchUsage getWrenchUsage(Item item, ItemStack itemStack, Player player, BlockPos pos) {
        return WrenchUsage.DISABLED;
        // @todo 1.14
//        IToolHammer hammer = (IToolHammer) item;
//        if (hammer.isUsable(itemStack, player, pos)) {
//            hammer.toolUsed(itemStack, player, pos);
//            return WrenchUsage.NORMAL;
//        } else {
//            return WrenchUsage.DISABLED;
//        }
    }
}
