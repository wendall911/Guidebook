package guidebook.client.book.gui;

import java.util.Collection;
import java.util.stream.Collectors;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import guidebook.client.base.PersistentData;
import guidebook.client.base.PersistentData.BookData;
import guidebook.client.book.BookEntry;
import guidebook.common.book.Book;

public class GuiBookHistory extends GuiBookEntryList {

    public GuiBookHistory(Book book) {
        super(book, Component.translatable("guidebook.gui.lexicon.history"));
    }

    @Override
    protected String getDescriptionText() {
        return I18n.get("guidebook.gui.lexicon.history.info");
    }

    @Override
    protected boolean shouldDrawProgressBar() {
        return false;
    }

    @Override
    protected boolean shouldSortEntryList() {
        return false;
    }

    @Override
    protected Collection<BookEntry> getEntries() {
        BookData data = PersistentData.data.getBookData(book);

        return data.history.stream()
                .map((res) -> book.getContents().entries.get(res))
                .filter((e) -> e != null && !e.isLocked())
                .collect(Collectors.toList());
    }

}
