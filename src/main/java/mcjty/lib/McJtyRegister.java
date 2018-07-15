package mcjty.lib;

import mcjty.lib.base.ModBase;
import mcjty.lib.datafix.fixes.TileEntityNamespace;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class McJtyRegister {

    private static final List<MBlock> blocks = new ArrayList<>();
    private static final Map<Block,MTile> tiles = new HashMap<>();
    private static final List<MItem> items = new ArrayList<>();

    public static void registerLater(Block block, ModBase mod, @Nullable Function<Block, ItemBlock> itemBlockFactory) {
        blocks.add(new MBlock(block, mod, itemBlockFactory));
    }

    /**
     * @deprecated use {@link McJtyRegister#registerLater(Block, Class, boolean)} instead
     */
    @Deprecated
    public static void registerLater(Block block, @Nullable Class<? extends TileEntity> tileEntityClass) {
        registerLater(block, tileEntityClass, true);
    }

    /**
     * @param needsNamespaceFixer Should always be false for all new blocks. True for any block that has a tile entity and used to call the deprecated {@link McJtyRegister#registerLater(Block, Class)} method above.
     */
    public static void registerLater(Block block, @Nullable Class<? extends TileEntity> tileEntityClass, boolean needsNamespaceFixer) {
        tiles.put(block, new MTile(tileEntityClass, needsNamespaceFixer));
    }

    public static void registerLater(Item item, ModBase mod) {
        items.add(new MItem(item, mod));
    }

    public static void registerBlocks(ModBase mod, IForgeRegistry<Block> registry) {
        registerBlocks(mod, registry, null, 1);
    }

    /**
     * @param modFixs If your mod uses a ModFixs, pass it here. Otherwise, pass null and one will be created.
     * @param fixVersion If your mod uses a ModFixs, pass the fix version to use for the tile entity namespace fix. Otherwise, pass 1.
     */
    public static void registerBlocks(ModBase mod, IForgeRegistry<Block> registry, @Nullable ModFixs modFixs, int fixVersion) {
        Map<String, String> oldToNewIdMap = new HashMap<>();
        for (MBlock mBlock : blocks) {
            if (mBlock.getMod().getModId().equals(mod.getModId())) {
                Block block = mBlock.getBlock();
                registry.register(block);
                if (tiles.containsKey(block)) {
                    MTile tile = tiles.get(block);
                    if (tile.getTileEntityClass() == null) {
                        throw new RuntimeException("Bad tile entity registration for block: " + block.getRegistryName().toString());
                    }
                    String newId = block.getRegistryName().toString();
                    GameRegistry.registerTileEntity(tile.getTileEntityClass(), newId);
                    if (tile.isNeedsNamespaceFixer()) {
                        String oldPath = mBlock.getMod().getModId() + "_" + block.getRegistryName().getResourcePath();
                        oldToNewIdMap.put(oldPath, newId);
                        oldToNewIdMap.put("minecraft:" + oldPath, newId);
                    }
                }
            }
        }
        if(!oldToNewIdMap.isEmpty()) {
            // We used to accidentally register TEs with names like "minecraft:xnet_controller" instead of "xnet:controller".
            // Set up a DataFixer to map these incorrect names to the correct ones, so that we don't break old saved games.
            // @todo Remove all this if we ever break saved-game compatibility.
            if(modFixs == null) {
                modFixs = FMLCommonHandler.instance().getDataFixer().init(mod.getModId(), fixVersion);
            }
            modFixs.registerFix(FixTypes.BLOCK_ENTITY, new TileEntityNamespace(oldToNewIdMap, fixVersion));
        }
    }

    public static void registerItems(ModBase mod, IForgeRegistry<Item> registry) {
        for (MItem item : items) {
            if (item.getMod().getModId().equals(mod.getModId())) {
                registry.register(item.getItem());
            }
        }
        for (MBlock mBlock : blocks) {
            if (mBlock.getItemBlockFactory() != null) {
                if (mBlock.getMod().getModId().equals(mod.getModId())) {
                    ItemBlock itemBlock = mBlock.getItemBlockFactory().apply(mBlock.getBlock());
                    itemBlock.setRegistryName(mBlock.getBlock().getRegistryName());
                    registry.register(itemBlock);
                }
            }
        }
    }

    private static class MTile {
        private final Class<? extends TileEntity> tileEntityClass;
        private final boolean needsNamespaceFixer;

        public MTile(Class<? extends TileEntity> tileEntityClass, boolean needsNamespaceFixer) {
            this.tileEntityClass = tileEntityClass;
            this.needsNamespaceFixer = needsNamespaceFixer;
        }

        public Class<? extends TileEntity> getTileEntityClass() {
            return tileEntityClass;
        }

        public boolean isNeedsNamespaceFixer() {
            return needsNamespaceFixer;
        }
    }

    private static class MBlock {
        private final Block block;
        private final ModBase mod;
        private final Function<Block, ItemBlock> itemBlockFactory;

        public MBlock(Block block, ModBase mod, Function<Block, ItemBlock> itemBlockFactory) {
            this.block = block;
            this.mod = mod;
            this.itemBlockFactory = itemBlockFactory;
        }

        public Block getBlock() {
            return block;
        }

        public ModBase getMod() {
            return mod;
        }

        public Function<Block, ItemBlock> getItemBlockFactory() {
            return itemBlockFactory;
        }
    }

    private static class MItem {
        private final Item item;
        private final ModBase mod;

        public MItem(Item item, ModBase mod) {
            this.item = item;
            this.mod = mod;
        }

        public Item getItem() {
            return item;
        }

        public ModBase getMod() {
            return mod;
        }
    }
}
