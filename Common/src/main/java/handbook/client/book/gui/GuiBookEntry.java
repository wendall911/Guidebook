package handbook.client.book.gui;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import handbook.api.IComponentRenderContext;
import handbook.client.base.PersistentData;
import handbook.client.base.PersistentData.BookData;
import handbook.client.base.PersistentData.Bookmark;
import handbook.client.book.BookEntry;
import handbook.client.book.BookPage;
import handbook.common.book.Book;

public class GuiBookEntry extends GuiBook implements IComponentRenderContext {

    protected final BookEntry entry;
    @Nullable private BookPage leftPage;
    @Nullable private BookPage rightPage;

    public GuiBookEntry(Book book, BookEntry entry) {
        this(book, entry, 0);
    }

    public GuiBookEntry(Book book, BookEntry entry, int spread) {
        super(book, entry.getName());
        this.entry = entry;
        this.spread = spread;
    }

    @Override
    public void init() {
        super.init();

        maxSpreads = (int) Math.ceil((float) entry.getPages().size() / 2);
        setupPages();
    }

    @Override
    public void onFirstOpened() {
        super.onFirstOpened();

        boolean dirty = false;
        Identifier key = entry.getId();

        BookData data = PersistentData.data.getBookData(book);

        if (!data.viewedEntries.contains(key)) {
            data.viewedEntries.add(key);
            dirty = true;
            entry.markReadStateDirty();
        }

        int index = data.history.indexOf(key);
        if (index != 0) {
            if (index > 0) {
                data.history.remove(key);
            }

            data.history.addFirst(key);
            while (data.history.size() > GuiBookEntryList.ENTRIES_PER_PAGE) {
                data.history.remove(GuiBookEntryList.ENTRIES_PER_PAGE);
            }

            dirty = true;
        }

        if (dirty) {
            PersistentData.save();
        }
    }

    @Override
    void drawForegroundElements(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        drawPage(guiGraphics, leftPage, mouseX, mouseY, partialTicks);
        drawPage(guiGraphics, rightPage, mouseX, mouseY, partialTicks);

        if (rightPage == null) {
            drawPageFiller(guiGraphics, entry.getBook());
        }
    }

    @Override
    public boolean mouseClickedScaled(MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        return clickPage(leftPage, mouseButtonEvent)
                || clickPage(rightPage, mouseButtonEvent)
                || super.mouseClickedScaled(mouseButtonEvent, isDoubleClick);
    }

    void drawPage(GuiGraphicsExtractor guiGraphics, @Nullable BookPage page, int mouseX, int mouseY, float pticks) {
        if (page == null) {
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(page.left, page.top);
        page.render(guiGraphics, mouseX - page.left, mouseY - page.top, pticks);
        guiGraphics.pose().popMatrix();
    }

    private boolean clickPage(@Nullable BookPage page, MouseButtonEvent mouseButtonEvent) {
        if (page != null) {
            MouseButtonEvent pageEvent = new MouseButtonEvent(
                mouseButtonEvent.x() - page.left,
                mouseButtonEvent.y() - page.top,
                mouseButtonEvent.buttonInfo()
            );

            return page.mouseClicked(pageEvent);
        }

        return false;
    }

    @Override
    void onPageChanged() {
        setupPages();
        needsBookmarkUpdate = true;
    }

    private void setupPages() {
        if (leftPage != null) {
            leftPage.onHidden(this);
        }
        if (rightPage != null) {
            rightPage.onHidden(this);
        }

        List<BookPage> pages = entry.getPages();
        int leftNum = spread * 2;
        int rightNum = (spread * 2) + 1;

        leftPage = leftNum < pages.size() ? pages.get(leftNum) : null;
        rightPage = rightNum < pages.size() ? pages.get(rightNum) : null;

        if (leftPage != null) {
            leftPage.onDisplayed(this, LEFT_PAGE_X, TOP_PADDING);
        }
        if (rightPage != null) {
            rightPage.onDisplayed(this, RIGHT_PAGE_X, TOP_PADDING);
        }
    }

    public BookEntry getEntry() {
        return entry;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof GuiBookEntry && ((GuiBookEntry) obj).entry == entry && ((GuiBookEntry) obj).spread == spread);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entry) * 31 + Objects.hashCode(spread);
    }

    @Override
    public boolean canBeOpened() {
        return !entry.isLocked() && !equals(Minecraft.getInstance().screen);
    }

    @Override
    protected boolean shouldAddAddBookmarkButton() {
        return !isBookmarkedAlready();
    }

    boolean isBookmarkedAlready() {
        if (entry == null || entry.getId() == null) {
            return false;
        }

        Identifier entryKey = entry.getId();
        BookData data = PersistentData.data.getBookData(book);

        for (Bookmark bookmark : data.bookmarks) {
            if (bookmark.entry.equals(entryKey) && bookmark.spread == spread) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void bookmarkThis() {
        Identifier entryKey = entry.getId();
        BookData data = PersistentData.data.getBookData(book);
        data.bookmarks.add(new Bookmark(entryKey, spread));
        PersistentData.save();
        needsBookmarkUpdate = true;
    }

    public static void displayOrBookmark(GuiBook currGui, BookEntry entry) {
        Book book = currGui.book;
        GuiBookEntry gui = new GuiBookEntry(currGui.book, entry);
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.hasShiftDown()) {
            BookData data = PersistentData.data.getBookData(book);

            if (gui.isBookmarkedAlready()) {
                Identifier key = entry.getId();
                data.bookmarks.removeIf((bm) -> bm.entry.equals(key) && bm.spread == 0);
                PersistentData.save();
                currGui.needsBookmarkUpdate = true;
                return;
            } else if (data.bookmarks.size() < MAX_BOOKMARKS) {
                gui.bookmarkThis();
                currGui.needsBookmarkUpdate = true;
                return;
            }
        }

        book.getContents().openLexiconGui(gui, true);
    }

    @Override
    public Screen getGui() {
        return this;
    }

    @Override
    public Style getFontStyle() {
        return book.getFontStyle();
    }

    @Override
    public void renderItemStack(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        guiGraphics.item(stack, x, y);
        guiGraphics.itemDecorations(font, stack, x, y);

        if (isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16)) {
            setTooltipStack(stack);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderIngredient(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, Ingredient ingredient) {
        ItemStack[] stacks = ingredient.items().map(itemHolder -> new ItemStack(itemHolder.value())).toArray(ItemStack[]::new);

        if (stacks.length > 0) {
            renderItemStack(guiGraphics, x, y, mouseX, mouseY, stacks[(ticksInBook / 20) % stacks.length]);
        }
    }

    @Override
    public void setHoverTooltip(List<String> tooltip) {
        setTooltip(tooltip.stream().map(Component::literal).collect(Collectors.toList()));
    }

    @Override
    public void setHoverTooltipComponents(@NotNull List<Component> tooltip) {
        setTooltip(tooltip);
    }

    @Override
    public boolean isAreaHovered(int mouseX, int mouseY, int x, int y, int w, int h) {
        return isMouseInRelativeRange(mouseX, mouseY, x, y, w, h);
    }

    @Override
    public boolean navigateToEntry(Identifier entry, int page, boolean push) {
        BookEntry bookEntry = book.getContents().entries.get(entry);

        if (bookEntry != null && !bookEntry.isLocked()) {
            displayLexiconGui(new GuiBookEntry(book, bookEntry, page), push);
            return true;
        }

        return false;
    }

    @SuppressWarnings("removal")
    @Override
    public void registerButton(Button button, int pageNum, Runnable onClick) {
        addWidget(button, pageNum);
    }

    @Override
    public void addWidget(AbstractWidget widget, int pageNum) {
        widget.setX(widget.getX() + (bookLeft + ((pageNum % 2) == 0 ? LEFT_PAGE_X : RIGHT_PAGE_X)));
        widget.setY(widget.getY() + bookTop);
        addRenderableWidget(widget);
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent keyEvent) {
        if (Minecraft.getInstance().options.keyInventory.matches(keyEvent)) {
            this.onClose();

            return true;
        }

        return super.keyPressed(keyEvent);
    }

    @Override
    protected boolean shouldAddMarkReadButton() {
        return false;
    }

    @Override
    public Identifier getBookTexture() {
        return book.bookTexture;
    }

    @Override
    public Identifier getCraftingTexture() {
        return book.craftingTexture;
    }

    @Override
    public int getTextColor() {
        return book.textColor;
    }

    @Override
    public int getHeaderColor() {
        return book.headerColor;
    }

    @Override
    public int getTicksInBook() {
        return ticksInBook;
    }

}
