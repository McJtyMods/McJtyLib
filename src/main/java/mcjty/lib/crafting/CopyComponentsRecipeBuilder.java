package mcjty.lib.crafting;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class CopyComponentsRecipeBuilder implements IRecipeBuilder<CopyComponentsRecipeBuilder> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Item result;
    private final int count;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private ShapedRecipeBuilder builder;

    private CopyComponentsRecipeBuilder(ItemLike resultIn, int countIn) {
        this.result = resultIn.asItem();
        this.count = countIn;
        builder = new ShapedRecipeBuilder(RecipeCategory.MISC, this.result, this.count);
    }

    public static CopyComponentsRecipeBuilder shapedRecipe(ItemLike resultIn) {
        return shapedRecipe(resultIn, 1);
    }

    public static CopyComponentsRecipeBuilder shapedRecipe(ItemLike resultIn, int countIn) {
        return new CopyComponentsRecipeBuilder(resultIn, countIn);
    }

    @Override
    public CopyComponentsRecipeBuilder define(Character symbol, TagKey<Item> tagIn) {
        builder = builder.define(symbol, Ingredient.of(tagIn));
        return this;
    }

    @Override
    public CopyComponentsRecipeBuilder define(Character symbol, ItemLike itemIn) {
        builder = builder.define(symbol, Ingredient.of(itemIn));
        return this;
    }

    @Override
    public CopyComponentsRecipeBuilder define(Character symbol, Ingredient ingredientIn) {
        builder = builder.define(symbol, ingredientIn);
        return this;
    }

    @Override
    public CopyComponentsRecipeBuilder patternLine(String patternIn) {
        builder = builder.pattern(patternIn);
        return this;
    }

    public CopyComponentsRecipeBuilder unlockedBy(String name, Criterion<? extends CriterionTriggerInstance> criterionIn) {
        builder = builder.unlockedBy(name, criterionIn);
        return this;
    }

    @Override
    public CopyComponentsRecipeBuilder setGroup(String groupIn) {
        builder = builder.group(groupIn);
        return this;
    }

    @Override
    public void build(RecipeOutput consumerIn) {
        this.build(consumerIn, BuiltInRegistries.ITEM.getKey(this.result));
    }

    @Override
    public void build(RecipeOutput consumerIn, String save) {
        Advancement.Builder advancementBuilder = this.advancementBuilder;
        builder.save(new RecipeOutput() {
            @Override
            public Advancement.Builder advancement() {
                return advancementBuilder;
            }

            @Override
            public void accept(ResourceLocation resourceLocation, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder, ICondition... iConditions) {
                consumerIn.accept(resourceLocation, new CopyComponentsRecipe((ShapedRecipe) recipe), advancementHolder, iConditions);
            }
        }, save);
    }

    @Override
    public void build(RecipeOutput consumerIn, ResourceLocation id) {
        Advancement.Builder advancementBuilder = this.advancementBuilder;
        builder.save(new RecipeOutput() {
            @Override
            public Advancement.Builder advancement() {
                return advancementBuilder;
            }

            @Override
            public void accept(ResourceLocation resourceLocation, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder, ICondition... iConditions) {
                consumerIn.accept(resourceLocation, new CopyComponentsRecipe((ShapedRecipe) recipe), advancementHolder, iConditions);
            }
        }, id);
    }
}