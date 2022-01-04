package mcjty.lib.varia;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SafeClientTools {

    public static World getClientWorld() {
        return Minecraft.getInstance().level;
    }

    public static World getWorld() {
        return Minecraft.getInstance().level;
    }

    public static PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static RecipeManager getRecipeManager(World world) {
        return world.getRecipeManager();
    }

    public static RayTraceResult getClientMouseOver() {
        return Minecraft.getInstance().hitResult;
    }

    public static boolean isSneaking() {
        return Screen.hasShiftDown();
    }

    public static boolean isCtrlKeyDown() {
        return Screen.hasControlDown();
    }

    public static boolean isJumpKeyDown() {
        return Minecraft.getInstance().options.keyJump.isDown();
    }
}
