package mcjty.lib.api.information;

import mcjty.lib.McJtyLib;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class CapabilityPowerInformation {
    public static final BlockCapability<IPowerInformation, @Nullable Direction> POWER_INFORMATION_CAPABILITY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "power_information"), IPowerInformation.class);
}
