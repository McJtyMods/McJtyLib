package mcjty.lib.compat.theoneprobe;

import mcjty.lib.McJtyLib;
import mcjty.lib.base.ModBase;
import mcjty.lib.multipart.MultipartHelper;
import mcjty.lib.multipart.MultipartTE;
import mcjty.lib.setup.Registration;
import mcjty.lib.varia.Logging;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.InterModComms;
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
//        InterModComms.sendTo("theoneprobe", "getTheOneProbe", "mcjty.lib.compat.theoneprobe.TOPCompatibility$GetTheOneProbe");
        // @todo make easier!
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> new GetTheOneProbe());
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
                public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
                    if (blockState.getBlock() instanceof TOPInfoProvider) {
                        TOPInfoProvider provider = (TOPInfoProvider) blockState.getBlock();
                        // @todo 1.14
//                        provider.addProbeInfo(mode, probeInfo, player, world, blockState, data);
                    }

                }
            });

            probe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
                Block block = blockState.getBlock();
                if (block == Registration.MULTIPART_BLOCK) {
                    String modid = McJtyLib.MODID;

                    ItemStack pickBlock = data.getPickBlock();
                    MultipartTE.Part part = Registration.MULTIPART_BLOCK.getHitPart(blockState, world, data.getPos(), MultipartHelper.getPlayerEyes(player), data.getHitVec());
                    if (part != null) {
                        pickBlock = part.getState().getBlock().getItem(world, data.getPos(), part.getState());
                        modid = part.getState().getBlock().getRegistryName().getNamespace();
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
                                .text(NAME + STARTLOC + block.getTranslationKey() + ".name" + ENDLOC)
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
