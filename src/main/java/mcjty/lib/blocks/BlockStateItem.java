package mcjty.lib.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

import net.minecraft.item.Item.Properties;

public class BlockStateItem extends BlockItem {

    private final BlockState state;

    public BlockStateItem(BlockState state, Properties builder) {
        super(state.getBlock(), builder);
        this.state = state;
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockItemUseContext context) {
        BlockState stateForPlacement = super.getPlacementState(context);
        if (stateForPlacement != null) {
            return state;
        }
        return null;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(new ItemStack(this));
        }
    }
}
