package mcjty.lib.compat.theoneprobe;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface TOPInfoProvider {

    void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, IBlockState blockState, IProbeHitData data);
}
