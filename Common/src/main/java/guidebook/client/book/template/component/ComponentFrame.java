package guidebook.client.book.template.component;

import net.minecraft.client.gui.GuiGraphics;

import guidebook.client.book.BookContentsBuilder;
import guidebook.client.book.BookEntry;
import guidebook.client.book.BookPage;
import guidebook.client.book.gui.GuiBook;
import guidebook.client.book.template.TemplateComponent;

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
    public void render(GuiGraphics guiGraphics, BookPage page, int mouseX, int mouseY, float partialTicks) {
        GuiBook.drawFromTexture(guiGraphics, page.book, x, y, 405, 149, 106, 106);
    }

}
