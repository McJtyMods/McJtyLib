package mcjty.lib.multiblock;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class MultiblockDriver<T extends IMultiblock> {

    private final Map<Integer,MultiblockHolder<T>> multiblocks = new HashMap<>();
    private int lastId = 0;

    private final Function<CompoundNBT, T> loader;
    private final BiConsumer<CompoundNBT, T> saver;
    private final Consumer<MultiblockDriver<T>> dirtySetter;
    private final IMultiblockFixer<T> fixer;
    private final BiFunction<World, BlockPos, IMultiblockConnector> holderGetter;

    private MultiblockDriver(Builder<T> builder) {
        this.loader = builder.loader;
        this.saver = builder.saver;
        this.dirtySetter = builder.dirtySetter;
        this.fixer = builder.fixer;
        this.holderGetter = builder.holderGetter;
    }

    public void clear() {
        multiblocks.clear();
        lastId = 0;
    }

    public IMultiblockFixer<T> getFixer() {
        return fixer;
    }

    public BiFunction<World, BlockPos, IMultiblockConnector> getHolderGetter() {
        return holderGetter;
    }

    /**
     * Create (or replace) a multiblock with a given id and initialize it
     */
    public void createOrUpdate(int id, T mb) {
        multiblocks.put(id, new MultiblockHolder<>(mb));
    }

    /**
     * Get a multiblock with a given id. Returns null if the multiblock doesn't exist
     */
    @Nullable
    public T get(int id) {
        MultiblockHolder<T> holder = multiblocks.get(id);
        return holder == null ? null : holder.getMb();
    }

    /**
     * Delete a multiblock
     */
    public void delete(int id) {
        multiblocks.remove(id);
        dirtySetter.accept(this);
    }

    /**
     * Modify a multiblock (holder) and make sure the data gets saved afterwards
     */
    public void modify(int id, Consumer<MultiblockHolder<T>> consumer) {
        MultiblockHolder<T> holder = multiblocks.get(id);
        if (holder != null) {
            consumer.accept(holder);
            dirtySetter.accept(this);
        }
    }

    /**
     * Create a new multiblock ID
     */
    public int createId() {
        lastId++;
        dirtySetter.accept(this);
        return lastId;
    }

    public void load(CompoundNBT tagCompound) {
        clear();
        ListNBT lst = tagCompound.getList("mb", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < lst.size() ; i++) {
            CompoundNBT tc = lst.getCompound(i);
            int id = tc.getInt("id");
            T value = loader.apply(tc);
            MultiblockHolder<T> holder = new MultiblockHolder<>(value);
            holder.load(tc);
            multiblocks.put(id, holder);
        }
        lastId = tagCompound.getInt("lastId");
    }

    public CompoundNBT save(CompoundNBT tagCompound) {
        ListNBT lst = new ListNBT();
        for (Map.Entry<Integer, MultiblockHolder<T>> entry : multiblocks.entrySet()) {
            CompoundNBT tc = new CompoundNBT();
            tc.putInt("id", entry.getKey());
            saver.accept(tc, entry.getValue().getMb());
            entry.getValue().save(tc);
            lst.add(tc);
        }
        tagCompound.put("mb", lst);
        tagCompound.putInt("lastId", lastId);
        return tagCompound;
    }

    public static <T extends IMultiblock> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T extends IMultiblock> {

        private Function<CompoundNBT, T> loader;
        private BiConsumer<CompoundNBT, T> saver;
        private Consumer<MultiblockDriver<T>> dirtySetter;
        private IMultiblockFixer<T> fixer;
        private BiFunction<World, BlockPos, IMultiblockConnector> holderGetter;

        public Builder<T> loader(Function<CompoundNBT, T> loader) {
            this.loader = loader;
            return this;
        }

        public Builder<T> saver(BiConsumer<CompoundNBT, T> saver) {
            this.saver = saver;
            return this;
        }

        public Builder<T> dirtySetter(Consumer<MultiblockDriver<T>> dirtySetter) {
            this.dirtySetter = dirtySetter;
            return this;
        }

        public Builder<T> fixer(IMultiblockFixer<T> fixer) {
            this.fixer = fixer;
            return this;
        }

        public Builder<T> holderGetter(BiFunction<World, BlockPos, IMultiblockConnector> holderGetter) {
            this.holderGetter = holderGetter;
            return this;
        }

        public MultiblockDriver<T> build() {
            return new MultiblockDriver<>(this);
        }
    }

}
