package mcjty.lib.tooltips;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mcjty.lib.McJtyLib;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.keys.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
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

    public static ITooltipSettings lastUsedTooltipItem = null;

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
                String spaces = "";//"\u00a7r\u00a7r\u00a7r\u00a7r\u00a7r";
                while (mc.fontRenderer.getStringWidth(spaces) < width) {
                    spaces += " ";
                }

                for (int j = 0; j < lines; j++) {
                    tooltip.add(new StringTextComponent(spaces));
                }
            }
        }
    }

    private static ITooltipSettings getSettings(Item item) {
        if (item instanceof ITooltipSettings) {
            return (ITooltipSettings) item;
        } else if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ITooltipSettings) {
            return (ITooltipSettings) ((BlockItem) item).getBlock();
        } else {
            return null;
        }
    }

    @SubscribeEvent
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        ITooltipSettings settings = getSettings(item);
        lastUsedTooltipItem = settings;
        if (settings != null) {
            ManualEntry entry = settings.getManualEntry();
            if (entry.getManual() != null) {
                if (KeyBindings.openManual != null) {
                    if (!McJtyLib.proxy.isSneaking()) {
                        String translationKey = KeyBindings.openManual.getTranslationKey();
                        event.getToolTip().add(new StringTextComponent("<Press ").mergeStyle(TextFormatting.YELLOW)
                                .append(new TranslationTextComponent(translationKey).mergeStyle(TextFormatting.GREEN))
                                .append(new StringTextComponent(" for help>").mergeStyle(TextFormatting.YELLOW)));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onTooltipPre(RenderTooltipEvent.Pre event) {
        Item item = event.getStack().getItem();
        ITooltipSettings settings = getSettings(item);
        lastUsedTooltipItem = settings;
        if (settings != null) {
            event.setMaxWidth(Math.max(event.getMaxWidth(), settings.getMaxWidth()));
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
            int by = event.getY()+3;

            List<? extends ITextProperties> tooltip = event.getLines();
            int lines = (((count - 1) / STACKS_PER_LINE) + 1);
            int width = Math.min(STACKS_PER_LINE, count) * 18;
            int height = lines * 20 + 1;

            for (ITextProperties s : tooltip) {
                // @todo 1.16 is this right?
                if (s.getString().startsWith("    ")) {
//                if (s.trim().equals("\u00a77\u00a7r\u00a7r\u00a7r\u00a7r\u00a7r")) {
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
                renderBlocks(event.getMatrixStack(), item.getLeft(), x, y, item.getLeft().getCount(), item.getRight());
                j++;
            }
        }
    }

    private static void renderBlocks(MatrixStack matrixStack, ItemStack itemStack, int x, int y, int count, int errorAmount) {
        Minecraft mc = Minecraft.getInstance();
        GlStateManager.disableDepthTest();
        ItemRenderer render = mc.getItemRenderer();

        net.minecraft.client.renderer.RenderHelper.setupGuiFlatDiffuseLighting();
        matrixStack.push();
        matrixStack.translate(0, 0, 400f);
        renderItemModelIntoGUI(render, matrixStack, itemStack, x, y, render.getItemModelWithOverrides(itemStack, (World)null, (LivingEntity)null));
//        render.renderItemIntoGUI(itemStack, x, y);  // @todo 1.16. Is there a version with matrixstack?
        matrixStack.pop();

        //String s1 = count == Integer.MAX_VALUE ? "\u221E" : TextFormatting.BOLD + Integer.toString((int) ((float) req));
        String s1 = count == Integer.MAX_VALUE ? "\u221E" : Integer.toString((int) ((float) count));
        int w1 = mc.fontRenderer.getStringWidth(s1);
        int color = 0xFFFFFF;

        boolean hasReq = true;

        if (errorAmount != ITooltipExtras.NOAMOUNT) {
            matrixStack.push();
            matrixStack.translate(16 - w1, (hasReq ? 8 : 10), 550f);
//            matrixStack.scale(0.5F, 0.5F, 0.5F);
//            matrixStack.translate(8 - w1/5, 0, 400f);
            mc.fontRenderer.drawStringWithShadow(matrixStack, s1, x, y, color);
            matrixStack.pop();
        }

        int missingCount = 0;

        if (errorAmount >= 0) {
            String fs = Integer.toString(errorAmount);
            //String s2 = TextFormatting.BOLD + "(" + fs + ")";
            String s2 = "(" + fs + ")";
            int w2 = mc.fontRenderer.getStringWidth(s2);

            matrixStack.push();
            matrixStack.translate(x + 8 - w2 / 4, y + 17, 550f);

//            matrixStack.scale(0.5F, 0.5F, 0.5F);
            mc.fontRenderer.drawStringWithShadow(matrixStack, s2, 0, 0, 0xFFFF0000);
            matrixStack.pop();
        }
        GlStateManager.enableDepthTest();
    }

    private static void renderItemModelIntoGUI(ItemRenderer render, MatrixStack matrixStack, ItemStack itemStack, int x, int y, IBakedModel bakedmodel) {
        matrixStack.push();
        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getInstance().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.translate((float)x, (float)y, 100.0F + render.zLevel);
        matrixStack.translate(8.0F, 8.0F, 0.0F);
        matrixStack.scale(1.0F, -1.0F, 1.0F);
        matrixStack.scale(16.0F, 16.0F, 16.0F);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        boolean flag = !bakedmodel.isSideLit();
        if (flag) {
            RenderHelper.setupGuiFlatDiffuseLighting();
        }

        render.renderItem(itemStack, ItemCameraTransforms.TransformType.GUI, false, matrixStack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        irendertypebuffer$impl.finish();
        RenderSystem.enableDepthTest();
        if (flag) {
            RenderHelper.setupGui3DDiffuseLighting();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        matrixStack.pop();
    }

}