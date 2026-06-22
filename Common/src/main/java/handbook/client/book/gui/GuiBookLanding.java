package handbook.client.book.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import handbook.client.base.PersistentData;
import handbook.client.book.BookCategory;
import handbook.client.book.BookEntry;
import handbook.client.book.gui.button.GuiButtonBook;
import handbook.client.book.gui.button.GuiButtonBookResize;
import handbook.client.book.gui.button.GuiButtonCategory;
import handbook.client.book.gui.button.GuiButtonEntry;
import handbook.client.gui.GuiAdvancementsExt;
import handbook.common.book.Book;
import handbook.common.util.ColorHelper.HandbookColors;

import static handbook.client.book.gui.GuiBookEntryList.ENTRIES_IN_FIRST_PAGE;

public class GuiBookLanding extends GuiBook {

    @Nullable BookTextRenderer text;
    int loadedCategories = 0;

    final List<Button> pamphletEntryButtons = new ArrayList<>();
    List<BookEntry> entriesInPamphlet;

    public GuiBookLanding(Book book) {
        super(book, Component.translatable(book.name));
    }

    @Override
    public void init() {
        super.init();

        text = new BookTextRenderer(this, Component.translatable(book.landingText), LEFT_PAGE_X, TOP_PADDING + 25);

        boolean disableBar = !book.showProgress || !book.advancementsEnabled();

        int x = bookLeft + (disableBar ? 25 : 20);
        int y = bookTop + FULL_HEIGHT - (disableBar ? 25 : 62);
        int dist = 15;
        int pos = 0;

        // Resize
        if (maxScale > 2) {
            addRenderableWidget(new GuiButtonBookResize(this, x + (pos++) * dist, y, this::handleButtonResize));
        }

        // History
        addRenderableWidget(new GuiButtonBook(this, x + (pos++) * dist, y, 330, 31, 11, 11,
            this::handleButtonHistory, Component.translatable("handbook.gui.lexicon.button.history")));

        // Advancements
        if (book.advancementsTab != null) {
            addRenderableWidget(new GuiButtonBook(this, x + (pos++) * dist, y, 330, 20, 11,
                11, this::handleButtonAdvancements, Component.translatable("handbook.gui.lexicon.button.advancements")));
        }

        if (minecraft != null && minecraft.player != null && minecraft.player.isCreative()) {
            addRenderableWidget(new GuiButtonBook(this, x + (pos++) * dist, y, 308, 9, 11, 11,
                this::handleButtonEdit,
                Component.translatable("handbook.gui.lexicon.button.editor"),
                Component.translatable("handbook.gui.lexicon.button.editor.info").withStyle(ChatFormatting.GRAY)));
        }

        if (this.book.getContents().pamphletCategory == null) {
            int i = 0;
            List<BookCategory> categories = new ArrayList<>(book.getContents().categories.values());
            Collections.sort(categories);

            for (BookCategory category : categories) {
                if (category.getParentCategory() != null || category.shouldHide()) {
                    continue;
                }

                addCategoryButton(i, category);
                i++;
            }
            addCategoryButton(i, null);
            loadedCategories = i + 1;
        }
        else {
            entriesInPamphlet = new ArrayList<>(book.getContents().entries.values());
            entriesInPamphlet.removeIf(BookEntry::shouldHide);
            Collections.sort(entriesInPamphlet);
            buildEntryButtons();

            // yes, technically we loaded one category, but for display purposes
            // it's more like we loaded zero.
            loadedCategories = 0;
        }

    }

    private void addCategoryButton(int i, BookCategory category) {
        int x = RIGHT_PAGE_X + 10 + (i % 4) * 24;
        int y = TOP_PADDING + 25 + (i / 4) * 24;

        if (category == null) {
            addRenderableWidget(new GuiButtonCategory(this, x, y, book.getIcon(), Component.translatable("handbook.gui.lexicon.index"), this::handleButtonIndex));
        } else {
            addRenderableWidget(new GuiButtonCategory(this, x, y, category, this::handleButtonCategory));
        }
    }

    @Override
    void drawForegroundElements(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (text != null) {
            text.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        }

        int topSeparator = TOP_PADDING + 12;
        int bottomSeparator = topSeparator + 25 + 24 * ((loadedCategories - 1) / 4 + 1);

        drawHeader(guiGraphics);

        if (book.getContents().pamphletCategory == null) {
            drawCenteredStringNoShadow(guiGraphics, I18n.get("handbook.gui.lexicon.categories"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);

            drawSeparator(guiGraphics, book, RIGHT_PAGE_X, topSeparator);

            if (loadedCategories <= 16) {
                drawSeparator(guiGraphics, book, RIGHT_PAGE_X, bottomSeparator);
            }
        }

        if (book.getContents().isErrored()) {
            int x = RIGHT_PAGE_X + PAGE_WIDTH / 2;
            int y = bottomSeparator + 12;

            drawCenteredStringNoShadow(guiGraphics, I18n.get("handbook.gui.lexicon.loading_error"), x, y, HandbookColors.ERROR_RED.toColor());
            drawCenteredStringNoShadow(guiGraphics, I18n.get("handbook.gui.lexicon.loading_error_hover"), x, y + 10, HandbookColors.ERROR_GRAY.toColor());

            x -= PAGE_WIDTH / 2;
            y -= 4;

            if (isMouseInRelativeRange(mouseX, mouseY, x, y, PAGE_WIDTH, 20)) {
                makeErrorTooltip();
            }
        }

        drawProgressBar(guiGraphics, book, mouseX, mouseY, (e) -> true);
    }

    @Override
    void onPageChanged() {
        buildEntryButtons();
    }

    private void buildEntryButtons() {
        removeDrawablesIn(pamphletEntryButtons);
        pamphletEntryButtons.clear();

        maxSpreads = 1;

        addEntryButtons(RIGHT_PAGE_X, TOP_PADDING, 0, ENTRIES_IN_FIRST_PAGE);
    }

    private void addEntryButtons(int x, int y, int start, int count) {
        if (!this.book.getContents().isErrored()) {
            for (int i = 0; i < count && (i + start) < entriesInPamphlet.size(); i++) {
                Button button = new GuiButtonEntry(this, bookLeft + x, bookTop + y + i * 11, entriesInPamphlet.get(start + i),
                    this::handleButtonPamphletEntry);
                addRenderableWidget(button);
                pamphletEntryButtons.add(button);
            }
        }
    }

    private void drawHeader(GuiGraphicsExtractor guiGraphics) {
        drawFromTexture(guiGraphics, book, -8, 12, 0, 180, 140, 31);

        int color = book.nameplateColor;
        guiGraphics.text(font, book.getBookItem().getHoverName(), 13, 16, color, false);
        Component toDraw = book.getSubtitle().withStyle(book.getFontStyle());
        guiGraphics.text(font, toDraw, 24, 24, color, false);
    }

    private void makeErrorTooltip() {
        Throwable e = book.getContents().getException();

        List<Component> lines = new ArrayList<>();
        while (e != null) {
            String msg = e.getMessage();
            if (msg != null && !msg.isEmpty()) {
                lines.add(Component.literal(e.getMessage()));
            }
            e = e.getCause();
        }

        if (!lines.isEmpty()) {
            lines.add(Component.translatable("handbook.gui.lexicon.loading_error_log").withStyle(ChatFormatting.GREEN));
            setTooltip(lines);
        }
    }

    @Override
    public boolean mouseClickedScaled(MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        return text != null && text.click(mouseButtonEvent)
            || super.mouseClickedScaled(mouseButtonEvent, isDoubleClick);
    }

    public void handleButtonIndex(Button button) {
        displayLexiconGui(new GuiBookIndex(book), true);
    }

    public void handleButtonCategory(Button button) {
        GuiButtonCategory guiButtonCategory = (GuiButtonCategory) button;

        if (guiButtonCategory.getCategory() != null) {
            displayLexiconGui(new GuiBookCategory(book, guiButtonCategory.getCategory()), true);
        }
    }

    private void handleButtonHistory(Button button) {
        displayLexiconGui(new GuiBookHistory(book), true);
    }

    private void handleButtonAdvancements(Button button) {
        if (minecraft != null && minecraft.player != null) {
            minecraft.gui.setScreen(
                new GuiAdvancementsExt(
                    minecraft.player.connection.getAdvancements(),
                    this,
                    book.advancementsTab
                )
            );
        }
    }

    private void handleButtonEdit(Button button) {
        if (minecraft != null && minecraft.hasShiftDown() && minecraft.player != null) {
            long time = System.currentTimeMillis();
            book.reloadContents(minecraft.level, true);
            book.reloadLocks(false);
            displayLexiconGui(new GuiBookLanding(book), false);
            minecraft.player.sendOverlayMessage(
                Component.translatable("handbook.gui.lexicon.reloaded", (System.currentTimeMillis() - time))
            );
        }
        else {
            displayLexiconGui(new GuiBookWriter(book), true);
        }
    }

    public void handleButtonResize(Button button) {
        if (PersistentData.data.bookGuiScale >= maxScale) {
            PersistentData.data.bookGuiScale = 0;
        }
        else {
            PersistentData.data.bookGuiScale = Math.max(2, PersistentData.data.bookGuiScale + 1);
        }

        PersistentData.save();
        displayLexiconGui(this, false);
    }

    public void handleButtonPamphletEntry(Button button) {
        GuiBookEntry.displayOrBookmark(this, ((GuiButtonEntry) button).getEntry());
    }

}
