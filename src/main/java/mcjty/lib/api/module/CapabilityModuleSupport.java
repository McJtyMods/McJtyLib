package mcjty.lib.api.module;

import mcjty.lib.McJtyLib;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class CapabilityModuleSupport {
    public static final BlockApiLookup<IModuleSupport, Direction> MODULE_CAPABILITY = BlockApiLookup.get(ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "module_support"), IModuleSupport.class, Direction.class);
}
