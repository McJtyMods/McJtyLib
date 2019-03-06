package mcjty.lib.compat.theoneprobe;

import mcjty.lib.McJtyLib;
import mcjty.lib.base.ModBase;
import mcjty.lib.multipart.MultipartHelper;
import mcjty.lib.multipart.MultipartTE;
import mcjty.lib.proxy.CommonSetup;
import mcjty.lib.varia.Logging;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;

import static mcjty.theoneprobe.api.IProbeInfo.ENDLOC;
import static mcjty.theoneprobe.api.IProbeInfo.STARTLOC;
import static mcjty.theoneprobe.api.TextStyleClass.MODNAME;
import static mcjty.theoneprobe.api.TextStyleClass.NAME;

public class TOPCompatibility {

    private static boolean registered;

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.lib.compat.theoneprobe.TOPCompatibility$GetTheOneProbe");
    }


    public static class GetTheOneProbe implements com.google.common.base.Function<ITheOneProbe, Void> {

        public static ITheOneProbe probe;

        @Nullable
        @Override
        public Void apply(ITheOneProbe theOneProbe) {
            probe = theOneProbe;
            Logging.log("Enabled support for The One Probe");
            probe.registerProvider(new IProbeInfoProvider() {
                @Override
                public String getID() {
                    return "mcjtylib:default";
                }

                @Override
                public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
                    if (blockState.getBlock() instanceof TOPInfoProvider) {
                        TOPInfoProvider provider = (TOPInfoProvider) blockState.getBlock();
                        provider.addProbeInfo(mode, probeInfo, player, world, blockState, data);
                    }

                }
            });

            probe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
                Block block = blockState.getBlock();
                if (block == CommonSetup.multipartBlock) {
                    String modid = McJtyLib.PROVIDES;

                    ItemStack pickBlock = data.getPickBlock();
                    MultipartTE.Part part = CommonSetup.multipartBlock.getHitPart(blockState, world, data.getPos(), MultipartHelper.getPlayerEyes(player), data.getHitVec());
                    if (part != null) {
                        pickBlock = part.getState().getBlock().getItem(world, data.getPos(), part.getState());
                        modid = part.getState().getBlock().getRegistryName().getResourceDomain();
                    }
                    modid = WordUtils.capitalize(modid);

                    if (!pickBlock.isEmpty()) {
                        probeInfo.horizontal()
                                .item(pickBlock)
                                .vertical()
                                .itemLabel(pickBlock)
                                .text(MODNAME + modid);
                    } else {
                        probeInfo.vertical()
                                .text(NAME + STARTLOC + block.getUnlocalizedName() + ".name" + ENDLOC)
                                .text(MODNAME + modid);
                    }

                    return true;
                }
                return false;
            });

            McJtyLib.forEachMod(ModBase::handleTopExtras);

            return null;
        }
    }
}
