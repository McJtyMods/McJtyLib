package mcjty.lib.datagen;

import mcjty.lib.varia.Tools;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public abstract class BaseItemModelProvider extends ItemModelProvider {

    protected String name(Item item) {
        return Tools.getId(item).getPath();
    }


    public BaseItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
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
