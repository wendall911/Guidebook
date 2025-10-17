package guidebook.client.book.page;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import guidebook.client.book.gui.GuiBook;
import guidebook.client.book.page.abstr.PageDoubleRecipeRegistry;
import guidebook.mixin.AccessorSmithingTransformRecipe;
import guidebook.mixin.AccessorSmithingTrimRecipe;

public class PageSmithing extends PageDoubleRecipeRegistry<SmithingRecipe> {

    public PageSmithing() {
        super(RecipeType.SMITHING);
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, SmithingRecipe recipe, int recipeX, int recipeY,
                              int mouseX, int mouseY, boolean second) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        // Manually setting this, as I'm not sure how to get it now
        ItemStack symbol = new ItemStack(Blocks.SMITHING_TABLE);
        Ingredient base = getBase(recipe);
        Optional<Ingredient> additional = getAddition(recipe);
        Optional<Ingredient> template = getTemplate(recipe);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, book.craftingTexture, recipeX, recipeY,
            11, 135, 96, 43, 128, 256);
        parent.drawCenteredStringNoShadow(guiGraphics, getTitle(second).getVisualOrderText(),
            GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

        parent.renderIngredient(guiGraphics, recipeX + 4, recipeY + 4, mouseX, mouseY, base);
        additional.ifPresent(ingredient -> parent.renderIngredient(
            guiGraphics,
            recipeX + 4,
            recipeY + 23,
            mouseX,
            mouseY,
            ingredient
        ));
        template.ifPresent(ingredient -> parent.renderIngredient(
            guiGraphics,
            recipeX + 40,
            recipeY + 4,
            mouseX,
            mouseY,
            ingredient
        ));
        parent.renderItemStack(guiGraphics, recipeX + 40, recipeY + 20, mouseX, mouseY, symbol);
        parent.renderItemStack(guiGraphics, recipeX + 76, recipeY + 13,
            mouseX, mouseY, getRecipeOutput(level, recipe));
    }

    public static Ingredient getBase(SmithingRecipe recipe) {
        if (recipe instanceof SmithingTrimRecipe) {
            return ((AccessorSmithingTrimRecipe) recipe).getBase();
        }
        if (recipe instanceof SmithingTransformRecipe) {
            return ((AccessorSmithingTransformRecipe) recipe).getBase();
        }

        return Ingredient.of(ItemStack.EMPTY.getItem());
    }

    public static Optional<Ingredient> getAddition(SmithingRecipe recipe) {
        if (recipe instanceof SmithingTrimRecipe) {
            return Optional.of(((AccessorSmithingTrimRecipe) recipe).getAddition());
        }
        if (recipe instanceof SmithingTransformRecipe) {
            return ((AccessorSmithingTransformRecipe) recipe).getAddition();
        }

        return Optional.of(Ingredient.of(ItemStack.EMPTY.getItem()));
    }

    public static Optional<Ingredient> getTemplate(SmithingRecipe recipe) {
        if (recipe instanceof SmithingTrimRecipe) {
            return Optional.of(((AccessorSmithingTrimRecipe) recipe).getTemplate());
        }
        if (recipe instanceof SmithingTransformRecipe) {
            return ((AccessorSmithingTransformRecipe) recipe).getTemplate();
        }

        return Optional.of(Ingredient.of(ItemStack.EMPTY.getItem()));
    }

    @Override
    protected ItemStack getRecipeOutput(Level level, SmithingRecipe recipe) {
        return getAnyRecipeOutput(level, recipe);
    }

    @Override
    protected int getRecipeHeight() {
        return 60;
    }

}
