package guidebook.common.book;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import org.apache.commons.lang3.tuple.Pair;

import guidebook.api.GuidebookAPI;
import guidebook.client.book.ClientBookRegistry;
import guidebook.common.base.GuidebookConfig;
import guidebook.common.IBookHelper;
import guidebook.common.CommonModContainer;

public class BookRegistry {

	public static final BookRegistry INSTANCE = new BookRegistry();
	public static final String BOOKS_LOCATION = GuidebookAPI.MODID + "_books";

	public final Map<ResourceLocation, Book> books = new HashMap<>();
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
			.create();

	private BookRegistry() {}

	public void init() {
		Collection<CommonModContainer> mods = IBookHelper.INSTANCE.getAllMods();
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
				loadBook(mod, res, stream, false);
			} catch (Exception e) {
				GuidebookAPI.LOGGER.error("Failed to load book {} defined by mod {}, skipping",
						res, mod.getId(), e);
			}
		});

		BookFolderLoader.findBooks();
		IBookHelper.INSTANCE.signalBooksLoaded();
	}

	public void loadBook(CommonModContainer mod, ResourceLocation res, InputStream stream,
			boolean external) {
		Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		var tree = GSON.fromJson(reader, JsonObject.class);
		books.put(res, new Book(tree, mod, res, external));
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

	// HELPER

	public static void findFiles(CommonModContainer mod, String base, Predicate<Path> rootFilter,
			BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles) {
		findFiles(mod, base, rootFilter, processor, visitAllFiles, Integer.MAX_VALUE);
	}

	public static void findFiles(CommonModContainer mod, String base, Predicate<Path> rootFilter,
			BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles, int maxDepth) {
		if (mod.getId().equals("minecraft")) {
			return;
		}

		try {
			for (var root : mod.getRootPaths()) {
				walk(root.resolve(base), rootFilter, processor, visitAllFiles, maxDepth);
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private static void walk(Path root, Predicate<Path> rootFilter, BiFunction<Path, Path, Boolean> processor,
			boolean visitAllFiles, int maxDepth) throws IOException {
		if (root == null || !Files.exists(root) || !rootFilter.test(root)) {
			return;
		}

		if (processor != null) {
			try (var stream = Files.walk(root, maxDepth)) {
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
