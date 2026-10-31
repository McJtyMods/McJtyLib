package mcjty.lib.tooltips;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
    public int getHeight() {
        return ((itemStack.size() + itemsPerLine - 1) / itemsPerLine) * getRowHeight();
    }

    private int getRowHeight() {
        return itemStack.stream().anyMatch(item -> item.getRight() >= 0) ? 37 : 20;
    }

    @Override
    public int getWidth(Font font) {
        return (Math.min(itemsPerLine, itemStack.size()) * 21) + 4;
    }

    @Override
    public void renderImage(Font font, int offsetX, int offsetY, GuiGraphics graphics) {
        int j = 0;
        int rowHeight = getRowHeight();
        // Look through all the ItemStacks and add them to the tooltip at an x/y
        for (Pair<ItemStack, Integer> item : itemStack) {
            int x = offsetX + (Math.floorMod(j, itemsPerLine) * 21);
            int y = offsetY + (Math.floorDiv(j, itemsPerLine) * rowHeight);
            // Render item
            graphics.renderItem(item.getLeft(), x, y, x * y * 31);
            // Render durability and/or cool down bar
            graphics.renderItemDecorations(font, item.getLeft(), x, y, "");

            renderItemCount(font, graphics, x, y + 20, item.getLeft().getCount(), item.getRight());
            j++;
        }
    }

    private static void renderItemCount(Font font, GuiGraphics graphics, int x, int y, int count, int errorAmount) {
        if (errorAmount == ITooltipExtras.NOAMOUNT) {
            return;
        }

        if (count == 1 && errorAmount == 0) {
            return;
        }

        String s1 = count == Integer.MAX_VALUE ? "\u221E" : Integer.toString(count);

        drawStringToWidth(font, graphics, x, y, 18, 0xFFFFFFFF, s1);

        if (errorAmount >= 0) {
            String s2 = "(" + Integer.toString(errorAmount) + ")";
            drawStringToWidth(font, graphics, x + 8, y + 17, 18, 0xFFFF0000, s2);
        }
    }

    /**
     * @param x bottom edge
     * @param y right side
     * @param width space to render in
     */
    private static void drawStringToWidth(Font font, GuiGraphics graphics, int x, int y, int width, int color, String str) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + width, y, 200);
        int strWidth = font.width(str);
        if (strWidth > width) {
            // Scale down to fit the space
            pose.scale((float) width / strWidth, (float) width / strWidth, 1F);
        }

        graphics.drawString(font, str, -strWidth, -font.lineHeight, color, true);

        pose.popPose();
    }
}
