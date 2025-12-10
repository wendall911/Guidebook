package handbook.client.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import handbook.api.HandbookAPI;
import handbook.client.book.BookEntry;
import handbook.common.book.Book;
import handbook.common.util.SerializationUtil;

public final class PersistentData {

	private static final Path saveFile = Paths.get("handbook_data.json");

	public static DataHolder data = new DataHolder(new JsonObject());

	public static void setup() {
		try (BufferedReader reader = Files.newBufferedReader(saveFile, Charsets.UTF_8)) {
            JsonObject root = SerializationUtil.RAW_GSON.fromJson(reader, JsonObject.class);

			data = new DataHolder(root);
		}
        catch (IOException e) {
			if (!(e instanceof NoSuchFileException)) {
				HandbookAPI.LOGGER.warn("Unable to load handbook_data.json, replacing with default", e);
			}

			data = new DataHolder(new JsonObject());
			save();
		}
        catch (Exception e) {
			HandbookAPI.LOGGER.warn("Corrupted handbook_data.json, replacing with default", e);
			data = new DataHolder(new JsonObject());
			save();
		}
	}

	public static void save() {
        JsonObject json = data.serialize();

		try (BufferedWriter writer = Files.newBufferedWriter(saveFile, Charsets.UTF_8)) {
			SerializationUtil.PRETTY_GSON.toJson(json, writer);
		}
        catch (IOException e) {
			HandbookAPI.LOGGER.warn("Unable to save handbook_data.json", e);
		}
	}

	public static final class DataHolder {
		public int bookGuiScale;
		public boolean clickedVisualize;

		private final Map<Identifier, PersistentData.BookData> bookData = new HashMap<>();

		public DataHolder(JsonObject root) {
			this.bookGuiScale = GsonHelper.getAsInt(root, "bookGuiScale", 0);
			this.clickedVisualize = GsonHelper.getAsBoolean(root, "clickedVisualize", false);
			JsonObject jsonObject = GsonHelper.getAsJsonObject(root, "bookData", new JsonObject());

			for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				this.bookData.put(
                    Identifier.tryParse(entry.getKey()),
                    new BookData(entry.getValue().getAsJsonObject())
                );
			}
		}

		public PersistentData.BookData getBookData(Book book) {
			return bookData.computeIfAbsent(book.id, k -> new BookData(new JsonObject()));
		}

		public JsonObject serialize() {
            JsonObject jsonObject = new JsonObject();
            JsonObject books = new JsonObject();

			jsonObject.addProperty("bookGuiScale", this.bookGuiScale);
			jsonObject.addProperty("clickedVisualize", this.clickedVisualize);

			for (Map.Entry<Identifier, PersistentData.BookData> bookDataEntry : bookData.entrySet()) {
				books.add(bookDataEntry.getKey().toString(), bookDataEntry.getValue().serialize());
			}

			jsonObject.add("bookData", books);

			return jsonObject;
		}
	}

	public static final class Bookmark {
		public final Identifier entry;
		public final int spread;

		public Bookmark(Identifier entry, int spread) {
			this.entry = entry;
			this.spread = spread;
		}

		public Bookmark(JsonObject root) {
			this.entry = Identifier.tryParse(GsonHelper.getAsString(root, "entry"));
			this.spread = GsonHelper.getAsInt(root, "page"); // Serialized as page for legacy reasons
		}

		public BookEntry getEntry(Book book) {
			return book.getContents().entries.get(entry);
		}

		public JsonObject serialize() {
            JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty("entry", this.entry.toString());
			jsonObject.addProperty("page", this.spread); // Serialized as page for legacy reasons

			return jsonObject;
		}
	}

	public static final class BookData {
		public final List<Identifier> viewedEntries = new ArrayList<>();
		public final List<Bookmark> bookmarks = new ArrayList<>();
		public final List<Identifier> history = new ArrayList<>();
		public final List<Identifier> completedManualQuests = new ArrayList<>();

		public BookData(JsonObject root) {
            JsonArray emptyArray = new JsonArray();

			for (JsonElement element: GsonHelper.getAsJsonArray(root, "viewedEntries", emptyArray)) {
				viewedEntries.add(Identifier.tryParse(element.getAsString()));
			}
			for (JsonElement element: GsonHelper.getAsJsonArray(root, "bookmarks", emptyArray)) {
				bookmarks.add(new Bookmark(element.getAsJsonObject()));
			}
			for (JsonElement element : GsonHelper.getAsJsonArray(root, "history", emptyArray)) {
				history.add(Identifier.tryParse(element.getAsString()));
			}
			for (JsonElement element : GsonHelper.getAsJsonArray(root, "completedManualQuests", emptyArray)) {
				completedManualQuests.add(Identifier.tryParse(element.getAsString()));
			}
		}

		public JsonObject serialize() {
            JsonObject jsonObject = new JsonObject();
            JsonArray viewed = new JsonArray();
            JsonArray bookmarks = new JsonArray();
            JsonArray completed = new JsonArray();

			this.viewedEntries.stream().map(Object::toString).forEach(viewed::add);
			jsonObject.add("viewedEntries", viewed);

			this.bookmarks.stream().map(Bookmark::serialize).forEach(bookmarks::add);
			jsonObject.add("bookmarks", bookmarks);

			this.completedManualQuests.stream().map(Object::toString).forEach(completed::add);
			jsonObject.add("completedManualQuests", completed);

			return jsonObject;
		}
	}

}
