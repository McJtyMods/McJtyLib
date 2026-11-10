package mcjty.lib.varia;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.container.IItemHandler;
import mcjty.lib.gui.GuiParser;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemStackTools {

    private static Set<TagKey<Item>> commonTags = null;

    /**
     * Extract itemstack out of a slot and return a new stack.
     */
    @Nonnull
    public static ItemStack extractItem(@Nullable BlockEntity tileEntity, int slot, int amount) {
        if (tileEntity == null) {
            return ItemStack.EMPTY;
        }
        IItemHandler handler = CapabilityTools.itemHandler(ItemStorage.SIDED.find(tileEntity.getLevel(), tileEntity.getBlockPos(), null));
        if (handler != null) {
            return handler.extractItem(slot, amount, false);
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Get an item from an inventory
     */
    @Nonnull
    public static ItemStack getStack(@Nullable BlockEntity tileEntity, int slot) {
        if (tileEntity == null) {
            return ItemStack.EMPTY;
        }
        IItemHandler h = CapabilityTools.itemHandler(ItemStorage.SIDED.find(tileEntity.getLevel(), tileEntity.getBlockPos(), null));
        if (h != null) {
            return h.getStackInSlot(slot);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static GuiParser.GuiCommand itemStackToGuiCommand(String name, ItemStack item) {
        GuiParser.GuiCommand object = new GuiParser.GuiCommand(name);
        object.parameter(Tools.getId(item).toString());
        object.parameter(item.getCount());
        return object;
    }

    public static ItemStack guiCommandToItemStack(GuiParser.GuiCommand obj) {
        String itemName = obj.getOptionalPar(0, "minecraft:stick");
        Item item = Tools.getItem(ResourceLocation.parse(itemName));
        int amount = obj.getOptionalPar(1, 1);
        return new ItemStack(item, amount);
    }

    public static void addCommonTags(Collection<TagKey<Item>> fromItem, Set<TagKey<Item>> tags) {
        findCommonTags();
        for (TagKey<Item> id : fromItem) {
            if (commonTags.contains(id)) {
                tags.add(id);
            }
        }
    }

    public static boolean hasCommonTag(Collection<TagKey<Item>> fromItem) {
        findCommonTags();
        for (TagKey<Item> id : fromItem) {
            if (commonTags.contains(id)) {
                return true;
            }
        }
        return false;
    }

    private static void findCommonTags() {
        if (commonTags == null) {
            commonTags = new HashSet<>();
            commonTags.add(ItemTags.SAND);
            commonTags.add(ItemTags.FENCES);
            commonTags.add(ItemTags.SAPLINGS);
            commonTags.add(ItemTags.LEAVES);
            commonTags.add(ItemTags.LOGS);
            commonTags.add(ItemTags.RAILS);
            commonTags.add(ItemTags.SLABS);
            commonTags.add(ItemTags.WOOL);
            commonTags.add(ItemTags.WOOL_CARPETS);
            commonTags.add(ItemTags.PLANKS);
            commonTags.add(ItemTags.STAIRS);
            commonTags.add(ItemTags.DIRT);
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "crops")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "glass_blocks")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "glass_panes")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "chests")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "cobblestones")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "netherracks")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "obsidians")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "gravels")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "sands")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "end_stones")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "stones")));

            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/coal")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/diamond")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/emerald")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/gold")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/redstone")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/quartz")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/iron")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/lapis")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/copper")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/tin")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/silver")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/manganese")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/platinum")));

            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/coal")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/diamond")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/emerald")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/gold")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/redstone")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/iron")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/lapis")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/copper")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/tin")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/silver")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/manganese")));
            commonTags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/platinum")));
        }
    }
}
