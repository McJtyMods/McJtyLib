package mcjty.lib.test;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.tooltips.TooltipRender;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class CreativeSearchRegression implements ModInitializer {

    private static final String TOOLTIP = "Creative search probe 鵬鸞醫馥 with enough text to wrap in the inventory";
    private static SearchItem searchItem;

    @Override
    public void onInitialize() {
        if (Boolean.getBoolean("mcjtylib.clientRegression")) {
            var id = Identifier.fromNamespaceAndPath("mcjtylib_transfer_test", "creative_search");
            searchItem = Registry.register(BuiltInRegistries.ITEM, id,
                    new SearchItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
        }
    }

    static void check() {
        // The fixture runs at the title screen, before the world's item components are loaded.
        searchItem.builtInRegistryHolder().bindComponents(DataComponentMap.builder()
                .set(DataComponents.ITEM_NAME, Component.literal(TOOLTIP))
                .set(DataComponents.MAX_STACK_SIZE, 64).build());
        var stack = new ItemStack(searchItem);
        var previousTooltip = TooltipRender.lastUsedTooltipItem;
        var searchTrees = new SessionSearchTrees();
        searchTrees.updateCreativeTooltips(HolderLookup.Provider.create(Stream.empty()), List.of(stack));
        if (!searchTrees.creativeNameSearch().search(TOOLTIP.toLowerCase(Locale.ROOT)).equals(List.of(stack))) {
            throw new AssertionError("Creative search should index the full tooltip text");
        }
        if (TooltipRender.lastUsedTooltipItem != previousTooltip) {
            throw new AssertionError("Creative search should leave the manual tooltip target alone");
        }

        // Read the tooltip on the render thread only after indexing has seen the uncached glyphs.
        var lines = stack.getTooltipLines(Item.TooltipContext.EMPTY, null, TooltipFlag.Default.NORMAL);
        if (lines.size() < 2 || !lines.getFirst().getString().startsWith("Creative search probe")
                || lines.getFirst().getString().equals(TOOLTIP)) {
            throw new AssertionError("Visible item tooltips should still wrap");
        }
        if (TooltipRender.lastUsedTooltipItem != searchItem) {
            throw new AssertionError("Visible item tooltips should update the manual tooltip target");
        }
    }

    private static final class SearchItem extends Item implements ITooltipSettings {

        private SearchItem(Properties properties) {
            super(properties);
        }
    }
}
