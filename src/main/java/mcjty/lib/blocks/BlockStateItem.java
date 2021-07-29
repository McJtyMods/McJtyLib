package mcjty.lib.blocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

import javax.annotation.Nullable;

import net.minecraft.world.item.Item.Properties;

public class BlockStateItem extends BlockItem {

    private final BlockState state;

    public BlockStateItem(BlockState state, Properties builder) {
        super(state.getBlock(), builder);
        this.state = state;
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getPlacementState(context);
        if (stateForPlacement != null) {
            return state;
        }
        return null;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(new ItemStack(this));
        }
    }
}
