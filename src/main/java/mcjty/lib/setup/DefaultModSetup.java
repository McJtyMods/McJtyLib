package mcjty.lib.setup;

import mcjty.lib.blocks.BaseBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;

public abstract class DefaultModSetup {

    private Logger logger;
    protected CreativeModeTab creativeTab;
    private List<Supplier<ItemStack>> tabItems;   // Here we collect all tab items

    public void init(FMLCommonSetupEvent e) {
        logger = LogManager.getLogger();

        setupModCompat();
    }

    protected abstract void setupModCompat();

    /**
     * Call this in the ModSetup constructor
     */
    protected void createTab(String modid, String name, Supplier<ItemStack> stack) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener((CreativeModeTabEvent.Register event) -> {
            event.registerCreativeModeTab(new ResourceLocation(modid, name), builder -> {
                builder.title(Component.translatable("itemGroup." + name))
                        .icon(stack::get)
                        .displayItems((enabledFeatures, output, tab) -> {
                            tabItems.forEach(s -> {
                                boolean todo = true;
                                ItemStack st = s.get();
                                if (st.getItem() instanceof BlockItem blockItem) {
                                    if (blockItem.getBlock() instanceof BaseBlock baseBlock) {
                                        List<ItemStack> itemsForTab = baseBlock.getItemsForTab();
                                        if (!itemsForTab.isEmpty()) {
                                            todo = false;
                                            itemsForTab.forEach(output::accept);
                                        }
                                    }
                                }
                                if (todo) {
                                    output.accept(st);
                                }
                            });
                        });
            });
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

    public <T extends Item> void tab(Supplier<T> supplier) {
        tabItems.add(() -> new ItemStack(supplier.get()));
    }
}
