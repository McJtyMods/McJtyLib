package mcjty.lib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.BakedModelWrapper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
abstract class ItemRendererMixin {

    @Redirect(
            method = "render",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/renderer/block/model/ItemTransform;apply(ZLcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void mcjtylib$itemTransform(
            ItemTransform transform,
            boolean leftHand,
            PoseStack poses,
            ItemStack stack,
            ItemDisplayContext context,
            boolean mirrored,
            PoseStack itemPoses,
            MultiBufferSource buffers,
            int light,
            int overlay,
            BakedModel model) {
        if (model instanceof BakedModelWrapper<?> wrapper) {
            wrapper.applyTransform(context, poses, leftHand);
        } else {
            transform.apply(leftHand, poses);
        }
    }
}
