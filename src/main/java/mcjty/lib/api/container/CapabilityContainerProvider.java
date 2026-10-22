package mcjty.lib.api.container;

import mcjty.lib.McJtyLib;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jspecify.annotations.Nullable;

public class CapabilityContainerProvider {
    public static final BlockCapability<MenuProvider, @Nullable Direction> CONTAINER_PROVIDER_CAPABILITY = BlockCapability.createSided(Identifier.fromNamespaceAndPath(McJtyLib.MODID, "container_provider"), MenuProvider.class);
}
