package mcjty.lib.multiblock;

import com.google.common.collect.Sets;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class MultiblockSupport {

    /**
     * Try to merge the multiblock on the given position to adjacent compatible multiblocks.
     * Return true if something was changed
     */
    public static <T extends IMultiblock> boolean merge(World level, BlockPos thisPos, MultiblockDriver<T> driver) {
        BiFunction<World, BlockPos, IMultiblockConnector> holderGetter = driver.getHolderGetter();
        IMultiblockFixer<T> fixer = driver.getFixer();
        BiPredicate<T, T> mergeChecker = driver.getMergeChecker();

        Set<Integer> adjacentGeneratorIds = new HashSet<>();
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos pos = thisPos.relative(direction);
            IMultiblockConnector holder = holderGetter.apply(level, pos);
            if (holder != null) {
                int multiblockId = holder.getMultiblockId();
                adjacentGeneratorIds.add(multiblockId);
            }
        }

        IMultiblockConnector thisHolder = holderGetter.apply(level, thisPos);
        int thisId = thisHolder.getMultiblockId();
        T thisMb = driver.get(thisId);

        if (adjacentGeneratorIds.isEmpty()) {
            // Nothing to do
            return false;
        } else if (adjacentGeneratorIds.size() == 1) {
            // Only one network adjacent. Check if it is compatible
            int mbId = adjacentGeneratorIds.iterator().next();
            if (mergeChecker.test(driver.get(mbId), thisMb)) {
                // Compatible so we can simply join this new block to that network
                fixer.merge(driver, level, driver.get(mbId), thisMb);
                thisHolder.setMultiblockId(mbId);
                return true;
            } else {
                // Not compatible. Nothing to do
                return false;
            }
        } else {
            // Multiple adjacent networks. Merge with the first compatible network
            boolean merged = false;
            for (Integer id : adjacentGeneratorIds) {
                T adjacentMb = driver.get(id);
                if (mergeChecker.test(thisMb, adjacentMb)) {
                    // Compatible. Merge
                    fixer.merge(driver, level, thisMb, adjacentMb);
                    merged = true;
                }
            }

            adjacentGeneratorIds.add(thisId);
            setBlocksToNetwork(level, thisPos, adjacentGeneratorIds, null, thisId, driver);

            return merged;
        }
    }

    /**
     * Add a new block to the adjacent multiblocks (possibly merging adjacent multiblocks)
     * The new block should not have a network id yet (set to -1!)
     */
    public static <T extends IMultiblock> void addBlock(World level, BlockPos thisPos, MultiblockDriver<T> driver, T newMb) {
        BiFunction<World, BlockPos, IMultiblockConnector> holderGetter = driver.getHolderGetter();
        IMultiblockFixer<T> fixer = driver.getFixer();

        int mbId = driver.createId();
        driver.createOrUpdate(mbId, newMb);
        fixer.initialize(driver, level, newMb, mbId);
        IMultiblockConnector thisHolder = holderGetter.apply(level, thisPos);
        thisHolder.setMultiblockId(mbId);
        merge(level, thisPos, driver);
    }

    public static <T extends IMultiblock> Set<BlockPos> findMultiblock(World level, BlockPos pos, MultiblockDriver<T> driver) {
        Set<BlockPos> positions = new HashSet<>();
        IMultiblockConnector holder = driver.getHolderGetter().apply(level, pos);
        if (holder != null) {
            findMultiblockInt(level, pos, driver.getHolderGetter(), positions);
        }
        return positions;
    }

    private static <T extends IMultiblock> void findMultiblockInt(World level, BlockPos pos, BiFunction<World, BlockPos, IMultiblockConnector> getter, Set<BlockPos> positions) {
        positions.add(pos);
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos p = pos.relative(direction);
            if (!positions.contains(p)) {
                if (getter.apply(level, p) != null) {
                    findMultiblockInt(level, p, getter, positions);
                }
            }
        }
    }

    /// After this routine the 'done' set will be all the blocks for this network
    private static <T extends IMultiblock> void setBlocksToNetwork(World level, BlockPos c,
                                                                   @Nonnull Set<Integer> ids, @Nullable Set<BlockPos> done, int newId, MultiblockDriver<T> driver) {
        IMultiblockConnector connector = driver.getHolderGetter().apply(level, c);
        if (connector == null) {
            return;
        }

        if (done != null) {
            done.add(c);
        }
        connector.setMultiblockId(newId);

        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = c.relative(direction);
            if (done == null || !done.contains(newC)) {
                IMultiblockConnector con = driver.getHolderGetter().apply(level, newC);
                if (con != null) {
                    int id = con.getMultiblockId();
                    if (id != newId && ids.contains(id)) {
                        setBlocksToNetwork(level, newC, ids, done, newId, driver);
                    }
                }
            }
        }
    }

    public static <T extends IMultiblock> void removeBlock(World level, BlockPos thisPos, MultiblockDriver<T> driver) {

        BiFunction<World, BlockPos, IMultiblockConnector> holderGetter = driver.getHolderGetter();
        IMultiblockConnector thisHolder = holderGetter.apply(level, thisPos);
        T thisData = driver.get(thisHolder.getMultiblockId());

        // Clear all networks adjacent to this one.
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = thisPos.relative(direction);
            IMultiblockConnector holder = holderGetter.apply(level, newC);
            if (holder != null) {
//                Set<BlockPos> done = Sets.newHashSet();
//                done.add(thisPos);
                setBlocksToNetwork(level, newC, Collections.singleton(holder.getMultiblockId()), null,-1, driver);
//                setBlocksToNetwork(level, newC, done, -1, driver);
            }
        }

        // Now assign new ones.
        List<Pair<Integer, Set<BlockPos>>> todo = new ArrayList<>();  // A list of networks we have to fix
        int idToUse = thisHolder.getMultiblockId();
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = thisPos.relative(direction);
            IMultiblockConnector holder = holderGetter.apply(level, newC);
            if (holder != null) {
                if (holder.getMultiblockId() == -1) {
                    if (idToUse == -1) {
                        idToUse = driver.createId();
                    }
                    Set<BlockPos> done = Sets.newHashSet();
                    done.add(thisPos);
                    setBlocksToNetwork(level, newC, Collections.singleton(-1), done, idToUse, driver);
                    done.remove(thisPos);
                    todo.add(Pair.of(idToUse, done));

                    idToUse = -1;
                }
            }
        }

        // We now have a list of multiblocks with associated positions (in todo)
        driver.getFixer().distribute(driver, level, thisData, todo);
    }


}
