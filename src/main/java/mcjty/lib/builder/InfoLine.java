package mcjty.lib.builder;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Predicate;

public class InfoLine {

    private final String translationKey;
    private final String suffix;
    private final Predicate<ItemStack> condition;
    private final Function<ItemStack, String> informationGetter;
    private final TextFormatting[] styles;

    InfoLine(String translationKey, String suffix, Predicate<ItemStack> condition, @Nullable Function<ItemStack, String> informationGetter, TextFormatting... styles) {
        this.translationKey = translationKey;
        this.suffix = suffix;
        this.condition = condition;
        this.informationGetter = informationGetter;
        this.styles = styles;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public String getSuffix() {
        return suffix;
    }

    public Predicate<ItemStack> getCondition() {
        return condition;
    }

    @Nullable
    public Function<ItemStack, String> getInformationGetter() {
        return informationGetter;
    }

    public TextFormatting[] getStyles() {
        return styles;
    }
}
