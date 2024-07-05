package mcjty.lib.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mcjty.lib.api.power.ItemEnergy;
import mcjty.lib.setup.Registration;
import mcjty.lib.varia.Tools;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    protected void addStandardTable(Block block, BlockEntityType<?> type) {
        lootTables.put(block, createStandardTable(Tools.getId(block).getPath(), block, type));
    }


    protected LootTable.Builder createStandardTable(String name, Block block, BlockEntityType<?> type) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block)
                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                .include(Registration.ITEM_ENERGY.get())
//                                .include(Registration.INFO.get()) @todo 1.21: how to do generic info?
                                .include(DataComponents.CONTAINER))
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

    // @todo 1.19.3
//    @Override
//    public void run(CachedOutput cache) {
//        addTables();
//
//        Map<ResourceLocation, LootTable> tables = new HashMap<>();
//        for (Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet()) {
//            tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootContextParamSets.BLOCK).build());
//        }
//        for (Map.Entry<EntityType<?>, LootTable.Builder> entry : entityLootTables.entrySet()) {
//            tables.put(entry.getKey().getDefaultLootTable(), entry.getValue().setParamSet(LootContextParamSets.ENTITY).build());
//        }
//        for (Map.Entry<ResourceLocation, LootTable.Builder> entry : chestLootTables.entrySet()) {
//            ResourceLocation id = entry.getKey();
//            tables.put(id, entry.getValue().setParamSet(LootContextParamSets.CHEST).build());
//        }
//
//        writeTables(cache, tables);
//    }

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

