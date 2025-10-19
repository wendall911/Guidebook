package handbook.client.book.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import handbook.client.base.PersistentData.Bookmark;
import handbook.client.book.BookEntry;
import handbook.client.book.gui.GuiBook;
import handbook.common.book.Book;
import handbook.common.util.ColorHelper.HandbookColors;

public class GuiButtonBookBookmark extends GuiButtonBook {

	private final Book book;

	public final Bookmark bookmark;

	public GuiButtonBookBookmark(GuiBook parent, int x, int y, Bookmark bookmark) {
		super(parent, x, y, 272, bookmark == null ? 170 : 160, 13, 10, parent::handleButtonBookmark, getTooltip(parent.book, bookmark));
		this.book = parent.book;
		this.bookmark = bookmark;
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.renderWidget(graphics, mouseX, mouseY, partialTicks);

		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);
		if (bookmark != null && entry != null) {
			int px = getX() * 2 + (isHoveredOrFocused() ? 6 : 2);
			int py = getY() * 2 + 2;

			graphics.pose().pushPose();
			graphics.pose().scale(0.5F, 0.5F, 0.5F);
			entry.getIcon().render(graphics, px, py);
			graphics.pose().popPose();
		}
	}

	private static Component[] getTooltip(Book book, Bookmark bookmark) {
		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);

		if (bookmark == null || entry == null) {
			return new Component[] {
                Component.translatable("handbook.gui.lexicon.add_bookmark")
            };
		}

		return new Component[] {
            entry.getName(),
            Component.translatable("handbook.gui.lexicon.remove_bookmark").withStyle(ChatFormatting.GRAY)
		};
	}

}
