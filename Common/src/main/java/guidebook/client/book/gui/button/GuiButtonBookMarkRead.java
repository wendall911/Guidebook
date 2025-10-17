package guidebook.client.book.gui.button;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import guidebook.client.base.PersistentData;
import guidebook.client.book.BookCategory;
import guidebook.client.book.BookEntry;
import guidebook.client.book.EntryDisplayState;
import guidebook.client.book.gui.GuiBook;
import guidebook.common.book.Book;
import guidebook.common.util.ColorHelper.GuidebookColors;

public class GuiButtonBookMarkRead extends GuiButtonBook {

	private final Book book;

	public GuiButtonBookMarkRead(GuiBook parent, int x, int y) {
		super(parent, x, y, 308, 31, 11, 11, button -> {}, getTooltip(parent.book));

		this.book = parent.book;
	}

	@Override
	public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int px = getX() + 1;
		int py = (int) (getY() + 0.5);
        Minecraft mc = parent.getMinecraft();

        if (mc == null) {
            return;
        }

		GuiBook.drawFromTexture(guiGraphics, book, getX(), getY(), 285, 160, 13, 10);
		GuiBook.drawFromTexture(guiGraphics, book, px, py, u, v, width, height);

		if (isHoveredOrFocused()) {
			GuiBook.drawFromTexture(guiGraphics, book, px, py, u + 11, v, width, height);
			parent.setTooltip(getTooltipLines());
		}

		guiGraphics.drawString(parent.getMinecraft().font, "+", px, py, GuidebookColors.BOOKMARK_READ.toColor(), true);
	}

	@Override
	public void onPress(@NotNull InputWithModifiers input) {
		for (BookEntry entry : this.book.getContents().entries.values()) {
			if (isMainPage(this.book)) {
				markEntry(entry);
			}
            else {
				markCategoryAsRead(entry, entry.getCategory(), this.book.getContents().entries.size());
			}
		}
	}

	private void markCategoryAsRead(BookEntry entry, BookCategory category, int maxRecursion) {
		if (category.getName().equals(this.book.getContents().getCurrentGui().getTitle())) {
			markEntry(entry);
		}
        else if (!category.isRootCategory() && maxRecursion > 0) {
            @Nullable BookCategory parentEntry = entry.getCategory().getParentCategory();

            if (parentEntry != null) {
                markCategoryAsRead(entry, entry.getCategory().getParentCategory(), maxRecursion - 1);
            }
		}
	}

	private void markEntry(BookEntry entry) {
		boolean dirty = false;
        ResourceLocation key = entry.getId();

		if (!entry.isLocked() && entry.getReadState().equals(EntryDisplayState.UNREAD)) {
			PersistentData.BookData data = PersistentData.data.getBookData(book);

			if (!data.viewedEntries.contains(key)) {
				data.viewedEntries.add(key);
				dirty = true;
				entry.markReadStateDirty();
			}
		}

		if (dirty) {
			PersistentData.save();
		}
	}

	private static Component getTooltip(Book book) {
		String text = isMainPage(book) ? "guidebook.gui.lexicon.button.mark_all_read" : "guidebook.gui.lexicon.button.mark_category_read";

		return Component.translatable(text);
	}

	private static boolean isMainPage(Book book) {
		return !book.getContents().currentGui.canSeeBackButton();
	}

}
