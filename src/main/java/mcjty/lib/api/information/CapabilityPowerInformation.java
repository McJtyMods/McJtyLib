package mcjty.lib.api.information;

import mcjty.lib.McJtyLib;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class CapabilityPowerInformation {
    public static final BlockApiLookup<IPowerInformation, Direction> POWER_INFORMATION_CAPABILITY = BlockApiLookup.get(ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "power_information"), IPowerInformation.class, Direction.class);
}
