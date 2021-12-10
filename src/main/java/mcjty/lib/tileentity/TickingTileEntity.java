package mcjty.lib.tileentity;

import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TickingTileEntity extends GenericTileEntity implements TickableBlockEntity {

    public TickingTileEntity(BlockEntityType<?> type) {
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
