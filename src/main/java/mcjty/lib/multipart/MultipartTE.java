package mcjty.lib.multipart;

import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public class MultipartTE extends TileEntity {

    public List<PartBlockId> getParts() {
        return Lists.newArrayList(
                new PartBlockId(Blocks.TORCH.getDefaultState()),
                new PartBlockId(Blocks.STONE_PRESSURE_PLATE.getDefaultState()));
    }
}
