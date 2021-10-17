package mcjty.lib.varia;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class BlockTools {

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
            StateContainer<Block, BlockState> statecontainer = state.getBlock().getStateDefinition();
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
