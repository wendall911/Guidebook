package handbook.client.book.gui.button;

import net.minecraft.network.chat.Component;

import handbook.client.book.gui.GuiBook;

public class GuiButtonBookArrow extends GuiButtonBook {

	public final boolean left;

	public GuiButtonBookArrow(GuiBook parent, int x, int y, boolean left) {
		super(parent, x, y, 272, left ? 10 : 0, 18, 10, () -> parent.canSeePageButton(left),
            parent::handleButtonArrow,
            Component.translatable(left ?
                "handbook.gui.lexicon.button.prev_page" : "handbook.gui.lexicon.button.next_page"));
		this.left = left;
	}

}
