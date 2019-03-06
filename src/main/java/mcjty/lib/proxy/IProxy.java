package mcjty.lib.proxy;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.concurrent.Callable;

public interface IProxy {

    void preInit(FMLPreInitializationEvent e);

    void init(FMLInitializationEvent e);

    void postInit(FMLPostInitializationEvent e);

    World getClientWorld();

    EntityPlayer getClientPlayer();

    <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule);

    ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule);

    void initStandardItemModel(Block block);

    void initCustomItemModel(Item item, int meta, ModelResourceLocation model);

    void initStateMapper(Block block, ModelResourceLocation model);

    void initItemModelMesher(Item item, int meta, ModelResourceLocation model);

    void initTESRItemStack(Item item, int meta, Class<? extends TileEntity> clazz);

    boolean isJumpKeyDown();

    IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters);
}
