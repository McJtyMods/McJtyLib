package mcjty.lib.datagen.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public abstract class BlockStateProvider extends ModelProvider {

    private final Map<Block, JsonObject> multipartStates = new LinkedHashMap<>();
    private final Map<Block, VariantBlockStateBuilder> states = new LinkedHashMap<>();

    protected BlockStateProvider(PackOutput output, String modid) {
        super(output, modid, "block");
    }

    public ModelProvider models() {
        return this;
    }

    protected abstract void registerStatesAndModels();

    @Override
    protected final void registerModels() {
        states.clear();
        multipartStates.clear();
        registerStatesAndModels();
    }

    public Identifier blockTexture(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).withPrefix("block/");
    }

    public VariantBlockStateBuilder getVariantBuilder(Block block) {
        return states.computeIfAbsent(block, VariantBlockStateBuilder::new);
    }

    public void simpleBlock(Block block) {
        simpleBlock(block, cubeAll(BuiltInRegistries.BLOCK.getKey(block).getPath(), blockTexture(block)));
    }

    public void simpleBlock(Block block, ModelFile model) {
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(model).build());
    }

    public void horizontalBlock(Block block, ModelFile model) {
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(model)
                .rotationY((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180).build());
    }

    public void paneBlock(Block block, String name, Identifier pane, Identifier edge) {
        var parts = new JsonArray();
        var post = withExistingParent(name + "_post", mcLoc("block/template_glass_pane_post")).texture("pane", pane).texture("edge", edge);
        var center = new JsonObject();
        var centerModel = new JsonObject();
        centerModel.addProperty("model", post.getLocation().toString());
        center.add("apply", centerModel);
        parts.add(center);
        for (var side : Direction.Plane.HORIZONTAL) {
            for (boolean connected : new boolean[]{false, true}) {
                String suffix = connected ? "side" : "noside";
                var model = withExistingParent(name + "_" + suffix, mcLoc("block/template_glass_pane_" + suffix))
                        .texture("pane", pane).texture("edge", edge);
                var part = new JsonObject();
                var condition = new JsonObject();
                condition.addProperty(side.getSerializedName(), Boolean.toString(connected));
                var apply = new JsonObject();
                apply.addProperty("model", model.getLocation().toString());
                apply.addProperty("y", Math.floorMod((int) side.toYRot() + 180, 360));
                part.add("when", condition);
                part.add("apply", apply);
                parts.add(part);
            }
        }
        var json = new JsonObject();
        json.add("multipart", parts);
        multipartStates.put(block, json);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        var models = super.run(cache);
        var paths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        var tasks = new ArrayList<CompletableFuture<?>>();
        tasks.add(models);
        states.forEach((block, state) -> tasks.add(DataProvider.saveStable(cache, state.toJson(), paths.json(BuiltInRegistries.BLOCK.getKey(block)))));
        multipartStates.forEach((block, state) -> tasks.add(DataProvider.saveStable(cache, state, paths.json(BuiltInRegistries.BLOCK.getKey(block)))));
        return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
    }
}
