package guidebook.common.book;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import org.apache.commons.lang3.tuple.Pair;

import guidebook.api.GuidebookAPI;
import guidebook.client.book.ClientBookRegistry;
import guidebook.common.CommonModContainer;
import guidebook.common.util.SerializationUtil.ResourceLocationSerializer;
import guidebook.config.GuidebookConfig;
import guidebook.platform.Services;

public class BookRegistry {

    public static final BookRegistry INSTANCE = new BookRegistry();
    public static final String BOOKS_LOCATION = GuidebookAPI.MODID + "_books";

    public final Map<ResourceLocation, Book> books = new HashMap<>();
    public static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(ResourceLocation.class, new ResourceLocationSerializer()).create();

    private BookRegistry() {}

    public void init() {
        Collection<CommonModContainer> mods = Services.BOOK_HELPER.getAllMods();
        Map<Pair<CommonModContainer, ResourceLocation>, String> foundBooks = new HashMap<>();

        mods.forEach(mod -> {
            String id = mod.getId();
            findFiles(mod, String.format("data/%s/%s", id, BOOKS_LOCATION), Files::exists,
                    (path, file) -> {
                        if (Files.isRegularFile(file)
                                && file.getFileName().toString().equals("book.json")) {
                            String fileStr = file.toString().replaceAll("\\\\", "/");
                            String relPath = fileStr
                                    .substring(fileStr.indexOf(BOOKS_LOCATION) + BOOKS_LOCATION.length() + 1);
                            String bookName = relPath.substring(0, relPath.indexOf("/"));

                            if (bookName.contains("/")) {
                                GuidebookAPI.LOGGER.warn("Ignored book.json @ {}", file);
                                return true;
                            }

                            String assetPath = fileStr.substring(fileStr.indexOf("data/"));
                            ResourceLocation bookId = ResourceLocation.fromNamespaceAndPath(id, bookName);
                            foundBooks.put(Pair.of(mod, bookId), assetPath);
                        }

                        return true;
                    }, true, 2);
        });

        foundBooks.forEach((pair, file) -> {
            CommonModContainer mod = pair.getLeft();
            ResourceLocation res = pair.getRight();

            try (InputStream stream = Files.newInputStream(mod.getPath(file))) {
                loadBook(mod, res, stream);
            }
            catch (Exception e) {
                GuidebookAPI.LOGGER.error("Failed to load book {} defined by mod {}, skipping",
                        res, mod.getId(), e);
            }
        });
    }

    public void loadBook(CommonModContainer mod, ResourceLocation res, InputStream stream) {
        Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        JsonObject tree = GSON.fromJson(reader, JsonObject.class);

        books.put(res, new Book(tree, mod, res));
    }

    /**
     * Must only be called on client
     */
    public void reloadContents(Level level) {
        GuidebookConfig.reloadBuiltinFlags();

        for (Book book : books.values()) {
            book.reloadContents(level, false);
        }

        ClientBookRegistry.INSTANCE.reloadLocks(false);
    }

    public static void findFiles(CommonModContainer mod, String base, Predicate<Path> rootFilter,
            BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles, int maxDepth) {
        if (mod.getId().equals("minecraft")) {
            return;
        }

        try {
            for (Path root : mod.getRootPaths()) {
                walk(root.resolve(base), rootFilter, processor, visitAllFiles, maxDepth);
            }
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void walk(Path root, Predicate<Path> rootFilter, BiFunction<Path, Path, Boolean> processor,
            boolean visitAllFiles, int maxDepth) throws IOException {
        if (root == null || !Files.exists(root) || !rootFilter.test(root)) {
            return;
        }

        if (processor != null) {
            try (Stream<@NotNull Path> stream = Files.walk(root, maxDepth)) {
                Iterator<Path> itr = stream.iterator();

                while (itr.hasNext()) {
                    boolean keepGoing = processor.apply(root, itr.next());

                    if (!visitAllFiles && !keepGoing) {
                        return;
                    }
                }
            }
        }
    }

}
