package mcjty.lib.api.infusable;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityInfusable {

    @CapabilityInject(IInfusable.class)
    public static Capability<IInfusable> INFUSABLE_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IInfusable.class, new Capability.IStorage<IInfusable>() {
            @Override
            public INBT writeNBT(Capability<IInfusable> capability, IInfusable instance, Direction side) {
                return IntNBT.valueOf(instance.getInfused());
            }

            @Override
            public void readNBT(Capability<IInfusable> capability, IInfusable instance, Direction side, INBT nbt) {
                instance.setInfused(((IntNBT)nbt).getAsInt());
            }
        }, () -> { throw new UnsupportedOperationException(); });
    }

}
