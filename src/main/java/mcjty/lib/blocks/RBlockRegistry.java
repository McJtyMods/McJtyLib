package mcjty.lib.blocks;

import mcjty.lib.setup.DeferredBlocks;
import mcjty.lib.setup.DeferredItems;
import mcjty.lib.tileentity.AnnotationHolder;
import mcjty.lib.tileentity.AnnotationTools;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredHolder;
import mcjty.lib.setup.DeferredItem;
import mcjty.lib.setup.DeferredRegister;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class RBlockRegistry {
    private final DeferredBlocks BLOCKS;
    private final DeferredItems ITEMS;
    private final DeferredRegister<BlockEntityType<?>> TILES;
    private final Consumer<Supplier<ItemStack>> tab;

    private final List<AnnotationHolder> holders = new ArrayList<>();

    public RBlockRegistry(String modid, Consumer<Supplier<ItemStack>> tab) {
        BLOCKS = DeferredBlocks.create(modid);
        ITEMS = DeferredItems.create(modid);
        TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modid);
        this.tab = tab;
    }

    public void register() {
        BLOCKS.register();
        ITEMS.register();
        TILES.register();
    }

    public List<AnnotationHolder> getHolders() {
        return holders;
    }

    public <B extends BaseBlock, I extends BlockItem, E extends GenericTileEntity> RBlock<B, I, E> registerBlock(
            String name,
            Class<E> clazz,
            Supplier<B> blockSupplier,
            @Nullable Function<Supplier<? extends Block>, I> itemSupplier,
            BlockEntityType.BlockEntitySupplier<E> tileSupplier) {
        DeferredBlock<B> block = BLOCKS.register(name, blockSupplier);
        DeferredItem<I> item;
        if (itemSupplier != null) {
            item = ITEMS.register(name, () -> itemSupplier.apply(block));
            tab.accept(() -> new ItemStack(item.get()));
        } else {
            item = null;
        }
        DeferredHolder<BlockEntityType<?>, BlockEntityType<E>> tile = TILES.register(name, () -> new BlockEntityType<>(tileSupplier, java.util.Set.of(block.get())));
        AnnotationHolder holder = AnnotationTools.createAnnotationHolder(clazz, block);
        holders.add(holder);
        return new RBlock<>(block, item, tile);
    }

    /** Registry-aware factories ensure that block and item properties carry their 26.2 ids. */
    public <B extends BaseBlock, I extends BlockItem, E extends GenericTileEntity> RBlock<B, I, E> registerBlock(
            String name, Class<E> clazz,
            Function<net.minecraft.world.level.block.state.BlockBehaviour.Properties, B> blockFactory,
            @Nullable BiFunction<Supplier<? extends Block>, Item.Properties, I> itemFactory,
            BlockEntityType.BlockEntitySupplier<E> tileFactory) {
        DeferredBlock<B> block = BLOCKS.register(name, blockFactory);
        DeferredItem<I> item = itemFactory == null ? null : ITEMS.register(name, properties -> itemFactory.apply(block, properties));
        if (item != null) tab.accept(() -> new ItemStack(item.get()));
        var tile = TILES.register(name, () -> new BlockEntityType<>(tileFactory, java.util.Set.of(block.get())));
        holders.add(AnnotationTools.createAnnotationHolder(clazz, block));
        return new RBlock<>(block, item, tile);
    }

    // Version of registerBlock for WIP stuff. Doesn't appear in the creative tab or JEI
    public <B extends BaseBlock, I extends BlockItem, E extends GenericTileEntity> RBlock<B, I, E> registerBlockWIP(
            String name,
            Class<E> clazz,
            Supplier<B> blockSupplier,
            Function<Supplier<? extends Block>, I> itemSupplier,
            BlockEntityType.BlockEntitySupplier<E> tileSupplier) {
        DeferredBlock<B> block = BLOCKS.register(name, blockSupplier);
        DeferredItem<I> item = ITEMS.register(name, () -> itemSupplier.apply(block));
        DeferredHolder<BlockEntityType<?>, BlockEntityType<E>> tile = TILES.register(name, () -> new BlockEntityType<>(tileSupplier, java.util.Set.of(block.get())));
        AnnotationHolder holder = AnnotationTools.createAnnotationHolder(clazz, block);
        holders.add(holder);
        return new RBlock<>(block, item, tile);
    }

    public <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> itemSupplier) {
        DeferredItem<T> item = ITEMS.register(name, itemSupplier);
        tab.accept(() -> new ItemStack(item.get()));
        return item;
    }

    public <T extends Item> DeferredItem<T> registerItem(String name, Function<Item.Properties, T> factory) {
        DeferredItem<T> item = ITEMS.register(name, factory);
        tab.accept(() -> new ItemStack(item.get()));
        return item;
    }

    // Version of registerItem for WIP stuff. Doesn't appear in the creative tab or JEI
    public <T extends Item> DeferredItem<T> registerItemWIP(String name, Supplier<T> itemSupplier) {
        DeferredItem<T> item = ITEMS.register(name, itemSupplier);
        return item;
    }
}
