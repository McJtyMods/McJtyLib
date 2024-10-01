package mcjty.lib.setup;

import mcjty.lib.api.ITabExpander;
import mcjty.lib.blocks.RBlockRegistry;
import mcjty.lib.tileentity.AnnotationHolder;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.Lazy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class DefaultModSetup {

    private Logger logger;
    protected CreativeModeTab creativeTab;
    private List<Supplier<ItemStack>> tabItems = new ArrayList<>();   // Here we collect all tab items

    public void init(FMLCommonSetupEvent e) {
        logger = LogManager.getLogger();

        setupModCompat();
    }

    protected abstract void setupModCompat();

    /**
     * Call this from within the creative tab registry object
     */
    public void populateTab(CreativeModeTab.Output output) {
        tabItems.forEach(s -> {
            boolean todo = true;
            ItemStack st = s.get();
            if (st.getItem() instanceof ITabExpander expander) {
                List<ItemStack> itemsForTab = expander.getItemsForTab();
                if (!itemsForTab.isEmpty()) {
                    todo = false;
                    itemsForTab.forEach(output::accept);
                }
            }
            if (todo) {
                output.accept(st);
            }
        });
    }

    public Logger getLogger() {
        return logger;
    }

    public Item.Properties defaultProperties() {
        return new Item.Properties();
    }

    public CreativeModeTab getTab() {
        return creativeTab;
    }

    public void addTabItem(Supplier<ItemStack> item) {
        tabItems.add(item);
    }

    public <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        Lazy<T> lazyItem = Lazy.of(supplier);
        tabItems.add(() -> new ItemStack(lazyItem.get()));
        return lazyItem;
    }

    public Consumer<RegisterCapabilitiesEvent> getBlockCapabilityRegistrar(RBlockRegistry registry) {
        return event -> {
            for (Map.Entry<Class<? extends GenericTileEntity>, AnnotationHolder> entry : registry.getHolders().entrySet()) {
                AnnotationHolder holder = entry.getValue();
                for (int i = 0; i < holder.getCapSize(); i++) {
                    var hd = holder.getCapHolder(i);
                    var bc = hd.capability();
                    var function = hd.function();
                    event.registerBlock(bc, new IBlockCapabilityProvider<>() {
                        @Nullable
                        @Override
                        public Object getCapability(Level level, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, Object o) {
                            if (blockEntity instanceof GenericTileEntity be) {
                                return function.apply(be);
                            }
                            return null;
                        }
                    }, hd.block().get());
                }
            }
        };
    }
}
