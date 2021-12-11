package mcjty.lib.tileentity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TickingTileEntity extends GenericTileEntity {

    public TickingTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

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
