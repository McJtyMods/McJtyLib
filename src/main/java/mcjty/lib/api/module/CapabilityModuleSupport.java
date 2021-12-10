package mcjty.lib.api.module;

import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityModuleSupport {

    @CapabilityInject(IModuleSupport.class)
    public static Capability<IModuleSupport> MODULE_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IModuleSupport.class, new Capability.IStorage<IModuleSupport>() {
            @Override
            public Tag writeNBT(Capability<IModuleSupport> capability, IModuleSupport instance, Direction side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability<IModuleSupport> capability, IModuleSupport instance, Direction side, Tag nbt) {
                throw new UnsupportedOperationException();
            }
        }, () -> {
            throw new UnsupportedOperationException();
        });
    }

}
