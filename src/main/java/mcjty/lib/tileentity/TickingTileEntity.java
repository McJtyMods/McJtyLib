package mcjty.lib.tileentity;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TickingTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public TickingTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void tick() {
        if (level != null) {
            if (level.isClientSide()) {
                tickClient();
            } else {
                tickServer();
            }
        }
    }

    protected void tickServer() {

    }

    protected void tickClient() {

    }
}
