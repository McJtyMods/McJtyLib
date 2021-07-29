package mcjty.lib.varia;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class BlockTools {

    private static final Random random = new Random();

    public static String getModid(ItemStack stack) {
        if (!stack.isEmpty()) {
            return stack.getItem().getRegistryName().getNamespace();
        } else {
            return "";
        }
    }

    public static String getModidForBlock(Block block) {
        ResourceLocation nameForObject = block.getRegistryName();
        if (nameForObject == null) {
            return "?";
        }
        return nameForObject.getNamespace();
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
        if (blockStack.getItem() instanceof BlockItem) {
            BlockItem itemBlock = (BlockItem) blockStack.getItem();
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

    // Write a blockstate to a string
    public static String writeBlockState(BlockState tag) {
        StringBuilder builder = new StringBuilder(ForgeRegistries.BLOCKS.getKey(tag.getBlock()).toString());
        ImmutableMap<Property<?>, Comparable<?>> properties = tag.getValues();
        if (!properties.isEmpty()) {
            char c = '@';

            for(Map.Entry<Property<?>, Comparable<?>> entry : properties.entrySet()) {
                Property<?> property = entry.getKey();
                builder.append(c); c = ',';
                builder.append(property.getName());
                builder.append('=');
                builder.append(getName(property, entry.getValue()));
            }
        }

        return builder.toString();
    }

    private static <T extends Comparable<T>> String getName(Property<T> property, Comparable<?> cmp) {
        return property.getName((T)cmp);
    }

    public static BlockState readBlockState(String s) {
        String blockName;
        String properties;
        if (s.contains("@")) {
            String[] split = StringUtils.split(s, '@');
            blockName = split[0];
            properties = split[1];
        } else {
            blockName = s;
            properties = null;
        }
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if (block == null) {
            throw new RuntimeException("Cannot find block '" + blockName + "'!");
        }
        BlockState state = block.defaultBlockState();
        if (properties != null) {
            StateDefinition<Block, BlockState> statecontainer = state.getBlock().getStateDefinition();
            String[] split = StringUtils.split(properties, ',');
            for (String pv : split) {
                String[] sp = StringUtils.split(pv, '=');
                Property<?> property = statecontainer.getProperty(sp[0]);
                if (property != null) {
                    state = setValueHelper(state, property, sp[1]);
                }
            }
        }
        return state;
    }

    private static <T extends Comparable<T>> BlockState setValueHelper(BlockState state, Property<T> property, String value) {
        Optional<T> optional = property.getValue(value);
        if (optional.isPresent()) {
            return state.setValue(property, (T) optional.get());
        } else {
            return state;
        }
    }
}
