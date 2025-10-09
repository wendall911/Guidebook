package guidebook.client.book.page;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import guidebook.api.IVariable;
import guidebook.client.book.BookContentsBuilder;
import guidebook.client.book.BookEntry;
import guidebook.client.book.gui.GuiBook;
import guidebook.client.book.gui.GuiBookEntry;

public class PageLink extends PageText {

    String url;
    @SerializedName("link_text") IVariable linkText;

    transient Component realText;

    @Override
    public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
        super.build(level, entry, builder, pageNum);
        realText = linkText.as(Component.class);
    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);

        addButton(Button.builder(i18nText(realText.getString()), (b) -> GuiBook.openWebLink(parent, url)).pos(GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 35).size(100, 20).build());
    }

}
