package handbook.client.book;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import handbook.client.book.template.BookTemplate;
import handbook.common.book.Book;
import handbook.common.book.BookRegistry;
import handbook.common.util.ItemStackUtil;
import handbook.common.util.SerializationUtil;

/**
 * Holds book content state while it is being assembled from JSON data
 */
public class BookContentsBuilder {

    public static final String DEFAULT_LANG = "en_us";
    private final Map<Identifier, BookCategory> categories = new HashMap<>();
    private final Map<Identifier, BookEntry> entries = new HashMap<>();
    private final Map<Identifier, Supplier<BookTemplate>> templates = new HashMap<>();
    private final Map<ItemStackUtil.StackWrapper, Pair<BookEntry, Integer>> recipeMappings = new HashMap<>();

    private final Book book;
    /**
     * Signals that the book was reloaded through the ingame button and should bypass the cached resource reload data.
     */
    private final boolean singleBookReload;

    private interface LoadFunc<T> {
        @Nullable
        T load(Book book, BookContentLoader loader, Identifier id, Identifier file);
    }

    private BookContentsBuilder(Book book, boolean singleBookReload) {
        this.book = book;
        this.singleBookReload = singleBookReload;
        templates.putAll(BookContents.addonTemplates);
    }

    @Nullable
    public BookCategory getCategory(Identifier id) {
        return categories.get(id);
    }

    @Nullable
    public BookEntry getEntry(Identifier id) {
        return entries.get(id);
    }

    @Nullable
    public Supplier<BookTemplate> getTemplate(Identifier id) {
        return templates.get(id);
    }

    public void addRecipeMapping(ItemStackUtil.StackWrapper stack, BookEntry entry, int spread) {
        recipeMappings.put(stack, Pair.of(entry, spread));
    }

    public static BookContents loadAndBuildFor(Level level, Book book, boolean singleBookReload) {
        BookContentsBuilder builder = new BookContentsBuilder(book, singleBookReload);
        SerializationUtil.VARIABLE_SERIALIZER.setRegistries(level.registryAccess());
        builder.loadFiles();
        return builder.build(level);
    }

    private void loadFiles() {
        load("categories", BookContentsBuilder::loadCategory, categories);
        load("entries",
                (b, l, id, file) -> BookContentsBuilder.loadEntry(b, l, id, file, categories::get),
                entries);
        load("templates", BookContentsBuilder::loadTemplate, templates);
    }

    private BookContents build(Level level) {
        categories.forEach((id, category) -> {
            try {
                category.build(this);
            } catch (Exception e) {
                throw new RuntimeException("Error while building category " + id, e);
            }
        });

        entries.values().forEach(entry -> {
            try {
                entry.build(level, this);
            } catch (Exception e) {
                throw new RuntimeException("Error building entry %s of book %s".formatted(entry.getId(), book.id), e);
            }
        });

        BookCategory pamphletCategory = null;
        if (book.isPamphlet) {
            if (categories.size() != 1) {
                throw new RuntimeException("Pamphlet %s should have exactly one category but instead there were %d"
                        .formatted(book.id, categories.size()));
            }
            pamphletCategory = categories.values().iterator().next();
        }

        return new BookContents(
                book,
                ImmutableMap.copyOf(categories),
                ImmutableMap.copyOf(entries),
                ImmutableMap.copyOf(recipeMappings),
                pamphletCategory
        );
    }

    private <T> void load(String thing, LoadFunc<T> loader, Map<Identifier, T> builder) {
        BookContentLoader contentLoader = getContentLoader();
        List<Identifier> foundIds = new ArrayList<>();
        contentLoader.findFiles(book, thing, foundIds);

        for (Identifier id : foundIds) {
            String filePath = String.format("%s/%s/%s/%s/%s.json",
                    BookRegistry.BOOKS_LOCATION, book.id.getPath(), DEFAULT_LANG, thing, id.getPath());
            T value = loader.load(book, contentLoader, id, Identifier.fromNamespaceAndPath(id.getNamespace(), filePath));
            if (value != null) {
                builder.put(id, value);
            }
        }
    }

    protected BookContentLoader getContentLoader() {
        // A quick reload should not reuse stale data, it is initiated by the user anyways
        return singleBookReload
                ? BookContentResourceDirectLoader.INSTANCE
                : BookContentResourceListenerLoader.INSTANCE;
    }

    @Nullable
    private static BookCategory loadCategory(Book book, BookContentLoader loader, Identifier id, Identifier file) {
        BookContentLoader.LoadResult result = loadLocalizedJson(book, loader, file);
        // TODO: Render the "added by" text in the category UI somewhere
        BookCategory category = new BookCategory(result.json().getAsJsonObject(), id, book);

        if (category.canAdd()) {
            return category;
        }

        return null;
    }

    @Nullable
    private static BookEntry loadEntry(Book book, BookContentLoader loader, Identifier id,
            Identifier file, Function<Identifier, BookCategory> categories) {
        BookContentLoader.LoadResult result = loadLocalizedJson(book, loader, file);
        BookEntry entry = new BookEntry(result.json().getAsJsonObject(), id, book, result.addedBy());

        if (entry.canAdd()) {
            entry.initCategory(file, categories);
            return entry;
        }

        return null;
    }

    private static Supplier<BookTemplate> loadTemplate(Book book, BookContentLoader loader, Identifier key, Identifier res) {
        JsonElement json = loadLocalizedJson(book, loader, res).json();

        Supplier<BookTemplate> supplier = () -> ClientBookRegistry.INSTANCE.gson.fromJson(json, BookTemplate.class);

        // test supplier
        BookTemplate template = supplier.get();

        if (template == null) {
            throw new IllegalArgumentException(res + " could not be instantiated by the supplier.");
        }

        return supplier;
    }

    private static BookContentLoader.LoadResult loadLocalizedJson(Book book, BookContentLoader loader, Identifier file) {
        Identifier localizedFile = Identifier.fromNamespaceAndPath(file.getNamespace(),
                file.getPath().replaceAll(DEFAULT_LANG, ClientBookRegistry.INSTANCE.currentLang));
        BookContentLoader.LoadResult input = loader.loadJson(book, localizedFile);

        if (input == null) {
            input = loader.loadJson(book, file);
            if (input == null) {
                throw new IllegalArgumentException(file + " does not exist.");
            }
        }

        return input;
    }

}
