package mcjty.lib.datagen;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;

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

    public void applyRotation(ConfiguredModel.Builder<VariantBlockStateBuilder> builder, Direction direction) {
        switch (direction) {
            case DOWN:
                builder.rotationX(90).addModel();
                break;
            case UP:
                builder.rotationX(-90).addModel();
                break;
            case NORTH:
                builder.addModel();
                break;
            case SOUTH:
                builder.rotationY(180).addModel();
                break;
            case WEST:
                builder.rotationY(270).addModel();
                break;
            case EAST:
                builder.rotationY(90).addModel();
                break;
        }
    }

    public void applyHorizRotation(ConfiguredModel.Builder<VariantBlockStateBuilder> builder, Direction direction) {
        switch (direction) {
            case NORTH:
                builder.addModel();
                break;
            case SOUTH:
                builder.rotationY(180).addModel();
                break;
            case WEST:
                builder.rotationY(270).addModel();
                break;
            case EAST:
                builder.rotationY(90).addModel();
                break;
        }
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

    protected void createFrame(BlockModelBuilder dimCellFrame, String txtName, float thick) {
        createFrame(dimCellFrame, txtName, thick, true, true);
    }

    protected void createFrame(BlockModelBuilder dimCellFrame, String txtName, float thick, boolean doTop, boolean doBottom) {
        // Vertical bars
        innerCube(dimCellFrame, txtName, 0f, 0f, 0f,                   thick, 16f, thick);
        innerCube(dimCellFrame, txtName, 16f-thick, 0f, 0f,        16f, 16f, thick);
        innerCube(dimCellFrame, txtName, 0f, 0f, 16f-thick,            thick, 16f, 16f);
        innerCube(dimCellFrame, txtName, 16f-thick, 0f, 16f-thick, 16f, 16f, 16f);

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
