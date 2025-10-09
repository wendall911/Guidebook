package guidebook.client.book.page;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.Level;

import guidebook.client.book.BookContentsBuilder;
import guidebook.client.book.BookEntry;
import guidebook.client.book.BookPage;
import guidebook.client.book.gui.GuiBookEntry;
import guidebook.client.book.template.BookTemplate;
import guidebook.client.book.template.JsonVariableWrapper;

public class PageTemplate extends BookPage {

    private transient BookTemplate template = null;
    private transient boolean resolved = false;

    @Override
    public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
        super.build(level, entry, builder, pageNum);

        if (!resolved) {
            template = BookTemplate.createTemplate(book, builder, type, null);
            resolved = true;
        }

        JsonVariableWrapper wrapper = new JsonVariableWrapper(sourceObject);

        template.compile(level, builder, wrapper);
        template.build(builder, this, entry, pageNum);
    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);

        template.onDisplayed(this, parent, left, top);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float pticks) {
        template.render(graphics, this, mouseX, mouseY, pticks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return template.mouseClicked(this, mouseX, mouseY, mouseButton);
    }

}
