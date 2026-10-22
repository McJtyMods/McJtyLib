package mcjty.lib.varia;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class SafeClientTools {

    public static Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    public static Level getWorld() {
        return Minecraft.getInstance().level;
    }

    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static HitResult getClientMouseOver() {
        return Minecraft.getInstance().hitResult;
    }

    public static boolean isSneaking() {
        return keyDown(340) || keyDown(344);
    }

    public static boolean isCtrlKeyDown() {
        return keyDown(341) || keyDown(345);
    }

    public static boolean isJumpKeyDown() {
        return Minecraft.getInstance().options.keyJump.isDown();
    }
    private static boolean keyDown(int key) {
        return com.mojang.blaze3d.platform.InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key);
    }
    public static boolean isShiftKeyDown() { return isSneaking(); }
    public static boolean isControlKeyDown() { return isCtrlKeyDown(); }
}
