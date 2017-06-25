package mcjty.lib;

import mcjty.lib.base.ModBase;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class McJtyRegister {

    private static final List<MBlock> blocks = new ArrayList<>();
    private static final List<MItem> items = new ArrayList<>();

    public static void registerLater(Block block, ModBase mod, @Nullable Class<? extends ItemBlock> itemBlockClass, @Nullable Class<? extends TileEntity> tileEntityClass) {
        blocks.add(new MBlock(block, mod, itemBlockClass, tileEntityClass));
    }

    public static void registerLater(Item item) {
        items.add(new MItem(item));
    }

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        for (MBlock mBlock : blocks) {
            registry.register(mBlock.getBlock());
            if (mBlock.getTileEntityClass() != null) {
                GameRegistry.registerTileEntity(mBlock.getTileEntityClass(), mBlock.getMod() + "_" + mBlock.getBlock().getRegistryName().getResourcePath());
            }
        }
    }

    public static void registerItems(IForgeRegistry<Item> registry) {
        for (MItem item : items) {
            registry.register(item.getItem());
        }
        for (MBlock mBlock : blocks) {
            if (mBlock.getItemBlockClass() != null) {
                ItemBlock itemBlock = createItemBlock(mBlock.getBlock(), mBlock.getItemBlockClass());
                itemBlock.setRegistryName(mBlock.getBlock().getRegistryName());
                registry.register(itemBlock);
            }
        }
    }


    private static ItemBlock createItemBlock(Block block, Class<? extends ItemBlock> itemBlockClass) {
        try {
            Class<?>[] ctorArgClasses = new Class<?>[1];
            ctorArgClasses[0] = Block.class;
            Constructor<? extends ItemBlock> itemCtor = itemBlockClass.getConstructor(ctorArgClasses);
            return itemCtor.newInstance(block);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    private static class MBlock {
        private final Block block;
        private final ModBase mod;
        private final Class<? extends ItemBlock> itemBlockClass;
        private final Class<? extends TileEntity> tileEntityClass;

        public MBlock(Block block, ModBase mod, Class<? extends ItemBlock> itemBlockClass, Class<? extends TileEntity> tileEntityClass) {
            this.block = block;
            this.mod = mod;
            this.itemBlockClass = itemBlockClass;
            this.tileEntityClass = tileEntityClass;
        }

        public Block getBlock() {
            return block;
        }

        public ModBase getMod() {
            return mod;
        }

        public Class<? extends ItemBlock> getItemBlockClass() {
            return itemBlockClass;
        }

        public Class<? extends TileEntity> getTileEntityClass() {
            return tileEntityClass;
        }
    }

    private static class MItem {
        private final Item item;

        public MItem(Item item) {
            this.item = item;
        }

        public Item getItem() {
            return item;
        }
    }
}
