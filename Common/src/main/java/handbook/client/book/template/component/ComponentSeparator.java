package handbook.client.book.template.component;

import net.minecraft.client.gui.GuiGraphics;

import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.BookPage;
import handbook.client.book.gui.GuiBook;
import handbook.client.book.template.TemplateComponent;

public class ComponentSeparator extends TemplateComponent {

    @Override
    public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
        if (x == -1) {
            x = 0;
        }
        if (y == -1) {
            y = 12;
        }
    }

    @Override
    public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
        GuiBook.drawSeparator(graphics, page.book, x, y);
    }

}
