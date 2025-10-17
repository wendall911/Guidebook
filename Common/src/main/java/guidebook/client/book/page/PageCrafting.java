package guidebook.client.book.page;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import guidebook.client.book.gui.GuiBook;
import guidebook.client.book.page.abstr.PageDoubleRecipeRegistry;
import guidebook.mixin.AccessorShapelessRecipe;
import guidebook.mixin.AccessorShapedRecipe;

public class PageCrafting extends PageDoubleRecipeRegistry<Recipe<?>> {

    public PageCrafting() {
        super(RecipeType.CRAFTING);
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, Recipe<?> recipe, int recipeX, int recipeY,
                              int mouseX, int mouseY, boolean second) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        ItemStack toastSymbol = new ItemStack(Blocks.CRAFTING_TABLE);
        List<Ingredient> ingredients;
        ItemStack result;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, book.craftingTexture, recipeX - 2, recipeY - 2,
            0, 0, 100, 62, 128, 256);

        boolean shaped = recipe instanceof ShapedRecipe;

        if (!shaped) {
            int iconX = recipeX + 62;
            int iconY = recipeY + 2;

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, book.craftingTexture, iconX, iconY,
                0, 64, 11, 11, 128, 256);

            if (parent.isMouseInRelativeRange(mouseX, mouseY, iconX, iconY, 11, 11)) {
                parent.setTooltip(Component.translatable("guidebook.gui.lexicon.shapeless"));
            }

            AccessorShapelessRecipe shapelessRecipe = (AccessorShapelessRecipe) recipe;

            ingredients = shapelessRecipe.getIngredients();
            result = shapelessRecipe.getResult();
        }
        else {
            List<Ingredient> shapedIngredients = new ArrayList<>();

            ((ShapedRecipe) recipe).getIngredients().forEach(optionalIngredient -> {
                optionalIngredient.ifPresent(shapedIngredients::add);
            });
            ingredients = shapedIngredients;
            result = ((AccessorShapedRecipe) recipe).getResult();
        }

        parent.drawCenteredStringNoShadow(guiGraphics, getTitle(second).getVisualOrderText(),
            GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

        parent.renderItemStack(guiGraphics, recipeX + 79, recipeY + 22, mouseX, mouseY, result);

        int wrap = 3;
        if (shaped) {
            wrap = ((ShapedRecipe) recipe).getWidth();
        }

        for (int i = 0; i < ingredients.size(); i++) {
            parent.renderIngredient(guiGraphics, recipeX + (i % wrap) * 19 + 3,
                recipeY + (i / wrap) * 19 + 3, mouseX, mouseY, ingredients.get(i));
        }

        parent.renderItemStack(guiGraphics, recipeX + 79, recipeY + 41, mouseX, mouseY, toastSymbol);
    }

    @Override
    protected int getRecipeHeight() {
        return 78;
    }

    @Override
    protected ItemStack getRecipeOutput(Level level, Recipe<?> recipe) {
        return getAnyRecipeOutput(level, recipe);
    }

}
