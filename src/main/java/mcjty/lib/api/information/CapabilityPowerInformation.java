package mcjty.lib.api.information;

import mcjty.lib.McJtyLib;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jspecify.annotations.Nullable;

public class CapabilityPowerInformation {
    public static final BlockCapability<IPowerInformation, @Nullable Direction> POWER_INFORMATION_CAPABILITY = BlockCapability.createSided(Identifier.fromNamespaceAndPath(McJtyLib.MODID, "power_information"), IPowerInformation.class);
}
