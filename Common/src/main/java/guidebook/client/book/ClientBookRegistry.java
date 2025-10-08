package guidebook.client.book;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;

import guidebook.api.GuidebookAPI;
import guidebook.client.book.template.BookTemplate;
import guidebook.client.book.template.TemplateComponent;
import guidebook.common.base.GuidebookSounds;
import guidebook.common.book.Book;
import guidebook.common.book.BookRegistry;
import guidebook.common.util.SerializationUtil;

public class ClientBookRegistry {

	public final Map<ResourceLocation, Class<? extends BookPage>> pageTypes = new HashMap<>();

	public final Gson gson = new GsonBuilder()
			.registerTypeHierarchyAdapter(BookPage.class, new LexiconPageAdapter())
			.registerTypeHierarchyAdapter(TemplateComponent.class, new TemplateComponentAdapter())
			.create();
	public String currentLang;

	public static final ClientBookRegistry INSTANCE = new ClientBookRegistry();

	private ClientBookRegistry() {}

	public void init() {
		addPageTypes();
	}

	private void addPageTypes() {
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "text"), PageText.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "crafting"), PageCrafting.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "smelting"), PageSmelting.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "blasting"), PageBlasting.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "smoking"), PageSmoking.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "campfire"), PageCampfireCooking.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "smithing"), PageSmithing.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "stonecutting"), PageStonecutting.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "image"), PageImage.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "spotlight"), PageSpotlight.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "empty"), PageEmpty.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "multiblock"), PageMultiblock.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "link"), PageLink.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "relations"), PageRelations.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "entity"), PageEntity.class);
		pageTypes.put(ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, "quest"), PageQuest.class);
	}

	public void reload() {
		currentLang = Minecraft.getInstance().getLanguageManager().getSelected();
		BookRegistry.INSTANCE.reloadContents(Minecraft.getInstance().level);
	}

	public void reloadLocks(boolean suppressToasts) {
		BookRegistry.INSTANCE.books.values().forEach(b -> b.reloadLocks(suppressToasts));
	}

	/**
	 * @param entryId Entry to force to the top of the stack
	 * @param page    Zero-indexed page in the entry to force. Ignored if {@code entryId} is null.
	 */
	public void displayBookGui(ResourceLocation bookStr, @Nullable ResourceLocation entryId, int page) {
		Minecraft mc = Minecraft.getInstance();
		currentLang = mc.getLanguageManager().getSelected();

		Book book = BookRegistry.INSTANCE.books.get(bookStr);

		if (book != null) {
			book.getContents().checkValidCurrentEntry();

			if (entryId != null) {
				book.getContents().setTopEntry(entryId, page);
			}

			book.getContents().openLexiconGui(book.getContents().getCurrentGui(), false);

			if (mc.player != null) {
				SoundEvent sfx = GuidebookSounds.getSound(book.openSound, GuidebookSounds.BOOK_OPEN);
				mc.player.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));
			}
		}
	}

	public static class LexiconPageAdapter implements JsonDeserializer<BookPage> {

		@Override
		public BookPage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json instanceof JsonPrimitive prim && prim.isString()) {
				// Shortcut: make strings instead of objects shortcut to text pages
				var out = new PageText();
				out.setText(prim.getAsString());
				return out;
			}

			JsonObject obj = json.getAsJsonObject();
			String string = GsonHelper.getAsString(obj, "type");
			if (string.indexOf(':') < 0) {
				string = GuidebookAPI.MODID + ":" + string;
			}
			ResourceLocation type = ResourceLocation.tryParse(string);
			Class<? extends BookPage> clazz = ClientBookRegistry.INSTANCE.pageTypes.get(type);
			if (clazz == null) {
				clazz = PageTemplate.class;
			}

			BookPage page = SerializationUtil.RAW_GSON.fromJson(json, clazz);
			page.sourceObject = obj;

			return page;
		}

	}

	public static class TemplateComponentAdapter implements JsonDeserializer<TemplateComponent> {

		@Override
		public TemplateComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			JsonPrimitive prim = (JsonPrimitive) obj.get("type");
			ResourceLocation type = ResourceLocation.tryParse(prim.getAsString());
			Class<? extends TemplateComponent> clazz = BookTemplate.componentTypes.get(type);

			if (clazz == null) {
				return null;
			}

			TemplateComponent component = SerializationUtil.RAW_GSON.fromJson(json, clazz);
			component.sourceObject = obj;

			return component;
		}

	}

}
