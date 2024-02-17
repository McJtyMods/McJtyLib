package mcjty.lib.varia;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
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

    public static <T> Supplier<IForgeRegistry<T>> makeCustomRegistry(DeferredRegister<T> defferedRegistry, Codec<T> codec) {
//        return defferedRegistry.makeRegistry(() -> new RegistryBuilder<T>().dataPackRegistry(codec));
        // Not on 1.19.3
        return null;
    }

    public interface IDPRegister {
        <T> void register(ResourceKey<Registry<T>> key, Codec<T> codec);
    }

    public static void onDataPackRegistry(IEventBus bus, Consumer<IDPRegister> consumer) {
        bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
            consumer.accept(new IDPRegister() {
                @Override
                public <T> void register(ResourceKey<Registry<T>> key, Codec<T> codec) {
                    event.dataPackRegistry(key, codec, codec);
                }
            });
        });
    }

    public static Block getBlock(ResourceLocation id) {
        return ForgeRegistries.BLOCKS.getValue(id);
    }

    public static Item getItem(ResourceLocation id) {
        return ForgeRegistries.ITEMS.getValue(id);
    }

    public static EntityType<?> getEntity(ResourceLocation id) {
        return ForgeRegistries.ENTITY_TYPES.getValue(id);
    }

    public static Fluid getFluid(ResourceLocation id) {
        return ForgeRegistries.FLUIDS.getValue(id);
    }

    public static ResourceLocation getId(EntityType<?> entityType) {
        return ForgeRegistries.ENTITY_TYPES.getKey(entityType);
    }

    public static ResourceLocation getId(ItemStack item) {
        return ForgeRegistries.ITEMS.getKey(item.getItem());
    }

    public static ResourceLocation getId(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static ResourceLocation getId(BlockState block) {
        return ForgeRegistries.BLOCKS.getKey(block.getBlock());
    }

    public static ResourceLocation getId(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    public static ResourceLocation getId(FluidStack fluid) {
        return ForgeRegistries.FLUIDS.getKey(fluid.getFluid());
    }

    public static ResourceLocation getId(FluidState fluid) {
        return ForgeRegistries.FLUIDS.getKey(fluid.getType());
    }

    public static ResourceLocation getId(Fluid fluid) {
        return ForgeRegistries.FLUIDS.getKey(fluid);
    }

    public static ResourceLocation getId(CommonLevelAccessor level, Biome biome) {
        return level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);
    }

    public static ResourceLocation getId(CommonLevelAccessor level, Structure feature) {
        return level.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(feature);
    }

    public static String getModid(ItemStack stack) {
        if (!stack.isEmpty()) {
            return Tools.getId(stack).getNamespace();
        } else {
            return "";
        }
    }

    public static String getModName(Fluid entry) {
        ResourceLocation registryName = ForgeRegistries.FLUIDS.getKey(entry);
        String modId = registryName == null ? "minecraft" : registryName.getNamespace();
        return ModList.get().getModContainerById(modId)
                .map(mod -> mod.getModInfo().getDisplayName())
                .orElse(StringUtils.capitalize(modId));
    }

    public static String getModName(Block entry) {
        ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(entry);
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
