package mcjty.lib.setup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public abstract class DefaultModSetup {

//    private File modConfigDir;
    private Logger logger;
    protected CreativeModeTab creativeTab;

    public void init(FMLCommonSetupEvent e) {
        logger = LogManager.getLogger();

        setupModCompat();
    }

    protected abstract void setupModCompat();

    protected void createTab(String name, Supplier<ItemStack> stack) {
        creativeTab = new CreativeModeTab(name) {
            @Override
            public ItemStack makeIcon() {
                return stack.get();
            }
        };
    }

    public Logger getLogger() {
        return logger;
    }

    public CreativeModeTab getTab() {
        return creativeTab;
    }
}
