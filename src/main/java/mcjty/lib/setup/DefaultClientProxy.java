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
}
