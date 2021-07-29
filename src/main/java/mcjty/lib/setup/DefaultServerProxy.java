package mcjty.lib.setup;

import mcjty.lib.varia.WorldTools;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public class DefaultServerProxy implements IProxy {

    @Override
    public Level getClientWorld() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public Level getWorld() {
        return WorldTools.getOverworld();
    }

    @Override
    public Player getClientPlayer() {
        throw new IllegalStateException("This should only be called from client side");
    }

    @Override
    public RecipeManager getRecipeManager(Level world) {
        return world.getServer().getRecipeManager();
    }

    @Override
    public HitResult getClientMouseOver() {
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
