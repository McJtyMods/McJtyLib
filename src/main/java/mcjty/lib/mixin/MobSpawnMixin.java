package mcjty.lib.mixin;

import mcjty.lib.varia.SpawnCanceler;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobSpawnMixin implements SpawnCanceler.SpawnState {

    @Unique private boolean mcjtylib$spawnCanceled;

    @Inject(method="finalizeSpawn",at=@At("HEAD"))
    private void mcjtylib$checkSpawn(ServerLevelAccessor level,DifficultyInstance difficulty,EntitySpawnReason reason,SpawnGroupData group,CallbackInfoReturnable<SpawnGroupData> cir) {
        mcjtylib$spawnCanceled=SpawnCanceler.shouldCancel((Mob)(Object)this);
    }

    public boolean mcjtylib$isSpawnCanceled() {
        return mcjtylib$spawnCanceled;
    }

}
