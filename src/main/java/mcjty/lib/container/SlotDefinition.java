package mcjty.lib.container;

import mcjty.lib.varia.TriConsumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.Objects;
import java.util.function.Predicate;

public class SlotDefinition {
    private final SlotType type;
    private boolean input = false;
    private boolean output = false;
    private final Predicate<ItemStack> validItems;
    private TriConsumer<TileEntity, PlayerEntity, ItemStack> onCraft = (te, playerEntity, stack) -> {};

    SlotDefinition(SlotType type, ItemStack... itemStacks) {
        this.type = type;
        // @todo temporary for compatibility
        input = type == SlotType.SLOT_INPUT || type == SlotType.SLOT_INPUTOUTPUT;
        output = type == SlotType.SLOT_OUTPUT || type == SlotType.SLOT_INPUTOUTPUT;
        this.validItems = stack -> {
            for (ItemStack itemStack : itemStacks) {
                if (itemStack.getItem() == stack.getItem()) {
                    return true;
                }
            }
            return false;
        };
    }

    public SlotDefinition onCraft(TriConsumer<TileEntity, PlayerEntity, ItemStack> onCraft) {
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

    public TriConsumer<TileEntity, PlayerEntity, ItemStack> getOnCraft() {
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

    public static SlotDefinition output() {
        return new SlotDefinition(SlotType.SLOT_OUTPUT);
    }

    public static SlotDefinition input() {
        return new SlotDefinition(SlotType.SLOT_INPUT);
    }

    public static SlotDefinition inputOutput() {
        return new SlotDefinition(SlotType.SLOT_INPUTOUTPUT);
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
