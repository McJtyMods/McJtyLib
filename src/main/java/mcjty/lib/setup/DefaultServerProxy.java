package mcjty.lib.setup;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class DefaultServerProxy implements IProxy {

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public World getWorld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return DimensionManager.getWorld(server, DimensionType.OVERWORLD, false, false);
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public RecipeManager getRecipeManager(World world) {
        return world.getServer().getRecipeManager();
    }

    @Override
    public RayTraceResult getClientMouseOver() {
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
    public boolean isAltKeyDown() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public boolean isCtrlKeyDown() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters) {
        throw new IllegalStateException("This should only be called from client side");
    }
}
