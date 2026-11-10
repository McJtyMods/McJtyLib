package mcjty.lib.api.infusable;

import mcjty.lib.McJtyLib;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class CapabilityInfusable {
    public static final BlockApiLookup<IInfusable, Direction> INFUSABLE_CAPABILITY = BlockApiLookup.get(ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "infusable"), IInfusable.class, Direction.class);
}
