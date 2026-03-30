package handbook.client.book.gui.button;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import handbook.client.base.ClientTicker;
import handbook.client.base.PersistentData;
import handbook.client.book.gui.GuiBook;
import handbook.common.util.ColorHelper.HandbookColors;

public class GuiButtonBookEye extends GuiButtonBook {

	public GuiButtonBookEye(GuiBook parent, int x, int y, Button.OnPress onPress) {
		super(parent, x, y, 308, 31, 11, 11, onPress,
            Component.translatable("handbook.gui.lexicon.button.visualize"),
            Component.translatable("handbook.gui.lexicon.button.visualize.info").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void extractContents(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.extractContents(guiGraphics, mouseX, mouseY, partialTicks);

		if (!PersistentData.data.clickedVisualize && (ClientTicker.ticksInGame) % 20 < 10) {
            Minecraft mc = parent.getMinecraft();

            if (mc != null) {
                guiGraphics.text(mc.font, "!", getX(), getY(), HandbookColors.BOOK_EYE.toColor(), true);
            }
		}
	}

}
