package mcjty.lib.test.mixin;
import mcjty.lib.test.TransferRegression;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Run after all common entrypoints, before a world or network listener starts. */
@Mixin(DedicatedServerSettings.class)
abstract class SettingsTestMixin {
    @Inject(method="<init>",at=@At("RETURN"))
    private void mcjtylib$checkTransfers(CallbackInfo ci) { TransferRegression.runChecks(); }
}
