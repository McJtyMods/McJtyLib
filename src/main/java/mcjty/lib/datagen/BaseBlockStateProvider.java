package mcjty.lib.datagen;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;

public abstract class BaseBlockStateProvider extends BlockStateProvider {

    public BaseBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    public ModelFile frontBasedModel(String modelName, ResourceLocation texture) {
        return models().cube(modelName,
                new ResourceLocation("rftoolsbase", "block/base/machinebottom"),
                new ResourceLocation("rftoolsbase", "block/base/machinetop"),
                texture,
                new ResourceLocation("rftoolsbase", "block/base/machineside"),
                new ResourceLocation("rftoolsbase", "block/base/machineside"),
                new ResourceLocation("rftoolsbase", "block/base/machineside"));
    }

    public void singleTextureBlock(Block block, String modelName, String textureName) {
        ModelFile model = models().cubeAll(modelName, modLoc(textureName));
        simpleBlock(block, model);
    }

    public VariantBlockStateBuilder horizontalOrientedBlock(Block block, ModelFile model) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        builder.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .modelForState().modelFile(model)
                .addModel();
        builder.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                .modelForState().modelFile(model)
                .rotationY(180)
                .addModel();
        builder.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
                .modelForState().modelFile(model)
                .rotationY(270)
                .addModel();
        builder.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                .modelForState().modelFile(model)
                .rotationY(90)
                .addModel();
        return builder;
    }

    public VariantBlockStateBuilder orientedBlock(Block block, ModelFile model) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        builder.partialState().with(BlockStateProperties.FACING, Direction.NORTH)
                .modelForState().modelFile(model)
                .addModel();
        builder.partialState().with(BlockStateProperties.FACING, Direction.SOUTH)
                .modelForState().modelFile(model)
                .rotationY(180)
                .addModel();
        builder.partialState().with(BlockStateProperties.FACING, Direction.WEST)
                .modelForState().modelFile(model)
                .rotationY(270)
                .addModel();
        builder.partialState().with(BlockStateProperties.FACING, Direction.EAST)
                .modelForState().modelFile(model)
                .rotationY(90)
                .addModel();
        builder.partialState().with(BlockStateProperties.FACING, Direction.UP)
                .modelForState().modelFile(model)
                .rotationX(-90)
                .addModel();
        builder.partialState().with(BlockStateProperties.FACING, Direction.DOWN)
                .modelForState().modelFile(model)
                .rotationX(90)
                .addModel();
        return builder;
    }
}
