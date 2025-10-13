package guidebook.common.book;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import guidebook.api.GuidebookAPI;
import guidebook.client.base.ClientAdvancements;
import guidebook.client.book.BookContents;
import guidebook.client.book.BookContentsBuilder;
import guidebook.client.book.BookEntry;
import guidebook.client.book.BookIcon;
import guidebook.common.base.GuidebookSounds;
import guidebook.common.item.ItemModBook;
import guidebook.common.util.ColorHelper;
import guidebook.common.util.ColorHelper.GuidebookColors;
import guidebook.common.util.ItemStackUtil;
import guidebook.common.util.SerializationUtil;
import guidebook.common.CommonModContainer;
import guidebook.config.GuidebookConfig;

public class Book {

    private static final String[] ORDINAL_SUFFIXES = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
    private static final ResourceLocation DEFAULT_MODEL = GuidebookAPI.prefix("book_brown");
    private static final ResourceLocation DEFAULT_BOOK_TEXTURE = GuidebookAPI.prefix("textures/gui/book_brown.png");
    private static final ResourceLocation DEFAULT_FILLER_TEXTURE = GuidebookAPI.prefix("textures/gui/page_filler.png");
    private static final ResourceLocation DEFAULT_CRAFTING_TEXTURE = GuidebookAPI.prefix("textures/gui/crafting.png");

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
    public final ResourceLocation id;
    private Supplier<ItemStack> bookItem;

    public final int textColor, headerColor, nameplateColor, linkColor, linkHoverColor, progressBarColor, progressBarBackground;

    // JSON Loaded properties

    public final String name;
    public final String landingText;

    public final ResourceLocation bookTexture, fillerTexture, craftingTexture;

    public final ResourceLocation model;

    public final boolean useBlockyFont;

    public final ResourceLocation openSound, flipSound;

    public final boolean showProgress;

    public final String indexIconRaw;

    public final String version;
    public final String subtitle;

    @Nullable public final ResourceLocation creativeTab;

    @Nullable public final ResourceLocation advancementsTab;

    public final boolean noBook;

    public final boolean showToasts;

    public final boolean pauseGame;

    public final boolean isPamphlet;

    public final boolean i18n;
    @Nullable public final GuidebookConfig.TextOverflowMode overflowMode;

    public final Map<String, String> macros = new HashMap<>();

    private static int parseColor(JsonObject root, String key, String defaultColor) {
        return ColorHelper.getGuidebookColor(GsonHelper.getAsString(root, key, defaultColor));
    }

    public Book(JsonObject root, CommonModContainer owner, ResourceLocation id) {
        this.name = GsonHelper.getAsString(root, "name");
        this.landingText = GsonHelper.getAsString(root, "landing_text", "guidebook.gui.lexicon.landing_info");
        this.bookTexture = SerializationUtil.getAsResourceLocation(root, "book_texture", DEFAULT_BOOK_TEXTURE);
        this.fillerTexture = SerializationUtil.getAsResourceLocation(root, "filler_texture", DEFAULT_FILLER_TEXTURE);
        this.craftingTexture = SerializationUtil.getAsResourceLocation(root, "crafting_texture", DEFAULT_CRAFTING_TEXTURE);
        this.model = SerializationUtil.getAsResourceLocation(root, "model", DEFAULT_MODEL).withPrefix("item/");
        this.useBlockyFont = GsonHelper.getAsBoolean(root, "use_blocky_font", false);

        this.owner = owner;
        this.id = id;
        this.textColor = parseColor(root, "text_color", GuidebookColors.TEXT.getHex());
        this.headerColor = parseColor(root, "header_color", GuidebookColors.HEADER.getHex());
        this.nameplateColor = parseColor(root, "nameplate_color", GuidebookColors.NAMEPLATE.getHex());
        this.linkColor = parseColor(root, "link_color", GuidebookColors.LINK.getHex());
        this.linkHoverColor = parseColor(root, "link_hover_color", GuidebookColors.LINK_HOVER.getHex());
        this.progressBarColor = parseColor(root, "progress_bar_color", GuidebookColors.PROGRESS_BAR.getHex());
        this.progressBarBackground = parseColor(root, "progress_bar_background", GuidebookColors.PROGRESS_BAR_BACKGROUND.getHex());
        this.openSound = SerializationUtil.getAsResourceLocation(root, "open_sound", GuidebookSounds.BOOK_OPEN.getLocation());
        this.flipSound = SerializationUtil.getAsResourceLocation(root, "flip_sound", GuidebookSounds.BOOK_FLIP.getLocation());
        this.showProgress = GsonHelper.getAsBoolean(root, "show_progress", true);
        this.indexIconRaw = GsonHelper.getAsString(root, "index_icon", "");
        this.version = GsonHelper.getAsString(root, "version", "0");
        this.subtitle = GsonHelper.getAsString(root, "subtitle", "");
        this.creativeTab = SerializationUtil.getAsResourceLocation(root, "creative_tab", null);
        this.advancementsTab = SerializationUtil.getAsResourceLocation(root, "advancements_tab", null);
        this.noBook = GsonHelper.getAsBoolean(root, "dont_generate_book", false);
        this.showToasts = GsonHelper.getAsBoolean(root, "show_toasts", true);
        this.pauseGame = GsonHelper.getAsBoolean(root, "pause_game", false);
        this.isPamphlet = GsonHelper.getAsBoolean(root, "pamphlet", false);
        this.i18n = GsonHelper.getAsBoolean(root, "i18n", false);
        this.overflowMode = SerializationUtil.getAsEnum(root, "text_overflow_mode", GuidebookConfig.TextOverflowMode.class, null);

        String customBookItem = GsonHelper.getAsString(root, "custom_book_item", "");

        if (noBook) {
            // Need lazy parsing for mods that load after Guidebook, as parser looks up item and components
            // in registries; wrap in try-catch in case of faulty item definition
            bookItem = Suppliers.memoize(() -> {
                try {
                    return ItemStackUtil.loadFromParsed(
                            ItemStackUtil.deserializeStack(customBookItem, VanillaRegistries.createLookup()));
                }
                catch (Exception e) {
                    GuidebookAPI.LOGGER.warn("Failed to parse item \"{}\" for book {} defined by mod {}, skipping",
                        customBookItem, id, owner.getId(), e);

                    return ItemStack.EMPTY;
                }
            });
        }
        else {
            bookItem = Suppliers.memoize(() -> ItemModBook.forBook(id));
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
            GuidebookAPI.LOGGER.error("Error loading and compiling book {}, using empty contents", id, e);
            contents = BookContents.empty(this, e);
        }
    }

    public final boolean advancementsEnabled() {
        return !GuidebookConfig.Client.disableAdvancementLocking()
                && !GuidebookConfig.Client.noAdvancementBooks().contains(id.toString());
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
        } else {
            return Style.EMPTY.withFont(Minecraft.UNIFORM_FONT);
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
            editionStr = Component.translatable("guidebook.gui.lexicon.dev_edition");
        }

        return Component.translatable("guidebook.gui.lexicon.edition_str", editionStr);
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

}
