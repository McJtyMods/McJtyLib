package mcjty.lib.api.container;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityContainerProvider {

    @CapabilityInject(INamedContainerProvider.class)
    public static Capability<INamedContainerProvider> CONTAINER_PROVIDER_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(INamedContainerProvider.class, new Capability.IStorage<INamedContainerProvider>() {
            @Override
            public INBT writeNBT(Capability<INamedContainerProvider> capability, INamedContainerProvider instance, Direction side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability<INamedContainerProvider> capability, INamedContainerProvider instance, Direction side, INBT base) {
                throw new UnsupportedOperationException();
            }
        }, () -> { throw new UnsupportedOperationException(); });
    }

}
