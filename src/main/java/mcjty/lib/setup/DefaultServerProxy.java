package mcjty.lib.setup;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import mcjty.lib.proxy.IProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.concurrent.Callable;

public abstract class DefaultServerProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
    }

    @Override
    public void init(FMLInitializationEvent e) {
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public void enqueueWork(Runnable runnable) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
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

    @Override
    public void initTESRItemStack(Item item, int meta, Class<? extends TileEntity> clazz) {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public void initCustomItemModel(Item item, int meta, ModelResourceLocation model) {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public boolean isJumpKeyDown() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public boolean isForwardKeyDown() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public boolean isBackKeyDown() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public boolean isSneakKeyDown() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters) {
        throw new IllegalStateException("This should only be called from client side");
    }
}
