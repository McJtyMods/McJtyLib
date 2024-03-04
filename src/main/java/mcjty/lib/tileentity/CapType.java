package mcjty.lib.tileentity;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.module.CapabilityModuleSupport;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;

public enum CapType {
    ITEMS(Capabilities.ItemHandler.BLOCK),
    ITEMS_AUTOMATION(Capabilities.ItemHandler.BLOCK),
    CONTAINER(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY),
    ENERGY(Capabilities.EnergyStorage.BLOCK),
    INFUSABLE(CapabilityInfusable.INFUSABLE_CAPABILITY),
    MODULE(CapabilityModuleSupport.MODULE_CAPABILITY),
    POWER_INFO(CapabilityPowerInformation.POWER_INFORMATION_CAPABILITY),
    FLUIDS(Capabilities.FluidHandler.BLOCK);

    private final BlockCapability capability;

    CapType(BlockCapability capability) {
        this.capability = capability;
    }

    public BlockCapability getCapability() {
        return capability;
    }
}
