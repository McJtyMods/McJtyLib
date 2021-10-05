package mcjty.lib.multiblock;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MultiblockDriver<T extends IMultiblock> {

    private final Map<Integer,MultiblockHolder<T>> multiblocks = new HashMap<>();
    private int lastId = 0;

    private final Supplier<T> blockSupplier;
    private final Consumer<MultiblockDriver<T>> dirtySetter;

    public MultiblockDriver(Supplier<T> blockSupplier, Consumer<MultiblockDriver<T>> dirtySetter) {
        this.blockSupplier = blockSupplier;
        this.dirtySetter = dirtySetter;
    }

    public void clear() {
        multiblocks.clear();
        lastId = 0;
    }

    public T getOrCreate(int id) {
        MultiblockHolder<T> holder = multiblocks.get(id);
        T mb = holder.getMb();
        if (mb == null) {
            mb = blockSupplier.get();
            multiblocks.put(id, new MultiblockHolder<>(mb));
            dirtySetter.accept(this);
        }
        return mb;
    }

    public T get(int id) {
        MultiblockHolder<T> holder = multiblocks.get(id);
        return holder == null ? null : holder.getMb();
    }

    public void delete(int id) {
        multiblocks.remove(id);
        dirtySetter.accept(this);
    }

    public void modify(int id, Consumer<MultiblockHolder<T>> consumer) {
        MultiblockHolder<T> holder = multiblocks.get(id);
        if (holder != null) {
            consumer.accept(holder);
            dirtySetter.accept(this);
        }
    }

    public int create() {
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
            T value = blockSupplier.get();
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
            entry.getValue().save(tc);
            lst.add(tc);
        }
        tagCompound.put("mb", lst);
        tagCompound.putInt("lastId", lastId);
        return tagCompound;
    }

}
