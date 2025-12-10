package handbook.client.book.page;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import handbook.client.base.ClientAdvancements;
import handbook.client.base.PersistentData;
import handbook.client.base.PersistentData.BookData;
import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.gui.GuiBook;
import handbook.client.book.gui.GuiBookEntry;
import handbook.client.book.page.abstr.PageWithText;
import handbook.common.book.Book;
import handbook.common.util.ColorHelper.HandbookColors;

public class PageQuest extends PageWithText {

    Identifier trigger;
    String title;

    transient boolean isManual;

    @Override
    public int getTextHeight() {
        return 22;
    }

    @Override
    public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
        super.build(level, entry, builder, pageNum);

        isManual = trigger == null;
    }

    public boolean isCompleted(Book book) {
        return isManual
                ? PersistentData.data.getBookData(book).completedManualQuests.contains(entry.getId())
                : trigger != null && ClientAdvancements.hasDone(trigger.toString());
    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);

        if (isManual) {
            Button button = Button.builder(
                Component.empty(),
                this::questButtonClicked).pos(
                    GuiBook.PAGE_WIDTH / 2 - 50,
                    GuiBook.PAGE_HEIGHT - 35
                ).size(100, 20).build();
            addButton(button);
            updateButtonText(button);
        }
    }

    private void updateButtonText(Button button) {
        boolean completed = isCompleted(parent.book);
        Component s = Component.translatable(completed ?
            "handbook.gui.lexicon.mark_incomplete" : "handbook.gui.lexicon.mark_complete");
        button.setMessage(s);
    }

    protected void questButtonClicked(Button button) {
        Identifier entryId = entry.getId();
        BookData data = PersistentData.data.getBookData(parent.book);

        if (data.completedManualQuests.contains(entryId)) {
            data.completedManualQuests.remove(entryId);
        }
        else {
            data.completedManualQuests.add(entryId);
        }
        PersistentData.save();

        updateButtonText(button);
        entry.markReadStateDirty();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        parent.drawCenteredStringNoShadow(
            guiGraphics,
            title == null || title.isEmpty() ? I18n.get("handbook.gui.lexicon.objective") : i18n(title),
            GuiBook.PAGE_WIDTH / 2,
            0,
            book.headerColor
        );
        GuiBook.drawSeparator(guiGraphics, book, 0, 12);

        if (!isManual) {
            GuiBook.drawSeparator(guiGraphics, book, 0, GuiBook.PAGE_HEIGHT - 25);

            boolean completed = isCompleted(parent.book);
            String s = I18n.get(completed ? "handbook.gui.lexicon.complete" : "handbook.gui.lexicon.incomplete");
            int color = completed ? HandbookColors.COMPLETE.toColor() : book.headerColor;

            parent.drawCenteredStringNoShadow(guiGraphics, s,
                GuiBook.PAGE_WIDTH / 2, GuiBook.PAGE_HEIGHT - 17, color);
        }

    }

}
