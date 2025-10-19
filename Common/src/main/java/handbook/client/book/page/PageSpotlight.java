package handbook.client.book.page;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import handbook.api.IVariable;
import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.gui.GuiBook;
import handbook.client.book.page.abstr.PageWithText;

public class PageSpotlight extends PageWithText {

    IVariable item;
    String title;
    @SerializedName("link_recipe") boolean linkRecipe;

    transient ItemStack[] stacks;

    @Override
    public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
        super.build(level, entry, builder, pageNum);
        stacks = item.as(ItemStack[].class);

        if (linkRecipe) {
            for (ItemStack stack : stacks) {
                entry.addRelevantStack(builder, stack, pageNum);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int w = 66;
        int h = 26;
        Component toDraw;

        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            book.craftingTexture,
            GuiBook.PAGE_WIDTH / 2 - w / 2,
            10,
            0,
            128 - h,
            w,
            h,
            128,
            256
        );

        if (title != null && !title.isEmpty()) {
            toDraw = i18nText(title);
        }
        else {
            toDraw = stacks[0].getHoverName();
        }

        parent.drawCenteredStringNoShadow(
            guiGraphics,
            toDraw.getVisualOrderText(),
            GuiBook.PAGE_WIDTH / 2,
            0,
            book.headerColor
        );
        if (stacks.length > 0) {
            parent.renderItemStack(
                guiGraphics,
                GuiBook.PAGE_WIDTH / 2 - 8,
                15,
                mouseX,
                mouseY,
                stacks[(parent.ticksInBook / 20) % stacks.length]
            );
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public int getTextHeight() {
        return 40;
    }

}
