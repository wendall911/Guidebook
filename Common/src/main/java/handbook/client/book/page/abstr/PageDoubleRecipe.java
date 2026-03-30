package handbook.client.book.page.abstr;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.gui.GuiBook;

public abstract class PageDoubleRecipe<T> extends PageWithText {

    @SerializedName("recipe") Identifier recipeId;
    @SerializedName("recipe2") Identifier recipe2Id;
    @SerializedName("link_recipe") boolean linkRecipe = true;
    @SerializedName("link_recipe2") boolean linkRecipe2 = true;
    String title;

    protected transient T recipe1, recipe2;
    protected transient Component title1, title2;

    @Override
    public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
        super.build(level, entry, builder, pageNum);

        recipe1 = loadRecipe(level, builder, entry, recipeId, linkRecipe);
        recipe2 = loadRecipe(level, builder, entry, recipe2Id, linkRecipe2);

        if (recipe1 == null && recipe2 != null) {
            recipe1 = recipe2;
            recipe2 = null;
        }

        boolean customTitle = title != null && !title.isEmpty();
        title1 = !customTitle ? getRecipeOutput(level, recipe1).getHoverName() : i18nText(title);
        title2 = Component.literal("-");
        if (recipe2 != null) {
            title2 = !customTitle ? getRecipeOutput(level, recipe2).getHoverName() : Component.empty();
            if (title1.equals(title2)) {
                title2 = Component.empty();
            }
        }
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (recipe1 != null) {
            int recipeX = getX();
            int recipeY = getY();
            drawRecipe(guiGraphics, recipe1, recipeX, recipeY, mouseX, mouseY, false);

            if (recipe2 != null) {
                drawRecipe(
                    guiGraphics,
                    recipe2,
                    recipeX,
                    recipeY + getRecipeHeight() - (title2.getString().isEmpty() ? 10 : 0),
                    mouseX,
                    mouseY,
                    true
                );
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public int getTextHeight() {
        return getY() + getRecipeHeight() * (recipe2 == null ? 1 : 2) - (title2.getString().isEmpty() ? 23 : 13);
    }

    @Override
    public boolean shouldRenderText() {
        return getTextHeight() + 10 < GuiBook.PAGE_HEIGHT;
    }

    protected abstract void drawRecipe(GuiGraphicsExtractor graphics, T recipe, int recipeX, int recipeY,
                                       int mouseX, int mouseY, boolean second);

    protected abstract T loadRecipe(Level level, BookContentsBuilder builder, BookEntry entry,
                                    Identifier loc, boolean linkRecipe);

    protected abstract ItemStack getRecipeOutput(Level level, T recipe);

    protected abstract int getRecipeHeight();

    protected int getX() {
        return GuiBook.PAGE_WIDTH / 2 - 49;
    }

    protected int getY() {
        return 4;
    }

    protected Component getTitle(boolean second) {
        return second ? title2 : title1;
    }

}
