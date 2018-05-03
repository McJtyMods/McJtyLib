package mcjty.lib.varia;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.gui.GuiParser;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ItemStackTools {

    /**
     * Extract itemstack out of a slot and return a new stack.
     * Supports both IItemHandler as IInventory
     * @param tileEntity
     * @param slot
     * @param amount
     */
    @Nonnull
    public static ItemStack extractItem(@Nullable TileEntity tileEntity, int slot, int amount) {
        if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            return capability.extractItem(slot, amount, false);
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            return inventory.decrStackSize(slot, amount);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Get an item from an inventory
     * Supports both IItemHandler as IInventory
     * @param tileEntity
     * @param slot
     */
    @Nonnull
    public static ItemStack getStack(@Nullable TileEntity tileEntity, int slot) {
        if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            return capability.getStackInSlot(slot);
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            return inventory.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Set a stack in a specific slot. This will totally replace whatever was in the slot before
     * Supports both IItemHandler as IInventory. Does not check for failure
     * @param tileEntity
     * @param slot
     * @param stack
     */
    public static void setStack(@Nullable TileEntity tileEntity, int slot, @Nonnull ItemStack stack) {
        if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            capability.extractItem(slot, 64, false);        // Clear slot
            capability.insertItem(slot, stack, false);
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            inventory.setInventorySlotContents(slot, stack);
        }
    }

    public static List<ItemStack> getOres(String name) {
        return OreDictionary.getOres(name);
    }

    public static List<ItemStack> getOres(String name, boolean alwaysCreateEntry) {
        return OreDictionary.getOres(name, alwaysCreateEntry);
    }

    @Nonnull
    public static Optional<NBTTagCompound> getTag(@Nonnull ItemStack stack) {
        return Optional.ofNullable(stack.getTagCompound());
    }

    @Nonnull
    public static <R> R mapTag(@Nonnull ItemStack stack, Function<NBTTagCompound,R> mapping, @Nonnull R def) {
        if (stack.hasTagCompound()) {
            return mapping.apply(stack.getTagCompound());
        } else {
            return def;
        }
    }

    @Nonnull
    public static Function<ItemStack, String> intGetter(String tag, Integer def) {
        return stack -> Integer.toString(ItemStackTools.mapTag(stack, nbt -> nbt.getInteger(tag), def));
    }

    @Nonnull
    public static Function<ItemStack, String> strGetter(String tag, String def) {
        return stack -> ItemStackTools.mapTag(stack, nbt -> nbt.getString(tag), def);
    }

    @Nonnull
    public static Stream<NBTBase> getListStream(NBTTagCompound compound, String tag) {
        NBTTagList list = compound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        return StreamSupport.stream(list.spliterator(), false);
    }

    public static JsonObject itemStackToJson(ItemStack item) {
        JsonObject object = new JsonObject();
        object.add("item", new JsonPrimitive(item.getItem().getRegistryName().toString()));
        if (item.getCount() != 1) {
            object.add("amount", new JsonPrimitive(item.getCount()));
        }
        if (item.getItemDamage() != 0) {
            object.add("meta", new JsonPrimitive(item.getItemDamage()));
        }
        if (item.hasTagCompound()) {
            String string = item.getTagCompound().toString();
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
        int meta = 0;
        if (obj.has("meta")) {
            meta = obj.get("meta").getAsInt();
        }
        ItemStack stack = new ItemStack(item, amount, meta);
        if (obj.has("nbt")) {
            try {
                NBTTagCompound nbt = JsonToNBT.getTagFromJson(obj.get("nbt").getAsString());
                stack.setTagCompound(nbt);
            } catch (NBTException e) {
                // @todo What to do?
            }
        }
        return stack;
    }

    public static GuiParser.GuiCommand itemStackToGuiCommand(String name, ItemStack item) {
        GuiParser.GuiCommand object = new GuiParser.GuiCommand(name);
        object.parameter(item.getItem().getRegistryName().toString());
        object.parameter(item.getCount());
        object.parameter(item.getItemDamage());
        if (item.hasTagCompound()) {
            String string = item.getTagCompound().toString();
            object.command(new GuiParser.GuiCommand("tag").parameter(string));
        }
        return object;
    }

    public static ItemStack guiCommandToItemStack(GuiParser.GuiCommand obj) {
        String itemName = obj.getOptionalPar(0, "minecraft:stick");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
        int amount = obj.getOptionalPar(1, 1);
        int meta = obj.getOptionalPar(2, 0);
        ItemStack stack = new ItemStack(item, amount, meta);
        obj.findCommand("tag").ifPresent(cmd -> {
            try {
                NBTTagCompound nbt = JsonToNBT.getTagFromJson(cmd.getOptionalPar(0, ""));
                stack.setTagCompound(nbt);
            } catch (NBTException e) {
                e.printStackTrace();
            }
        });
        return stack;
    }

}
