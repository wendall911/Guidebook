package handbook.client.book.text;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import handbook.client.book.gui.GuiBook;
import handbook.common.book.Book;

/**
 * A {@code Word} is the smallest textual unit of rendering in Handbook, and knows its
 * position, dimensions, and formatting.
 */
public class Word {

    private final Book book;
    private final GuiBook gui;
    private final Component text;
    private final List<Word> linkCluster;
    private final int linkClusterWidth;
    private final Supplier<Boolean> onClick;
    public final int x, y, width, height;
    private boolean hovered = false;

    public Word(GuiBook gui, Span span, MutableComponent text, int x, int y, int strWidth, int lineHeight, List<Word> cluster) {
        int clusterWidth = 0;

        this.book = gui.book;
        this.gui = gui;
        this.x = x;
        this.y = y;
        this.width = strWidth + 2;
        this.height = lineHeight + 2;
        this.onClick = span.onClick;
        this.linkCluster = cluster;
        if (!span.tooltip.getString().isEmpty()) {
            text = text.withStyle(s -> s.withHoverEvent(new HoverEvent.ShowText(span.tooltip)));
        }
        this.text = text;

        if (linkCluster != null) {
            for (Word word : linkCluster) {
                clusterWidth += word.width;
            }

            if (clusterWidth < this.width) {
                clusterWidth = this.width;
            }
            else {
                clusterWidth += 2;
            }
        }
        else {
            clusterWidth = this.width;
        }

        this.linkClusterWidth = clusterWidth;
    }

    public void render(GuiGraphics guiGraphics, Font font, Style styleOverride, int scaledX, int scaledY) {
        MutableComponent toRender = text.copy().withStyle(styleOverride);

        guiGraphics.drawString(font, toRender, this.x, this.y, -1, false);

        if (isClusterHovered(scaledX, scaledY)) {
            if (onClick != null) {

                hovered = true;

                guiGraphics.renderComponentHoverEffect(
                    font,
                    text.getStyle(),
                    gui.currentBookMouseX,
                    gui.currentBookMouseY
                );

                toRender.withStyle(s -> s.withColor(TextColor.fromRgb(book.linkHoverColor)));
            }
        }
        else if(onClick != null) {
            hovered = false;
        }
    }

    public boolean click() {
        if (onClick != null && hovered) {
            return onClick.get();
        }

        return false;
    }

    private boolean isClusterHovered(double mouseX, double mouseY) {
        return gui.isMouseInRelativeRange(mouseX, mouseY, x, y, linkClusterWidth, height);
    }

}
