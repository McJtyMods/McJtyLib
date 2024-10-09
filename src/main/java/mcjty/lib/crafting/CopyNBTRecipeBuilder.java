package mcjty.lib.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static mcjty.lib.setup.Registration.COPYNBT_SERIALIZER;

public class CopyNBTRecipeBuilder implements IRecipeBuilder<CopyNBTRecipeBuilder> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Item result;
    private final int count;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private String group;

    public CopyNBTRecipeBuilder(ItemLike resultIn, int countIn) {
        this.result = resultIn.asItem();
        this.count = countIn;
    }

    public static CopyNBTRecipeBuilder shapedRecipe(ItemLike resultIn) {
        return shapedRecipe(resultIn, 1);
    }

    public static CopyNBTRecipeBuilder shapedRecipe(ItemLike resultIn, int countIn) {
        return new CopyNBTRecipeBuilder(resultIn, countIn);
    }

    @Override
    public CopyNBTRecipeBuilder define(Character symbol, TagKey<Item> tagIn) {
        return this.define(symbol, Ingredient.of(tagIn));
    }

    @Override
    public CopyNBTRecipeBuilder define(Character symbol, ItemLike itemIn) {
        return this.define(symbol, Ingredient.of(itemIn));
    }

    @Override
    public CopyNBTRecipeBuilder define(Character symbol, Ingredient ingredientIn) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, ingredientIn);
            return this;
        }
    }

    @Override
    public CopyNBTRecipeBuilder patternLine(String patternIn) {
        if (!this.pattern.isEmpty() && patternIn.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.pattern.add(patternIn);
            return this;
        }
    }

    public CopyNBTRecipeBuilder unlockedBy(String name, Criterion<? extends CriterionTriggerInstance> criterionIn) {
        this.advancementBuilder.addCriterion(name, criterionIn);
        return this;
    }

    @Override
    public CopyNBTRecipeBuilder setGroup(String groupIn) {
        this.group = groupIn;
        return this;
    }

    @Override
    public void build(RecipeOutput consumerIn) {
        this.build(consumerIn, BuiltInRegistries.ITEM.getKey(this.result));
    }

    @Override
    public void build(RecipeOutput consumerIn, String save) {
        ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(this.result);
        if ((ResourceLocation.parse(save)).equals(resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
        } else {
            this.build(consumerIn, ResourceLocation.parse(save));
        }
    }

    @Override
    public void build(RecipeOutput consumerIn, ResourceLocation id) {
        this.validate(id);
        ShapedRecipeBuilder builder = new ShapedRecipeBuilder(RecipeCategory.MISC, this.result, this.count);
        // @todo 1.21 NEO
//        this.advancementBuilder.parent(ResourceLocation.parse("recipes/root")).addCriterion("has_the_recipe",
//                new RecipeUnlockedTrigger.TriggerInstance(Optional.empty(), id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        String folder = ""; // @todo 1.19.3 this.result.getItemCategory().getRecipeFolderName();
        Result r = new Result(id, this.result, this.count, this.group == null ? "" : this.group, this.pattern, this.key, this.advancementBuilder, ResourceLocation.parse("recipes/root"));
        consumerIn.accept(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipe/" + folder + "/" + id.getPath()), r, null);// @todo 1.21 advancement holder
    }

    private void validate(ResourceLocation id) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        } else {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');

            for(String s : this.pattern) {
                for(int i = 0; i < s.length(); ++i) {
                    char c0 = s.charAt(i);
                    if (!this.key.containsKey(c0) && c0 != ' ') {
                        throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
                    }

                    set.remove(c0);
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
            } else if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
                throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
                // @todo 1.21
//            } else if (this.advancementBuilder.getCriteria().isEmpty()) {
//                throw new IllegalStateException("No way of obtaining recipe " + id);
            }
        }
    }

    public static class Result implements net.minecraft.world.item.crafting.Recipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation idIn, Item resultIn, int countIn, String groupIn, List<String> patternIn, Map<Character, Ingredient> keyIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
            this.id = idIn;
            this.result = resultIn;
            this.count = countIn;
            this.group = groupIn;
            this.pattern = patternIn;
            this.key = keyIn;
            this.advancementBuilder = advancementBuilderIn;
            this.advancementId = advancementIdIn;
        }


        // @todo 1.21
//        @Override
        public void serializeRecipeData(@Nonnull JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }

            JsonArray jsonarray = new JsonArray();

            for(String s : this.pattern) {
                jsonarray.add(s);
            }

            json.add("pattern", jsonarray);
            JsonObject jsonobject = new JsonObject();

            // @todo 1.21
//            for(Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
//                jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
//            }

            json.add("key", jsonobject);
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                jsonobject1.addProperty("count", this.count);
            }

            json.add("result", jsonobject1);
        }

        // @todo 1.21
        @Override
        public boolean matches(RecipeInput recipeInput, Level level) {
            return false;
        }

        // @todo 1.21

        @Override
        public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
            return null;
        }

        // @todo 1.21
        @Override
        public boolean canCraftInDimensions(int i, int i1) {
            return false;
        }

        // @todo 1.21
        @Override
        public ItemStack getResultItem(HolderLookup.Provider provider) {
            return null;
        }

        // @todo 1.21
        @Override
        public RecipeSerializer<?> getSerializer() {
            return null;
        }

        // @todo 1.21
        @Override
        public RecipeType<?> getType() {
            return null;
        }

        // @todo 1.21
//        @Override
//        @Nonnull
//        public RecipeSerializer<?> getType() {
//            return COPYNBT_SERIALIZER.get();
//        }

        /**
         * Gets the ID for the recipe.
         */
        // @todo 1.21
//        @Override
//        @Nonnull
//        public ResourceLocation getId() {
//            return this.id;
//        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        // @todo 1.21
//        @Override
//        @Nullable
//        public JsonObject serializeAdvancement() {
//            return this.advancementBuilder.serializeToJson();
//        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
         * is non-null.
         */
        // @todo 1.21
//        @Override
//        @Nullable
//        public ResourceLocation getAdvancementId() {
//            return this.advancementId;
//        }
    }
}