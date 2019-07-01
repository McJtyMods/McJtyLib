package mcjty.lib.setup;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

import java.util.concurrent.Callable;

public class DefaultServerProxy implements IProxy {

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public NetworkManager getNetworkManager(PlayerEntity player) {
        return ((ServerPlayerEntity) player).connection.netManager;
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
        // @todo 1.14
//        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
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
    public void initItemModelMesher(Item item, ModelResourceLocation model) {
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
    public boolean isShiftKeyDown() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters) {
        throw new IllegalStateException("This should only be called from client side");
    }
}
