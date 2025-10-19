package handbook.client.book.gui;

import java.util.Collection;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import handbook.client.book.BookEntry;
import handbook.common.book.Book;

public class GuiBookIndex extends GuiBookEntryList {

    public GuiBookIndex(Book book) {
        super(book, Component.translatable("handbook.gui.lexicon.index"));
    }

    @Override
    protected String getDescriptionText() {
        return I18n.get("handbook.gui.lexicon.index.info");
    }

    @Override
    protected Collection<BookEntry> getEntries() {
        return book.getContents().entries.values();
    }

}
