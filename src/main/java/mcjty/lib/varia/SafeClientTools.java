package mcjty.lib.varia;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
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
        return keyDown(InputConstants.KEY_LSHIFT) || keyDown(InputConstants.KEY_RSHIFT);
    }

    public static boolean isCtrlKeyDown() {
        return keyDown(InputConstants.KEY_LCONTROL) || keyDown(InputConstants.KEY_RCONTROL);
    }

    public static boolean isJumpKeyDown() {
        return Minecraft.getInstance().options.keyJump.isDown();
    }

    private static boolean keyDown(int key) {
        return InputConstants.isKeyDown(key);
    }

    public static boolean isShiftKeyDown() { return isSneaking(); }

    public static boolean isControlKeyDown() { return isCtrlKeyDown(); }
}
