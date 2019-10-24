package mcjty.lib.setup;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

import java.util.concurrent.Callable;

public interface IProxy {

    World getClientWorld();

    // Return current world on client and overworld on server
    World getWorld();

    RecipeManager getRecipeManager(World world);

    PlayerEntity getClientPlayer();

    RayTraceResult getClientMouseOver();

    void enqueueWork(Runnable runnable);

    void initStandardItemModel(Block block);

    void initStateMapper(Block block, ModelResourceLocation model);

    boolean isJumpKeyDown();

    boolean isForwardKeyDown();

    boolean isBackKeyDown();

    boolean isSneakKeyDown();

    // This version directly checks the actual key
    boolean isShiftKeyDown();

    // This version directly checks the actual key
    boolean isAltKeyDown();

    // This version directly checks the actual key
    boolean isCtrlKeyDown();

    IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters);
}
