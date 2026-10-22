package mcjty.lib.api.module;

import mcjty.lib.McJtyLib;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import org.jspecify.annotations.Nullable;

public class CapabilityModuleSupport {
    public static final BlockApiLookup<IModuleSupport, @Nullable Direction> MODULE_CAPABILITY = BlockApiLookup.get(Identifier.fromNamespaceAndPath(McJtyLib.MODID, "module_support"), IModuleSupport.class, Direction.class);
}
