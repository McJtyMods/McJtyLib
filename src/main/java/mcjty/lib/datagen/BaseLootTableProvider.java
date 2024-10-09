package mcjty.lib.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mcjty.lib.setup.Registration;
import mcjty.lib.varia.Tools;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class BaseLootTableProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    protected final Map<EntityType<?>, LootTable.Builder> entityLootTables = new HashMap<>();
    protected final Map<ResourceLocation, LootTable.Builder> chestLootTables = new HashMap<>();

    protected final Map<Block, LootTable.Builder> lootTables = new HashMap<>();

    public void addLootTable(Block block, LootTable.Builder builder) {
        lootTables.put(block, builder);
    }

    public void addItemDropTable(EntityType<?> entityType, ItemLike item) {
        entityLootTables.put(entityType, createItemDropTable(Tools.getId(entityType).getPath(), item));
    }

    public void addChestLootTable(ResourceLocation id, LootTable.Builder builder) {
        chestLootTables.put(id, builder);
    }

    public void addItemDropTable(HolderLookup.Provider provider, EntityType<?> entityType, ItemLike item,
                                 float min, float max,
                                 float lmin, float lmax) {
        entityLootTables.put(entityType, createItemDropTable(provider,
                Tools.getId(entityType).getPath(), item, min, max, lmin, lmax));
    }

    public LootTable.Builder createItemDropTable(String name, ItemLike item) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item));
        return LootTable.lootTable().withPool(builder);
    }

    public LootTable.Builder createItemDropTable(HolderLookup.Provider provider, String name, ItemLike item,
                                                 float min, float max,
                                                 float lmin, float lmax) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction
                                .setCount(UniformGenerator.between(min, max)))
                        .apply(EnchantedCountIncreaseFunction
                                .lootingMultiplier(provider, UniformGenerator.between(lmin, lmax))));
        return LootTable.lootTable().withPool(builder);
    }

    public LootTable.Builder createSilkTouchTable(HolderLookup.Provider provider, String name, Block block, Item lootItem, float min, float max) {
        Optional<Holder.Reference<Enchantment>> silkTouch = provider.lookup(Registries.ENCHANTMENT).get().get(Enchantments.SILK_TOUCH);
        Optional<Holder.Reference<Enchantment>> fortune = provider.lookup(Registries.ENCHANTMENT).get().get(Enchantments.FORTUNE);
        EnchantmentPredicate silkTouchPredicate = new EnchantmentPredicate(silkTouch.get(), MinMaxBounds.Ints.atLeast(1));
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(AlternativesEntry.alternatives(
                                LootItem.lootTableItem(block)
                                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                                .withSubPredicate(ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(List.of(silkTouchPredicate))))),
                                LootItem.lootTableItem(lootItem)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                        .apply(ApplyBonusCount.addUniformBonusCount(fortune.get(), 1))
                                        .apply(ApplyExplosionDecay.explosionDecay())
                        )
                );
        return LootTable.lootTable().withPool(builder);
    }

    protected void addBlockStateTable(Block block, Property<?> property) {
        lootTables.put(block, createBlockStateTable(Tools.getId(block).getPath(), block, property));
    }

    protected LootTable.Builder createBlockStateTable(String name, Block block, Property<?> property) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(Tools.getId(block).getPath())
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block)
                        .apply(CopyBlockState.copyState(block).copy(property))
                );
        return LootTable.lootTable().withPool(builder);
    }

    protected void addStandardTable(Block block, DataComponentType<?>... types) {
        lootTables.put(block, createStandardTable(Tools.getId(block).getPath(), block, types));
    }


    protected LootTable.Builder createStandardTable(String name, Block block, DataComponentType<?>... types) {
        CopyComponentsFunction.Builder copyBuilder = CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                .include(Registration.ITEM_ENERGY.get())
                .include(Registration.ITEM_INVENTORY.get());
        for (DataComponentType<?> type : types) {
            copyBuilder.include(type);
        }
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block)
                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                        .apply(copyBuilder)
                );
        return LootTable.lootTable().withPool(builder);
    }

    protected void addSimpleTable(Block block) {
        lootTables.put(block, createSimpleTable(Tools.getId(block).getPath(), block));
    }

    protected LootTable.Builder createSimpleTable(String name, Block block) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block));
        return LootTable.lootTable().withPool(builder);
    }

    protected LootTable.Builder createSimpleTable(String name, Item item) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item));
        return LootTable.lootTable().withPool(builder);
    }

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> builder, LootContextParamSet paramSet) {
//        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        if (paramSet == LootContextParamSets.BLOCK) {
            for (Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet()) {
                builder.accept(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootContextParamSets.BLOCK));
//            tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootContextParamSets.BLOCK).build());
            }
        }
        if (paramSet == LootContextParamSets.ENTITY) {
            for (Map.Entry<EntityType<?>, LootTable.Builder> entry : entityLootTables.entrySet()) {
                builder.accept(entry.getKey().getDefaultLootTable(), entry.getValue().setParamSet(LootContextParamSets.ENTITY));
//            tables.put(entry.getKey().getDefaultLootTable(), entry.getValue().setParamSet(LootContextParamSets.ENTITY).build());
            }
        }
        if (paramSet == LootContextParamSets.CHEST) {
            for (Map.Entry<ResourceLocation, LootTable.Builder> entry : chestLootTables.entrySet()) {
                ResourceLocation id = entry.getKey();
                ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, id.withPrefix("chests/"));
                builder.accept(key, entry.getValue().setParamSet(LootContextParamSets.CHEST));
//            tables.put(id, entry.getValue().setParamSet(LootContextParamSets.CHEST).build());
            }
        }
    }

//    private void writeTables(CachedOutput cache, Map<ResourceLocation, LootTable> tables) {
//        Path outputFolder = this.generator.getOutputFolder();
//        tables.forEach((key, lootTable) -> {
//            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
//            try {
//                DataProvider.saveStable(cache, LootTables.serialize(lootTable), path);
//            } catch (IOException e) {
//                LOGGER.error("Couldn't write loot table {}", path, e);
//                throw new RuntimeException(e);
//            }
//        });
//    }
}

