package handbook.client.book.page.abstr;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import handbook.api.IVariable;
import handbook.client.book.BookPage;
import handbook.client.book.gui.BookTextRenderer;
import handbook.client.book.gui.GuiBookEntry;

public abstract class PageWithText extends BookPage {

    protected IVariable text;

    transient BookTextRenderer textRender;

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);

        if (text == null) {
            text = IVariable.wrap("");
        }

        textRender = new BookTextRenderer(parent, text.as(Component.class), 0, getTextHeight());
    }

    public abstract int getTextHeight();

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (shouldRenderText()) {
            textRender.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent) {
        return shouldRenderText() && textRender.click(mouseButtonEvent);
    }

    public boolean shouldRenderText() {
        return true;
    }

}
