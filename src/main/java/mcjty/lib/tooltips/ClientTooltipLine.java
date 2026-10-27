package mcjty.lib.tooltips;

import com.mojang.datafixers.util.Either;
import mcjty.lib.gui.WindowTools;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** A widget tooltip line with inline item icons, rendered in the native tooltip pass. */
public record ClientTooltipLine(List<Object> parts) implements ClientTooltipComponent, TooltipComponent {
    public ClientTooltipLine {
        parts = List.copyOf(parts);
    }

    public static List<Either<FormattedText, TooltipComponent>> createElements(List<String> lines, List<ItemStack> items) {
        List<Either<FormattedText, TooltipComponent>> elements = new ArrayList<>();
        for (String line : lines) {
            if (items != null && !items.isEmpty() && line.contains("@")) {
                List<Object> parts = WindowTools.parseString(line, items).stream()
                        .map(part -> part instanceof String text ? ComponentFactory.formatted(text) : part)
                        .toList();
                elements.add(Either.right(new ClientTooltipLine(parts)));
            } else {
                elements.add(Either.left(ComponentFactory.formatted(line)));
            }
        }
        return elements;
    }

    public static void queue(GuiGraphicsExtractor graphics, Font font, List<String> lines, List<ItemStack> items, int mouseX, int mouseY) {
        if (!lines.isEmpty()) {
            graphics.setComponentTooltipFromElementsForNextFrame(font, createElements(lines, items), mouseX, mouseY, ItemStack.EMPTY);
        }
    }

    @Override
    public int getHeight(Font font) {
        return parts.stream().anyMatch(ItemStack.class::isInstance) ? 18 : 10;
    }

    @Override
    public int getWidth(Font font) {
        return parts.stream().mapToInt(part -> part instanceof Component text ? font.width(text) : 20).sum();
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        for (Object part : parts) {
            if (part instanceof Component text) {
                graphics.text(font, text, x, y, 0xffffffff);
                x += font.width(text);
            } else {
                x += 20;
            }
        }
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        for (Object part : parts) {
            if (part instanceof Component text) {
                x += font.width(text);
            } else if (part instanceof ItemStack stack) {
                graphics.item(stack, x + 1, y);
                graphics.itemDecorations(font, stack, x + 1, y);
                x += 20;
            }
        }
    }
}
