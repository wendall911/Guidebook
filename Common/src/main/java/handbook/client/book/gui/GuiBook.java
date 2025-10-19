package handbook.client.book.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import org.lwjgl.glfw.GLFW;

import handbook.client.base.ClientTicker;
import handbook.client.base.PersistentData;
import handbook.client.base.PersistentData.Bookmark;
import handbook.client.book.BookCategory;
import handbook.client.book.BookEntry;
import handbook.client.book.EntryDisplayState;
import handbook.client.book.gui.button.GuiButtonBook;
import handbook.client.book.gui.button.GuiButtonBookArrow;
import handbook.client.book.gui.button.GuiButtonBookBookmark;
import handbook.client.book.gui.button.GuiButtonBookMarkRead;
import handbook.client.jei.HandbookJeiPlugin;
import handbook.common.base.HandbookSounds;
import handbook.common.book.Book;
import handbook.common.util.ColorHelper.HandbookColors;
import handbook.mixin.client.AccessorScreen;
import handbook.platform.Services;

import static technology.roughness.whitenoise.platform.Services.PLATFORM;

public abstract class GuiBook extends Screen {

    public static final int FULL_WIDTH = 272;
    public static final int FULL_HEIGHT = 180;
    public static final int PAGE_WIDTH = 116;
    public static final int PAGE_HEIGHT = 156;
    public static final int TOP_PADDING = 18;
    public static final int LEFT_PAGE_X = 15;
    public static final int RIGHT_PAGE_X = 141;
    public static final int TEXT_LINE_HEIGHT = 9;
    public static final int MAX_BOOKMARKS = 10;

    public final Book book;

    private static long lastSound;
    public int bookLeft, bookTop;
    private float scaleFactor;

    @Nullable private List<Component> tooltip;
    @Nullable private ItemStack tooltipStack;
    @Nullable private Pair<BookEntry, Integer> targetPage;
    protected int spread = 0, maxSpreads = 0;

    public int ticksInBook;
    public int maxScale;

    public int currentBookMouseX = 0, currentBookMouseY = 0;

    protected boolean needsBookmarkUpdate = false;

    public GuiBook(Book book, Component title) {
        super(title);

        this.book = book;
    }

    @Override
    public void init() {
        if (getMinecraft() == null) {
            return;
        }

        Window res = getMinecraft().getWindow();
        int oldGuiScale = res.calculateScale(getMinecraft().options.guiScale().get(), getMinecraft().isEnforceUnicode());

        maxScale = getMaxAllowedScale();
        int persistentScale = Math.min(PersistentData.data.bookGuiScale, maxScale);
        int newGuiScale = res.calculateScale(persistentScale, getMinecraft().isEnforceUnicode());

        if (persistentScale > 0 && newGuiScale != oldGuiScale) {
            scaleFactor = (float) newGuiScale / (float) res.getGuiScale();

            res.setGuiScale(newGuiScale);
            width = res.getGuiScaledWidth();
            height = res.getGuiScaledHeight();
            res.setGuiScale(oldGuiScale);
        }
        else {
            scaleFactor = 1;
        }

        bookLeft = width / 2 - FULL_WIDTH / 2;
        bookTop = height / 2 - FULL_HEIGHT / 2;

        book.getContents().currentGui = this;

        addRenderableWidget(new GuiButtonBook(
            this,
            width / 2 - 9,
            bookTop + FULL_HEIGHT - 5,
            308,
            0,
            18,
            9,
            this::canSeeBackButton,
            this::handleButtonBack,
            Component.translatable("handbook.gui.lexicon.button.back"),
            Component.translatable("handbook.gui.lexicon.button.back.info").withStyle(ChatFormatting.GRAY)
        ));
        addRenderableWidget(new GuiButtonBookArrow(this, bookLeft - 4, bookTop + FULL_HEIGHT - 6, true));
        addRenderableWidget(new GuiButtonBookArrow(this, bookLeft + FULL_WIDTH - 14, bookTop + FULL_HEIGHT - 6, false));

        addBookmarkButtons();
    }

    public Minecraft getMinecraft() {
        Minecraft mc = Minecraft.getInstance();

        return mc == null ? null : mc;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.pose().pushMatrix();
        currentBookMouseX = mouseX;
        currentBookMouseY = mouseY;

        if (scaleFactor != 1) {
            guiGraphics.pose().scale(scaleFactor, scaleFactor);

            mouseX = getScaledMouseX(mouseX);
            mouseY = getScaledMouseY(mouseY);
        }

        drawScreenAfterScale(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.pose().popMatrix();
    }

    private void drawScreenAfterScale(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        resetTooltip();

        graphics.pose().pushMatrix();
        graphics.pose().translate(bookLeft, bookTop);
        drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        drawForegroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.pose().popMatrix();

        super.render(graphics, mouseX, mouseY, partialTicks);

        Services.BOOK_HELPER.fireDrawBookScreen(this.book.id, this, mouseX, mouseY, partialTicks, graphics);

        drawTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {}

    public void addBookmarkButtons() {
        removeDrawablesIf((b) -> b instanceof GuiButtonBookBookmark);

        int y = 0;
        List<Bookmark> bookmarks = PersistentData.data.getBookData(book).bookmarks;

        for (Bookmark bookmark : bookmarks) {
            addRenderableWidget(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, bookmark));
            y += 12;
        }

        if (shouldAddAddBookmarkButton() && bookmarks.size() <= MAX_BOOKMARKS) {
            addRenderableWidget(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, null));
        }

        if (shouldAddMarkReadButton()) {
            addRenderableWidget(new GuiButtonBookMarkRead(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + PAGE_HEIGHT - 10));
        }
    }

    public final void removeDrawablesIf(Predicate<Renderable> pred) {
        ((AccessorScreen) (this)).getRenderables().removeIf(pred);
        children().removeIf(listener -> listener instanceof Renderable w && pred.test(w));
        ((AccessorScreen) (this)).getNarratables().removeIf(listener -> listener instanceof Renderable w && pred.test(w));
    }

    public final void removeDrawablesIn(Collection<?> coll) {
        removeDrawablesIf(coll::contains);
    }

    @Override // make public
    public <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(@NotNull T drawableElement) {
        return super.addRenderableWidget(drawableElement);
    }

    protected boolean shouldAddAddBookmarkButton() {
        return false;
    }

    protected boolean shouldAddMarkReadButton() {
        if (this instanceof GuiBookIndex) {
            return false;
        }
        return book.getContents().entries.values().stream().anyMatch(v -> !v.isLocked() && v.getReadState().equals(EntryDisplayState.UNREAD));
    }

    public void bookmarkThis() {
        // NO-OP
    }

    public void onFirstOpened() {
        playBookOpenSound(book);
    }

    @Override
    public void tick() {
        if (getMinecraft() == null) {
            return;
        }

        if (!getMinecraft().hasShiftDown()) {
            ticksInBook++;
        }

        if (needsBookmarkUpdate) {
            needsBookmarkUpdate = false;
            addBookmarkButtons();
        }
    }

    final void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        drawFromTexture(graphics, book, 0, 0, 0, 0, FULL_WIDTH, FULL_HEIGHT);
    }

    void drawForegroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {}

    final void drawTooltip(GuiGraphics guiGraphics, int x, int y) {
        int mouseX = (int) ((float) x * scaleFactor);
        int mouseY = (int) ((float) y * scaleFactor);

        if (getMinecraft() != null && tooltipStack != null) {
            List<Component> tooltip = Screen.getTooltipFromItem(getMinecraft(), tooltipStack);
            Pair<BookEntry, Integer> provider = book.getContents().getEntryForStack(tooltipStack);

            if (provider != null && (!(this instanceof GuiBookEntry) || ((GuiBookEntry) this).entry != provider.getFirst())) {
                Component t = Component.literal("(")
                    .append(Component.translatable("handbook.gui.lexicon.shift_for_recipe"))
                    .append(")")
                    .withStyle(ChatFormatting.GOLD);
                tooltip.add(t);
                targetPage = provider;
            }
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
        else if (tooltip != null && !tooltip.isEmpty()) {
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }

    final void resetTooltip() {
        tooltipStack = null;
        tooltip = null;
        targetPage = null;
    }

    public static void drawFromTexture(GuiGraphics guiGraphics, Book book, int x, int y, int u, int v, int w, int h) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, book.bookTexture, x, y, u, v, w, h, 512, 256);
    }

    public static void drawFromTexture(GuiGraphics guiGraphics, Book book, int x, int y, int u, int v, int w, int h, float alpha) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, book.bookTexture, x, y, u, v, w, h, 512, 256, HandbookColors.WHITE.toAlphaColor(alpha));
    }

    @Override
    public boolean isPauseScreen() {
        return book.pauseGame;
    }

    private void handleButtonBack(Button button) {
        back(false);
    }

    public void handleButtonArrow(Button button) {
        changePage(((GuiButtonBookArrow) button).left, false);
    }

    public void handleButtonBookmark(Button button) {
        GuiButtonBookBookmark bookmarkButton = (GuiButtonBookBookmark) button;
        Bookmark bookmark = bookmarkButton.bookmark;

        if (bookmark == null || bookmark.getEntry(book) == null) {
            bookmarkThis();
        }
        else if (getMinecraft() != null) {
            if (getMinecraft().hasShiftDown()) {
                List<Bookmark> bookmarks = PersistentData.data.getBookData(book).bookmarks;
                bookmarks.remove(bookmark);
                PersistentData.save();
                needsBookmarkUpdate = true;
            }
            else {
                displayLexiconGui(new GuiBookEntry(book, bookmark.getEntry(book), bookmark.spread), true);
            }
        }
    }

    @Override
    public final boolean mouseClicked(@NotNull MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        return mouseClickedScaled(mouseButtonEvent, isDoubleClick);
    }

    public boolean mouseClickedScaled(MouseButtonEvent mouseButtonEvent, boolean isDoubleClick) {
        double mouseX = getScaledMouseX((int) mouseButtonEvent.x());
        double mouseY = getScaledMouseY((int) mouseButtonEvent.y());

        MouseButtonEvent scaledMouseEvent = new MouseButtonEvent(mouseX, mouseY, mouseButtonEvent.buttonInfo());

        switch (mouseButtonEvent.button()) {
            case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                if (targetPage != null && getMinecraft() != null && getMinecraft().hasShiftDown()) {
                    displayLexiconGui(new GuiBookEntry(book, targetPage.getFirst(), targetPage.getSecond()), true);
                    playBookFlipSound(book);
                    return true;
                }
            }
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
                back(true);
                return true;
            }
            case GLFW.GLFW_MOUSE_BUTTON_4 -> {
                changePage(true, true);
                return true;
            }
            case GLFW.GLFW_MOUSE_BUTTON_5 -> {
                changePage(false, true);
                return true;
            }
        }

        for (GuiEventListener listener : children()) {
            if (listener.mouseClicked(scaledMouseEvent, isDoubleClick)) {
                if (mouseButtonEvent.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    setDragging(true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent keyEvent) {
        int keyCode = keyEvent.key();

        if (Minecraft.getInstance().options.keyInventory.matches(keyEvent) && !this.canSeeBackButton()) {
            this.onClose();
            return true;
        }
        else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            back(true);
            return true;
        }
        else if (tooltipStack != null && Services.BOOK_HELPER.handleRecipeKeybind(keyEvent, tooltipStack)) {
            return true;
        }
        else if (tooltipStack != null && PLATFORM.isModLoaded("jei")
                && HandbookJeiPlugin.handleRecipeKeybind(keyEvent, tooltipStack)) {
            return true;
        }

        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollX < 0) {
            changePage(false, true);
        }
        else if (scrollX > 0) {
            changePage(true, true);
        }

        return true;
    }

    void back(boolean sfx) {
        if (!book.getContents().guiStack.isEmpty()) {
            if (getMinecraft() != null && getMinecraft().hasShiftDown()) {
                displayLexiconGui(new GuiBookLanding(book), false);
                book.getContents().guiStack.clear();
            }
            else {
                displayLexiconGui(book.getContents().guiStack.pop(), false);
            }

            if (sfx) {
                playBookFlipSound(book);
            }
        }
    }

    void changePage(boolean left, boolean sfx) {
        if (canSeePageButton(left)) {
            if (left) {
                spread--;
            }
            else {
                spread++;
            }

            onPageChanged();
            if (sfx) {
                playBookFlipSound(book);
            }
        }
    }

    void onPageChanged() {
        // NO-OP
    }

    public boolean canBeOpened() {
        return true;
    }

    public boolean canSeePageButton(boolean left) {
        return left ? spread > 0 : (spread + 1) < maxSpreads;
    }

    public boolean canSeeBackButton() {
        return !book.getContents().guiStack.isEmpty();
    }

    public void setTooltip(Component... strings) {
        setTooltip(Arrays.asList(strings));
    }

    public void setTooltip(List<Component> strings) {
        tooltip = strings;
    }

    public void setTooltipStack(ItemStack stack) {
        setTooltip(Collections.emptyList());
        tooltipStack = stack;
    }

    public boolean isMouseInRelativeRange(double absMx, double absMy, int x, int y, int w, int h) {
        double mx = getRelativeX(absMx);
        double my = getRelativeY(absMy);

        return mx > x && my > y && mx <= (x + w) && my <= (y + h);
    }

    /**
     * Convert the given argument from global screen coordinates to local coordinates
     */
    public double getRelativeX(double absX) {
        return absX - bookLeft;
    }

    /**
     * Convert the given argument from global screen coordinates to local coordinates
     */
    public double getRelativeY(double absY) {
        return absY - bookTop;
    }

    public int getScaledMouseX(int mouseX) {
        return (int) ((float) mouseX / scaleFactor);
    }

    public int getScaledMouseY(int mouseY) {
        return (int) ((float) mouseY / scaleFactor);
    }

    public void drawProgressBar(GuiGraphics graphics, Book book, int mouseX, int mouseY, Predicate<BookEntry> filter) {
        if (!book.showProgress || !book.advancementsEnabled()) {
            return;
        }

        int barLeft = 19;
        int barTop = FULL_HEIGHT - 36;
        int barWidth = PAGE_WIDTH - 10;
        int barHeight = 12;

        int totalEntries = 0;
        int unlockedEntries = 0;

        int unlockedSecretEntries = 0;

        for (BookEntry entry : book.getContents().entries.values()) {
            if (filter.test(entry)) {
                if (entry.isSecret()) {
                    if (!entry.isLocked()) {
                        unlockedSecretEntries++;
                    }
                }
                else {
                    BookCategory category = entry.getCategory();
                    if (category.isSecret() && !category.isLocked()) {
                        continue;
                    }

                    totalEntries++;
                    if (!entry.isLocked()) {
                        unlockedEntries++;
                    }
                }
            }
        }

        float unlockFract = (float) unlockedEntries / Math.max(1, (float) totalEntries);
        int progressWidth = (int) (((float) barWidth - 1) * unlockFract);

        graphics.fill(barLeft, barTop, barLeft + barWidth, barTop + barHeight, book.headerColor);

        drawGradient(graphics, barLeft + 1, barTop + 1, barLeft + barWidth - 1, barTop + barHeight - 1, book.progressBarBackground);
        drawGradient(graphics, barLeft + 1, barTop + 1, barLeft + progressWidth, barTop + barHeight - 1, book.progressBarColor);

        graphics.drawString(this.font, Component.translatable("handbook.gui.lexicon.progress_meter"), barLeft, barTop - 9, book.headerColor, false);

        if (isMouseInRelativeRange(mouseX, mouseY, barLeft, barTop, barWidth, barHeight)) {
            List<Component> tooltip = new ArrayList<>();
            Component progressStr = Component.translatable("handbook.gui.lexicon.progress_tooltip", unlockedEntries, totalEntries);
            tooltip.add(progressStr);

            if (unlockedSecretEntries > 0) {
                if (unlockedSecretEntries == 1) {
                    tooltip.add(Component.translatable("handbook.gui.lexicon.progress_tooltip.secret1").withStyle(ChatFormatting.GRAY));
                }
                else {
                    tooltip.add(Component.translatable("handbook.gui.lexicon.progress_tooltip.secret", unlockedSecretEntries).withStyle(ChatFormatting.GRAY));
                }
            }

            if (unlockedEntries != totalEntries) {
                tooltip.add(Component.translatable("handbook.gui.lexicon.progress_tooltip.info").withStyle(ChatFormatting.GRAY));
            }

            setTooltip(tooltip);
        }
    }

    private void drawGradient(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        int darkerColor = new Color(color).darker().getRGB();

        graphics.fillGradient(x, y, w, h, color, darkerColor);
    }

    public void drawCenteredStringNoShadow(GuiGraphics graphics, FormattedCharSequence s, int x, int y, int color) {
        graphics.drawString(font, s, x - font.width(s) / 2, y, color, false);
    }

    public void drawCenteredStringNoShadow(GuiGraphics graphics, String s, int x, int y, int color) {
        graphics.drawString(font, s, x - font.width(s) / 2, y, color, false);
    }

    private int getMaxAllowedScale() {
        if (getMinecraft() == null) {
            return 1;
        }

        return getMinecraft().getWindow().calculateScale(0, getMinecraft().isEnforceUnicode());
    }

    public int getSpread() {
        return spread;
    }

    public static void drawSeparator(GuiGraphics guiGraphics, Book book, int x, int y) {
        int w = 110;
        int h = 3;
        int rx = x + PAGE_WIDTH / 2 - w / 2;

        drawFromTexture(guiGraphics, book, rx, y, 140, 180, w, h, 0.8F);
    }

    public static void drawLock(GuiGraphics guiGraphics, Book book, int x, int y) {
        drawFromTexture(guiGraphics, book, x, y, 250, 180, 16, 16, 0.7F);
    }

    public static void drawMarking(GuiGraphics guiGraphics, Book book, int x, int y, int rand, EntryDisplayState state) {
        if (!state.hasIcon) {
            return;
        }

        float alpha = state.hasAnimation ? ((float) Math.sin(ClientTicker.total * 0.2F) * 0.3F + 0.7F) : 1F;

        drawFromTexture(guiGraphics, book, x, y, state.u, 197, 8, 8, alpha);
    }

    public static void drawPageFiller(GuiGraphics guiGraphics, Book book) {
        drawPageFiller(guiGraphics, book, RIGHT_PAGE_X, TOP_PADDING);
    }

    public static void drawPageFiller(GuiGraphics guiGraphics, Book book, int x, int y) {
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            book.fillerTexture,
            x + PAGE_WIDTH / 2 - 64,
            y + PAGE_HEIGHT / 2 - 74,
            0,
            0,
            128,
            128,
            128,
            128,
            HandbookColors.WHITE.toAlphaColor(0.7F)
        );
    }

    public static void playBookFlipSound(Book book) {
        if (ClientTicker.ticksInGame - lastSound > 6) {
            SoundEvent sfx = HandbookSounds.getSound(book.flipSound, HandbookSounds.BOOK_FLIP);

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sfx, (float) (0.7 + Math.random() * 0.3)));
            lastSound = ClientTicker.ticksInGame;
        }
    }

    public static void playBookOpenSound(Book book) {
        SoundEvent sfx = HandbookSounds.getSound(book.openSound, HandbookSounds.BOOK_OPEN);

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sfx, 1F,  (float) (0.7 + Math.random() * 0.4)));
    }

    public static void openWebLink(Screen prevScreen, String address) {
        Minecraft mc = Minecraft.getInstance();

        mc.setScreen(new ConfirmLinkScreen(yes -> {
            if (yes) {
                Util.getPlatform().openUri(address);
            }

            mc.setScreen(prevScreen);
        }, address, false));
    }

    public void displayLexiconGui(GuiBook gui, boolean push) {
        book.getContents().openLexiconGui(gui, push);
    }

}
