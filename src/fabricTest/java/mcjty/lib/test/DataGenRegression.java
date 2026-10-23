package mcjty.lib.test;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

/** Exercises recipes, advancements, loot, models and global functions in the real datagen bootstrap. */
public final class DataGenRegression implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        generator.createPack().addProvider((output, lookup) -> {
            var data = new DataGen(output, lookup);
            data.add(Dob.itemBuilder(() -> Items.DIAMOND)
                    .name("Regression diamond")
                    .generatedItem("minecraft:item/diamond")
                    .shapedComponentPreserve("components", recipe -> recipe.define('X', Items.IRON_INGOT)
                            .unlockedBy("iron", DataGen.has(Items.IRON_INGOT)), "XX", "XX"));
            data.add(Dob.blockBuilder(() -> Blocks.STONE).simpleLoot());
            data.add(Dob.builder().glm("count", () -> SetItemCountFunction.setCount(ContextIntProviders.exactly(2)).build()));
            data.generate();
            return data;
        });
    }
}
