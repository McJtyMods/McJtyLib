package mcjty.lib.compat.theoneprobe;

import mcjty.lib.McJtyLib;
import mcjty.lib.multipart.MultipartHelper;
import mcjty.lib.multipart.MultipartTE;
import mcjty.lib.setup.Registration;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.Tools;
import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.InterModComms;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.util.function.Function;

import static mcjty.theoneprobe.api.TextStyleClass.MODNAME;

public class TOPCompatibility {

    private static boolean registered;

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
//        InterModComms.sendTo("theoneprobe", "getTheOneProbe", "mcjty.lib.compat.theoneprobe.TOPCompatibility$GetTheOneProbe");
        // @todo make easier!
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", GetTheOneProbe::new);
    }


    public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {

        public static ITheOneProbe probe;

        @Nullable
        @Override
        public Void apply(ITheOneProbe theOneProbe) {
            probe = theOneProbe;
            Logging.log("Enabled support for The One Probe");
            probe.registerProvider(new IProbeInfoProvider() {
                @Override
                public ResourceLocation getID() {
                    return new ResourceLocation("mcjtylib:default");
                }

                @Override
                public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
                    if (blockState.getBlock() instanceof TOPInfoProvider) {
                        TOPInfoProvider provider = (TOPInfoProvider) blockState.getBlock();
                        TOPDriver driver = provider.getProbeDriver();
                        if (driver != null) {
                            driver.addProbeInfo(mode, probeInfo, player, world, blockState, data);
                        }
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
                        pickBlock = part.getState().getBlock().getCloneItemStack(world, data.getPos(), part.getState());
                        modid = Tools.getId(part.getState()).getNamespace();
                    }
                    modid = WordUtils.capitalize(modid);

                    if (!pickBlock.isEmpty()) {
                        probeInfo.horizontal()
                                .item(pickBlock)
                                .vertical()
                                .itemLabel(pickBlock)
                                .text(CompoundText.create().style(MODNAME).text(modid));
                    } else {
                        probeInfo.vertical()
                                .text(CompoundText.create().name(block.getDescriptionId()))
                                .text(CompoundText.create().style(MODNAME).text(modid));
                    }

                    return true;
                }
                return false;
            });

            return null;
        }
    }
}
