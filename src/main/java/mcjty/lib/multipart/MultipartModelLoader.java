package mcjty.lib.multipart;

public class MultipartModelLoader {} /*implements IGeometryLoader<MultipartModelLoader.MultipartModelGeometry> {


    public static void register(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "multipartloader"), new MultipartModelLoader());
    }

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {

    }

    @Nonnull
    @Override
    public MultipartModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new MultipartModelGeometry();
    }

    public static class MultipartModelGeometry implements IModelGeometry<MultipartModelGeometry> {

        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new MultipartBakedModel();
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            return Collections.emptyList();
        }
    }
}
*/