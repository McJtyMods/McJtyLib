package mcjty.lib.api.container;

import mcjty.lib.McJtyLib;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;

public class CapabilityContainerProvider {
    public static final BlockApiLookup<MenuProvider, Direction> CONTAINER_PROVIDER_CAPABILITY = BlockApiLookup.get(ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "container_provider"), MenuProvider.class, Direction.class);
}
