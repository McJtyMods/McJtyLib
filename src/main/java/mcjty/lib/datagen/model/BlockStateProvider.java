package mcjty.lib.datagen.model;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import java.util.*;
import java.util.concurrent.CompletableFuture;
public abstract class BlockStateProvider extends ModelProvider {
    private final Map<Block,VariantBlockStateBuilder> states=new LinkedHashMap<>();
    protected BlockStateProvider(PackOutput output,String modid) { super(output,modid,"block"); }
    public ModelProvider models() { return this; }
    protected abstract void registerStatesAndModels();
    @Override protected final void registerModels() { states.clear();registerStatesAndModels(); }
    public Identifier blockTexture(Block block) { return BuiltInRegistries.BLOCK.getKey(block).withPrefix("block/"); }
    public VariantBlockStateBuilder getVariantBuilder(Block block) { return states.computeIfAbsent(block,VariantBlockStateBuilder::new); }
    public void simpleBlock(Block block) { simpleBlock(block,cubeAll(BuiltInRegistries.BLOCK.getKey(block).getPath(),blockTexture(block))); }
    public void simpleBlock(Block block,ModelFile model) { getVariantBuilder(block).forAllStates(state->ConfiguredModel.builder().modelFile(model).build()); }
    @Override public CompletableFuture<?> run(CachedOutput cache) {
        var models=super.run(cache);var paths=output.createPathProvider(PackOutput.Target.RESOURCE_PACK,"blockstates");var tasks=new ArrayList<CompletableFuture<?>>();tasks.add(models);
        states.forEach((block,state)->tasks.add(DataProvider.saveStable(cache,state.toJson(),paths.json(BuiltInRegistries.BLOCK.getKey(block)))));
        return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
    }
}
