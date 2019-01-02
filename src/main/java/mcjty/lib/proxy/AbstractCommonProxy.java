package mcjty.lib.proxy;

import com.google.common.util.concurrent.ListenableFuture;
import mcjty.lib.McJtyLib;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.varia.WrenchChecker;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.concurrent.Callable;

public abstract class AbstractCommonProxy implements IProxy {

    public File modConfigDir;
    protected Configuration mainConfig = null;

    public void preInit(FMLPreInitializationEvent e) {
        McJtyLib.preInit(e);
        GeneralConfig.preInit(e);
        modConfigDir = e.getModConfigurationDirectory();
    }

    public void init(FMLInitializationEvent e) {
    }

    public void postInit(FMLPostInitializationEvent e) {
        WrenchChecker.init();
    }

    public World getClientWorld() {
        throw new IllegalStateException("This should only be called from client side");
    }

    public EntityPlayer getClientPlayer() {
        throw new IllegalStateException("This should only be called from client side");
    }

    public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
        throw new IllegalStateException("This should only be called from client side");
    }

    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public void initStandardItemModel(Block block) {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public void initStateMapper(Block block, ModelResourceLocation model) {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public void initItemModelMesher(Item item, int meta, ModelResourceLocation model) {
        throw new IllegalStateException("This should only be called from client side");
    }
}
