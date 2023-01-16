package mcjty.lib.setup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class DefaultModSetup {

    private Logger logger;
    protected CreativeModeTab creativeTab;

    public void init(FMLCommonSetupEvent e) {
        logger = LogManager.getLogger();

        setupModCompat();
    }

    protected abstract void setupModCompat();

    protected void createTab(String modid, String name, Supplier<ItemStack> stack) {
        creativeTab = new CreativeModeTab(name) {
            @Override
            @Nonnull
            public ItemStack makeIcon() {
                return stack.get();
            }
        };
    }

    public Logger getLogger() {
        return logger;
    }

    public Item.Properties defaultProperties() {
        return new Item.Properties().tab(getTab());
    }

    public CreativeModeTab getTab() {
        return creativeTab;
    }

    public <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return supplier;
    }
}
