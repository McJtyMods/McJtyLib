package mcjty.lib.tileentity;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.module.CapabilityModuleSupport;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import team.reborn.energy.api.EnergyStorage;

public enum CapType {
    ITEMS(ItemStorage.SIDED),
    ITEMS_AUTOMATION(ItemStorage.SIDED),
    CONTAINER(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY),
    ENERGY(EnergyStorage.SIDED),
    INFUSABLE(CapabilityInfusable.INFUSABLE_CAPABILITY),
    MODULE(CapabilityModuleSupport.MODULE_CAPABILITY),
    POWER_INFO(CapabilityPowerInformation.POWER_INFORMATION_CAPABILITY),
    FLUIDS(FluidStorage.SIDED);

    private final BlockApiLookup capability;

    CapType(BlockApiLookup capability) {
        this.capability = capability;
    }

    public BlockApiLookup getCapability() {
        return capability;
    }
}
