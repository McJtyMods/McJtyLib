package mcjty.lib.varia;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.gui.GuiParser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ItemStackTools {

    private static Set<ResourceLocation> commonTags = null;

    /**
     * Extract itemstack out of a slot and return a new stack.
     * Supports both IItemHandler as IInventory
     * @param tileEntity
     * @param slot
     * @param amount
     */
    @Nonnull
    public static ItemStack extractItem(@Nullable TileEntity tileEntity, int slot, int amount) {
        if (tileEntity == null) {
            return ItemStack.EMPTY;
        }
        return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .map(handler -> handler.extractItem(slot, amount, false))
                .orElse(ItemStack.EMPTY);
    }

    /**
     * Get an item from an inventory
     * Supports both IItemHandler as IInventory
     * @param tileEntity
     * @param slot
     */
    @Nonnull
    public static ItemStack getStack(@Nullable TileEntity tileEntity, int slot) {
        if (tileEntity == null) {
            return ItemStack.EMPTY;
        }
        return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .map(handler -> handler.getStackInSlot(slot))
                .orElse(ItemStack.EMPTY);
    }

    /**
     * Set a stack in a specific slot. This will totally replace whatever was in the slot before
     * Supports both IItemHandler as IInventory. Does not check for failure
     * @param tileEntity
     * @param slot
     * @param stack
     */
    public static void setStack(@Nullable TileEntity tileEntity, int slot, @Nonnull ItemStack stack) {
        if (tileEntity == null) {
            return;
        }
        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
                handler -> {
                    handler.extractItem(slot, 64, false);        // Clear slot
                    handler.insertItem(slot, stack, false);
                }
        );
    }

    public static List<ItemStack> getOres(String name) {
//        return OreDictionary.getOres(name);
        // @todo 1.14
        return Collections.emptyList();
    }

    public static List<ItemStack> getOres(String name, boolean alwaysCreateEntry) {
//        return OreDictionary.getOres(name, alwaysCreateEntry);
        // @todo 1.14
        return Collections.emptyList();
    }

    @Nonnull
    public static Optional<CompoundNBT> getTag(@Nonnull ItemStack stack) {
        return Optional.ofNullable(stack.getTag());
    }

    @Nonnull
    public static <R> R mapTag(@Nonnull ItemStack stack, Function<CompoundNBT,R> mapping, @Nonnull R def) {
        if (stack.hasTag()) {
            return mapping.apply(stack.getTag());
        } else {
            return def;
        }
    }

    @Nonnull
    public static Function<ItemStack, String> intGetter(String tag, Integer def) {
        return stack -> Integer.toString(ItemStackTools.mapTag(stack, nbt -> nbt.getInt(tag), def));
    }

    @Nonnull
    public static Function<ItemStack, String> strGetter(String tag, String def) {
        return stack -> ItemStackTools.mapTag(stack, nbt -> nbt.getString(tag), def);
    }

    @Nonnull
    public static Stream<INBT> getListStream(CompoundNBT compound, String tag) {
        ListNBT list = compound.getList("Items", Constants.NBT.TAG_COMPOUND);
        return StreamSupport.stream(list.spliterator(), false);
    }

    public static JsonObject itemStackToJson(ItemStack item) {
        JsonObject object = new JsonObject();
        object.add("item", new JsonPrimitive(item.getItem().getRegistryName().toString()));
        if (item.getCount() != 1) {
            object.add("amount", new JsonPrimitive(item.getCount()));
        }
        if (item.hasTag()) {
            String string = item.getTag().toString();
            object.add("nbt", new JsonPrimitive(string));
        }
        return object;
    }

    public static ItemStack jsonToItemStack(JsonObject obj) {
        String itemName = obj.get("item").getAsString();
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
        // @todo error checking
        int amount = 1;
        if (obj.has("amount")) {
            amount = obj.get("amount").getAsInt();
        }
        ItemStack stack = new ItemStack(item, amount);
        if (obj.has("nbt")) {
            try {
                CompoundNBT nbt = JsonToNBT.getTagFromJson(obj.get("nbt").getAsString());
                stack.setTag(nbt);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
                // @todo
            }
        }
        return stack;
    }

    public static GuiParser.GuiCommand itemStackToGuiCommand(String name, ItemStack item) {
        GuiParser.GuiCommand object = new GuiParser.GuiCommand(name);
        object.parameter(item.getItem().getRegistryName().toString());
        object.parameter(item.getCount());
        if (item.hasTag()) {
            String string = item.getTag().toString();
            object.command(new GuiParser.GuiCommand("tag").parameter(string));
        }
        return object;
    }

    public static ItemStack guiCommandToItemStack(GuiParser.GuiCommand obj) {
        String itemName = obj.getOptionalPar(0, "minecraft:stick");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
        int amount = obj.getOptionalPar(1, 1);
        ItemStack stack = new ItemStack(item, amount);
        obj.findCommand("tag").ifPresent(cmd -> {
            try {
                CompoundNBT nbt = JsonToNBT.getTagFromJson(cmd.getOptionalPar(0, ""));
                stack.setTag(nbt);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        });
        return stack;
    }

    public static void addCommonTags(Collection<ResourceLocation> fromItem, Set<ResourceLocation> tags) {
        findCommonTags();
        for (ResourceLocation id : fromItem) {
            if (commonTags.contains(id)) {
                tags.add(id);
            }
        }
    }

    public static boolean hasCommonTag(Collection<ResourceLocation> fromItem) {
        findCommonTags();
        for (ResourceLocation id : fromItem) {
            if (commonTags.contains(id)) {
                return true;
            }
        }
        return false;
    }

    private static void findCommonTags() {
        if (commonTags == null) {
            commonTags = new HashSet<>();
            commonTags.add(BlockTags.SAND.getId());
            commonTags.add(BlockTags.FENCES.getId());
            commonTags.add(BlockTags.SAPLINGS.getId());
            commonTags.add(BlockTags.LEAVES.getId());
            commonTags.add(BlockTags.LOGS.getId());
            commonTags.add(BlockTags.RAILS.getId());
            commonTags.add(BlockTags.SLABS.getId());
            commonTags.add(BlockTags.WOOL.getId());
            commonTags.add(BlockTags.CARPETS.getId());
            commonTags.add(BlockTags.CROPS.getId());
            commonTags.add(BlockTags.PLANKS.getId());
            commonTags.add(BlockTags.STAIRS.getId());
            commonTags.add(Tags.Blocks.GLASS.getId());
            commonTags.add(Tags.Blocks.GLASS_PANES.getId());
            commonTags.add(Tags.Blocks.CHESTS.getId());
            commonTags.add(Tags.Blocks.COBBLESTONE.getId());
            commonTags.add(Tags.Blocks.DIRT.getId());
            commonTags.add(Tags.Blocks.NETHERRACK.getId());
            commonTags.add(Tags.Blocks.OBSIDIAN.getId());
            commonTags.add(Tags.Blocks.GRAVEL.getId());
            commonTags.add(Tags.Blocks.SANDSTONE.getId());
            commonTags.add(Tags.Blocks.END_STONES.getId());
            commonTags.add(Tags.Blocks.STONE.getId());

            commonTags.add(Tags.Blocks.ORES_COAL.getId());
            commonTags.add(Tags.Blocks.ORES_DIAMOND.getId());
            commonTags.add(Tags.Blocks.ORES_EMERALD.getId());
            commonTags.add(Tags.Blocks.ORES_GOLD.getId());
            commonTags.add(Tags.Blocks.ORES_REDSTONE.getId());
            commonTags.add(Tags.Blocks.ORES_QUARTZ.getId());
            commonTags.add(Tags.Blocks.ORES_IRON.getId());
            commonTags.add(Tags.Blocks.ORES_LAPIS.getId());
            commonTags.add(new ResourceLocation("forge", "ores/copper"));
            commonTags.add(new ResourceLocation("forge", "ores/tin"));
            commonTags.add(new ResourceLocation("forge", "ores/silver"));
            commonTags.add(new ResourceLocation("forge", "ores/manganese"));
            commonTags.add(new ResourceLocation("forge", "ores/platinum"));

            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_COAL.getId());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_DIAMOND.getId());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_EMERALD.getId());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_GOLD.getId());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_REDSTONE.getId());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_QUARTZ.getId());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_IRON.getId());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_LAPIS.getId());
            commonTags.add(new ResourceLocation("forge", "storage_blocks/copper"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/tin"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/silver"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/manganese"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/platinum"));
        }
    }
}
