package mcjty.lib.mixin;

import mcjty.lib.varia.SpawnCanceler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
abstract class ServerLevelMixin {

    @Inject(method="addEntity",at=@At("HEAD"),cancellable=true)
    private void mcjtylib$cancelSpawn(Entity entity,CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof SpawnCanceler.SpawnState state&&state.mcjtylib$isSpawnCanceled())cir.setReturnValue(false);
    }

}
