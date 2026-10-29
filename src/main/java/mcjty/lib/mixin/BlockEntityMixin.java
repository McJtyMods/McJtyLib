package mcjty.lib.mixin;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
abstract class BlockEntityMixin {

    @Inject(method = {"loadWithComponents", "loadCustomOnly"}, at = @At("TAIL"))
    private void mcjtylib$loadClientData(ValueInput input, CallbackInfo ci) {
        // Subclass save-data readers must finish before applying the separate client payload.
        // Both initial chunk data and subsequent block entity packets use these entrypoints.
        if ((Object) this instanceof GenericTileEntity tile) {
            input.read("mcjtylib_client", CompoundTag.CODEC)
                    .ifPresent(client -> tile.loadClientDataFromNBT(client, input.lookup()));
        }
    }
}
