package mcjty.lib.setup;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.concurrent.Callable;

public interface IProxy {

    void init(FMLCommonSetupEvent e);

    World getClientWorld();

    PlayerEntity getClientPlayer();

    <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule);

    ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule);

    void enqueueWork(Runnable runnable);

    void initStandardItemModel(Block block);

    void initCustomItemModel(Item item, int meta, ModelResourceLocation model);

    void initStateMapper(Block block, ModelResourceLocation model);

    void initItemModelMesher(Item item, ModelResourceLocation model);

    void initTESRItemStack(Item item, int meta, Class<? extends TileEntity> clazz);

    boolean isJumpKeyDown();

    boolean isForwardKeyDown();

    boolean isBackKeyDown();

    boolean isSneakKeyDown();

    // This version goes directly to LWJGL
    boolean isShiftKeyDown();

    IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters);
}
