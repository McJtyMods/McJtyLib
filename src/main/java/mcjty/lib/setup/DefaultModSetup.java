package mcjty.lib.setup;

import mcjty.lib.api.ITabExpander;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
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
     * Call this in the ModSetup constructor
     */
    protected void createTab(String modid, String name, Supplier<ItemStack> stack) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            // @todo 1.20
//            event.registerCreativeModeTab(new ResourceLocation(modid, name), builder -> {
//                builder.title(Component.translatable("itemGroup." + name))
//                        .icon(stack::get)
//                        .displayItems((enabledFeatures, output) -> {
//                            tabItems.forEach(s -> {
//                                boolean todo = true;
//                                ItemStack st = s.get();
//                                if (st.getItem() instanceof ITabExpander expander) {
//                                    List<ItemStack> itemsForTab = expander.getItemsForTab();
//                                    if (!itemsForTab.isEmpty()) {
//                                        todo = false;
//                                        itemsForTab.forEach(output::accept);
//                                    }
//                                }
//                                if (todo) {
//                                    output.accept(st);
//                                }
//                            });
//                        });
//            });
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

    public <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        Lazy<T> lazyItem = Lazy.of(supplier);
        tabItems.add(() -> new ItemStack(lazyItem.get()));
        return lazyItem;
    }
}
