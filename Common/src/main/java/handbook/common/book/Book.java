package handbook.common.book;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import handbook.api.HandbookAPI;
import handbook.client.base.ClientAdvancements;
import handbook.client.book.BookContents;
import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.BookIcon;
import handbook.common.base.HandbookSounds;
import handbook.common.item.HandbookBook;
import handbook.common.util.ColorHelper;
import handbook.common.util.ColorHelper.HandbookColors;
import handbook.common.util.ItemStackUtil;
import handbook.common.util.SerializationUtil;
import handbook.common.CommonModContainer;
import handbook.config.HandbookConfig;

public class Book {

    private static final String[] ORDINAL_SUFFIXES = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
    private static final Identifier DEFAULT_MODEL = HandbookAPI.prefix("book_brown");
    private static final Identifier DEFAULT_FILLER_TEXTURE = HandbookAPI.prefix("textures/gui/page_filler.png");
    private static final Identifier DEFAULT_CRAFTING_TEXTURE = HandbookAPI.prefix("textures/gui/crafting.png");

    private static final Map<String, String> DEFAULT_MACROS = Util.make(() -> {
        Map<String, String> ret = new HashMap<>();
        ret.put("$(list", "$(li"); //  The lack of ) is intended
        ret.put("/$", "$()");
        ret.put("<br>", "$(br)");

        ret.put("$(item)", "$(#b0b)");
        ret.put("$(thing)", "$(#490)");
        return ret;
    });

    private BookContents contents;

    private boolean wasUpdated = false;

    public final CommonModContainer owner;
    public final Identifier id;
    private Supplier<ItemStack> bookItem;

    public final int textColor, headerColor, nameplateColor, linkColor, linkHoverColor, progressBarColor, progressBarBackground;

    // JSON Loaded properties

    public final String name;
    public final String landingText;

    public final Identifier bookTexture, fillerTexture, craftingTexture;

    public final Identifier model;

    public final boolean useBlockyFont;

    public final Identifier openSound, flipSound;

    public final boolean showProgress;

    public final String indexIconRaw;

    public final String version;
    public final String subtitle;

    @Nullable public final Identifier creativeTab;

    @Nullable public final Identifier advancementsTab;

    public final boolean noBook;

    public final boolean showToasts;

    public final boolean pauseGame;

    public final boolean isPamphlet;

    public final boolean i18n;
    public final HandbookConfig.@Nullable TextOverflowMode overflowMode;

    public final Map<String, String> macros = new HashMap<>();

    private static int parseColor(JsonObject root, String key, String defaultColor) {
        return ColorHelper.getHandbookColor(GsonHelper.getAsString(root, key, defaultColor));
    }

    public Book(JsonObject root, CommonModContainer owner, Identifier id) {
        this.name = GsonHelper.getAsString(root, "name");
        this.landingText = GsonHelper.getAsString(root, "landing_text", "handbook.gui.lexicon.landing_info");
        this.bookTexture = SerializationUtil.getAsIdentifier(root, "book_texture", BookLayoutTexture.DEFAULT.texture());
        this.fillerTexture = SerializationUtil.getAsIdentifier(root, "filler_texture", DEFAULT_FILLER_TEXTURE);
        this.craftingTexture = SerializationUtil.getAsIdentifier(root, "crafting_texture", DEFAULT_CRAFTING_TEXTURE);
        this.model = SerializationUtil.getAsIdentifier(root, "model", DEFAULT_MODEL).withPrefix("item/");
        this.useBlockyFont = GsonHelper.getAsBoolean(root, "use_blocky_font", false);

        this.owner = owner;
        this.id = id;
        this.textColor = parseColor(root, "text_color", HandbookColors.TEXT.getHex());
        this.headerColor = parseColor(root, "header_color", HandbookColors.HEADER.getHex());
        this.nameplateColor = parseColor(root, "nameplate_color", HandbookColors.NAMEPLATE.getHex());
        this.linkColor = parseColor(root, "link_color", HandbookColors.LINK.getHex());
        this.linkHoverColor = parseColor(root, "link_hover_color", HandbookColors.LINK_HOVER.getHex());
        this.progressBarColor = parseColor(root, "progress_bar_color", HandbookColors.PROGRESS_BAR.getHex());
        this.progressBarBackground = parseColor(root, "progress_bar_background", HandbookColors.PROGRESS_BAR_BACKGROUND.getHex());
        this.openSound = SerializationUtil.getAsIdentifier(root, "open_sound", HandbookSounds.BOOK_OPEN.location());
        this.flipSound = SerializationUtil.getAsIdentifier(root, "flip_sound", HandbookSounds.BOOK_FLIP.location());
        this.showProgress = GsonHelper.getAsBoolean(root, "show_progress", true);
        this.indexIconRaw = GsonHelper.getAsString(root, "index_icon", "");
        this.version = GsonHelper.getAsString(root, "version", "0");
        this.subtitle = GsonHelper.getAsString(root, "subtitle", "");
        this.creativeTab = SerializationUtil.getAsIdentifier(root, "creative_tab", null);
        this.advancementsTab = SerializationUtil.getAsIdentifier(root, "advancements_tab", null);
        this.noBook = GsonHelper.getAsBoolean(root, "dont_generate_book", false);
        this.showToasts = GsonHelper.getAsBoolean(root, "show_toasts", true);
        this.pauseGame = GsonHelper.getAsBoolean(root, "pause_game", false);
        this.isPamphlet = GsonHelper.getAsBoolean(root, "pamphlet", false);
        this.i18n = GsonHelper.getAsBoolean(root, "i18n", false);
        this.overflowMode = SerializationUtil.getAsEnum(root, "text_overflow_mode", HandbookConfig.TextOverflowMode.class, null);

        String customBookItem = GsonHelper.getAsString(root, "custom_book_item", "");

        if (noBook) {
            // Need lazy parsing for mods that load after Handbook, as parser looks up item and components
            // in registries; wrap in try-catch in case of faulty item definition
            bookItem = Suppliers.memoize(() -> {
                try {
                    return ItemStackUtil.loadFromParsed(
                            ItemStackUtil.deserializeStack(customBookItem, VanillaRegistries.createLookup()));
                }
                catch (Exception e) {
                    HandbookAPI.LOGGER.warn("Failed to parse item \"{}\" for book {} defined by mod {}, skipping",
                        customBookItem, id, owner.getId(), e);

                    return ItemStack.EMPTY;
                }
            });
        }
        else {
            bookItem = Suppliers.memoize(() -> HandbookBook.forBook(id));
        }

        macros.putAll(DEFAULT_MACROS);

        for (Map.Entry<String, JsonElement> e : GsonHelper.getAsJsonObject(root, "macros", new JsonObject()).entrySet()) {
            macros.put(e.getKey(), GsonHelper.convertToString(e.getValue(), "macro value"));
        }
    }

    public ItemStack getBookItem() {
        return this.bookItem.get();
    }

    public void markUpdated() {
        wasUpdated = true;
    }

    public boolean popUpdated() {
        boolean updated = wasUpdated;
        wasUpdated = false;
        return updated;
    }

    /**
     * Must only be called on client
     * 
     * @param singleBook Hint that the book was reloaded through the button on the main page
     */
    public void reloadContents(Level level, boolean singleBook) {
        try {
            contents = BookContentsBuilder.loadAndBuildFor(level, this, singleBook);
        }
        catch (Exception e) {
            HandbookAPI.LOGGER.error("Error loading and compiling book {}, using empty contents", id, e);
            contents = BookContents.empty(this, e);
        }
    }

    public final boolean advancementsEnabled() {
        return !HandbookConfig.Client.disableAdvancementLocking()
                && !HandbookConfig.Client.noAdvancementBooks().contains(id.toString());
    }

    public void reloadLocks(boolean suppressToasts) {
        getContents().entries.values().forEach(BookEntry::updateLockStatus);
        getContents().categories.values().forEach(c -> c.updateLockStatus(true));

        boolean updated = popUpdated();

        if (updated && !suppressToasts && advancementsEnabled() && showToasts) {
            ClientAdvancements.sendBookToast(this);
        }
    }

    public String getOwnerName() {
        return owner.getName();
    }

    public Style getFontStyle() {
        if (useBlockyFont) {
            return Style.EMPTY;
        }
        else {
            return Style.EMPTY.withFont(new FontDescription.Resource(Identifier.withDefaultNamespace("uniform")));
        }
    }

    public MutableComponent getSubtitle() {
        Component editionStr;

        try {
            int ver = Integer.parseInt(version);
            if (ver == 0) {
                return Component.translatable(subtitle);
            }

            editionStr = Component.literal(numberToOrdinal(ver));
        }
        catch (NumberFormatException e) {
            editionStr = Component.translatable("handbook.gui.lexicon.dev_edition");
        }

        return Component.translatable("handbook.gui.lexicon.edition_str", editionStr);
    }

    public BookIcon getIcon() {
        if (indexIconRaw == null || indexIconRaw.isEmpty()) {
            return new BookIcon.StackIcon(getBookItem());
        } else {
            return BookIcon.from(indexIconRaw);
        }
    }

    private static String numberToOrdinal(int i) {
        return i % 100 == 11 || i % 100 == 12 || i % 100 == 13 ? i + "th" : i + ORDINAL_SUFFIXES[i % 10];
    }

    public BookContents getContents() {
        return contents != null ? contents : BookContents.empty(this, null);
    }

    public enum BookLayoutTexture {
        BLUE,
        BROWN,
        CYAN,
        GRAY,
        GREEN,
        PURPLE,
        RED,
        DEFAULT;

        @Override
        public String toString() {
            return switch (this) {
                case BLUE -> "book_blue";
                case BROWN -> "book_brown";
                case CYAN -> "book_cyan";
                case GRAY -> "book_gray";
                case GREEN -> "book_green";
                case PURPLE -> "book_purple";
                case RED -> "book_red";
                case DEFAULT -> "book_brown";
            };
        }

        public Identifier texture() {
            return HandbookAPI.prefix("textures/gui/" + this + ".png");
        }
    }

}
