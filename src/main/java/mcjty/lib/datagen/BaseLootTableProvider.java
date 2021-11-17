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

import javax.annotation.Nonnull;
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
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(item));
        return LootTable.lootTable().withPool(builder);
    }

    protected LootTable.Builder createItemDropTable(String name, IItemProvider item,
                                                    float min, float max,
                                                    float lmin, float lmax) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(item)
                        .apply(SetCount
                                .setCount(RandomValueRange.between(min, max)))
                        .apply(LootingEnchantBonus
                                .lootingMultiplier(RandomValueRange.between(lmin, lmax))));
        return LootTable.lootTable().withPool(builder);
    }

    protected LootTable.Builder createSilkTouchTable(String name, Block block, Item lootItem, float min, float max) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantRange.exactly(1))
                .add(AlternativesLootEntry.alternatives(
                        ItemLootEntry.lootTableItem(block)
                                .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                        .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))))),
                        ItemLootEntry.lootTableItem(lootItem)
                                .apply(SetCount.setCount(new RandomValueRange(min, max)))
                                .apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1))
                                .apply(ExplosionDecay.explosionDecay())
                        )
                );
        return LootTable.lootTable().withPool(builder);
    }

    protected void addBlockStateTable(Block block, Property<?> property) {
        lootTables.put(block, createBlockStateTable(block.getRegistryName().getPath(), block, property));
    }

    protected LootTable.Builder createBlockStateTable(String name, Block block, Property<?> property) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(block.getRegistryName().getPath())
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(block)
                        .apply(CopyBlockState.copyState(block).copy(property))
                );
        return LootTable.lootTable().withPool(builder);
    }

    protected void addStandardTable(Block block) {
        lootTables.put(block, createStandardTable(block.getRegistryName().getPath(), block));
    }

    protected LootTable.Builder createStandardTable(String name, Block block) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(block)
                        .apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
                        .apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
                                .copy("Info", "BlockEntityTag.Info", CopyNbt.Action.REPLACE)
                                .copy("Items", "BlockEntityTag.Items", CopyNbt.Action.REPLACE)
                                .copy("Energy", "BlockEntityTag.Energy", CopyNbt.Action.REPLACE))
                        .apply(SetContents.setContents()
                                .withEntry(DynamicLootEntry.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
                );
        return LootTable.lootTable().withPool(builder);
    }

    protected void addSimpleTable(Block block) {
        lootTables.put(block, createSimpleTable(block.getRegistryName().getPath(), block));
    }

    protected LootTable.Builder createSimpleTable(String name, Block block) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(block));
        return LootTable.lootTable().withPool(builder);
    }

    protected LootTable.Builder createSimpleTable(String name, Item item) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(item));
        return LootTable.lootTable().withPool(builder);
    }

    @Override
    public void run(@Nonnull DirectoryCache cache) {
        addTables();

        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        for (Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet()) {
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootParameterSets.BLOCK).build());
        }
        for (Map.Entry<EntityType<?>, LootTable.Builder> entry : entityLootTables.entrySet()) {
            tables.put(entry.getKey().getDefaultLootTable(), entry.getValue().setParamSet(LootParameterSets.ENTITY).build());
        }
        for (Map.Entry<ResourceLocation, LootTable.Builder> entry : chestLootTables.entrySet()) {
            ResourceLocation id = entry.getKey();
            tables.put(id, entry.getValue().setParamSet(LootParameterSets.CHEST).build());
        }

        writeTables(cache, tables);
    }

    private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, LootTableManager.serialize(lootTable), path);
            } catch (IOException e) {
                LOGGER.error("Couldn't write loot table {}", path, e);
                throw new RuntimeException(e);
            }
        });
    }
}

