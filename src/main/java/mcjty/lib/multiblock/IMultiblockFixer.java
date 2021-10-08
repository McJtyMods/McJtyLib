package mcjty.lib.multiblock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * Implement this interface for various ways to fix a multiblock after a change occured (extra blocks, removed blocks, merged, ...)
 */
public interface IMultiblockFixer<T extends IMultiblock> {

    /**
     * Initialize a new multiblock with a single block
     */
    void initialize(MultiblockDriver<T> driver, World level, T newMb, int id);

    /**
     * A new block was added to the multiblock
     */
    void blockAdded(MultiblockDriver<T> driver, World level, BlockPos pos, int id, @Nonnull T newMb);

    /**
     * Merge serveral multiblocks to one and assign it as masterId
     */
    void merge(MultiblockDriver<T> driver, World level, Set<T> mbs, int masterId);

    /**
     * Take an original multiblock and distribute it to the multiblocks in the todo list
     */
    void distribute(MultiblockDriver<T> driver, World level, T original, List<Pair<Integer, Set<BlockPos>>> todo);
}
