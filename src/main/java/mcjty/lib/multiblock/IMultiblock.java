package mcjty.lib.multiblock;

import net.minecraft.nbt.CompoundNBT;

public interface IMultiblock {

    void load(CompoundNBT nbt);

    CompoundNBT save(CompoundNBT tagCompound);

}
