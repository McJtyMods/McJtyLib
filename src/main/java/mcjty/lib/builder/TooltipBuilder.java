package mcjty.lib.builder;

import mcjty.lib.McJtyLib;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class TooltipBuilder {

    private InfoLine[] infoLines;
    private InfoLine[] shiftInfoLines;

    public TooltipBuilder info(InfoLine... lines) {
        infoLines = lines;
        return this;
    }

    public TooltipBuilder infoShift(InfoLine... lines) {
        shiftInfoLines = lines;
        return this;
    }

    private static TranslationTextComponent stylize(String translationKey, TextFormatting... formattings) {
        TranslationTextComponent component = new TranslationTextComponent(translationKey);
        for (TextFormatting format : formattings) {
            component.applyTextStyle(format);
        }
        return component;
    }


    public void makeTooltip(@Nonnull ResourceLocation id, @Nonnull ItemStack stack, @Nonnull List<ITextComponent> tooltip) {
        String namespace = id.getNamespace();
        String path = id.getPath();
        String prefix = "message." + namespace + "." + path + ".";
        InfoLine[] lines = infoLines;
        if (infoLines == null || (McJtyLib.proxy.isShiftKeyDown() && shiftInfoLines != null)) {
            lines = shiftInfoLines;
        }
        for (InfoLine line : lines) {
            if (line.getCondition().test(stack)) {
                ITextComponent component;
                if (line.getTranslationKey() != null) {
                    component = stylize(line.getTranslationKey(), line.getStyles());
                } else {
                    component = stylize(prefix + line.getSuffix(), line.getStyles());
                }
                if (line.getInformationGetter() != null) {
                    String extra = line.getInformationGetter().apply(stack);
                    component.appendSibling(new StringTextComponent(TextFormatting.WHITE + extra));
                }
                tooltip.add(component);
            }
        }


    }

    public boolean isActive() {
        return infoLines != null || shiftInfoLines != null;
    }

    public static final InfoLine key(String key) {
        return new InfoLine(key, null, itemStack -> true, null, TextFormatting.WHITE);
    }

    public static final InfoLine header() {
        return new InfoLine(null, "header", itemStack -> true, null, TextFormatting.GREEN);
    }

    public static final InfoLine warning(Predicate<ItemStack> condition) {
        return new InfoLine(null, "warning", condition, null, TextFormatting.RED);
    }

    public static final InfoLine warning() {
        return new InfoLine(null, "warning", itemStack -> true, null, TextFormatting.RED);
    }

    public static final InfoLine gold(Predicate<ItemStack> condition) {
        return new InfoLine(null, "gold", condition, null, TextFormatting.GOLD);
    }

    public static final InfoLine gold() {
        return new InfoLine(null, "gold", itemStack -> true, null, TextFormatting.GOLD);
    }

    public static final InfoLine parameter(String suffix, Function<ItemStack, String> getter) {
        return new InfoLine(null, suffix, itemStack -> true,
                getter, TextFormatting.GRAY, TextFormatting.BOLD);
    }

    public static final InfoLine parameter(String suffix, Predicate<ItemStack> condition, Function<ItemStack, String> getter) {
        return new InfoLine(null, suffix, condition,
                getter, TextFormatting.GRAY, TextFormatting.BOLD);
    }
}
