package mcjty.lib.setup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public abstract class DefaultModSetup {

//    private File modConfigDir;
    private Logger logger;
    protected ItemGroup creativeTab;

    public void init(FMLCommonSetupEvent e) {
        logger = LogManager.getLogger();
//        modConfigDir = e.getModConfigurationDirectory();

        setupConfig();
        setupModCompat();
    }

    protected abstract void setupModCompat();

    protected abstract void setupConfig();

    protected void createTab(String name, Supplier<ItemStack> stack) {
        creativeTab = new ItemGroup(name) {
            @Override
            public ItemStack createIcon() {
                return stack.get();
            }
        };
    }

    public Logger getLogger() {
        return logger;
    }

    public ItemGroup getTab() {
        return creativeTab;
    }
}
