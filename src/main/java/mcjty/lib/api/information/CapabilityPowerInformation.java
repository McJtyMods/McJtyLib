package mcjty.lib.api.information;

import mcjty.lib.McJtyLib;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import org.jspecify.annotations.Nullable;

public class CapabilityPowerInformation {
    public static final BlockApiLookup<IPowerInformation, @Nullable Direction> POWER_INFORMATION_CAPABILITY = BlockApiLookup.get(Identifier.fromNamespaceAndPath(McJtyLib.MODID, "power_information"), IPowerInformation.class, Direction.class);
}
