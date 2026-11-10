package mcjty.lib.datagen.model;

import net.minecraft.resources.ResourceLocation;

public class ModelFile {
    private final ResourceLocation location;

    public ModelFile(ResourceLocation location) {
        this.location = location;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public static class UncheckedModelFile extends ModelFile {
        public UncheckedModelFile(ResourceLocation id) {
            super(id);
        }
    }
}
