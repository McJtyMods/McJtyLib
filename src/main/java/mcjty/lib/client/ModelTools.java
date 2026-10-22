package mcjty.lib.client;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;
public final class ModelTools {
 public static void registerModelBakeEvent(Consumer<Map<BlockState,BlockStateModel>> consumer){
  ModelLoadingPlugin.register(plugin -> plugin.modifyBlockModelAfterBake().register((model,context)->{
   Map<BlockState,BlockStateModel> models=new HashMap<>();models.put(context.state(),model);consumer.accept(models);return models.get(context.state());
  }));
 }
}
