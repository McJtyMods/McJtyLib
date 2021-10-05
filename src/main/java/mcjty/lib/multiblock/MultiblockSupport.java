package mcjty.lib.multiblock;

public class MultiblockSupport {}/*{

    public static <T extends IMultiblock> void addBlockToNetwork(World level, BlockPos thisPos,
                                                                 MultiblockDriver<T> driver, BiFunction<World, BlockPos, IMultiblockConnector> holderGetter) {
        Set<Integer> adjacentGeneratorIds = new HashSet<>();
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos pos = thisPos.relative(direction);
            IMultiblockConnector holder = holderGetter.apply(level, pos);
            if (holder != null) {
                adjacentGeneratorIds.add(holder.getMultiblockId());
            }
        }

        IMultiblockConnector thisHolder = holderGetter.apply(level, thisPos);
        int mbId = thisHolder.getMultiblockId();

        if (adjacentGeneratorIds.isEmpty()) {
            // New network
            mbId = driver.create();
            T mb = driver.getOrCreate(mbId);
            mb.setGeneratorBlocks(1);
        } else if (adjacentGeneratorIds.size() == 1) {
            // Only one network adjacent. So we can simply join this new block to that network
            mbId = adjacentGeneratorIds.iterator().next();
            T mb = driver.getOrCreate(mbId);
            mb.setActive(false);       // Deactivate to make sure it properly restarts
            mb.incGeneratorBlocks();
        } else {
            // We need to merge networks. The first network will be the master. First we
            // calculate the total amount of energy in all the networks that are merged this way
            int energy = 0;
            for (Integer netId : adjacentGeneratorIds) {
                T mb = driver.getOrCreate(netId);
                mb.setActive(false);       // Deactivate to make sure it properly restarts
                energy += mb.getEnergy();
            }

            int id = adjacentGeneratorIds.iterator().next();
            Set<BlockPos> done = Sets.newHashSet();
            setBlocksToNetwork(thisPos, done, id, driver, holderGetter);

            T mb = driver.getOrCreate(mbId);
            mb.setEnergy(energy);
        }
    }

    private static <T extends IMultiblock> void setBlocksToNetwork(BlockPos c, Set<BlockPos> done, int newId,
                                           MultiblockDriver<T> driver, BiFunction<World, BlockPos, IMultiblockConnector> holderGetter) {
        done.add(c);

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(level);
        TileEntityGeneratorPart generatorTileEntity = (TileEntityGeneratorPart) level.getBlockEntity(c);
        int oldNetworkId = generatorTileEntity.getNetworkId();
        if (oldNetworkId != newId) {
            if (oldNetworkId != -1) {
                generatorNetwork.getOrCreateNetwork(oldNetworkId).decGeneratorBlocks();
            }
            generatorTileEntity.setNetworkId(newId);
            if (newId != -1) {
                generatorNetwork.getOrCreateNetwork(newId).incGeneratorBlocks();
            }
        }

        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = c.relative(direction);
            if (!done.contains(newC)) {
                Block block = level.getBlockState(newC).getBlock();
                if (block == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                    setBlocksToNetwork(newC, done, newId);
                }
            }
        }
    }


}
*/