package mcjty.lib.multipart;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class MultipartHelper {

    public static TileEntity getTileEntity(IBlockAccess access, BlockPos pos, PartSlot slot) {
        TileEntity te = access.getTileEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE.Part part = ((MultipartTE) te).getParts().get(slot);
            if (part != null) {
                return part.getTileEntity();
            }
        }
        return null;
    }
}
