package mcjty.lib.container;

import mcjty.lib.varia.TriConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;
import java.util.function.Predicate;

public class SlotDefinition {
    private final SlotType type;
    private boolean input = false;
    private boolean output = false;
    private final Predicate<ItemStack> validItems;
    private TriConsumer<BlockEntity, Player, ItemStack> onCraft = (te, playerEntity, stack) -> {};

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

    public SlotDefinition onCraft(TriConsumer<BlockEntity, Player, ItemStack> onCraft) {
        this.onCraft = onCraft;
        return this;
    }

    public SlotDefinition in() {
        input = true;
        return this;
    }

    public SlotDefinition out() {
        output = true;
        return this;
    }

    public TriConsumer<BlockEntity, Player, ItemStack> getOnCraft() {
        return onCraft;
    }

    private SlotDefinition(SlotType type, Predicate<ItemStack> validItems) {
        this.type = type;
        this.validItems = validItems;
    }


    public static SlotDefinition specific(ItemStack... stacks) {
        return new SlotDefinition(SlotType.SLOT_SPECIFICITEM, stacks);
    }

    public static SlotDefinition specific(Predicate<ItemStack> validItems) {
        return new SlotDefinition(SlotType.SLOT_SPECIFICITEM, validItems);
    }

    public static SlotDefinition generic() {
        return new SlotDefinition(SlotType.SLOT_GENERIC);
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

    public boolean isSpecific() {
        return type == SlotType.SLOT_SPECIFICITEM;
    }

    public boolean isInput() {
        return input;
    }

    public boolean isOutput() {
        return output;
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
