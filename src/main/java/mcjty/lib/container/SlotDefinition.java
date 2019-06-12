package mcjty.lib.container;

import net.minecraft.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

public class SlotDefinition {
    private final SlotType type;
    private final Predicate<ItemStack> validItems;

    public SlotDefinition(SlotType type, ItemStack... itemStacks) {
        this.type = type;
        this.validItems = stack -> {
            for (ItemStack itemStack : itemStacks) {
                if (itemStack.getItem() == stack.getItem()) {
                    return true;
                }
            }
            return false;
        };
    }

    public SlotDefinition(SlotType type, Class<?> itemClass) {
        this.type = type;
        this.validItems = stack -> {
            if (itemClass != null && itemClass.isInstance(stack.getItem())) {
                return true;
            }
            return false;
        };
    }

    public SlotDefinition(SlotType type, Predicate<ItemStack> validItems) {
        this.type = type;
        this.validItems = validItems;
    }



    public SlotType getType() {
        return type;
    }

    public boolean itemStackMatches(ItemStack stack) {
        return validItems.test(stack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SlotDefinition that = (SlotDefinition) o;
        return type == that.type;

    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
