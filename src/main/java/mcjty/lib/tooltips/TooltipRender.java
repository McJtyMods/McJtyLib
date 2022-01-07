package mcjty.lib.tooltips;

import com.mojang.datafixers.util.Either;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.keys.KeyBindings;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

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
        if (stack.getItem() instanceof ITooltipExtras extras) {
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
            if (entry.manual() != null) {
                if (KeyBindings.openManual != null) {
                    if (!SafeClientTools.isSneaking()) {
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
    public void onTooltipGatherComponents(RenderTooltipEvent.GatherComponents event) {
        Item item = event.getItemStack().getItem();
        ITooltipSettings settings = getSettings(item);
        lastUsedTooltipItem = settings;
        if (settings != null) {
            event.setMaxWidth(Math.max(event.getMaxWidth(), settings.getMaxWidth()));
        }

        onTooltipAddIcons(event);
    }

    protected void onTooltipAddIcons(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof ITooltipExtras extras) {
            List<Pair<ItemStack, Integer>> items = extras.getItems(stack);
            List<Either<FormattedText, TooltipComponent>> components = event.getTooltipElements();

            components.add(Either.right(new ClientTooltipIcon(items, STACKS_PER_LINE)));
        }
    }

}