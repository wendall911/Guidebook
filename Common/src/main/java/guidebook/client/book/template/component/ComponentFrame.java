package guidebook.client.book.template.component;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;

import guidebook.client.book.BookContentsBuilder;
import guidebook.client.book.BookEntry;
import guidebook.client.book.BookPage;
import guidebook.client.book.gui.GuiBook;
import guidebook.client.book.template.TemplateComponent;

public class ComponentFrame extends TemplateComponent {

	@Override
	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		if (x == -1) {
			x = GuiBook.PAGE_WIDTH / 2 - 53;
		}
		if (y == -1) {
			y = 7;
		}
	}

	@Override
	public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		GuiBook.drawFromTexture(graphics, page.book, x, y, 405, 149, 106, 106);
	}

}
