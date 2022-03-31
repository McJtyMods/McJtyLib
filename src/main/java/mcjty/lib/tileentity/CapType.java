package mcjty.lib.tileentity;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.module.CapabilityModuleSupport;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public enum CapType {
    ITEMS(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY),
    ITEMS_AUTOMATION(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY),
    CONTAINER(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY),
    ENERGY(CapabilityEnergy.ENERGY),
    INFUSABLE(CapabilityInfusable.INFUSABLE_CAPABILITY),
    MODULE(CapabilityModuleSupport.MODULE_CAPABILITY),
    POWER_INFO(CapabilityPowerInformation.POWER_INFORMATION_CAPABILITY),
    FLUIDS(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);

    private final Capability capability;

    CapType(Capability capability) {
        this.capability = capability;
    }

    public Capability getCapability() {
        return capability;
    }
}
