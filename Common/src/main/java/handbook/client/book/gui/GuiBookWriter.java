package handbook.client.book.gui;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import handbook.api.HandbookAPI;
import handbook.client.book.gui.button.GuiButtonBook;
import handbook.common.book.Book;

public class GuiBookWriter extends GuiBook {

    private BookTextRenderer text, editableText;
    private EditBox textfield;

    private static String savedText = "";
    private static boolean drawHeader;

    public GuiBookWriter(Book book) {
        super(book, Component.empty());
    }

    @Override
    public void init() {
        super.init();

        this.text = new BookTextRenderer(this, Component.translatable("handbook.gui.lexicon.editor.info"), LEFT_PAGE_X, TOP_PADDING + 20);
        this.textfield = new EditBox(font, 15, FULL_HEIGHT - 40, PAGE_WIDTH, 20, textfield, Component.empty());
        this.textfield.setMaxLength(Integer.MAX_VALUE);
        if (this.textfield.getValue().isEmpty()) {
            this.textfield.setValue(savedText);
        }
        this.editableText = new BookTextRenderer(this, Component.literal(""), RIGHT_PAGE_X, TOP_PADDING + (drawHeader ? 22 : -4));

        addRenderableWidget(new GuiButtonBook(this, bookLeft + 115, bookTop + PAGE_HEIGHT - 36, 330, 9, 11, 11, this::handleToggleHeaderButton, Component.translatable("handbook.gui.lexicon.button.toggle_mock_header")));
        refreshText();
    }

    @Override
    void drawForegroundElements(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.drawForegroundElements(guiGraphics, mouseX, mouseY, partialTicks);

        drawCenteredStringNoShadow(guiGraphics, I18n.get("handbook.gui.lexicon.editor"), LEFT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
        drawSeparator(guiGraphics, book, LEFT_PAGE_X, TOP_PADDING + 12);

        if (drawHeader) {
            drawCenteredStringNoShadow(guiGraphics, I18n.get("handbook.gui.lexicon.editor.mock_header"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
            drawSeparator(guiGraphics, book, RIGHT_PAGE_X, TOP_PADDING + 12);
        }

        textfield.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        text.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        editableText.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClickedScaled(MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        MouseButtonEvent relevantEvent = new MouseButtonEvent(
            getRelativeX(mouseButtonEvent.x()),
            getRelativeY(mouseButtonEvent.y()),
            mouseButtonEvent.buttonInfo()
        );

        if (textfield.mouseClicked(relevantEvent, isDoubleClick)) {
            textfield.setFocused(true);

            return true;
        }
        else if (text.click(mouseButtonEvent)) {
            return true;
        }
        else if (editableText.click(mouseButtonEvent)) {
            return true;
        }

        return super.mouseClickedScaled(mouseButtonEvent, isDoubleClick);
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent keyEvent) {
        if (textfield.keyPressed(keyEvent)) {
            refreshText();

            return true;
        }

        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean charTyped(@NotNull CharacterEvent charEvent) {
        if (textfield.charTyped(charEvent)) {
            refreshText();

            return true;
        }

        return super.charTyped(charEvent);
    }

    private void handleToggleHeaderButton(Button button) {
        drawHeader = !drawHeader;
        init();
    }

    private void refreshText() {
        savedText = textfield.getValue();
        try {
            editableText.setText(Component.literal(savedText));
        }
        catch (Throwable e) {
            editableText.setText(Component.literal("[ERROR]"));
            HandbookAPI.LOGGER.error(e.getMessage(), e);
        }
    }
}
