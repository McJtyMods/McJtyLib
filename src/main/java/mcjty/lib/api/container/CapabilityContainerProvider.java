package mcjty.lib.api.container;

import net.minecraft.world.MenuProvider;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityContainerProvider {

    @CapabilityInject(MenuProvider.class)
    public static Capability<MenuProvider> CONTAINER_PROVIDER_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(MenuProvider.class, new Capability.IStorage<MenuProvider>() {
            @Override
            public Tag writeNBT(Capability<MenuProvider> capability, MenuProvider instance, Direction side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability<MenuProvider> capability, MenuProvider instance, Direction side, Tag base) {
                throw new UnsupportedOperationException();
            }
        }, () -> { throw new UnsupportedOperationException(); });
    }

}
