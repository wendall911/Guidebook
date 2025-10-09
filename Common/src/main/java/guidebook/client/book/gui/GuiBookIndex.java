package guidebook.client.book.gui;

import java.util.Collection;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import guidebook.client.book.BookEntry;
import guidebook.common.book.Book;

public class GuiBookIndex extends GuiBookEntryList {

    public GuiBookIndex(Book book) {
        super(book, Component.translatable("guidebook.gui.lexicon.index"));
    }

    @Override
    protected String getDescriptionText() {
        return I18n.get("guidebook.gui.lexicon.index.info");
    }

    @Override
    protected Collection<BookEntry> getEntries() {
        return book.getContents().entries.values();
    }

}
