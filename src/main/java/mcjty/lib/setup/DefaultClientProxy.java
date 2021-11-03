package mcjty.lib.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class DefaultClientProxy implements IProxy {

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public World getWorld() {
        return Minecraft.getInstance().level;
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
        return Minecraft.getInstance().hitResult;
    }

    @Override
    public boolean isSneaking() {
        return Screen.hasShiftDown();
    }

    @Override
    public boolean isCtrlKeyDown() {
        return Screen.hasControlDown();
    }
}
