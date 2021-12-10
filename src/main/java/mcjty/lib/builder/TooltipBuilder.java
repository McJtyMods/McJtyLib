package mcjty.lib.builder;

import mcjty.lib.McJtyLib;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.text.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class TooltipBuilder {

    private InfoLine[] infoLines;
    private InfoLine[] shiftInfoLines;
    private InfoLine[] advancedInfoLines;

    public TooltipBuilder info(InfoLine... lines) {
        infoLines = lines;
        return this;
    }

    public TooltipBuilder infoShift(InfoLine... lines) {
        shiftInfoLines = lines;
        return this;
    }

    public TooltipBuilder infoAdvanced(InfoLine... lines) {
        advancedInfoLines = lines;
        return this;
    }

    private static TranslatableComponent stylize(String translationKey, ChatFormatting... formattings) {
        TranslatableComponent component = new TranslatableComponent(translationKey);
        for (ChatFormatting format : formattings) {
            component.withStyle(format);
        }
        return component;
    }


    public void makeTooltip(@Nonnull ResourceLocation id, @Nonnull ItemStack stack, @Nonnull List<Component> tooltip, TooltipFlag flag) {
        String namespace = id.getNamespace();
        String path = id.getPath();
        String prefix = "message." + namespace + "." + path + ".";
        InfoLine[] lines = infoLines;
        if (infoLines == null || (McJtyLib.proxy.isSneaking() && shiftInfoLines != null)) {
            lines = shiftInfoLines;
        }
        addLines(stack, tooltip, prefix, lines);

        if (advancedInfoLines != null && flag.isAdvanced()) {
            addLines(stack, tooltip, prefix, advancedInfoLines);
        }
    }

    private void addLines(@Nonnull ItemStack stack, @Nonnull List<Component> tooltip, String prefix, InfoLine[] lines) {
        for (InfoLine line : lines) {
            if (line.getCondition().test(stack)) {
                if (line.getRepeatingParameter() != null) {
                    line.getRepeatingParameter().apply(stack).forEach(s -> {
                        Component component;
                        if (line.getTranslationKey() != null) {
                            component = stylize(line.getTranslationKey(), line.getStyles());
                        } else {
                            component = stylize(prefix + line.getSuffix(), line.getStyles());
                        }
                        ((MutableComponent)component).append(new TextComponent(ChatFormatting.WHITE + s));
                        tooltip.add(component);
                    });
                } else {
                    Component component;
                    if (line.getTranslationKey() != null) {
                        component = stylize(line.getTranslationKey(), line.getStyles());
                    } else {
                        component = stylize(prefix + line.getSuffix(), line.getStyles());
                    }
                    if (line.getInformationGetter() != null) {
                        String extra = line.getInformationGetter().apply(stack);
                        if (extra != null) {
                            ((MutableComponent)component).append(new TextComponent(ChatFormatting.WHITE + extra));
                            tooltip.add(component);
                        } // Else we don't add the entire component
                    } else {
                        tooltip.add(component);
                    }
                }
            }
        }
    }

    public boolean isActive() {
        return infoLines != null || shiftInfoLines != null;
    }

    public static InfoLine key(String key) {
        return new InfoLine(key, null, stack -> true, null, null, ChatFormatting.YELLOW);
    }

    public static InfoLine header() {
        return new InfoLine(null, "header", stack -> true, null, null, ChatFormatting.GREEN);
    }

    public static InfoLine warning(Predicate<ItemStack> condition) {
        return new InfoLine(null, "warning", condition, null, null, ChatFormatting.RED);
    }

    public static InfoLine warning() {
        return new InfoLine(null, "warning", stack -> true, null, null, ChatFormatting.RED);
    }

    public static InfoLine gold(Predicate<ItemStack> condition) {
        return new InfoLine(null, "gold", condition, null, null, ChatFormatting.GOLD);
    }

    public static InfoLine gold() {
        return new InfoLine(null, "gold", stack -> true, null, null, ChatFormatting.GOLD);
    }

    public static InfoLine general(String suffix, ChatFormatting... styles) {
        return new InfoLine(null, suffix, stack -> true, null, null, styles);
    }

    public static InfoLine general(String suffix, Predicate<ItemStack> condition, ChatFormatting... styles) {
        return new InfoLine(null, suffix, condition, null, null, styles);
    }

    public static InfoLine parameter(String suffix, Function<ItemStack, String> getter) {
        return new InfoLine(null, suffix, stack -> true, getter, null, ChatFormatting.GRAY, ChatFormatting.BOLD);
    }

    public static InfoLine parameter(String suffix, Predicate<ItemStack> condition, Function<ItemStack, String> getter) {
        return new InfoLine(null, suffix, condition, getter, null, ChatFormatting.GRAY, ChatFormatting.BOLD);
    }

    public static InfoLine repeatingParameter(String suffix, Function<ItemStack, Stream<String>> repeater) {
        return new InfoLine(null, suffix, stack -> true, null, repeater, ChatFormatting.GRAY, ChatFormatting.BOLD);
    }
}
