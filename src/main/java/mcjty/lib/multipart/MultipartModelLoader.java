package mcjty.lib.multipart;

public class MultipartModelLoader {} /* @todo 1.15 implements ICustomModelLoader {

    public static final MultipartModel MULTIPART_MODEL = new MultipartModel();

    private static final Set<String> NAMES = ImmutableSet.of(
            MultipartBakedModel.MODEL.getPath());

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if (!modelLocation.getNamespace().equals(McJtyLib.MODID)) {
            return false;
        }
        return NAMES.contains(modelLocation.getPath());
    }

    @Override
    public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception {
        return MULTIPART_MODEL;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
    }
}
*/