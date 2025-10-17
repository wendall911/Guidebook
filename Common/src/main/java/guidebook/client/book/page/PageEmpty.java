package guidebook.client.book.page;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphics;

import guidebook.client.book.BookPage;
import guidebook.client.book.gui.GuiBook;

public class PageEmpty extends BookPage {

    @SerializedName("draw_filler") boolean filler = true;

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (filler) {
            GuiBook.drawPageFiller(guiGraphics, book, 0, 0);
        }
    }

}
