package mcjty.lib.api.information;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityPowerInformation {

    @CapabilityInject(IPowerInformation.class)
    public static Capability<IPowerInformation> POWER_INFORMATION_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IPowerInformation.class, new Capability.IStorage<IPowerInformation>() {
            @Override
            public INBT writeNBT(Capability<IPowerInformation> capability, IPowerInformation instance, Direction side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability<IPowerInformation> capability, IPowerInformation instance, Direction side, INBT nbt) {
                throw new UnsupportedOperationException();
            }
        }, () -> {
            throw new UnsupportedOperationException();
        });
    }

}
