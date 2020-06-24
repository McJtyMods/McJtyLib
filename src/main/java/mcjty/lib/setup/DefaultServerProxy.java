package mcjty.lib.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
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
    public boolean isSneaking() {
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
}
