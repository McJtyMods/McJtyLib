package mcjty.lib.datagen.model;
import com.google.gson.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.function.Function;
import java.util.stream.Collectors;
public class VariantBlockStateBuilder {
    private final Block block;
    final JsonObject variants = new JsonObject();
    public VariantBlockStateBuilder(Block block) { this.block=block; }
    public VariantBlockStateBuilder forAllStates(Function<BlockState, ConfiguredModel[]> factory) {
        for (var state:block.getStateDefinition().getPossibleStates()) {
            var key=state.getValues().map(Object::toString).collect(Collectors.joining(","));
            var models=factory.apply(state); if(models.length==1)variants.add(key,models[0].toJson()); else { var a=new JsonArray();for(var model:models)a.add(model.toJson());variants.add(key,a); }
        } return this;
    }
    @SuppressWarnings({"rawtypes","unchecked"}) private static String name(Property property, Comparable value) { return property.getName(value); }
    public JsonObject toJson() { var json=new JsonObject();json.add("variants",variants);return json; }
}
