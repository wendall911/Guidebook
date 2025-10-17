package guidebook.client.book.page.abstr;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

import guidebook.api.GuidebookAPI;
import guidebook.client.book.BookContentsBuilder;
import guidebook.client.book.BookEntry;
import guidebook.client.book.page.PageSmithing;
import guidebook.mixin.AccessorRecipeManager;
import guidebook.mixin.AccessorShapedRecipe;
import guidebook.mixin.AccessorShapelessRecipe;
import guidebook.mixin.AccessorSingleItemRecipe;

public abstract class PageDoubleRecipeRegistry<T extends Recipe<?>> extends PageDoubleRecipe<T> {

    private final RecipeType<? extends T> recipeType;

    public PageDoubleRecipeRegistry(RecipeType<? extends T> recipeType) {
        this.recipeType = recipeType;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private T getRecipe(ResourceLocation id) {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();

        if (server == null) {
            GuidebookAPI.LOGGER.warn("Server is null, cannot get recipe");

            return null;
        }

        AccessorRecipeManager manager = (AccessorRecipeManager) server.getRecipeManager();
        RecipeHolder<?> recipeHolder = manager.getRecipes().values().stream().filter(
            holder -> holder.id().location().equals(id) && holder.value().getType() == recipeType
        ).findFirst().orElse(null);

        return recipeHolder != null ? (T) recipeHolder.value() : null;
    }

    @Override
    protected T loadRecipe(Level level, BookContentsBuilder builder, BookEntry entry,
                           ResourceLocation res, boolean linkRecipe) {
        if (res == null || level == null) {
            return null;
        }
        T tempRecipe = getRecipe(res);

        // this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
        if (tempRecipe == null) {
            tempRecipe = getRecipe(ResourceLocation.fromNamespaceAndPath("crafttweaker", res.getPath()));
        }

        if (tempRecipe != null) {
            if (linkRecipe) {
                entry.addRelevantStack(builder, getAnyRecipeOutput(level, tempRecipe), pageNum);
            }

            return tempRecipe;
        }

        GuidebookAPI.LOGGER.warn("Recipe {} (of type {}) not found", res, BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));

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
                return ((AccessorSingleItemRecipe) recipe).getResult();
            }
            case CraftingRecipe ignored -> {
                boolean shaped = recipe instanceof ShapedRecipe;

                if (!shaped) {
                    return ((AccessorShapelessRecipe) recipe).getResult();
                }
                else {
                    return ((AccessorShapedRecipe) recipe).getResult();
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

                return smithingRecipe.assemble(recipeInput, level.registryAccess());
            }
            default -> throw new IllegalStateException("Unexpected value: " + recipe);
        }
    }

}
