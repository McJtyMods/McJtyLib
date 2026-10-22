package mcjty.lib.client;

import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.*;
import net.minecraft.client.resources.model.geometry.*;
import net.minecraft.client.resources.model.sprite.TextureSlots;

public abstract class BaseGeometry implements UnbakedGeometry {
    public abstract QuadCollection bake();
    @Override public QuadCollection bake(TextureSlots slots, ModelBaker baker, ModelState state, ModelDebugName name) { return bake(); }
}
