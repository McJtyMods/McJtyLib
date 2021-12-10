package mcjty.lib.tooltips;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mcjty.lib.McJtyLib;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.keys.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

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
                List<Component> tooltip = event.getToolTip();
                int count = items.size();
                int lines = (((count - 1) / STACKS_PER_LINE) + 1) * 2;
                int width = Math.min(STACKS_PER_LINE, count) * 18;
                String spaces = "";//"\u00a7r\u00a7r\u00a7r\u00a7r\u00a7r";
                while (mc.font.width(spaces) < width) {
                    spaces += " ";
                }

                for (int j = 0; j < lines; j++) {
                    tooltip.add(new TextComponent(spaces));
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
                        String translationKey = KeyBindings.openManual.saveString();
                        event.getToolTip().add(new TextComponent("<Press ").withStyle(ChatFormatting.YELLOW)
                                .append(new TranslatableComponent(translationKey).withStyle(ChatFormatting.GREEN))
                                .append(new TextComponent(" for help>").withStyle(ChatFormatting.YELLOW)));
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

            List<? extends FormattedText> tooltip = event.getLines();
            int lines = (((count - 1) / STACKS_PER_LINE) + 1);
            int width = Math.min(STACKS_PER_LINE, count) * 18;
            int height = lines * 20 + 1;

            for (FormattedText s : tooltip) {
                // @todo 1.16 is this right?
                if (s.getString().startsWith("    ")) {
//                if (s.trim().equals("\u00a77\u00a7r\u00a7r\u00a7r\u00a7r\u00a7r")) {
                    break;
                } else {
                    by += 10;
                }
            }

            GlStateManager._enableBlend();
            GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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

    private static void renderBlocks(PoseStack matrixStack, ItemStack itemStack, int x, int y, int count, int errorAmount) {
        Minecraft mc = Minecraft.getInstance();
        GlStateManager._disableDepthTest();
        ItemRenderer render = mc.getItemRenderer();

        com.mojang.blaze3d.platform.Lighting.setupForFlatItems();
        matrixStack.pushPose();
        matrixStack.translate(0, 0, 400f);
        renderItemModelIntoGUI(render, matrixStack, itemStack, x, y, render.getModel(itemStack, null, null));
//        render.renderItemIntoGUI(itemStack, x, y);  // @todo 1.16. Is there a version with matrixstack?
        matrixStack.popPose();

        //String s1 = count == Integer.MAX_VALUE ? "\u221E" : TextFormatting.BOLD + Integer.toString((int) ((float) req));
        String s1 = count == Integer.MAX_VALUE ? "\u221E" : Integer.toString((int) ((float) count));
        int w1 = mc.font.width(s1);
        int color = 0xFFFFFF;

        boolean hasReq = true;

        if (errorAmount != ITooltipExtras.NOAMOUNT) {
            matrixStack.pushPose();
            matrixStack.translate(16 - w1, (hasReq ? 8 : 10), 550f);
//            matrixStack.scale(0.5F, 0.5F, 0.5F);
//            matrixStack.translate(8 - w1/5, 0, 400f);
            mc.font.drawShadow(matrixStack, s1, x, y, color);
            matrixStack.popPose();
        }

        int missingCount = 0;

        if (errorAmount >= 0) {
            String fs = Integer.toString(errorAmount);
            //String s2 = TextFormatting.BOLD + "(" + fs + ")";
            String s2 = "(" + fs + ")";
            int w2 = mc.font.width(s2);

            matrixStack.pushPose();
            matrixStack.translate(x + 8 - w2 / 4, y + 17, 550f);

//            matrixStack.scale(0.5F, 0.5F, 0.5F);
            mc.font.drawShadow(matrixStack, s2, 0, 0, 0xFFFF0000);
            matrixStack.popPose();
        }
        GlStateManager._enableDepthTest();
    }

    private static void renderItemModelIntoGUI(ItemRenderer render, PoseStack matrixStack, ItemStack itemStack, int x, int y, BakedModel bakedmodel) {
        matrixStack.pushPose();
        Minecraft.getInstance().getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.translate((float)x, (float)y, 100.0F + render.blitOffset);
        matrixStack.translate(8.0F, 8.0F, 0.0F);
        matrixStack.scale(1.0F, -1.0F, 1.0F);
        matrixStack.scale(16.0F, 16.0F, 16.0F);
        MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !bakedmodel.usesBlockLight();
        if (flag) {
            Lighting.setupForFlatItems();
        }

        render.render(itemStack, ItemTransforms.TransformType.GUI, false, matrixStack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        irendertypebuffer$impl.endBatch();
        RenderSystem.enableDepthTest();
        if (flag) {
            Lighting.setupFor3DItems();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        matrixStack.popPose();
    }

}