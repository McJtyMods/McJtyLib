package mcjty.lib.compat.theoneprobe;

import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.multipart.MultipartBlock;
import mcjty.lib.multipart.MultipartHelper;
import mcjty.lib.multipart.MultipartTE;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.theoneprobe.api.CompoundText;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;

import static mcjty.theoneprobe.api.TextStyleClass.ERROR;
import static mcjty.theoneprobe.api.TextStyleClass.HIGHLIGHTED;

public class McJtyLibTOPDriver implements TOPDriver {

    public static final McJtyLibTOPDriver DRIVER = new McJtyLibTOPDriver();

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
        if (blockState.getBlock() == Registration.MULTIPART_BLOCK) {
            MultipartTE.Part part = MultipartBlock.getHitPart(blockState, world, data.getPos(), MultipartHelper.getPlayerEyes(player), data.getHitVec());
            if (part != null) {
                if (part.getTileEntity() instanceof TOPInfoProvider) {
                    TOPDriver driver = ((TOPInfoProvider) part.getTileEntity()).getProbeDriver();
                    if (driver != null) {
                        driver.addProbeInfo(mode, probeInfo, player, world, blockState, data);
                    }
                } else if (part.getState().getBlock() instanceof TOPInfoProvider) {
                    TOPDriver driver = ((TOPInfoProvider) part.getState().getBlock()).getProbeDriver();
                    driver.addProbeInfo(mode, probeInfo, player, world, blockState, data);
                }
            }
        } else if (blockState.getBlock() instanceof BaseBlock) {
            addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
        }
    }

    public void addStandardProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
        if (mode == ProbeMode.EXTENDED) {
        BlockEntity te = world.getBlockEntity(data.getPos());
        if (te instanceof GenericTileEntity) {
            GenericTileEntity generic = (GenericTileEntity) te;
                te.getCapability(CapabilityInfusable.INFUSABLE_CAPABILITY).ifPresent(h -> {
                    int infused = h.getInfused();
                    int pct = infused * 100 / GeneralConfig.maxInfuse.get();
                    probeInfo.text(CompoundText.create().style(HIGHLIGHTED).text("Infused: " + pct + "%"));
                });
                if (GeneralConfig.manageOwnership.get()) {
                    if (generic.getOwnerName() != null && !generic.getOwnerName().isEmpty()) {
                        int securityChannel = generic.getSecurityChannel();
                        if (securityChannel == -1) {
                            probeInfo.text(CompoundText.create().style(HIGHLIGHTED).text("Owned by: " + generic.getOwnerName()));
                        } else {
                            probeInfo.text(CompoundText.create().style(HIGHLIGHTED).text("Owned by: " + generic.getOwnerName() + " (channel " + securityChannel + ")"));
                        }
                        if (generic.getOwnerUUID() == null) {
                            probeInfo.text(CompoundText.create().style(ERROR).text( "Warning! Ownership not correctly set! Please place block again!"));
                        }
                    }
                }
            }
        }
    }

}
