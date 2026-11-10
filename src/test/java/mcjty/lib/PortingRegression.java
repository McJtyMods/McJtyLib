package mcjty.lib;

import java.nio.file.Path;
import mcjty.lib.datagen.model.BlockModelBuilder;
import mcjty.lib.datagen.model.ConfiguredModel;
import mcjty.lib.datagen.model.ModelProvider;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.varia.NamedCodec;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

/** Regression checks that do not require a running Minecraft game. */
public final class PortingRegression {
    @Test
    void energyTransactions() {
        // No block entity is needed: aborted transfers must never notify it.
        var energy = new GenericEnergyStorage(null, true, 1000, 200);
        try (var outer = Transaction.openOuter()) {
            require(energy.insert(500, outer) == 200, "Energy receive rate");
            try (var inner = outer.openNested()) {
                energy.insert(100, inner);
                inner.commit();
            }
            require(energy.getEnergy() == 300, "Nested commit");
            try (var inner = outer.openNested()) {
                energy.insert(100, inner);
            }
            require(energy.getEnergy() == 300, "Nested rollback");
        }
        require(energy.getEnergy() == 0, "Outer rollback must undo nested commits");
        try (var transaction = Transaction.openOuter()) {
            for (int i = 0; i < 6; i++) energy.insert(200, transaction);
            require(energy.getEnergy() == 1000, "Energy capacity");
            require(energy.extract(100, transaction) == 0, "Input-only energy storage");
        }
        require(energy.getEnergy() == 0, "Capacity test rollback");
    }

    @Test
    void namedNbtValues() {
        var tag = new CompoundTag();
        tag.putString("name", "port");
        tag.putLong("energy", 123L);
        var nested = new CompoundTag();
        nested.putInt("channel", 7);
        tag.put("nested", nested);
        var codec = NamedCodec.map(CompoundTag.CODEC, tag);
        require(codec.get("name").equals("port"), "NBT string decoding");
        require(codec.get("energy").equals(123L), "NBT numeric decoding");
        require(codec.get("channel").equals(7), "Nested NBT decoding");
        require(
                codec.set("channel", 42).getCompound("nested").getInt("channel") == 42,
                "Nested NBT update");
    }

    @Test
    void generatedModels() {
        var model = new BlockModelBuilder(ResourceLocation.parse("test:block/frame"));
        model.texture("all", ResourceLocation.parse("test:block/metal"));
        model.element()
                .from(0, 0, 0)
                .to(16, 4, 16)
                .allFaces((direction, face) -> face.texture("#all"))
                .end();
        var elements = model.toJson().getAsJsonArray("elements");
        require(elements.size() == 1, "Model geometry");
        require(
                elements.get(0).getAsJsonObject().getAsJsonObject("faces").size() == 6,
                "Model faces");
        var variant =
                ConfiguredModel.builder()
                        .modelFile(model)
                        .rotationX(-90)
                        .rotationY(450)
                        .build()[0]
                        .toJson();
        require(
                variant.get("x").getAsInt() == 270 && variant.get("y").getAsInt() == 90,
                "Blockstate rotations must be normalized");
    }

    @Test
    void generatedModelPaths() {
        var provider =
                new ModelProvider(new PackOutput(Path.of("unused")), "test", "block") {
                    @Override
                    protected void registerModels() {}
                };
        require(
                provider.getExistingFile(ResourceLocation.parse("minecraft:block"))
                        .getLocation()
                        .toString()
                        .equals("minecraft:block/block"),
                "Vanilla parent model folder");
        require(
                provider.getBuilder("machine")
                        .getLocation()
                        .toString()
                        .equals("test:block/machine"),
                "Generated model folder");
        require(
                provider.getBuilder("custom/machine")
                        .getLocation()
                        .toString()
                        .equals("test:custom/machine"),
                "Explicit model folder");
    }

    private static void require(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
    }
}
