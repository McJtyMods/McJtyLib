package mcjty.lib.mixin;

import mcjty.lib.blocks.LogicSlabBlock;
import net.minecraft.core.*;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
abstract class RedStoneWireMixin {

    @Inject(method="shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",at=@At("HEAD"),cancellable=true)
    private static void mcjtylib$connectLogic(BlockState state,Direction side,CallbackInfoReturnable<Boolean> cir) {
        if(state.getBlock() instanceof LogicSlabBlock logic)cir.setReturnValue(side!=null&&logic.canConnectRedstone(state,null,BlockPos.ZERO,side));
    }

}
