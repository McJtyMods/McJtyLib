package mcjty.lib.mixin.client;

import mcjty.lib.client.ClientStartup;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {

    @Inject(
            method = "<init>",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;registerReloadListener(Lnet/minecraft/server/packs/resources/PreparableReloadListener;)V",
                            ordinal = 1,
                            shift = At.Shift.AFTER))
    private void mcjtylib$clientResources(CallbackInfo ci) {
        ClientStartup.initialize((Minecraft) (Object) this);
    }
}
