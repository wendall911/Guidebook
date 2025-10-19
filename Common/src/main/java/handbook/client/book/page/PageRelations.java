package handbook.client.book.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.gui.GuiBook;
import handbook.client.book.gui.GuiBookEntry;
import handbook.client.book.gui.button.GuiButtonEntry;
import handbook.client.book.page.abstr.PageWithText;

public class PageRelations extends PageWithText {

    List<String> entries;
    String title;

    transient List<BookEntry> entryObjs;

    @Override
    public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
        super.build(level, entry, builder, pageNum);

        this.entryObjs = new ArrayList<>();
        for (String s : this.entries) {
            ResourceLocation targetId = ResourceLocation.tryParse(s);
            BookEntry targetEntry = builder.getEntry(targetId);
            if (targetEntry == null) {
                throw new IllegalArgumentException("Could not find entry " + targetId);
            }
            this.entryObjs.add(targetEntry);
        }
    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);

        List<BookEntry> displayedEntries = new ArrayList<>(entryObjs);
        displayedEntries.removeIf(BookEntry::shouldHide);
        Collections.sort(displayedEntries);
        for (int i = 0; i < displayedEntries.size(); i++) {
            Button button = new GuiButtonEntry(parent, 0, 20 + i * 11,
                displayedEntries.get(i), this::handleButtonEntry);
            addButton(button);
        }
    }

    public void handleButtonEntry(Button button) {
        GuiBookEntry.displayOrBookmark(parent, ((GuiButtonEntry) button).getEntry());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        parent.drawCenteredStringNoShadow(
            guiGraphics,
            title == null || title.isEmpty() ? I18n.get("handbook.gui.lexicon.relations") : i18n(title),
            GuiBook.PAGE_WIDTH / 2,
            0,
            book.headerColor
        );
        GuiBook.drawSeparator(guiGraphics, book, 0, 12);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public int getTextHeight() {
        return 22 + entryObjs.size() * 11;
    }

}
