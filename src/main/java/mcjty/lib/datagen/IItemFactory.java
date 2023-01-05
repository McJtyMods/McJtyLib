package mcjty.lib.datagen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface IItemFactory {

    void parented(Supplier<? extends Block> blockSupplier, String model);

    void parented(Supplier<? extends Block> blockSupplier);

    void generated(Supplier<? extends Item> itemSupplier, String texture);

    void cubeAll(Supplier<? extends Item> itemSupplier, ResourceLocation texture);
}
