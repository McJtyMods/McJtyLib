package mcjty.lib.syncpositional;

import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;

/**
 * Package private, this key is not useful outside this package
 */
record PositionalDataKey(ResourceLocation id, GlobalPos pos) {
}
