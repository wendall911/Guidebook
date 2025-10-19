package handbook.client.book.gui;

import java.util.Collection;
import java.util.stream.Collectors;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import handbook.client.base.PersistentData;
import handbook.client.base.PersistentData.BookData;
import handbook.client.book.BookEntry;
import handbook.common.book.Book;

public class GuiBookHistory extends GuiBookEntryList {

    public GuiBookHistory(Book book) {
        super(book, Component.translatable("handbook.gui.lexicon.history"));
    }

    @Override
    protected String getDescriptionText() {
        return I18n.get("handbook.gui.lexicon.history.info");
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
