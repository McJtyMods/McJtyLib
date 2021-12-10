package mcjty.lib.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public class DefaultClientProxy implements IProxy {

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public Level getWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public RecipeManager getRecipeManager(Level world) {
        return world.getRecipeManager();
    }

    @Override
    public HitResult getClientMouseOver() {
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
