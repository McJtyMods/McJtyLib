package mcjty.lib.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import java.util.concurrent.CompletableFuture;

abstract class LanguageProvider extends FabricLanguageProvider {

    private TranslationBuilder translations;

    LanguageProvider(FabricPackOutput output,String language,CompletableFuture<HolderLookup.Provider> registries) {
        super(output,language,registries);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider lookup,TranslationBuilder builder) {
        translations=builder;
        addTranslations();
    }

    protected abstract void addTranslations();

    protected void add(String key,String value) {
        translations.add(key,value);
    }

    protected void add(Block block,String value) {
        translations.add(block,value);
    }

    protected void add(Item item,String value) {
        translations.add(item,value);
    }

    protected void add(EntityType<?> entity,String value) {
        translations.add(entity,value);
    }

}
