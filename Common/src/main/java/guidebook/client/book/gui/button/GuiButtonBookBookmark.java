package guidebook.client.book.gui.button;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import guidebook.client.base.PersistentData.Bookmark;
import guidebook.client.book.BookEntry;
import guidebook.client.book.gui.GuiBook;
import guidebook.common.book.Book;

public class GuiButtonBookBookmark extends GuiButtonBook {

	private final Book book;

	public final Bookmark bookmark;

	public GuiButtonBookBookmark(GuiBook parent, int x, int y, Bookmark bookmark) {
		super(parent, x, y, 272, bookmark == null ? 170 : 160, 13, 10, parent::handleButtonBookmark, getTooltip(parent.book, bookmark));
		this.book = parent.book;
		this.bookmark = bookmark;
	}

	@Override
	public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);

		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);
		if (bookmark != null && entry != null) {
            int px = getX() * 2 + (isHoveredOrFocused() ? 6 : 2);
            int py = getY() * 2 + 2;

			guiGraphics.pose().pushMatrix();
			guiGraphics.pose().scale(0.5F, 0.5F);
			entry.getIcon().render(guiGraphics, px, py);
			guiGraphics.pose().popMatrix();
		}
	}

	private static Component[] getTooltip(Book book, Bookmark bookmark) {
		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);

		if (bookmark == null || entry == null) {
			return new Component[] {
                Component.translatable("guidebook.gui.lexicon.add_bookmark")
            };
		}

		return new Component[] {
            entry.getName(),
            Component.translatable("guidebook.gui.lexicon.remove_bookmark").withStyle(ChatFormatting.GRAY)
		};
	}

}
