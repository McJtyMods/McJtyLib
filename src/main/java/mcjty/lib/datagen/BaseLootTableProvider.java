package mcjty.lib.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.functions.*;
import net.minecraft.state.Property;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseLootTableProvider extends LootTableProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    protected final Map<Block, LootTable.Builder> lootTables = new HashMap<>();
    protected final Map<EntityType<?>, LootTable.Builder> entityLootTables = new HashMap<>();
    protected final Map<ResourceLocation, LootTable.Builder> chestLootTables = new HashMap<>();
    private final DataGenerator generator;

    public BaseLootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
        this.generator = dataGeneratorIn;
    }

    protected abstract void addTables();

    protected void addItemDropTable(EntityType<?> entityType, IItemProvider item) {
        entityLootTables.put(entityType, createItemDropTable(entityType.getRegistryName().getPath(), item));
    }

    protected void addChestLootTable(ResourceLocation id, LootTable.Builder builder) {
        chestLootTables.put(id, builder);
    }

    protected void addItemDropTable(EntityType<?> entityType, IItemProvider item,
                                    float min, float max,
                                    float lmin, float lmax) {
        entityLootTables.put(entityType, createItemDropTable(
                entityType.getRegistryName().getPath(), item, min, max, lmin, lmax));
    }

    protected LootTable.Builder createItemDropTable(String name, IItemProvider item) {
        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(item));
        return LootTable.builder().addLootPool(builder);
    }

    protected LootTable.Builder createItemDropTable(String name, IItemProvider item,
                                                    float min, float max,
                                                    float lmin, float lmax) {
        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(item)
                        .acceptFunction(SetCount
                                .builder(RandomValueRange.of(min, max)))
                        .acceptFunction(LootingEnchantBonus
                                .builder(RandomValueRange.of(lmin, lmax))));
        return LootTable.builder().addLootPool(builder);
    }

    protected LootTable.Builder createSilkTouchTable(String name, Block block, Item lootItem, float min, float max) {
        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(AlternativesLootEntry.builder(
                        ItemLootEntry.builder(block)
                                .acceptCondition(MatchTool.builder(ItemPredicate.Builder.create()
                                        .enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))))),
                        ItemLootEntry.builder(lootItem)
                                .acceptFunction(SetCount.builder(new RandomValueRange(min, max)))
                                .acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE, 1))
                                .acceptFunction(ExplosionDecay.builder())
                        )
                );
        return LootTable.builder().addLootPool(builder);
    }

    protected void addBlockStateTable(Block block, Property property) {
        lootTables.put(block, createBlockStateTable(block.getRegistryName().getPath(), block, property));
    }

    protected LootTable.Builder createBlockStateTable(String name, Block block, Property property) {
        LootPool.Builder builder = LootPool.builder()
                .name(block.getRegistryName().getPath())
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block)
                        .acceptFunction(CopyBlockState.func_227545_a_(block).func_227552_a_(property))
                );
        return LootTable.builder().addLootPool(builder);
    }

    protected void addStandardTable(Block block) {
        lootTables.put(block, createStandardTable(block.getRegistryName().getPath(), block));
    }

    protected LootTable.Builder createStandardTable(String name, Block block) {
        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block)
                        .acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
                        .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
                                .addOperation("Info", "BlockEntityTag.Info", CopyNbt.Action.REPLACE)
                                .addOperation("Items", "BlockEntityTag.Items", CopyNbt.Action.REPLACE)
                                .addOperation("Energy", "BlockEntityTag.Energy", CopyNbt.Action.REPLACE))
                        .acceptFunction(SetContents.builderIn()
                                .addLootEntry(DynamicLootEntry.func_216162_a(new ResourceLocation("minecraft", "contents"))))
                );
        return LootTable.builder().addLootPool(builder);
    }

    protected void addSimpleTable(Block block) {
        lootTables.put(block, createSimpleTable(block.getRegistryName().getPath(), block));
    }

    protected LootTable.Builder createSimpleTable(String name, Block block) {
        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block));
        return LootTable.builder().addLootPool(builder);
    }

    protected LootTable.Builder createSimpleTable(String name, Item item) {
        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(item));
        return LootTable.builder().addLootPool(builder);
    }

    @Override
    public void act(DirectoryCache cache) {
        addTables();

        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        for (Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet()) {
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParameterSet(LootParameterSets.BLOCK).build());
        }
        for (Map.Entry<EntityType<?>, LootTable.Builder> entry : entityLootTables.entrySet()) {
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParameterSet(LootParameterSets.ENTITY).build());
        }
        for (Map.Entry<ResourceLocation, LootTable.Builder> entry : chestLootTables.entrySet()) {
            ResourceLocation id = entry.getKey();
            tables.put(id, entry.getValue().setParameterSet(LootParameterSets.CHEST).build());
        }

        writeTables(cache, tables);
    }

    private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), path);
            } catch (IOException e) {
                LOGGER.error("Couldn't write loot table {}", path, e);
                throw new RuntimeException(e);
            }
        });
    }
}

