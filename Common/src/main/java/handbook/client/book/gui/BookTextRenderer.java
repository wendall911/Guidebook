package handbook.client.book.gui;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.PlainTextContents;

import handbook.client.book.text.BookTextParser;
import handbook.client.book.text.TextLayouter;
import handbook.client.book.text.Word;
import handbook.common.book.Book;
import handbook.config.HandbookConfig;

public class BookTextRenderer implements Renderable {

    private final Book book;

    private final BookTextParser parser;
    private final TextLayouter layouter;
    private List<Word> words;
    private float scale;

    public BookTextRenderer(GuiBook gui, Component text, int x, int y) {
        this(gui, text, x, y, GuiBook.PAGE_WIDTH, GuiBook.TEXT_LINE_HEIGHT, gui.book.textColor);
    }

    public BookTextRenderer(GuiBook gui, Component text, int x, int y, int width, int lineHeight, int baseColor) {
        this.book = gui.book;

        Style baseStyle = book.getFontStyle().withColor(TextColor.fromRgb(baseColor));
        HandbookConfig.TextOverflowMode overflowMode = this.book.overflowMode;

        this.parser = new BookTextParser(gui, this.book, x, y, width, lineHeight, baseStyle);

        if (overflowMode == null) {
            overflowMode = HandbookConfig.Client.overflowMode();
        }

        this.layouter = new TextLayouter(gui, x, y, lineHeight, width, overflowMode);

        setText(text);
    }

    void setText(Component text) {
        Component text1;

        if (this.book.i18n && text.getContents() instanceof PlainTextContents.LiteralContents(String text2)) {
            text1 = Component.literal(I18n.get(text2));
        }
        else {
            text1 = text;
        }

        this.layouter.layout(Minecraft.getInstance().font, this.parser.parse(text1));
        this.scale = this.layouter.getScale();
        this.words = this.layouter.getWords();
    }

    /*
     * Simulates inverse scaling to get mouse coordinates relative to unscaled text.
     * TODO: Figure out if I can use existing matrix stack methods for this.
     */
    private double rescale(double in, double origin) {
        return origin + (in - origin) / scale;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (!words.isEmpty()) {
            Font font = Minecraft.getInstance().font;
            Style style = book.getFontStyle();
            Word first = words.getFirst();
            int scaledX = (int) rescale(mouseX, first.x);
            int scaledY = (int) rescale(mouseY, first.y);

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(first.x, first.y);
            guiGraphics.pose().scale(scale, scale);
            guiGraphics.pose().translate(-first.x, -first.y);
            words.forEach(word -> word.render(guiGraphics, font, style, scaledX, scaledY));
            guiGraphics.pose().popMatrix();
        }
    }

    public boolean click(MouseButtonEvent mouseButtonEvent) {
        if (!words.isEmpty()) {
            for (Word word : words) {
                if (word.click()) {
                    return true;
                }
            }
        }

        return false;
    }

}
