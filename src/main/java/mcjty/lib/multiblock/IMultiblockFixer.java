package mcjty.lib.multiblock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

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
    void blockAdded(MultiblockDriver<T> driver, World level, BlockPos pos, int id, T newMb);

    /**
     * Merge serveral multiblocks to one. Warning! Keep in mind that
     * 'master' can be part of the set too!
     */
    void merge(MultiblockDriver<T> driver, World level, Set<T> mbs, int masterId, T newData);

    /**
     * Take an original multiblock and distribute it to the multiblocks in the todo list
     */
    void distribute(MultiblockDriver<T> driver, World level, T original, List<Pair<T, Set<BlockPos>>> todo);
}
