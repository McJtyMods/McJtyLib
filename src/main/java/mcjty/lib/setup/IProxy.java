package mcjty.lib.setup;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public interface IProxy {

    Level getClientWorld();

    // Return current world on client and overworld on server
    Level getWorld();

    RecipeManager getRecipeManager(Level world);

    Player getClientPlayer();

    HitResult getClientMouseOver();

    boolean isJumpKeyDown();

    boolean isForwardKeyDown();

    boolean isBackKeyDown();

    boolean isSneakKeyDown();

    // This version directly checks the actual key
    boolean isSneaking();

    // This version directly checks the actual key
    boolean isAltKeyDown();

    // This version directly checks the actual key
    boolean isCtrlKeyDown();
}
