package mcjty.lib.datagen.model;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ModelProvider implements DataProvider {
    public static final String BLOCK_FOLDER = "block";
    protected final PackOutput output;
    protected final String modid;
    protected final String folder;
    protected final Map<Identifier, BlockModelBuilder> models = new LinkedHashMap<>();

    protected ModelProvider(PackOutput output, String modid, String folder) {
        this.output = output;
        this.modid = modid;
        this.folder = folder;
    }

    public Identifier modLoc(String path) { return Identifier.fromNamespaceAndPath(modid, path); }
    public Identifier mcLoc(String path) { return Identifier.withDefaultNamespace(path); }

    public BlockModelBuilder getBuilder(String name) {
        var id = modLoc(name.contains("/") ? name : folder + "/" + name);
        return models.computeIfAbsent(id, BlockModelBuilder::new);
    }

    public ModelFile getExistingFile(Identifier id) {
        return new ModelFile(id.getPath().contains("/") ? id : id.withPrefix(folder + "/"));
    }

    public BlockModelBuilder cubeAll(String name, Identifier texture) {
        return getBuilder(name).parent(getExistingFile(mcLoc("block/cube_all"))).texture("all", texture);
    }

    public BlockModelBuilder cube(String name, Identifier down, Identifier up, Identifier north,
                                  Identifier south, Identifier east, Identifier west) {
        return getBuilder(name).parent(getExistingFile(mcLoc("block/cube")))
                .texture("down", down).texture("up", up).texture("north", north)
                .texture("south", south).texture("east", east).texture("west", west);
    }

    protected abstract void registerModels();

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        models.clear();
        registerModels();
        var paths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        var tasks = new ArrayList<CompletableFuture<?>>();
        models.forEach((id, model) -> tasks.add(DataProvider.saveStable(cache, model.toJson(), paths.json(id))));
        if (folder.equals("item")) {
            var items = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
            models.forEach((id, model) -> {
                var itemId = id.withPath(id.getPath().replaceFirst("^item/", ""));
                tasks.add(DataProvider.saveStable(cache, itemDefinition(id), items.json(itemId)));
            });
        }
        return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
    }

    public static JsonObject itemDefinition(Identifier model) {
        var definition = new JsonObject();
        var reference = new JsonObject();
        reference.addProperty("type", "minecraft:model");
        reference.addProperty("model", model.toString());
        definition.add("model", reference);
        return definition;
    }

    @Override
    public String getName() { return modid + " " + folder + " models"; }
}
