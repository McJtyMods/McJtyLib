package mcjty.lib.api.modules;

import net.minecraft.core.GlobalPos;

/**
 * This can be used as a data component for items that are modules
 */
public record ItemModule(GlobalPos pos, String name) {
}
