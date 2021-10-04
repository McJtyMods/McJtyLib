package mcjty.lib.datagen;

import mcjty.lib.blocks.LogicSlabBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public abstract class BaseBlockStateProvider extends BlockStateProvider {

    public static final ResourceLocation RFTOOLSBASE_SIDE = new ResourceLocation("rftoolsbase", "block/base/machineside");
    public static final ResourceLocation RFTOOLSBASE_TOP = new ResourceLocation("rftoolsbase", "block/base/machinetop");
    public static final ResourceLocation RFTOOLSBASE_BOTTOM = new ResourceLocation("rftoolsbase", "block/base/machinebottom");

    protected String name(Block block) {
        return block.getRegistryName().getPath();
    }

    public BaseBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    public ModelFile frontBasedModel(String modelName, ResourceLocation texture) {
        return frontBasedModel(modelName, texture, RFTOOLSBASE_SIDE, RFTOOLSBASE_TOP, RFTOOLSBASE_BOTTOM);
    }

    public ModelFile frontBasedModel(String modelName, ResourceLocation front, ResourceLocation side, ResourceLocation top, ResourceLocation bottom) {
        return models().cube(modelName, bottom, top, front, side, side, side)
                .texture("particle", front);
    }

    public ModelFile topBasedModel(String modelName, ResourceLocation texture) {
        return topBasedModel(modelName, texture, RFTOOLSBASE_SIDE, RFTOOLSBASE_BOTTOM);
    }

    public ModelFile topBasedModel(String modelName, ResourceLocation top, ResourceLocation side, ResourceLocation bottom) {
        return models().cube(modelName, bottom, top, side, side, side, side)
                .texture("particle", top);
    }

    private ModelFile logicSlabModel(String modelName, ResourceLocation texture, ModelBuilder.FaceRotation faceRotation) {
        BlockModelBuilder model = models().getBuilder(BLOCK_FOLDER + "/" + modelName)
                .parent(models().getExistingFile(mcLoc("block")));
        model.element().from(0, 0, 0).to(16, 4, 16)
                .face(Direction.DOWN).cullface(Direction.DOWN).texture("#down").end()
                .face(Direction.UP).texture("#up").uvs(0, 0, 16, 16).rotation(faceRotation).end()
                .face(Direction.EAST).cullface(Direction.EAST).texture("#side").end()
                .face(Direction.WEST).cullface(Direction.WEST).texture("#side").end()
                .face(Direction.NORTH).cullface(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).cullface(Direction.SOUTH).texture("#side").end()
                .end()
                .texture("side", new ResourceLocation("rftoolsbase", "block/base/machineside"))
                .texture("down", RFTOOLSBASE_BOTTOM)
                .texture("up", texture)
                .texture("particle", texture);
        return model;
    }

    public void logicSlabBlock(LogicSlabBlock block, String modelPrefix, ResourceLocation topTexture) {
        ModelFile[] models = getLogicSlabModels(modelPrefix, topTexture);
        variantBlock(block,
                state -> models[state.getValue(LogicSlabBlock.LOGIC_FACING).getRotationStep()],
                state -> getXRotation(state.getValue(LogicSlabBlock.LOGIC_FACING).getSide()),
                state -> getYRotation(state.getValue(LogicSlabBlock.LOGIC_FACING).getSide()));
    }

    // Get logic slab models indexed by the logic facing rotation step
    public ModelFile[] getLogicSlabModels(String modelPrefix, ResourceLocation topTexture) {
        ModelFile models[] = new ModelFile[4];
        models[0] = logicSlabModel(modelPrefix + "_0", topTexture, ModelBuilder.FaceRotation.ZERO);
        models[1] = logicSlabModel(modelPrefix + "_1", topTexture, ModelBuilder.FaceRotation.UPSIDE_DOWN);
        models[2] = logicSlabModel(modelPrefix + "_2", topTexture, ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90);
        models[3] = logicSlabModel(modelPrefix + "_3", topTexture, ModelBuilder.FaceRotation.CLOCKWISE_90);
        return models;
    }

    public void variantBlock(Block block,
                             Function<BlockState, ModelFile> modelSelector,
                             Function<BlockState, Integer> xRotation,
                             Function<BlockState, Integer> yRotation) {
        getVariantBuilder(block)
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(modelSelector.apply(state))
                        .rotationX(xRotation.apply(state))
                        .rotationY(yRotation.apply(state))
                        .build()
                );
    }

    public void variantBlock(Block block,
                             Function<BlockState, ModelFile> modelSelector) {
        getVariantBuilder(block)
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(modelSelector.apply(state))
                        .build()
                );
    }

    public int getXRotation(Direction direction) {
        switch (direction) {
            case DOWN: return 0;
            case UP: return 180;
            case NORTH: return -90;
            case SOUTH: return 90;
            case WEST: return 90;
            case EAST: return 90;
        }
        return 0;
    }

    public int getYRotation(Direction direction) {
        switch (direction) {
            case DOWN: return 0;
            case UP: return 0;
            case NORTH: return 0;
            case SOUTH: return 0;
            case WEST: return 90;
            case EAST: return 270;
        }
        return 0;
    }

    public void applyRotation(ConfiguredModel.Builder<?> builder, Direction direction) {
        applyRotationBld(builder, direction);
        builder.addModel();
    }

    private void applyRotationBld(ConfiguredModel.Builder<?> builder, Direction direction) {
        switch (direction) {
            case DOWN:
                builder.rotationX(90);
                break;
            case UP:
                builder.rotationX(-90);
                break;
            case NORTH:
                break;
            case SOUTH:
                builder.rotationY(180);
                break;
            case WEST:
                builder.rotationY(270);
                break;
            case EAST:
                builder.rotationY(90);
                break;
        }
    }

    public void applyHorizRotation(ConfiguredModel.Builder<VariantBlockStateBuilder> builder, Direction direction) {
        applyHorizRotationBld(builder, direction);
        builder.addModel();
    }

    private void applyHorizRotationBld(ConfiguredModel.Builder<VariantBlockStateBuilder> builder, Direction direction) {
        switch (direction) {
            case SOUTH:
                builder.rotationY(180);
                break;
            case WEST:
                builder.rotationY(270);
                break;
            case EAST:
                builder.rotationY(90);
                break;
        }
    }

    protected void singleTextureBlock(Block block, String modelName, String textureName) {
        ModelFile model = models().cubeAll(modelName, modLoc(textureName));
        simpleBlock(block, model);
    }

    protected VariantBlockStateBuilder horizontalOrientedBlock(Block block, ModelFile model) {
        return horizontalOrientedBlock(block, (blockState, builder) -> builder.modelFile(model));
    }

    protected VariantBlockStateBuilder horizontalOrientedBlock(Block block, BiConsumer<BlockState, ConfiguredModel.Builder<?>> model) {
        return directionBlock(block, model, BlockStateProperties.HORIZONTAL_FACING);
    }

    protected VariantBlockStateBuilder orientedBlock(Block block, ModelFile model) {
        return orientedBlock(block, (blockState, builder) -> builder.modelFile(model));
    }

    protected VariantBlockStateBuilder orientedBlock(Block block, BiConsumer<BlockState, ConfiguredModel.Builder<?>> model) {
        return directionBlock(block, model, BlockStateProperties.FACING);
    }

    private VariantBlockStateBuilder directionBlock(Block block, BiConsumer<BlockState, ConfiguredModel.Builder<?>> model, DirectionProperty directionProperty) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        builder.forAllStates(state -> {
            ConfiguredModel.Builder<?> bld = ConfiguredModel.builder();
            model.accept(state, bld);
            applyRotationBld(bld, state.getValue(directionProperty));
            return bld.build();
        });
        return builder;
    }

    protected void createFrame(BlockModelBuilder dimCellFrame, String txtName, float thick) {
        createFrame(dimCellFrame, txtName, thick, true, true);
    }

    protected void createFrame(BlockModelBuilder dimCellFrame, String txtName, float thick, boolean doTop, boolean doBottom) {
        // Vertical bars
        innerCube(dimCellFrame, txtName, 0f, 0f, 0f, thick, 16f, thick);
        innerCube(dimCellFrame, txtName, 16f - thick, 0f, 0f, 16f, 16f, thick);
        innerCube(dimCellFrame, txtName, 0f, 0f, 16f - thick, thick, 16f, 16f);
        innerCube(dimCellFrame, txtName, 16f - thick, 0f, 16f - thick, 16f, 16f, 16f);

        if (doTop) {
            // Top bars
            innerCube(dimCellFrame, txtName, thick, 16f - thick, 0f, 16f - thick, 16f, thick);
            innerCube(dimCellFrame, txtName, thick, 16f - thick, 16f - thick, 16f - thick, 16f, 16f);
            innerCube(dimCellFrame, txtName, 0f, 16f - thick, thick, thick, 16f, 16f - thick);
            innerCube(dimCellFrame, txtName, 16f - thick, 16f - thick, thick, 16f, 16f, 16f - thick);
        }

        if (doBottom) {
            // Bottom bars
            innerCube(dimCellFrame, txtName, thick, 0f, 0f, 16f - thick, thick, thick);
            innerCube(dimCellFrame, txtName, thick, 0f, 16f - thick, 16f - thick, thick, 16f);
            innerCube(dimCellFrame, txtName, 0f, 0f, thick, thick, thick, 16f - thick);
            innerCube(dimCellFrame, txtName, 16f - thick, 0f, thick, 16f, thick, 16f - thick);
        }
    }

    protected void innerCube(BlockModelBuilder builder, String txtName, float fx, float fy, float fz, float tx, float ty, float tz) {
        builder.element().from(fx, fy, fz).to(tx, ty, tz).allFaces((direction, faceBuilder) -> faceBuilder.texture(txtName)).end();
    }
}
