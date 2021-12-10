package mcjty.lib.varia;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NBTTools {

    public static <T> T getInfoNBT(ItemStack stack, BiFunction<CompoundTag, String, T> getter, String name, T def) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return def;
        }
        CompoundTag info = tag.getCompound("BlockEntityTag").getCompound("Info");
        if (info.contains(name)) {
            return getter.apply(info, name);
        } else {
            return def;
        }
    }

    public static <T> T getBlockEntityNBT(ItemStack stack, BiFunction<CompoundTag, String, T> getter, String name, T def) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return def;
        }
        CompoundTag info = tag.getCompound("BlockEntityTag");
        if (info.contains(name)) {
            return getter.apply(info, name);
        } else {
            return def;
        }
    }

    public static <T> void setInfoNBT(ItemStack stack, TriConsumer<CompoundTag, String, T> setter, String name, T value) {
        CompoundTag entityTag = stack.getOrCreateTagElement("BlockEntityTag");
        CompoundTag info = entityTag.getCompound("Info");
        setter.accept(info, name, value);
        entityTag.put("Info", info);
    }

    public static boolean hasInfoNBT(ItemStack stack, String name) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }
        CompoundTag info = tag.getCompound("BlockEntityTag").getCompound("Info");
        return info.contains(name);
    }

    public static int getInt(ItemStack stack, String name, int def) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return def;
        }
        if (tag.contains(name)) {
            return tag.getInt(name);
        } else {
            return def;
        }
    }

    public static float getFloat(ItemStack stack, String name, float def) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return def;
        }
        if (tag.contains(name)) {
            return tag.getFloat(name);
        } else {
            return def;
        }
    }

    public static String getString(ItemStack stack, String name, String def) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return def;
        }
        if (tag.contains(name)) {
            return tag.getString(name);
        } else {
            return def;
        }
    }

    @Nonnull
    public static Optional<CompoundTag> getTag(@Nonnull ItemStack stack) {
        return Optional.ofNullable(stack.getTag());
    }

    @Nonnull
    public static <R> R mapTag(@Nonnull ItemStack stack, Function<CompoundTag,R> mapping, @Nonnull R def) {
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
    public static Stream<Tag> getListStream(CompoundTag compound, String tag) {
        ListTag list = compound.getList("Items", Tag.TAG_COMPOUND);
        return StreamSupport.stream(list.spliterator(), false);
    }
}
