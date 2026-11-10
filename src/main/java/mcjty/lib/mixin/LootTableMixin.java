package mcjty.lib.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.ArrayList;
import java.util.function.Consumer;
import mcjty.lib.varia.GlobalLootFunctions;
import mcjty.lib.varia.LootGeneratedEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LootTable.class)
abstract class LootTableMixin {

    @WrapMethod(
            method =
                    "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V")
    private void mcjtylib$globalLootFunctions(
            LootContext context, Consumer<ItemStack> consumer, Operation<Void> original) {
        var drops = new ArrayList<ItemStack>();
        original.call(context, (Consumer<ItemStack>) drops::add);
        GlobalLootFunctions.modify(context, drops);
        var id = GlobalLootFunctions.tableId((LootTable) (Object) this);
        if (id != null) LootGeneratedEvent.EVENT.invoker().modify(id, context, drops);
        drops.forEach(consumer);
    }
}
