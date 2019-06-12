package mcjty.lib.setup;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.concurrent.Callable;

public class DefaultClientProxy implements IProxy {

    @Override
    public void init(FMLCommonSetupEvent e) {
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public void enqueueWork(Runnable runnable) {
        // @todo 1.14
//        Minecraft.getInstance().addScheduledTask(runnable);
    }

    @Override
    public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
        // @todo 1.14
//        return Minecraft.getInstance().addScheduledTask(callableToSchedule);
        return null;
    }

    @Override
    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
        // @todo 1.14
//        return Minecraft.getInstance().addScheduledTask(runnableToSchedule);
        return null;
    }

    @Override
    public void initStandardItemModel(Block block) {
        // @todo 1.14
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }

    @Override
    public void initStateMapper(Block block, ModelResourceLocation model) {
        // @todo 1.14
//        StateMapperBase ignoreState = new StateMapperBase() {
//            @Override
//            protected ModelResourceLocation getModelResourceLocation(BlockState BlockState) {
//                return model;
//            }
//        };
//        ModelLoader.setCustomStateMapper(block, ignoreState);
    }

    @Override
    public void initItemModelMesher(Item item, ModelResourceLocation model) {
        Minecraft.getInstance().getItemRenderer().getItemModelMesher().register(item, model);
    }

    @Override
    public void initTESRItemStack(Item item, int meta, Class<? extends TileEntity> clazz) {
        // @todo 1.14
//        ForgeHooksClient.registerTESRItemStack(item, meta, clazz);
    }

    @Override
    public void initCustomItemModel(Item item, int meta, ModelResourceLocation model) {
        // @todo 1.14
//        ModelLoader.setCustomModelResourceLocation(item, meta, model);
    }

    @Override
    public boolean isJumpKeyDown() {
        return Minecraft.getInstance().gameSettings.keyBindJump.isKeyDown();
    }

    @Override
    public boolean isForwardKeyDown() {
        return Minecraft.getInstance().gameSettings.keyBindForward.isKeyDown();
    }

    @Override
    public boolean isBackKeyDown() {
        return Minecraft.getInstance().gameSettings.keyBindBack.isKeyDown();
    }

    @Override
    public boolean isSneakKeyDown() {
        return Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown();
    }

    @Override
    public IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters) {
        return ModelLoaderRegistry.loadASM(location, parameters);
    }

}
