package handbook.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import handbook.api.HandbookAPI;
import handbook.client.book.BookContents;
import handbook.client.book.EntryDisplayState;
import handbook.client.book.gui.GuiBook;
import handbook.common.book.Book;

public class GuiButtonInventoryBook extends Button {

    private final Book book;

    public GuiButtonInventoryBook(Book book, int x, int y) {
        super(x, y, 20, 20, Component.empty(), (b) -> {
            BookContents contents = book.getContents();
            contents.openLexiconGui(contents.getCurrentGui(), false);
        }, DEFAULT_NARRATION);
        this.book = book;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float pticks) {
        boolean hovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
        ItemStack stack = book.getBookItem();
        EntryDisplayState readState = book.getContents().getReadState();

        guiGraphics.blit(
            ResourceLocation.fromNamespaceAndPath(HandbookAPI.MODID, "textures/gui/inventory_button.png"),
            getX(),
            getY(),
            (hovered ? 20 : 0),
            0,
            width,
            height,
            64,
            64
        );
        guiGraphics.renderItem(stack, getX() + 2, getY() + 2);
        guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, getX() + 2, getY() + 2);

        if (readState.hasIcon && readState.showInInventory) {
            GuiBook.drawMarking(guiGraphics, book, getX(), getY(), 0, readState);
        }
    }

    public Book getBook() {
        return book;
    }

}
