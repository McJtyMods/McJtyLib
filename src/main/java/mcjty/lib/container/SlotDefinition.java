package mcjty.lib.container;

import net.minecraft.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

public class SlotDefinition {
    private final SlotType type;
    private final Predicate<ItemStack> validItems;

    SlotDefinition(SlotType type, ItemStack... itemStacks) {
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

    SlotDefinition(SlotType type, Class<?> itemClass) {
        this.type = type;
        this.validItems = stack -> {
            if (itemClass != null && itemClass.isInstance(stack.getItem())) {
                return true;
            }
            return false;
        };
    }

    SlotDefinition(SlotType type, Predicate<ItemStack> validItems) {
        this.type = type;
        this.validItems = validItems;
    }


    public static SlotDefinition specific(ItemStack... stacks) {
        return new SlotDefinition(SlotType.SLOT_SPECIFICITEM, stacks);
    }

    public static SlotDefinition specific(Predicate<ItemStack> validItems) {
        return new SlotDefinition(SlotType.SLOT_SPECIFICITEM, validItems);
    }

    public static SlotDefinition output() {
        return new SlotDefinition(SlotType.SLOT_OUTPUT);
    }

    public static SlotDefinition input() {
        return new SlotDefinition(SlotType.SLOT_INPUT);
    }

    public static SlotDefinition container() {
        return new SlotDefinition(SlotType.SLOT_CONTAINER);
    }

    public static SlotDefinition ghost() {
        return new SlotDefinition(SlotType.SLOT_GHOST);
    }

    public static SlotDefinition ghostOut() {
        return new SlotDefinition(SlotType.SLOT_GHOSTOUT);
    }

    public static SlotDefinition craftResult() {
        return new SlotDefinition(SlotType.SLOT_CRAFTRESULT);
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
