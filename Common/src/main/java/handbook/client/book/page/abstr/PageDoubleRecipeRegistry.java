package handbook.client.book.page.abstr;

import java.util.Optional;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;

import handbook.api.HandbookAPI;
import handbook.client.base.RecipeData;
import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.page.PageSmithing;
import handbook.mixin.AccessorShapedRecipe;
import handbook.mixin.AccessorShapelessRecipe;
import handbook.mixin.AccessorSingleItemRecipe;

public abstract class PageDoubleRecipeRegistry<T extends Recipe<?>> extends PageDoubleRecipe<T> {

    private final RecipeType<? extends T> recipeType;

    public PageDoubleRecipeRegistry(RecipeType<? extends T> recipeType) {
        this.recipeType = recipeType;
    }

    @Nullable
    private RecipeHolder<?> getRecipe(Identifier id) {
        return RecipeData.getRecipeHolder(id, recipeType);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T loadRecipe(Level level, BookContentsBuilder builder, BookEntry entry,
                           Identifier res, boolean linkRecipe) {
        if (res == null || level == null) {
            return null;
        }
        RecipeHolder<?> recipeHolder = getRecipe(res);

        // this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
        if (recipeHolder == null) {
            recipeHolder = getRecipe(Identifier.fromNamespaceAndPath("crafttweaker", res.getPath()));
        }

        if (recipeHolder != null) {
            T tempRecipe = (T) recipeHolder.value();

            if (linkRecipe) {
                entry.addRelevantStack(builder, getAnyRecipeOutput(level, tempRecipe), pageNum);
            }

            return tempRecipe;
        }

        HandbookAPI.LOGGER.debug("Recipe {} (of type {}) not found", res, BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));

        return null;
    }

    /*
     * This is only resolving a single stack for smithing recipes, but there
     * can be multiple outputs depending on template and base
     * Might need to be reworked later
     * Reference: https://github.com/mezz/JustEnoughItems/blob/1.21.9/Library/src/main/java/mezz/jei/library/plugins/vanilla/anvil/SmithingCategoryExtension.java#L45
     */
    protected ItemStack getAnyRecipeOutput(Level level, T recipe) {
        if (level == null) {
            return ItemStack.EMPTY;
        }

        switch (recipe) {
            case null -> {
                return ItemStack.EMPTY;
            }
            case SingleItemRecipe ignored -> {
                return ((AccessorSingleItemRecipe) recipe).getResult().create();
            }
            case CraftingRecipe ignored -> {
                boolean shaped = recipe instanceof ShapedRecipe;

                if (!shaped) {
                    return ((AccessorShapelessRecipe) recipe).getResult().create();
                }
                else {
                    return ((AccessorShapedRecipe) recipe).getResult().create();
                }
            }
            case SmithingRecipe smithingRecipe -> {
                ContextMap contextMap = SlotDisplayContext.fromLevel(level);
                Ingredient base = PageSmithing.getBase(smithingRecipe);
                Optional<Ingredient> addition = PageSmithing.getAddition(smithingRecipe);
                Optional<Ingredient> template = PageSmithing.getTemplate(smithingRecipe);
                ItemStack baseStack = base.display().resolveForFirstStack(contextMap);
                ItemStack templateStack = ItemStack.EMPTY;
                ItemStack additionStack = ItemStack.EMPTY;

                if (addition.isPresent()) {
                    additionStack = addition.get().display().resolveForFirstStack(contextMap);
                }

                if (template.isPresent()) {
                    templateStack = template.get().display().resolveForFirstStack(contextMap);
                }

                SmithingRecipeInput recipeInput = new SmithingRecipeInput(templateStack, baseStack, additionStack);

                return smithingRecipe.assemble(recipeInput);
            }
            default -> throw new IllegalStateException("Unexpected value: " + recipe);
        }
    }

}
