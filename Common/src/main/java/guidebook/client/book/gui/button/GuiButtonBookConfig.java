package guidebook.client.book.gui.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import guidebook.client.book.gui.GuiBook;

public class GuiButtonBookConfig extends GuiButtonBook {

	public GuiButtonBookConfig(GuiBook parent, int x, int y, Button.OnPress onPress) {
		super(parent, x, y, 308, 20, 11, 11, onPress,
				Component.translatable("guidebook.gui.lexicon.button.config"));
	}

}
