package mcjty.lib.crafting;

import net.minecraft.core.component.DataComponentType;

import java.util.Collection;

/**
 * Implement this interface on blocks or items that (when used in a CopyNBT recipe) will preserve
 * some of the components to the output
 */
public interface IComponentsToPreserve {

    Collection<DataComponentType<?>> getComponentsToPreserve();
}
