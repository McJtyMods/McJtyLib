package mcjty.lib.builder;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record InfoLine(
    String translationKey,
    String suffix,
    Predicate<ItemStack> condition,
    Function<ItemStack, String> informationGetter,
    ChatFormatting[] styles,
    Function<ItemStack, Stream<String>> repeatingParameter) {

    InfoLine(String translationKey, String suffix, Predicate<ItemStack> condition, @Nullable Function<ItemStack, String> informationGetter, Function<ItemStack, Stream<String>> repeatingParameter, ChatFormatting... styles) {
        this(translationKey, suffix, condition, informationGetter, styles, repeatingParameter);
    }
}
