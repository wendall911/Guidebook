package handbook.client.book.page.abstr;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import handbook.api.HandbookAPI;
import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;

public abstract class PageDoubleRecipeRegistry<T extends Recipe<?>> extends PageDoubleRecipe<T> {

    private final RecipeType<? extends T> recipeType;

    public PageDoubleRecipeRegistry(RecipeType<? extends T> recipeType) {
        this.recipeType = recipeType;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private T getRecipe(Level level, ResourceLocation id) {
        RecipeManager manager = level.getRecipeManager();
        RecipeHolder<?> recipeHolder = manager.byKey(id).filter(recipe -> recipe.value().getType() == recipeType).orElse(null);

        return recipeHolder != null ? (T) recipeHolder.value() : null;
    }

    @Override
    protected T loadRecipe(Level level, BookContentsBuilder builder, BookEntry entry, ResourceLocation res, boolean linkRecipe) {
        if (res == null || level == null) {
            return null;
        }

        T tempRecipe = getRecipe(level, res);
        if (tempRecipe == null) { // this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
            tempRecipe = getRecipe(level, ResourceLocation.fromNamespaceAndPath("crafttweaker", res.getPath()));
        }

        if (tempRecipe != null) {
            if (linkRecipe) {
                entry.addRelevantStack(builder, tempRecipe.getResultItem(level.registryAccess()), pageNum);
            }

            return tempRecipe;
        }

        HandbookAPI.LOGGER.warn("Recipe {} (of type {}) not found", res, BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));

        return null;
    }

}
