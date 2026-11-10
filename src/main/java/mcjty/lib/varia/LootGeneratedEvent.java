package mcjty.lib.varia;

import java.util.List;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

@FunctionalInterface
public interface LootGeneratedEvent {

    Event<LootGeneratedEvent> EVENT =
            EventFactory.createArrayBacked(
                    LootGeneratedEvent.class,
                    listeners ->
                            (id, context, drops) -> {
                                for (var listener : listeners) listener.modify(id, context, drops);
                            });

    void modify(ResourceLocation id, LootContext context, List<ItemStack> drops);
}
