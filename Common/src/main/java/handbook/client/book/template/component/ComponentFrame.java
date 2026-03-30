package handbook.client.book.template.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.BookPage;
import handbook.client.book.gui.GuiBook;
import handbook.client.book.template.TemplateComponent;

public class ComponentFrame extends TemplateComponent {

    @Override
    public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
        if (x == -1) {
            x = GuiBook.PAGE_WIDTH / 2 - 53;
        }
        if (y == -1) {
            y = 7;
        }
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, BookPage page, int mouseX, int mouseY, float partialTicks) {
        GuiBook.drawFromTexture(guiGraphics, page.book, x, y, 405, 149, 106, 106);
    }

}
