package mcjty.lib.mixin.client;

import mcjty.lib.tooltips.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import java.util.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
abstract class ItemStackTooltipMixin {

    @Inject(method="getTooltipImage",at=@At("RETURN"),cancellable=true)
    private void mcjtylib$addIcons(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {

        TooltipRender.icons((ItemStack)(Object)this).ifPresent(icon -> cir.setReturnValue(Optional.of(cir.getReturnValue().<TooltipComponent>map(original -> new TooltipBundle(List.of(original,icon))).orElse(icon))));

    }

}
