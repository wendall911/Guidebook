package guidebook.client.book.gui.button;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import guidebook.client.base.ClientTicker;
import guidebook.client.base.PersistentData;
import guidebook.client.book.gui.GuiBook;
import guidebook.common.util.ColorHelper.GuidebookColors;

public class GuiButtonBookEye extends GuiButtonBook {

	public GuiButtonBookEye(GuiBook parent, int x, int y, Button.OnPress onPress) {
		super(parent, x, y, 308, 31, 11, 11, onPress,
            Component.translatable("guidebook.gui.lexicon.button.visualize"),
            Component.translatable("guidebook.gui.lexicon.button.visualize.info").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);

		if (!PersistentData.data.clickedVisualize && (ClientTicker.ticksInGame) % 20 < 10) {
            Minecraft mc = parent.getMinecraft();

            if (mc != null) {
                guiGraphics.drawString(mc.font, "!", getX(), getY(), GuidebookColors.BOOK_EYE.toColor(), true);
            }
		}
	}

}
