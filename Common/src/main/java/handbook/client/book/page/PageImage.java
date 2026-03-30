package handbook.client.book.page;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import handbook.client.book.gui.GuiBook;
import handbook.client.book.gui.GuiBookEntry;
import handbook.client.book.gui.button.GuiButtonBookArrowSmall;
import handbook.client.book.page.abstr.PageWithText;
import handbook.common.util.ColorHelper;

public class PageImage extends PageWithText {

    Identifier[] images;
    String title;
    boolean border;

    transient int index;

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);

        int x = 90;
        int y = 100;
        addButton(new GuiButtonBookArrowSmall(parent, x, y, true, () -> index > 0, this::handleButtonArrow));
        addButton(new GuiButtonBookArrowSmall(parent, x + 10, y, false,
            () -> index < images.length - 1, this::handleButtonArrow));
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

        int x = GuiBook.PAGE_WIDTH / 2 - 53;
        int y = 7;
        guiGraphics.pose().scale(0.5F, 0.5F);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, images[index],
            x * 2 + 6, y * 2 + 6, 0, 0, 200, 200, 200, 200);
        guiGraphics.pose().scale(2F, 2F);

        if (border) {
            GuiBook.drawFromTexture(guiGraphics, book, x, y, 405, 149, 106, 106);
        }

        if (title != null && !title.isEmpty()) {
            parent.drawCenteredStringNoShadow(guiGraphics, i18n(title),
                GuiBook.PAGE_WIDTH / 2, -3, book.headerColor);
        }

        if (images.length > 1 && border) {
            int xs = x + 83;
            int ys = y + 92;
            guiGraphics.fill(xs, ys, xs + 20, ys + 11, ColorHelper.fillBlack(0.17F));
            guiGraphics.fill(xs - 1, ys - 1, xs + 20, ys + 11,
                ColorHelper.fillBlack(0.17F));
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public void handleButtonArrow(Button button) {
        boolean left = ((GuiButtonBookArrowSmall) button).left;
        if (left) {
            index--;
        }
        else {
            index++;
        }
    }

    @Override
    public int getTextHeight() {
        return 120;
    }

}
