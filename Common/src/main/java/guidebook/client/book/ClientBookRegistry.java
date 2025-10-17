package guidebook.client.book;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.GsonHelper;

import guidebook.api.GuidebookAPI;
import guidebook.client.book.page.PageBlasting;
import guidebook.client.book.page.PageCampfireCooking;
import guidebook.client.book.page.PageCrafting;
import guidebook.client.book.page.PageEmpty;
import guidebook.client.book.page.PageEntity;
import guidebook.client.book.page.PageImage;
import guidebook.client.book.page.PageLink;
import guidebook.client.book.page.PageQuest;
import guidebook.client.book.page.PageRelations;
import guidebook.client.book.page.PageSmelting;
import guidebook.client.book.page.PageSmithing;
import guidebook.client.book.page.PageSmoking;
import guidebook.client.book.page.PageSpotlight;
import guidebook.client.book.page.PageStonecutting;
import guidebook.client.book.page.PageTemplate;
import guidebook.client.book.page.PageText;
import guidebook.client.book.template.BookTemplate;
import guidebook.client.book.template.TemplateComponent;
import guidebook.common.book.Book;
import guidebook.common.book.BookRegistry;
import guidebook.common.util.SerializationUtil;

public class ClientBookRegistry implements PreparableReloadListener {

    public static final ResourceLocation ID = GuidebookAPI.prefix("reload_hook");
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
        pageTypes.put(GuidebookAPI.prefix("text"), PageText.class);
        pageTypes.put(GuidebookAPI.prefix("crafting"), PageCrafting.class);
        pageTypes.put(GuidebookAPI.prefix("smelting"), PageSmelting.class);
        pageTypes.put(GuidebookAPI.prefix("blasting"), PageBlasting.class);
        pageTypes.put(GuidebookAPI.prefix("smoking"), PageSmoking.class);
        pageTypes.put(GuidebookAPI.prefix("campfire"), PageCampfireCooking.class);
        pageTypes.put(GuidebookAPI.prefix("smithing"), PageSmithing.class);
        pageTypes.put(GuidebookAPI.prefix("stonecutting"), PageStonecutting.class);
        pageTypes.put(GuidebookAPI.prefix("image"), PageImage.class);
        pageTypes.put(GuidebookAPI.prefix("spotlight"), PageSpotlight.class);
        pageTypes.put(GuidebookAPI.prefix("empty"), PageEmpty.class);
        pageTypes.put(GuidebookAPI.prefix("link"), PageLink.class);
        pageTypes.put(GuidebookAPI.prefix("relations"), PageRelations.class);
        pageTypes.put(GuidebookAPI.prefix("entity"), PageEntity.class);
        pageTypes.put(GuidebookAPI.prefix("quest"), PageQuest.class);
    }

    @Override
    public @NotNull String getName() {
        return "Guidebook Client Book Registry";
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull SharedState sharedState,
            @NotNull Executor prepareExecutor, PreparationBarrier preparationBarrier, @NotNull Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> null, prepareExecutor)
            .thenCompose(preparationBarrier::wait).thenAcceptAsync((v) -> {
                // Only reload if resource packs changed after initial load
                if (Minecraft.getInstance().level != null) {
                    GuidebookAPI.LOGGER.info("Reloading resource pack-based books");
                    reload();
                }
            }, applyExecutor);
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
        }
    }

    public static class LexiconPageAdapter implements JsonDeserializer<BookPage> {

        @Override
        public BookPage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json instanceof JsonPrimitive prim && prim.isString()) {
                // Shortcut: make strings instead of objects shortcut to text pages
                PageText out = new PageText();

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
