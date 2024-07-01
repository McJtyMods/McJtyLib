package mcjty.lib.api.module;

import mcjty.lib.McJtyLib;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class CapabilityModuleSupport {
    public static final BlockCapability<IModuleSupport, @Nullable Direction> MODULE_CAPABILITY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "module_support"), IModuleSupport.class);
}
