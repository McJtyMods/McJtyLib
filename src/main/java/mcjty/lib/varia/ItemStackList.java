package mcjty.lib.varia;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemStackList extends NonNullList<ItemStack> {

    public static final ItemStackList EMPTY = ItemStackList.create(0);

    public static ItemStackList create(int size) {
        Validate.notNull(ItemStack.EMPTY);
        ItemStack[] aobject = new ItemStack[size];
        Arrays.fill(aobject, ItemStack.EMPTY);
        return new ItemStackList(Arrays.asList(aobject), ItemStack.EMPTY);
    }

    public static ItemStackList create() {
        return new ItemStackList(new ArrayList<>(), ItemStack.EMPTY);
    }

    public ItemStackList(List<ItemStack> delegateIn, @Nullable ItemStack stack) {
        super(delegateIn, stack);
    }
}