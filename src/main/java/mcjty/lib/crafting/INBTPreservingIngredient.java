package mcjty.lib.crafting;

import java.util.Collection;

/**
 * Implement this interface on blocks or items that (when used in a CopyNBT recipe) will preserve
 * the NBT to the output
 */
public interface INBTPreservingIngredient {

    Collection<String> getTagsToPreserve();
}
