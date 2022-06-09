package mcjty.lib.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class Tools {

    public static ResourceLocation getId(EntityType<?> entityType) {
        return entityType.getRegistryName();
    }

    public static ResourceLocation getId(ItemStack item) {
        return item.getItem().getRegistryName();
    }

    public static ResourceLocation getId(Item item) {
        return item.getRegistryName();
    }

    public static ResourceLocation getId(BlockState block) {
        return block.getBlock().getRegistryName();
    }

    public static ResourceLocation getId(Block block) {
        return block.getRegistryName();
    }

    public static ResourceLocation getId(FluidStack fluid) {
        return fluid.getFluid().getRegistryName();
    }

    public static ResourceLocation getId(FluidState fluid) {
        return fluid.getType().getRegistryName();
    }

    public static ResourceLocation getId(Fluid fluid) {
        return fluid.getRegistryName();
    }

    public static ResourceLocation getId(Biome biome) {
        return biome.getRegistryName();
    }

    public static ResourceLocation getId(StructureFeature<?> feature) {
        return feature.getRegistryName();
    }

    public static String getModid(ItemStack stack) {
        if (!stack.isEmpty()) {
            return Tools.getId(stack).getNamespace();
        } else {
            return "";
        }
    }

    public static String getModName(Fluid entry) {
        ResourceLocation registryName = entry.getRegistryName();
        String modId = registryName == null ? "minecraft" : registryName.getNamespace();
        return ModList.get().getModContainerById(modId)
                .map(mod -> mod.getModInfo().getDisplayName())
                .orElse(StringUtils.capitalize(modId));
    }

    public static String getModName(Block entry) {
        ResourceLocation registryName = entry.getRegistryName();
        String modId = registryName == null ? "minecraft" : registryName.getNamespace();
        return ModList.get().getModContainerById(modId)
                .map(mod -> mod.getModInfo().getDisplayName())
                .orElse(StringUtils.capitalize(modId));
    }

    public static <INPUT extends BASE, BASE> void safeConsume(BASE o, Consumer<INPUT> consumer, String error) {
        try {
            consumer.accept((INPUT) o);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(error, e);
        }
    }

    public static <INPUT extends BASE, BASE> void safeConsume(BASE o, Consumer<INPUT> consumer) {
        try {
            consumer.accept((INPUT) o);
        } catch (ClassCastException ignore) {
        }
    }

    public static <INPUT extends BASE, BASE, RET> RET safeMap(BASE o, Function<INPUT, RET> consumer, String error) {
        try {
            return consumer.apply((INPUT) o);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(error, e);
        }
    }

    public static String getReadableName(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return getReadableName(state.getBlock().getCloneItemStack(world, pos, state));
    }

    public static String getReadableName(ItemStack stack) {
        return stack.getHoverName().getString() /* was getFormattedText() */;
    }

    @Nullable
    public static BlockState placeStackAt(Player player, ItemStack blockStack, Level world, BlockPos pos, @Nullable BlockState origState) {
        BlockHitResult trace = new BlockHitResult(new Vec3(0, 0, 0), Direction.UP, pos, false);
        BlockPlaceContext context = new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, trace));
        if (blockStack.getItem() instanceof BlockItem itemBlock) {
            if (origState == null) {
                origState = itemBlock.getBlock().getStateForPlacement(context);
                if (origState == null) {
                    // Cannot place!
                    return null;
                }
            }
            if (itemBlock.place(context).consumesAction()) {
                blockStack.shrink(1);
            }
            return origState;
        } else {
            player.setItemInHand(InteractionHand.MAIN_HAND, blockStack);
            player.setPos(pos.getX()+.5, pos.getY()+1.5, pos.getZ()+.5);
            blockStack.getItem().useOn(context);
            return world.getBlockState(pos);
        }
    }
}
