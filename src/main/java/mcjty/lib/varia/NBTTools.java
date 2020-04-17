package mcjty.lib.varia;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NBTTools {

    public static <T> T getInfoNBT(ItemStack stack, BiFunction<CompoundNBT, String, T> getter, String name, T def) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return def;
        }
        CompoundNBT info = tag.getCompound("BlockEntityTag").getCompound("Info");
        if (info.contains(name)) {
            return getter.apply(info, name);
        } else {
            return def;
        }
    }

    public static <T> void setInfoNBT(ItemStack stack, TriConsumer<CompoundNBT, String, T> setter, String name, T value) {
        CompoundNBT entityTag = stack.getOrCreateChildTag("BlockEntityTag");
        CompoundNBT info = entityTag.getCompound("Info");
        setter.accept(info, name, value);
        entityTag.put("Info", info);
    }

    public static boolean hasInfoNBT(ItemStack stack, String name) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return false;
        }
        CompoundNBT info = tag.getCompound("BlockEntityTag").getCompound("Info");
        return info.contains(name);
    }

    public static int getInt(ItemStack stack, String name, int def) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return def;
        }
        return tag.getInt(name);
    }

    public static String getString(ItemStack stack, String name, String def) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return def;
        }
        return tag.getString(name);
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
        return stack -> Integer.toString(mapTag(stack, nbt -> nbt.getInt(tag), def));
    }

    @Nonnull
    public static Function<ItemStack, String> strGetter(String tag, String def) {
        return stack -> mapTag(stack, nbt -> nbt.getString(tag), def);
    }

    @Nonnull
    public static Stream<INBT> getListStream(CompoundNBT compound, String tag) {
        ListNBT list = compound.getList("Items", Constants.NBT.TAG_COMPOUND);
        return StreamSupport.stream(list.spliterator(), false);
    }
}
