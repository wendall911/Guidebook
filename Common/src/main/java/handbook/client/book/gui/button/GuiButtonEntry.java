package handbook.client.book.gui.button;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import handbook.client.base.ClientTicker;
import handbook.client.book.BookEntry;
import handbook.client.book.gui.GuiBook;
import handbook.common.util.ColorHelper;

public class GuiButtonEntry extends Button {

    private static final int ANIM_TIME = 5;

    private final GuiBook parent;
    private final BookEntry entry;
    private float timeHovered;

    public GuiButtonEntry(GuiBook parent, int x, int y, BookEntry entry, Button.OnPress onPress) {
        super(x, y, GuiBook.PAGE_WIDTH, 10, entry.getName(), onPress, DEFAULT_NARRATION);
        this.parent = parent;
        this.entry = entry;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (!active) {
            return;
        }
        if (isHoveredOrFocused()) {
            timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
        }
        else {
            timeHovered = Math.max(0, timeHovered - ClientTicker.delta);
        }

        float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (isHoveredOrFocused() ? partialTicks : -partialTicks)));
        float widthFract = time / ANIM_TIME;
        boolean locked = entry.isLocked();

        guiGraphics.pose().scale(0.5F, 0.5F);
        guiGraphics.fill(
            getX() * 2,
            getY() * 2,
            (getX() + (int) ((float) width * widthFract)) * 2,
            (getY() + height) * 2,
            ColorHelper.fillBlack(0.12F)
        );

        if (locked) {
            GuiBook.drawLock(guiGraphics, parent.book, getX() * 2 + 2, getY() * 2 + 2);
        }
        else {
            entry.getIcon().render(guiGraphics, getX() * 2 + 2, getY() * 2 + 2);
        }

        guiGraphics.pose().scale(2F, 2F);

        MutableComponent name;
        if (locked) {
            name = Component.translatable("handbook.gui.lexicon.locked");
        } else {
            name = entry.getName();
            if (entry.isPriority()) {
                name = name.withStyle(ChatFormatting.ITALIC);
            }
        }

        name = name.withStyle(entry.getBook().getFontStyle());
        guiGraphics.drawString(Minecraft.getInstance().font, name, getX() + 12, getY(), getColor(), false);

        if (!entry.isLocked()) {
            GuiBook.drawMarking(guiGraphics, parent.book, getX() + width - 5, getY() + 1, entry.hashCode(), entry.getReadState());
        }
    }

    private int getColor() {
        if (entry.isSecret()) {
            return ColorHelper.getSecret(parent.book.textColor);
        }
        else if (entry.isLocked()) {
            return ColorHelper.getLocked(parent.book.textColor);
        }

        return entry.getEntryColor();
    }

    @Override
    public void playDownSound(@NotNull SoundManager soundHandlerIn) {
        if (entry != null && !entry.isLocked()) {
            GuiBook.playBookFlipSound(parent.book);
        }
    }

    public BookEntry getEntry() {
        return entry;
    }

}
