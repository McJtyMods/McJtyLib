package mcjty.lib.builder;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;

public interface IPlacementGetter {

    BlockState getStateForPlacement(BlockItemUseContext context);
}
