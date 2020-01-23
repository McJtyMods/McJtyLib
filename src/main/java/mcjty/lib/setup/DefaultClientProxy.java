package mcjty.lib.setup;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

public class DefaultClientProxy implements IProxy {

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public World getWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public RecipeManager getRecipeManager(World world) {
        return world.getRecipeManager();
    }

    @Override
    public RayTraceResult getClientMouseOver() {
        return Minecraft.getInstance().objectMouseOver;
    }

    @Override
    public void enqueueWork(Runnable runnable) {
        // @todo 1.14
//        Minecraft.getInstance().addScheduledTask(runnable);
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
    public boolean isShiftKeyDown() {
        return Screen.hasShiftDown();
    }

    @Override
    public boolean isAltKeyDown() {
        return Screen.hasAltDown();
    }

    @Override
    public boolean isCtrlKeyDown() {
        return Screen.hasControlDown();
    }

    @Override
    public IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters) {
//        return ModelLoaderRegistry.loadASM(location, parameters);
        // @todo 1.15
        return null;
    }

}
