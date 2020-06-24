package mcjty.lib.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public interface IProxy {

    World getClientWorld();

    // Return current world on client and overworld on server
    World getWorld();

    RecipeManager getRecipeManager(World world);

    PlayerEntity getClientPlayer();

    RayTraceResult getClientMouseOver();

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
