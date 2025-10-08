package guidebook.client.book.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import guidebook.client.base.ClientTicker;
import guidebook.client.base.PersistentData;
import guidebook.client.book.gui.GuiBook;

public class GuiButtonBookEye extends GuiButtonBook {

	public GuiButtonBookEye(GuiBook parent, int x, int y, Button.OnPress onPress) {
		super(parent, x, y, 308, 31, 11, 11, onPress,
				Component.translatable("guidebook.gui.lexicon.button.visualize"),
				Component.translatable("guidebook.gui.lexicon.button.visualize.info").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.renderWidget(graphics, mouseX, mouseY, partialTicks);

		if (!PersistentData.data.clickedVisualize && (ClientTicker.ticksInGame) % 20 < 10) {
			graphics.drawString(parent.getMinecraft().font, "!", getX(), getY(), 0xFF3333, true);
		}
	}

}
