package mcjty.lib.api.infusable;

import mcjty.lib.McJtyLib;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jspecify.annotations.Nullable;

public class CapabilityInfusable {
    public static final BlockCapability<IInfusable, @Nullable Direction> INFUSABLE_CAPABILITY = BlockCapability.createSided(Identifier.fromNamespaceAndPath(McJtyLib.MODID, "infusable"), IInfusable.class);
}
