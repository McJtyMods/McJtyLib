package mcjty.lib;

import mcjty.lib.base.ModBase;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
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

    public static void registerLater(Block block, @Nullable Class<? extends TileEntity> tileEntityClass) {
        tiles.put(block, new MTile(tileEntityClass));
    }

    public static void registerLater(Item item, ModBase mod) {
        items.add(new MItem(item, mod));
    }

    public static void registerBlocks(ModBase mod, IForgeRegistry<Block> registry) {
        for (MBlock mBlock : blocks) {
            if (mBlock.getMod().getModId().equals(mod.getModId())) {
                registry.register(mBlock.getBlock());
                if (tiles.containsKey(mBlock.getBlock())) {
                    MTile tile = tiles.get(mBlock.getBlock());
                    if (tile.getTileEntityClass() == null) {
                        throw new RuntimeException("Bad tile entity registration for block: " + mBlock.getBlock().getRegistryName().toString());
                    }
                    GameRegistry.registerTileEntity(tile.getTileEntityClass(), mBlock.getMod().getModId() + "_" + mBlock.getBlock().getRegistryName().getResourcePath());
                }
            }
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

        public MTile(Class<? extends TileEntity> tileEntityClass) {
            this.tileEntityClass = tileEntityClass;
        }

        public Class<? extends TileEntity> getTileEntityClass() {
            return tileEntityClass;
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
