package mcjty.lib.datagen.model;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public abstract class ModelProvider implements DataProvider {
    public static final String BLOCK_FOLDER = "block";
    protected final PackOutput output;
    protected final String modid;
    protected final String folder;
    protected final Map<ResourceLocation, BlockModelBuilder> models = new LinkedHashMap<>();

    protected ModelProvider(PackOutput output, String modid, String folder) {
        this.output = output;
        this.modid = modid;
        this.folder = folder;
    }

    public ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(modid, path);
    }

    public ResourceLocation mcLoc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public BlockModelBuilder getBuilder(String name) {
        var id = modLoc(name.contains("/") ? name : folder + "/" + name);
        return models.computeIfAbsent(id, BlockModelBuilder::new);
    }

    public ModelFile getExistingFile(ResourceLocation id) {
        return new ModelFile(id.getPath().contains("/") ? id : id.withPrefix(folder + "/"));
    }

    public BlockModelBuilder cubeAll(String name, ResourceLocation texture) {
        return getBuilder(name)
                .parent(getExistingFile(mcLoc("block/cube_all")))
                .texture("all", texture);
    }

    public BlockModelBuilder cube(
            String name,
            ResourceLocation down,
            ResourceLocation up,
            ResourceLocation north,
            ResourceLocation south,
            ResourceLocation east,
            ResourceLocation west) {
        return getBuilder(name)
                .parent(getExistingFile(mcLoc("block/cube")))
                .texture("down", down)
                .texture("up", up)
                .texture("north", north)
                .texture("south", south)
                .texture("east", east)
                .texture("west", west);
    }

    public BlockModelBuilder withExistingParent(String name, ResourceLocation parent) {
        return getBuilder(name).parent(getExistingFile(parent));
    }

    public BlockModelBuilder cubeBottomTop(
            String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return withExistingParent(name, mcLoc("block/cube_bottom_top"))
                .texture("side", side)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    public BlockModelBuilder slab(
            String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return withExistingParent(name, mcLoc("block/slab"))
                .texture("side", side)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    public BlockModelBuilder slabTop(
            String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return withExistingParent(name, mcLoc("block/slab_top"))
                .texture("side", side)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    protected abstract void registerModels();

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        models.clear();
        registerModels();
        var paths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        var tasks = new ArrayList<CompletableFuture<?>>();
        models.forEach(
                (id, model) ->
                        tasks.add(DataProvider.saveStable(cache, model.toJson(), paths.json(id))));
        return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
    }

    protected JsonObject itemDefinitionFor(ResourceLocation model) {
        return itemDefinition(model);
    }

    public static JsonObject itemDefinition(ResourceLocation model) {
        var definition = new JsonObject();
        var reference = new JsonObject();
        reference.addProperty("type", "minecraft:model");
        reference.addProperty("model", model.toString());
        definition.add("model", reference);
        return definition;
    }

    @Override
    public String getName() {
        return modid + " " + folder + " models";
    }
}
