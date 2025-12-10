package handbook.client.book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonElement;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;

import handbook.api.HandbookAPI;
import handbook.common.book.Book;

/**
 * BookContentLoader similar to {@link BookContentResourceDirectLoader}, but it
 * pre-caches the JSONs at during resource load to avoid I/O during book reloads.
 */
public class BookContentResourceListenerLoader extends SimpleJsonResourceReloadListener<JsonElement>
        implements BookContentLoader {

    public static final Identifier ID = HandbookAPI.prefix("resource_pack_books");
    public static final BookContentResourceListenerLoader INSTANCE = new BookContentResourceListenerLoader();
    private static final Pattern ID_READER = Pattern.compile(
        "(?<bookId>[a-z0-9_.-]+)" +
        "/(?<lang>[a-z0-9_.-]+)" +
        "/(?<folder>[a-z0-9_.-]+)" +
        "/(?<entryId>[a-z0-9/._-]+)"
    );

    // book id -> (entry id -> entry json)
    private Map<Identifier, Map<Identifier, JsonElement>> data;

    private BookContentResourceListenerLoader() {
        super(ExtraCodecs.JSON, FileToIdConverter.json("handbook_books"));
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        Map<Identifier, Map<Identifier, JsonElement>> data = new HashMap<>();
        for (Map.Entry<Identifier, JsonElement> entry : map.entrySet()) {
            // namespace:book_name/en_us/entries/entry
            Identifier key = entry.getKey();
            Matcher matcher = ID_READER.matcher(key.getPath());

            if (!matcher.matches()) {
                HandbookAPI.LOGGER.trace("Ignored file {}", key);

                continue;
            }

            Identifier bookId = Identifier.fromNamespaceAndPath(key.getNamespace(), matcher.group("bookId"));

            data.computeIfAbsent(bookId, id -> new HashMap<>()).put(entry.getKey(), entry.getValue());
        }

        int count = data.values().stream().mapToInt(Map::size).sum();

        HandbookAPI.LOGGER.info("{} preloaded {} jsons", getClass().getSimpleName(), count);
        this.data = data;
    }

    @Override
    public void findFiles(Book book, String dir, List<Identifier> list) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<Identifier, JsonElement> map = data.get(book.id);

        if (map == null) {
            return;
        }

        for (Identifier id : map.keySet()) {
            Matcher matcher = ID_READER.matcher(id.getPath());

            if (!matcher.matches()) {
                continue;
            }
            if (dir.equals(matcher.group("folder"))
                    && BookContentsBuilder.DEFAULT_LANG.equals(matcher.group("lang"))) {
                list.add(Identifier.fromNamespaceAndPath(id.getNamespace(), matcher.group("entryId")));
            }
        }

        HandbookAPI.LOGGER.info("{}: Files found in {}", getClass().getSimpleName(), stopwatch.stop());
    }

    @Nullable
    @Override
    public LoadResult loadJson(Book book, Identifier file) {
        HandbookAPI.LOGGER.trace("Loading {}", file);
        Map<Identifier, JsonElement> map = data.get(book.id);

        if (map == null) {
            return null;
        }

        String path = file.getPath();
        // Drop handbook_books/ and json suffix
        String relativizedPath = path.substring(0, path.length() - 5).split("/", 2)[1];

        JsonElement json = map.get(Identifier.fromNamespaceAndPath(file.getNamespace(), relativizedPath));
        if (json != null) {
            return new LoadResult(
                    json,
                    // todo implement this
                    null
            );
        }

        return null;
    }

}
