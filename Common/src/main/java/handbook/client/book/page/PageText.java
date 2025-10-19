package handbook.client.book.page;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import handbook.api.IVariable;
import handbook.client.book.gui.GuiBook;
import handbook.client.book.page.abstr.PageWithText;

public class PageText extends PageWithText {

    String title;

    public void setText(String text) {
        this.text = IVariable.wrap(text);
    }

    @Override
    public int getTextHeight() {
        if (pageNum == 0) {
            return 22;
        }

        if (title != null && !title.isEmpty()) {
            return 12;
        }

        return -4;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (pageNum == 0) {
            boolean renderedSmol = false;
            String smolText = "";

            if (mc.options.advancedItemTooltips) {
                ResourceLocation res = parent.getEntry().getId();
                smolText = res.toString();
            }
            else if (entry.getAddedBy() != null) {
                smolText = I18n.get("handbook.gui.lexicon.added_by", entry.getAddedBy());
            }

            if (!smolText.isEmpty()) {
                guiGraphics.pose().scale(0.5F, 0.5F);
                parent.drawCenteredStringNoShadow(guiGraphics, smolText, GuiBook.PAGE_WIDTH, 12, book.headerColor);
                guiGraphics.pose().scale(2F, 2F);
                renderedSmol = true;
            }

            parent.drawCenteredStringNoShadow(
                guiGraphics,
                parent.getEntry().getName().getVisualOrderText(),
                GuiBook.PAGE_WIDTH / 2,
                renderedSmol ? -3 : 0,
                book.headerColor
            );
            GuiBook.drawSeparator(guiGraphics, book, 0, 12);
        }
        else if (title != null && !title.isEmpty()) {
            parent.drawCenteredStringNoShadow(guiGraphics, i18n(title), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
        }
    }

}
