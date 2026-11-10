package mcjty.lib.mixin;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
abstract class BlockEntityMixin {

    @Inject(
            method = {"loadWithComponents", "loadCustomOnly"},
            at = @At("TAIL"))
    private void mcjtylib$loadClientData(
            CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        if ((Object) this instanceof GenericTileEntity tile
                && tile.getLevel() != null
                && tile.getLevel().isClientSide) {
            tile.onDataPacket(
                    null,
                    ClientboundBlockEntityDataPacket.create(tile, (blockEntity, lookup) -> tag),
                    provider);
        }
    }
}
