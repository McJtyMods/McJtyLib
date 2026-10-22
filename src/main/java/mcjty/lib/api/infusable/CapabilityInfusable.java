package mcjty.lib.api.infusable;

import mcjty.lib.McJtyLib;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import org.jspecify.annotations.Nullable;

public class CapabilityInfusable {
    public static final BlockApiLookup<IInfusable, @Nullable Direction> INFUSABLE_CAPABILITY = BlockApiLookup.get(Identifier.fromNamespaceAndPath(McJtyLib.MODID, "infusable"), IInfusable.class, Direction.class);
}
