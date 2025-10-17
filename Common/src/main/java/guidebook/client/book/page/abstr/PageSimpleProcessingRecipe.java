package guidebook.client.book.page.abstr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import guidebook.client.book.gui.GuiBook;
import guidebook.mixin.AccessorSingleItemRecipe;

public abstract class PageSimpleProcessingRecipe<T extends Recipe<?>> extends PageDoubleRecipeRegistry<T> {

    public PageSimpleProcessingRecipe(RecipeType<T> recipeType) {
        super(recipeType);
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, T recipe, int recipeX, int recipeY, int mouseX,
                              int mouseY, boolean second) {
        Level level = Minecraft.getInstance().level;
        ItemStack toastSymbol = ItemStack.EMPTY;
        AccessorSingleItemRecipe recipeAccessor = (AccessorSingleItemRecipe) recipe;
        Ingredient input = recipeAccessor.getInput();
        ItemStack result = recipeAccessor.getResult();

        if (level == null) {
            return;
        }

        if (recipe.getType() == RecipeType.BLASTING) {
            toastSymbol = new ItemStack(Blocks.BLAST_FURNACE);
        }
        else if (recipe.getType() == RecipeType.CAMPFIRE_COOKING) {
            toastSymbol = new ItemStack(Blocks.CAMPFIRE);
        }
        else if (recipe.getType() == RecipeType.SMELTING) {
            toastSymbol = new ItemStack(Blocks.FURNACE);
        }
        else if (recipe.getType() == RecipeType.SMOKING) {
            toastSymbol = new ItemStack(Blocks.SMOKER);
        }
        else if (recipe.getType() == RecipeType.STONECUTTING) {
            toastSymbol = new ItemStack(Blocks.STONECUTTER);
        }

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, book.craftingTexture, recipeX, recipeY,
            11, 71, 96, 24, 128, 256);
        parent.drawCenteredStringNoShadow(guiGraphics, getTitle(second).getVisualOrderText(),
            GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

        parent.renderIngredient(guiGraphics, recipeX + 4, recipeY + 4, mouseX, mouseY, input);
        parent.renderItemStack(guiGraphics, recipeX + 40, recipeY + 4, mouseX, mouseY, toastSymbol);
        parent.renderItemStack(guiGraphics, recipeX + 76, recipeY + 4, mouseX, mouseY, result);
    }

    @Override
    protected ItemStack getRecipeOutput(Level level, T recipe) {
        if (recipe == null) {
            return ItemStack.EMPTY;
        }

        return ((AccessorSingleItemRecipe) recipe).getResult();
    }

    @Override
    protected int getRecipeHeight() {
        return 45;
    }

}
