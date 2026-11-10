package mcjty.lib.setup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class DeferredItem<T extends Item> extends DeferredHolder<Item, T> {

    DeferredItem(ResourceLocation id) {
        super(id);
    }
}
