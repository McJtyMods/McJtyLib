package mcjty.lib.datagen;

import mcjty.lib.varia.Tools;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import mcjty.lib.datagen.model.ModelProvider;
import mcjty.lib.datagen.model.ModelFile;

public abstract class BaseItemModelProvider extends ModelProvider {

    protected String name(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }


    public BaseItemModelProvider(DataGenerator generator, String modid) {
        super(generator.getPackOutput(), modid, "item");
    }

    public void parentedBlock(Block block) {
        parentedBlock(block, "block/" + Tools.getId(block).getPath());
    }

    public void parentedBlock(Block block, String model) {
        getBuilder(Tools.getId(block).getPath())
                .parent(new ModelFile.UncheckedModelFile(modLoc(model)));
    }

    public void parentedItem(Item item, String model) {
        getBuilder(Tools.getId(item).getPath())
                .parent(new ModelFile.UncheckedModelFile(modLoc(model)));
    }

    public void itemGenerated(Item item, String texture) {
        getBuilder(Tools.getId(item).getPath()).parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", texture);
    }

    public void itemHandheld(Item item, String texture) {
        getBuilder(Tools.getId(item).getPath()).parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", texture);
    }
}
