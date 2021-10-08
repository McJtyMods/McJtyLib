package mcjty.lib.multiblock;

import com.google.common.collect.Sets;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class MultiblockSupport {

    /**
     * Add a new block to the adjacent multiblocks (possibly merging adjacent multiblocks)
     * The new block should not have a network id yet (set to -1!)
     */
    public static <T extends IMultiblock> void addBlock(World level, BlockPos thisPos, MultiblockDriver<T> driver, T newMb) {
        BiFunction<World, BlockPos, IMultiblockConnector> holderGetter = driver.getHolderGetter();
        IMultiblockFixer<T> fixer = driver.getFixer();

        Set<Integer> adjacentGeneratorIds = new HashSet<>();
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos pos = thisPos.relative(direction);
            IMultiblockConnector holder = holderGetter.apply(level, pos);
            if (holder != null) {
                adjacentGeneratorIds.add(holder.getMultiblockId());
            }
        }

        IMultiblockConnector thisHolder = holderGetter.apply(level, thisPos);

        if (adjacentGeneratorIds.isEmpty()) {
            // New network
            int mbId = driver.createId();
            driver.createOrUpdate(mbId, newMb);
            fixer.initialize(driver, level, newMb, mbId);
        } else if (adjacentGeneratorIds.size() == 1) {
            // Only one network adjacent. So we can simply join this new block to that network
            int mbId = adjacentGeneratorIds.iterator().next();
            fixer.blockAdded(driver, level, thisPos, mbId, newMb);
        } else {
            // We need to merge networks
            Set<T> mbs = adjacentGeneratorIds.stream().map(driver::get).collect(Collectors.toSet());

            int masterId = adjacentGeneratorIds.iterator().next();
            Set<BlockPos> done = Sets.newHashSet();
            setBlocksToNetwork(level, thisPos, done, masterId, driver);

            fixer.merge(driver, level, mbs, masterId, newMb);
        }
    }

    /// After this routine the 'done' set will be all the blocks for this network
    private static <T extends IMultiblock> void setBlocksToNetwork(World level, BlockPos c, Set<BlockPos> done, int newId, MultiblockDriver<T> driver) {
        IMultiblockConnector connector = driver.getHolderGetter().apply(level, c);
        if (connector == null) {
            return;
        }

        done.add(c);
        connector.setMultiblockId(newId);

        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = c.relative(direction);
            if (!done.contains(newC)) {
                if (driver.getHolderGetter().apply(level, newC) != null) {
                    setBlocksToNetwork(level, newC, done, newId, driver);
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
                Set<BlockPos> done = Sets.newHashSet();
                done.add(thisPos);
                setBlocksToNetwork(level, newC, done, -1, driver);
            }
        }

        // Now assign new ones.
        List<Pair<T, Set<BlockPos>>> todo = new ArrayList<>();  // A list of networks we have to fix
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
                    setBlocksToNetwork(level, newC, done, idToUse, driver);
                    T mb = driver.get(idToUse);
                    todo.add(Pair.of(mb, done));

                    idToUse = -1;
                }
            }
        }

        // We now have a list of multiblocks with associated positions (in todo)
        driver.getFixer().distribute(driver, level, thisData, todo);

        // Now we need to redistribute the total energy based on the size of the adjacent networks.
//        int energy = totalEnergy / totalBlocks;
//        int remainder = totalEnergy % totalBlocks;
//        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
//            BlockPos newC = thisPos.relative(direction);
//            Block block = level.getBlockState(newC).getBlock();
//            if (block == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
//                TileEntityGeneratorPart generatorTileEntity = (TileEntityGeneratorPart) level.getBlockEntity(newC);
//                DRGeneratorNetwork.Network network = generatorTileEntity.getNetwork();
//                if (network.getEnergy() == -1) {
//                    network.setEnergy(energy * network.getGeneratorBlocks() + remainder);
//                    remainder = 0;  // Only the first network gets the remainder.
//                }
//            }
//        }
//        generatorNetwork.save();
    }


}