package mcjty.lib.api.container;

import mcjty.lib.McJtyLib;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import org.jspecify.annotations.Nullable;

public class CapabilityContainerProvider {
    public static final BlockApiLookup<MenuProvider, @Nullable Direction> CONTAINER_PROVIDER_CAPABILITY = BlockApiLookup.get(Identifier.fromNamespaceAndPath(McJtyLib.MODID, "container_provider"), MenuProvider.class, Direction.class);
}
