package mcjty.lib.setup;

import net.minecraft.world.item.Item;
import net.minecraft.resources.Identifier;

public final class DeferredItem<T extends Item> extends DeferredHolder<Item,T> {

    DeferredItem(Identifier id) {
        super(id);
    }

}
