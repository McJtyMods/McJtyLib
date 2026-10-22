package mcjty.lib.tooltips;

import com.mojang.datafixers.util.Either;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.keys.KeyBindings;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;

/**
 * This class was adapted from code written by Vazkii which was adapted by Direwolf20
 * Thanks Vazkii and Direwolf20!!
 */
public class TooltipRender {

    private static final int STACKS_PER_LINE = 8;

    public static ITooltipSettings lastUsedTooltipItem = null;

    public static void register() {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register((stack,context,flag,lines)->{onItemTooltip(stack,lines);wrapTooltip(stack,lines);});
    }

    public static ITooltipSettings getSettings(Item item) {
        if (item instanceof ITooltipSettings) {
            return (ITooltipSettings) item;
        } else if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ITooltipSettings) {
            return (ITooltipSettings) ((BlockItem) item).getBlock();
        } else {
            return null;
        }
    }

    public static void onItemTooltip(ItemStack stack, List<Component> lines) {
        Item item = stack.getItem();
        ITooltipSettings settings = getSettings(item);
        lastUsedTooltipItem = settings;
        if (settings != null) {
            ManualEntry entry = settings.getManualEntry();
            if (entry.manual() != null) {
                if (KeyBindings.openManual != null && !KeyBindings.openManual.isUnbound()) {
                    if (!SafeClientTools.isSneaking()) {
                        Component translationKey = KeyBindings.openManual.getTranslatedKeyMessage();
                        lines.add(ComponentFactory.literal("<Press ").withStyle(ChatFormatting.YELLOW)
                                .append(translationKey)
                                .append(ComponentFactory.literal(" for help>").withStyle(ChatFormatting.YELLOW)));
                    }
                }
            }
        }
    }

    private static void wrapTooltip(ItemStack stack,List<Component> lines) {
        var settings=getSettings(stack.getItem());if(settings==null||settings.getMaxWidth()<=0)return;
        var font=Minecraft.getInstance().font;var wrapped=new java.util.ArrayList<Component>();
        for(var line:lines)for(var part:font.getSplitter().splitLines(line,settings.getMaxWidth(),net.minecraft.network.chat.Style.EMPTY)){
            var text=Component.empty();part.visit((style,fragment)->{text.append(Component.literal(fragment).setStyle(style));return java.util.Optional.empty();},net.minecraft.network.chat.Style.EMPTY);wrapped.add(text);
        }
        lines.clear();lines.addAll(wrapped);
    }

    public static java.util.Optional<TooltipComponent> icons(ItemStack stack) {
        if (stack.getItem() instanceof ITooltipExtras extras) {
            var items=extras.getItems(stack);
            if(!items.isEmpty())return java.util.Optional.of(new ClientTooltipIcon(items,STACKS_PER_LINE));
        }
        return java.util.Optional.empty();
    }
}
