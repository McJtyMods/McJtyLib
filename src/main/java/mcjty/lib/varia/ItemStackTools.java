package mcjty.lib.varia;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.gui.GuiParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import team.reborn.energy.api.EnergyStorage;
import mcjty.lib.container.IItemHandler;
import javax.annotation.Nonnull;
import org.jspecify.annotations.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
        IItemHandler handler = mcjty.lib.varia.CapabilityTools.itemHandler(ItemStorage.SIDED.find(tileEntity.getLevel(), tileEntity.getBlockPos(), null));
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
        IItemHandler h = mcjty.lib.varia.CapabilityTools.itemHandler(ItemStorage.SIDED.find(tileEntity.getLevel(), tileEntity.getBlockPos(), null));
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
        Item item = Tools.getItem(Identifier.parse(itemName));
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
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "crops")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "glass_blocks")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "glass_panes")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "chests")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "cobblestones")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "netherracks")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "obsidians")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "gravels")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "sands")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "end_stones")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "stones")));

            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "ores/coal")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "ores/diamond")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "ores/emerald")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "ores/gold")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "ores/redstone")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "ores/quartz")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "ores/iron")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "ores/lapis")));
            commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ores/copper")));
            commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ores/tin")));
            commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ores/silver")));
            commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ores/manganese")));
            commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ores/platinum")));

            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "storage_blocks/coal")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "storage_blocks/diamond")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "storage_blocks/emerald")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "storage_blocks/gold")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "storage_blocks/redstone")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "storage_blocks/iron")));
            commonTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "storage_blocks/lapis")));
            commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/copper")));
            commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/tin")));
            commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/silver")));
            commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/manganese")));
            commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/platinum")));
        }
    }
}
