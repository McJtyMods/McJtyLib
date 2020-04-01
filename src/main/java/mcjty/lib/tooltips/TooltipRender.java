package mcjty.lib.tooltips;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * This class was adapted from code written by Vazkii which was adapted by Direwolf20
 * Thanks Vazkii and Direwolf20!!
 */
public class TooltipRender {

    private static final int STACKS_PER_LINE = 8;

    @SubscribeEvent
    public void onMakeTooltip(ItemTooltipEvent event) {
        //This method extends the tooltip box size to fit the item's we will render in onDrawTooltip
        Minecraft mc = Minecraft.getInstance();
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof ITooltipExtras) {
            ITooltipExtras extras = (ITooltipExtras) stack.getItem();
            List<Pair<ItemStack, Integer>> items = extras.getItems(stack);
            if (!items.isEmpty()) {
                List<ITextComponent> tooltip = event.getToolTip();
                int count = items.size();
                int lines = (((count - 1) / STACKS_PER_LINE) + 1) * 2;
                int width = Math.min(STACKS_PER_LINE, count) * 18;
                String spaces = "\u00a7r\u00a7r\u00a7r\u00a7r\u00a7r";
                while (mc.fontRenderer.getStringWidth(spaces) < width) {
                    spaces += " ";
                }

                for (int j = 0; j < lines; j++) {
                    tooltip.add(new StringTextComponent(spaces));
                }
            }
        }
    }

    @SubscribeEvent
    public void onTooltipPre(RenderTooltipEvent.Pre event) {
        Item item = event.getStack().getItem();
        if (item instanceof ITooltipSettings) {
            event.setMaxWidth(Math.max(event.getMaxWidth(), ((ITooltipSettings) item).getMaxWidth()));
        } else if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ITooltipSettings) {
            event.setMaxWidth(Math.max(event.getMaxWidth(), ((ITooltipSettings) ((BlockItem) item).getBlock()).getMaxWidth()));
        }
    }

    @SubscribeEvent
    public void onDrawTooltip(RenderTooltipEvent.PostText event) {
        //This method will draw items on the tooltip
        ItemStack stack = event.getStack();
        if (stack.getItem() instanceof ITooltipExtras) {
            ITooltipExtras extras = (ITooltipExtras) stack.getItem();
            List<Pair<ItemStack, Integer>> items = extras.getItems(stack);
            int count = items.size();

            int bx = event.getX();
            int by = event.getY();

            List<String> tooltip = event.getLines();
            int lines = (((count - 1) / STACKS_PER_LINE) + 1);
            int width = Math.min(STACKS_PER_LINE, count) * 18;
            int height = lines * 20 + 1;

            for (String s : tooltip) {
                if (s.trim().equals("\u00a77\u00a7r\u00a7r\u00a7r\u00a7r\u00a7r")) {
                    break;
                } else {
                    by += 10;
                }
            }

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            //Gui.drawRect(bx, by, bx + width, by + height, 0x55000000);

            int j = 0;
            //Look through all the ItemStacks and draw each one in the specified X/Y position
            for (Pair<ItemStack, Integer> item : items) {
                int x = bx + (j % STACKS_PER_LINE) * 18;
                int y = by + (j / STACKS_PER_LINE) * 20;
                renderBlocks(item.getLeft(), x, y, item.getLeft().getCount(), item.getRight());
                j++;
            }
        }
    }

    private static void renderBlocks(ItemStack itemStack, int x, int y, int count, int errorAmount) {
        Minecraft mc = Minecraft.getInstance();
        GlStateManager.disableDepthTest();
        ItemRenderer render = mc.getItemRenderer();

        net.minecraft.client.renderer.RenderHelper.setupGuiFlatDiffuseLighting();
        render.renderItemIntoGUI(itemStack, x, y);

        //String s1 = count == Integer.MAX_VALUE ? "\u221E" : TextFormatting.BOLD + Integer.toString((int) ((float) req));
        String s1 = count == Integer.MAX_VALUE ? "\u221E" : Integer.toString((int) ((float) count));
        int w1 = mc.fontRenderer.getStringWidth(s1);
        int color = 0xFFFFFF;

        boolean hasReq = true;

        GlStateManager.pushMatrix();
        GlStateManager.translatef(x + 8 - w1 / 4, y + (hasReq ? 12 : 14), 0);
        GlStateManager.scalef(0.5F, 0.5F, 0.5F);
        mc.fontRenderer.drawStringWithShadow(s1, 0, 0, color);
        GlStateManager.popMatrix();

        int missingCount = 0;

        if (errorAmount != -1) {
            String fs = Integer.toString(errorAmount);
            //String s2 = TextFormatting.BOLD + "(" + fs + ")";
            String s2 = "(" + fs + ")";
            int w2 = mc.fontRenderer.getStringWidth(s2);

            GlStateManager.pushMatrix();
            GlStateManager.translatef(x + 8 - w2 / 4, y + 17, 0);
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
            mc.fontRenderer.drawStringWithShadow(s2, 0, 0, 0xFF0000);
            GlStateManager.popMatrix();
        }
        GlStateManager.enableDepthTest();
    }
}