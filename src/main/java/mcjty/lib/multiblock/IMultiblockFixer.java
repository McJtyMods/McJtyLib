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
     * Merge the other multiblock into the main one. The given multiblocks
     * are guaranteed to be compatible
     */
    void merge(MultiblockDriver<T> driver, World level, T mbMain, T mbOther);

    /**
     * Take an original multiblock and distribute it to the multiblocks in the todo list
     */
    void distribute(MultiblockDriver<T> driver, World level, T original, List<Pair<Integer, Set<BlockPos>>> todo);
}
