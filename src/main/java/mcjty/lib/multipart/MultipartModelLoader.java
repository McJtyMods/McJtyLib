package mcjty.lib.multipart;

import com.google.common.collect.ImmutableSet;
import mcjty.lib.McJtyLib;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.Set;

public class MultipartModelLoader implements ICustomModelLoader {

    public static final MultipartModel MULTIPART_MODEL = new MultipartModel();

    private static final Set<String> NAMES = ImmutableSet.of(
            MultipartBakedModel.MODEL.getResourcePath());

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if (!modelLocation.getResourceDomain().equals(McJtyLib.PROVIDES)) {
            return false;
        }
        return NAMES.contains(modelLocation.getResourcePath());
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) {
        return MULTIPART_MODEL;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}
