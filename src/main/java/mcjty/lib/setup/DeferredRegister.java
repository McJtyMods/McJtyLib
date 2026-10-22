package mcjty.lib.setup;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import java.util.*;
import java.util.function.*;

/** Deferred native registry writes. Call register() from the mod's Fabric initializer. */
public class DeferredRegister<R> {

    protected final ResourceKey<? extends Registry<R>> key;
    protected final String namespace;

    private final List<Runnable> entries=new ArrayList<>();
    private boolean registered;

    protected DeferredRegister(ResourceKey<? extends Registry<R>> key,String namespace) {
        this.key=key;
        this.namespace=namespace;
    }

    public static <R> DeferredRegister<R> create(ResourceKey<? extends Registry<R>> key,String namespace) {
        return new DeferredRegister<>(key,namespace);
    }

    public static Items createItems(String ns) {
        return new Items(ns);
    }
    public static Blocks createBlocks(String ns) {
        return new Blocks(ns);
    }

    public static DataComponents createDataComponents(ResourceKey<? extends Registry<DataComponentType<?>>> key,String ns) {
        return new DataComponents(ns);
    }

    protected Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(namespace,name);
    }

    protected <T extends R,H extends DeferredHolder<R,T>> H enqueue(H holder,Supplier<T> factory) {

        if(registered)throw new IllegalStateException("Registration already finished: "+namespace);

        entries.add(()-> {
            Registry<R> registry=(Registry<R>)BuiltInRegistries.REGISTRY.getValue(key.identifier());holder.bind(Registry.register(registry,holder.getId(),factory.get()));
        });
        return holder;

    }

    public <T extends R> DeferredHolder<R,T> register(String name,Supplier<T> factory) {
        return enqueue(new DeferredHolder<>(id(name)),factory);
    }

    public void register() {
        if(registered)return;
        registered=true;
        entries.forEach(Runnable::run);
    }

    public static final class Items extends DeferredRegister<Item> {

        private Items(String ns) {
            super(Registries.ITEM,ns);
        }

        @Override
    public <T extends Item> DeferredItem<T> register(String name,Supplier<T> factory) {
            return enqueue(new DeferredItem<>(id(name)),factory);
        }

        public <T extends Item> DeferredItem<T> registerItem(String name,Function<Item.Properties,T> factory) {
            return register(name,()->factory.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id(name)))));
        }

    }

    public static final class Blocks extends DeferredRegister<Block> {

        private Blocks(String ns) {
            super(Registries.BLOCK,ns);
        }

        @Override
    public <T extends Block> DeferredBlock<T> register(String name,Supplier<T> factory) {
            return enqueue(new DeferredBlock<>(id(name)),factory);
        }

        public <T extends Block> DeferredBlock<T> registerBlock(String name,Function<BlockBehaviour.Properties,T> factory) {
            return register(name,()->factory.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK,id(name)))));
        }

    }

    public static final class DataComponents extends DeferredRegister<DataComponentType<?>> {

        private DataComponents(String ns) {
            super(Registries.DATA_COMPONENT_TYPE,ns);
        }

        public <T> DeferredHolder<DataComponentType<?>,DataComponentType<T>> registerComponentType(String name,UnaryOperator<DataComponentType.Builder<T>> factory) {
            return register(name,()->factory.apply(DataComponentType.<T>builder()).build());
        }

    }

}
