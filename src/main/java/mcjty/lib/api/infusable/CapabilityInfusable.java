package mcjty.lib.api.infusable;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.IntTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityInfusable {

    @CapabilityInject(IInfusable.class)
    public static Capability<IInfusable> INFUSABLE_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IInfusable.class, new Capability.IStorage<IInfusable>() {
            @Override
            public Tag writeNBT(Capability<IInfusable> capability, IInfusable instance, Direction side) {
                return IntTag.valueOf(instance.getInfused());
            }

            @Override
            public void readNBT(Capability<IInfusable> capability, IInfusable instance, Direction side, Tag nbt) {
                instance.setInfused(((IntTag)nbt).getAsInt());
            }
        }, () -> { throw new UnsupportedOperationException(); });
    }

}
