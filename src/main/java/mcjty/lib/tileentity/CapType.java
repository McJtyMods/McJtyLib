package mcjty.lib.tileentity;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.module.CapabilityModuleSupport;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;

public enum CapType {
    ITEMS(ForgeCapabilities.ITEM_HANDLER),
    ITEMS_AUTOMATION(ForgeCapabilities.ITEM_HANDLER),
    CONTAINER(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY),
    ENERGY(ForgeCapabilities.ENERGY),
    INFUSABLE(CapabilityInfusable.INFUSABLE_CAPABILITY),
    MODULE(CapabilityModuleSupport.MODULE_CAPABILITY),
    POWER_INFO(CapabilityPowerInformation.POWER_INFORMATION_CAPABILITY),
    FLUIDS(ForgeCapabilities.FLUID_HANDLER);

    private final Capability capability;

    CapType(Capability capability) {
        this.capability = capability;
    }

    public Capability getCapability() {
        return capability;
    }
}
