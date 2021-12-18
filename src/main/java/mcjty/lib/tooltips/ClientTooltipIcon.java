package mcjty.lib.tooltips;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
        return Math.max(1, (itemStack.size() / itemsPerLine) * 20);
    }

    @Override
    public int getWidth(Font font) {
        return Math.min(itemsPerLine, itemStack.size()) * 20;
    }

    @Override
    public void renderImage(Font font, int offsetX, int offsetY, PoseStack pose, ItemRenderer itemRenderer, int p_174257_) {
        int j = 0;
        // Look through all the ItemStacks and add them to the tooltip at an x/y
        for (Pair<ItemStack, Integer> item : itemStack) {
            int x = offsetX + (Math.floorMod(j, itemsPerLine) * 18);
            int y = offsetY + (Math.floorDiv(j, itemsPerLine) * 20) - 20;
            // Render item
            itemRenderer.renderAndDecorateItem(item.getLeft(), x, y, p_174257_);
            // Render durability and/or cool down bar
            itemRenderer.renderGuiItemDecorations(font, item.getLeft(), x, y, "");
            renderItemCount(font, itemRenderer, x, y, item.getLeft().getCount(), item.getRight());
            j++;
        }
    }

    private static void renderItemCount(Font font, ItemRenderer itemRenderer, int x, int y, int count, int errorAmount) {
        if (errorAmount == ITooltipExtras.NOAMOUNT) {
            return;
        }

        if (count == 1 && errorAmount == 0) {
            return;
        }

        String s1 = count == Integer.MAX_VALUE ? "\u221E" : Integer.toString(count);

        if (errorAmount >= 0) {
            String s2 = ChatFormatting.RED + "(" + Integer.toString(errorAmount) + ")";
            s1 = s1 + s2;
        }
        int width = font.width(s1);
        PoseStack pose = new PoseStack();
        pose.translate(x + 17,y + font.lineHeight, 200D + itemRenderer.blitOffset);
        if (width > 18) {
            // Scale down to fit the space
            pose.pushPose();
            pose.scale(17F / width, 17F / width, 1F);
            width = 17;
        }
        MultiBufferSource.BufferSource buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        font.drawInBatch(s1,  -width, 0, 0xFFFFFF, true, pose.last().pose(), buffersource, false, 0, 0xFF00FF);
        buffersource.endBatch();
    }
}
