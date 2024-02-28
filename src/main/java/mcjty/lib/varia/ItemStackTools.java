package mcjty.lib.varia;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.gui.GuiParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        return tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(handler -> handler.extractItem(slot, amount, false))
                .orElse(ItemStack.EMPTY);
    }

    /**
     * Get an item from an inventory
     */
    @Nonnull
    public static ItemStack getStack(@Nullable BlockEntity tileEntity, int slot) {
        if (tileEntity == null) {
            return ItemStack.EMPTY;
        }
        return tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(handler -> handler.getStackInSlot(slot))
                .orElse(ItemStack.EMPTY);
    }

    public static GuiParser.GuiCommand itemStackToGuiCommand(String name, ItemStack item) {
        GuiParser.GuiCommand object = new GuiParser.GuiCommand(name);
        object.parameter(Tools.getId(item).toString());
        object.parameter(item.getCount());
        if (item.hasTag()) {
            String string = item.getTag().toString();
            object.command(new GuiParser.GuiCommand("tag").parameter(string));
        }
        return object;
    }

    public static ItemStack guiCommandToItemStack(GuiParser.GuiCommand obj) {
        String itemName = obj.getOptionalPar(0, "minecraft:stick");
        Item item = Tools.getItem(new ResourceLocation(itemName));
        int amount = obj.getOptionalPar(1, 1);
        ItemStack stack = new ItemStack(item, amount);
        obj.findCommand("tag").ifPresent(cmd -> {
            try {
                CompoundTag nbt = TagParser.parseTag(cmd.getOptionalPar(0, ""));
                stack.setTag(nbt);
            } catch (CommandSyntaxException e) {
                Logging.logError("Error", e);
            }
        });
        return stack;
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
            commonTags.add(Tags.Items.CROPS);
            commonTags.add(Tags.Items.GLASS);
            commonTags.add(Tags.Items.GLASS_PANES);
            commonTags.add(Tags.Items.CHESTS);
            commonTags.add(Tags.Items.COBBLESTONE);
            commonTags.add(Tags.Items.NETHERRACK);
            commonTags.add(Tags.Items.OBSIDIAN);
            commonTags.add(Tags.Items.GRAVEL);
            commonTags.add(Tags.Items.SANDSTONE);
            commonTags.add(Tags.Items.END_STONES);
            commonTags.add(Tags.Items.STONE);

            commonTags.add(Tags.Items.ORES_COAL);
            commonTags.add(Tags.Items.ORES_DIAMOND);
            commonTags.add(Tags.Items.ORES_EMERALD);
            commonTags.add(Tags.Items.ORES_GOLD);
            commonTags.add(Tags.Items.ORES_REDSTONE);
            commonTags.add(Tags.Items.ORES_QUARTZ);
            commonTags.add(Tags.Items.ORES_IRON);
            commonTags.add(Tags.Items.ORES_LAPIS);
            commonTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "ores/copper")));
            commonTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "ores/tin")));
            commonTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "ores/silver")));
            commonTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "ores/manganese")));
            commonTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "ores/platinum")));

            commonTags.add(Tags.Items.STORAGE_BLOCKS_COAL);
            commonTags.add(Tags.Items.STORAGE_BLOCKS_DIAMOND);
            commonTags.add(Tags.Items.STORAGE_BLOCKS_EMERALD);
            commonTags.add(Tags.Items.STORAGE_BLOCKS_GOLD);
            commonTags.add(Tags.Items.STORAGE_BLOCKS_REDSTONE);
            commonTags.add(Tags.Items.STORAGE_BLOCKS_QUARTZ);
            commonTags.add(Tags.Items.STORAGE_BLOCKS_IRON);
            commonTags.add(Tags.Items.STORAGE_BLOCKS_LAPIS);
            commonTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "storage_blocks/copper")));
            commonTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "storage_blocks/tin")));
            commonTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "storage_blocks/silver")));
            commonTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "storage_blocks/manganese")));
            commonTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "storage_blocks/platinum")));
        }
    }
}
