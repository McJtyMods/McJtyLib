package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.varia.WrenchChecker;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

public abstract class DefaultCommonSetup {

    protected File modConfigDir;
//    protected Configuration mainConfig = null;
    private Logger logger;
    protected CreativeTabs creativeTab;

    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();
        createTabs();
        McJtyLib.preInit(e);
        GeneralConfig.init(e);
        modConfigDir = e.getModConfigurationDirectory();
    }

    public abstract void createTabs();

    protected void createTab(String name, ItemStack stack) {
        creativeTab = new CreativeTabs(name) {
            @Override
            public ItemStack getTabIconItem() {
                return stack;
            }
        };
    }

    public void init(FMLInitializationEvent e) {

    }

    public void postInit(FMLPostInitializationEvent e) {
        WrenchChecker.init();
    }

    public File getModConfigDir() {
        return modConfigDir;
    }

//    public Configuration getConfig() {
//        return mainConfig;
//    }
//
    public Logger getLogger() {
        return logger;
    }

    public CreativeTabs getTab() {
        return creativeTab;
    }
}
