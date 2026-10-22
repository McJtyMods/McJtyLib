package mcjty.lib.tooltips;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ClientTooltipIcon implements ClientTooltipComponent, TooltipComponent {
    protected List<Pair<ItemStack, Integer>> itemStack;
    protected int itemsPerLine;

    public ClientTooltipIcon(List<Pair<ItemStack, Integer>> pItemStack, int pItemsPerLine) {
        itemStack = pItemStack;
        itemsPerLine = pItemsPerLine;
    }

    @Override
    public int getHeight(Font font) {
        return Math.max(1, ((itemStack.size() + itemsPerLine - 1) / itemsPerLine) * 20);
    }

    @Override
    public int getWidth(Font font) {
        return (Math.min(itemsPerLine, itemStack.size()) * 21) + 4;
    }

    @Override
    public void extractImage(Font font, int offsetX, int offsetY, int width, int height, GuiGraphicsExtractor graphics) {
        int j = 0;
        // Look through all the ItemStacks and add them to the tooltip at an x/y
        for (Pair<ItemStack, Integer> item : itemStack) {
            int x = offsetX + (Math.floorMod(j, itemsPerLine) * 21);
            int y = offsetY + (Math.floorDiv(j, itemsPerLine) * 20) - 20;
            // Render item
            graphics.item(item.getLeft(), x, y, x * y * 31);
            // Render durability and/or cool down bar
            graphics.itemDecorations(Minecraft.getInstance().font, item.getLeft(), x, y, "");

            renderItemCount(graphics, font, x, y + 20, item.getLeft().getCount(), item.getRight());
            j++;
        }
    }

    private static void renderItemCount(GuiGraphicsExtractor graphics, Font font, int x, int y, int count, int errorAmount) {
        if (errorAmount == ITooltipExtras.NOAMOUNT) {
            return;
        }

        if (count == 1 && errorAmount == 0) {
            return;
        }

        String countText = count == Integer.MAX_VALUE ? "\u221E" : Integer.toString(count);
        drawStringToWidth(graphics, font, x, y, 18, 0xffffffff, countText);
        if (errorAmount >= 0) drawStringToWidth(graphics, font, x + 8, y + 17, 18, 0xffff0000, "(" + errorAmount + ")");
    }

    private static void drawStringToWidth(GuiGraphicsExtractor graphics, Font font, int x, int y, int width, int color, String text) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(x + width, y);
        int textWidth = font.width(text);
        if (textWidth > width) graphics.pose().scale((float)width / textWidth);
        graphics.text(font, text, -textWidth, -font.lineHeight, color);
        graphics.pose().popMatrix();
    }
}
