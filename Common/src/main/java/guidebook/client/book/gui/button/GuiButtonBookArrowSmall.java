package guidebook.client.book.gui.button;

import java.util.function.Supplier;

import net.minecraft.network.chat.Component;

import guidebook.client.book.gui.GuiBook;

public class GuiButtonBookArrowSmall extends GuiButtonBook {

	public final boolean left;

	public GuiButtonBookArrowSmall(GuiBook parent, int x, int y, boolean left,
                                   Supplier<Boolean> displayCondition, OnPress onPress) {
		super(parent, x, y, 272, left ? 27 : 20, 5, 7, displayCondition, onPress,
            Component.translatable(left ?
                "guidebook.gui.lexicon.button.prev_page" : "guidebook.gui.lexicon.button.next_page"));
		this.left = left;
	}

}
