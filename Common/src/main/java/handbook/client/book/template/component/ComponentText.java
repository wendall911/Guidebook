package handbook.client.book.template.component;

import java.util.function.UnaryOperator;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;

import handbook.api.IVariable;
import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.BookPage;
import handbook.client.book.gui.BookTextRenderer;
import handbook.client.book.gui.GuiBook;
import handbook.client.book.gui.GuiBookEntry;
import handbook.client.book.template.TemplateComponent;

public class ComponentText extends TemplateComponent {

    public IVariable text;

    @SerializedName("color") public IVariable colorStr;

    @SerializedName("max_width") int maxWidth = GuiBook.PAGE_WIDTH;
    @SerializedName("line_height") int lineHeight = GuiBook.TEXT_LINE_HEIGHT;

    transient Component actualText;
    transient BookTextRenderer textRenderer;
    transient int color;

    @Override
    public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
        try {
            color = Integer.parseInt(colorStr.asString(""), 16);
        }
        catch (NumberFormatException e) {
            color = page.book.textColor;
        }
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        super.onVariablesAvailable(lookup, registries);
        actualText = lookup.apply(text).as(Component.class);
        colorStr = lookup.apply(colorStr);
    }

    @Override
    public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
        textRenderer = new BookTextRenderer(parent, actualText, x, y, maxWidth, lineHeight, color);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, BookPage page, int mouseX, int mouseY, float partialTicks) {
        textRenderer.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(BookPage page, MouseButtonEvent mouseButtonEvent) {
        return textRenderer.click(mouseButtonEvent);
    }

}
