package handbook.client.book.gui.button;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import handbook.client.base.ClientTicker;
import handbook.client.book.BookCategory;
import handbook.client.book.BookIcon;
import handbook.client.book.gui.GuiBook;

public class GuiButtonCategory extends Button {

	private static final int ANIM_TIME = 5;

	private final GuiBook parent;
	@Nullable private BookCategory category;
	private final BookIcon icon;
	private final Component name;
	private final int u, v;
	private float timeHovered;

	public GuiButtonCategory(GuiBook parent, int x, int y, BookCategory category, Button.OnPress onPress) {
		this(parent, x, y, category.getIcon(), category.getName(), onPress);
		this.category = category;
	}

	public GuiButtonCategory(GuiBook parent, int x, int y, BookIcon icon, Component name, Button.OnPress onPress) {
		super(parent.bookLeft + x, parent.bookTop + y, 20, 20, name, onPress, DEFAULT_NARRATION);
		this.parent = parent;
		this.u = x;
		this.v = y;
		this.icon = icon;
		this.name = name;
	}

	@Override
	public void extractContents(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if (active) {
			if (isHoveredOrFocused()) {
				timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
			} else {
				timeHovered = Math.max(0, timeHovered - ClientTicker.delta);
			}

			float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (isHoveredOrFocused() ? partialTicks : -partialTicks)));
			float transparency = 0.5F - (time / ANIM_TIME) * 0.5F;
			boolean locked = category != null && category.isLocked();

			if (locked) {
				GuiBook.drawLock(guiGraphics, parent.book, getX() + 2, getY() + 2);
			}
            else {
				icon.render(guiGraphics, getX() + 2, getY() + 2);
			}

			guiGraphics.pose().pushMatrix();
			guiGraphics.pose().translate(0, 0);
			GuiBook.drawFromTexture(guiGraphics, parent.book, getX(), getY(), u, v, width, height, transparency);

			if (category != null && !category.isLocked()) {
				GuiBook.drawMarking(guiGraphics, parent.book, getX(), getY(), 0, category.getReadState());
			}
			guiGraphics.pose().popMatrix();

			if (isHoveredOrFocused()) {
				parent.setTooltip(locked
						? Component.translatable("handbook.gui.lexicon.locked").withStyle(ChatFormatting.GRAY)
						: name);
			}
		}
	}

	@Override
	public void playDownSound(@NonNull SoundManager soundHandlerIn) {
		if (category != null && !category.isLocked()) {
			GuiBook.playBookFlipSound(parent.book);
		}
	}

	public @Nullable BookCategory getCategory() {
		return category;
	}

}
