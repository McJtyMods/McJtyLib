package mcjty.lib.varia;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcjty.lib.McJtyLib;
import mcjty.lib.gui.GuiParser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ItemStackTools {

    private static Set<ResourceLocation> commonTags = null;

    /**
     * Extract itemstack out of a slot and return a new stack.
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
                CompoundNBT nbt = JsonToNBT.parseTag(cmd.getOptionalPar(0, ""));
                stack.setTag(nbt);
            } catch (CommandSyntaxException e) {
                Logging.logError("Error", e);
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
            commonTags.add(BlockTags.SAND.getName());
            commonTags.add(BlockTags.FENCES.getName());
            commonTags.add(BlockTags.SAPLINGS.getName());
            commonTags.add(BlockTags.LEAVES.getName());
            commonTags.add(BlockTags.LOGS.getName());
            commonTags.add(BlockTags.RAILS.getName());
            commonTags.add(BlockTags.SLABS.getName());
            commonTags.add(BlockTags.WOOL.getName());
            commonTags.add(BlockTags.CARPETS.getName());
            commonTags.add(BlockTags.CROPS.getName());
            commonTags.add(BlockTags.PLANKS.getName());
            commonTags.add(BlockTags.STAIRS.getName());
            commonTags.add(Tags.Blocks.GLASS.getName());
            commonTags.add(Tags.Blocks.GLASS_PANES.getName());
            commonTags.add(Tags.Blocks.CHESTS.getName());
            commonTags.add(Tags.Blocks.COBBLESTONE.getName());
            commonTags.add(Tags.Blocks.DIRT.getName());
            commonTags.add(Tags.Blocks.NETHERRACK.getName());
            commonTags.add(Tags.Blocks.OBSIDIAN.getName());
            commonTags.add(Tags.Blocks.GRAVEL.getName());
            commonTags.add(Tags.Blocks.SANDSTONE.getName());
            commonTags.add(Tags.Blocks.END_STONES.getName());
            commonTags.add(Tags.Blocks.STONE.getName());

            commonTags.add(Tags.Blocks.ORES_COAL.getName());
            commonTags.add(Tags.Blocks.ORES_DIAMOND.getName());
            commonTags.add(Tags.Blocks.ORES_EMERALD.getName());
            commonTags.add(Tags.Blocks.ORES_GOLD.getName());
            commonTags.add(Tags.Blocks.ORES_REDSTONE.getName());
            commonTags.add(Tags.Blocks.ORES_QUARTZ.getName());
            commonTags.add(Tags.Blocks.ORES_IRON.getName());
            commonTags.add(Tags.Blocks.ORES_LAPIS.getName());
            commonTags.add(new ResourceLocation("forge", "ores/copper"));
            commonTags.add(new ResourceLocation("forge", "ores/tin"));
            commonTags.add(new ResourceLocation("forge", "ores/silver"));
            commonTags.add(new ResourceLocation("forge", "ores/manganese"));
            commonTags.add(new ResourceLocation("forge", "ores/platinum"));

            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_COAL.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_DIAMOND.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_EMERALD.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_GOLD.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_REDSTONE.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_QUARTZ.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_IRON.getName());
            commonTags.add(Tags.Blocks.STORAGE_BLOCKS_LAPIS.getName());
            commonTags.add(new ResourceLocation("forge", "storage_blocks/copper"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/tin"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/silver"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/manganese"));
            commonTags.add(new ResourceLocation("forge", "storage_blocks/platinum"));
        }
    }
}
