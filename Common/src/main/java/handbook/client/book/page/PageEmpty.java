package handbook.client.book.page;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import handbook.client.book.BookPage;
import handbook.client.book.gui.GuiBook;

public class PageEmpty extends BookPage {

    @SerializedName("draw_filler") boolean filler = true;

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (filler) {
            GuiBook.drawPageFiller(guiGraphics, book, 0, 0);
        }
    }

}
