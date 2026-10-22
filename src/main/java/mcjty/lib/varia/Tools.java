package mcjty.lib.varia;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import mcjty.lib.varia.FluidStack;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import mcjty.lib.setup.DeferredRegister;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Tools {

    public static RegistryAccess getRegistryAccess(Level level) {
        return level.registryAccess();
    }

    public static DeferredRegister<PlacementModifierType<?>> createPlacementRegistry(String modid) {
        return DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, modid);
    }

    public interface IDPRegister {
        <T> void register(ResourceKey<Registry<T>> key, Codec<T> codec);
    }

    public static void onDataPackRegistry(Consumer<IDPRegister> consumer) {
        consumer.accept(new IDPRegister() { public <T> void register(ResourceKey<Registry<T>> key, Codec<T> codec) { DynamicRegistries.registerSynced(key,codec); } });
    }

    public static Block getBlock(Identifier id) {
        return BuiltInRegistries.BLOCK.getValue(id);
    }

    public static Item getItem(Identifier id) {
        return BuiltInRegistries.ITEM.getValue(id);
    }

    public static EntityType<?> getEntity(Identifier id) {
        return BuiltInRegistries.ENTITY_TYPE.getValue(id);
    }

    public static Fluid getFluid(Identifier id) {
        return BuiltInRegistries.FLUID.getValue(id);
    }

    public static MobEffect getEffect(Identifier id) {
        return BuiltInRegistries.MOB_EFFECT.getValue(id);
    }

    public static SoundEvent getSound(Identifier resourceLocation) {
        return BuiltInRegistries.SOUND_EVENT.getValue(resourceLocation);
    }

    public static Identifier getId(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }

    public static Identifier getId(ItemStack item) {
        return BuiltInRegistries.ITEM.getKey(item.getItem());
    }

    public static Identifier getId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static Identifier getId(BlockState block) {
        return BuiltInRegistries.BLOCK.getKey(block.getBlock());
    }

    public static Identifier getId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static Identifier getId(FluidStack fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid.getFluid());
    }

    public static Identifier getId(FluidState fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid.getType());
    }

    public static Identifier getId(Fluid fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid);
    }

    public static Identifier getId(CommonLevelAccessor level, Biome biome) {
        return level.registryAccess().lookupOrThrow(Registries.BIOME).getKey(biome);
    }

    public static Identifier getId(CommonLevelAccessor level, Structure feature) {
        return level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getKey(feature);
    }

    public static String getModid(ItemStack stack) {
        if (!stack.isEmpty()) {
            return Tools.getId(stack).getNamespace();
        } else {
            return "";
        }
    }

    public static String getModName(Fluid entry) {
        Identifier registryName = BuiltInRegistries.FLUID.getKey(entry);
        String modId = registryName == null ? "minecraft" : registryName.getNamespace();
        return net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(modId)
                .map(mod -> mod.getMetadata().getName())
                .orElse(StringUtils.capitalize(modId));
    }

    public static String getModName(Block entry) {
        Identifier registryName = BuiltInRegistries.BLOCK.getKey(entry);
        String modId = registryName == null ? "minecraft" : registryName.getNamespace();
        return net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(modId)
                .map(mod -> mod.getMetadata().getName())
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
        return getReadableName(state.getBlock().getCloneItemStack(world, pos, state, false));
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
