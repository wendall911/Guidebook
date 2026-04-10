package handbook.client.book;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.GsonHelper;

import handbook.api.HandbookAPI;
import handbook.client.book.page.PageBlasting;
import handbook.client.book.page.PageCampfireCooking;
import handbook.client.book.page.PageCrafting;
import handbook.client.book.page.PageEmpty;
import handbook.client.book.page.PageEntity;
import handbook.client.book.page.PageImage;
import handbook.client.book.page.PageLink;
import handbook.client.book.page.PageQuest;
import handbook.client.book.page.PageRelations;
import handbook.client.book.page.PageSmelting;
import handbook.client.book.page.PageSmithing;
import handbook.client.book.page.PageSmoking;
import handbook.client.book.page.PageSpotlight;
import handbook.client.book.page.PageStonecutting;
import handbook.client.book.page.PageTemplate;
import handbook.client.book.page.PageText;
import handbook.client.book.template.BookTemplate;
import handbook.client.book.template.TemplateComponent;
import handbook.common.book.Book;
import handbook.common.book.BookRegistry;
import handbook.common.util.SerializationUtil;

public class ClientBookRegistry implements PreparableReloadListener {

    public static final Identifier ID = HandbookAPI.prefix("reload_hook");
    public final Map<Identifier, Class<? extends BookPage>> pageTypes = new HashMap<>();

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
        pageTypes.put(HandbookAPI.prefix("text"), PageText.class);
        pageTypes.put(HandbookAPI.prefix("crafting"), PageCrafting.class);
        pageTypes.put(HandbookAPI.prefix("smelting"), PageSmelting.class);
        pageTypes.put(HandbookAPI.prefix("blasting"), PageBlasting.class);
        pageTypes.put(HandbookAPI.prefix("smoking"), PageSmoking.class);
        pageTypes.put(HandbookAPI.prefix("campfire"), PageCampfireCooking.class);
        pageTypes.put(HandbookAPI.prefix("smithing"), PageSmithing.class);
        pageTypes.put(HandbookAPI.prefix("stonecutting"), PageStonecutting.class);
        pageTypes.put(HandbookAPI.prefix("image"), PageImage.class);
        pageTypes.put(HandbookAPI.prefix("spotlight"), PageSpotlight.class);
        pageTypes.put(HandbookAPI.prefix("empty"), PageEmpty.class);
        pageTypes.put(HandbookAPI.prefix("link"), PageLink.class);
        pageTypes.put(HandbookAPI.prefix("relations"), PageRelations.class);
        pageTypes.put(HandbookAPI.prefix("entity"), PageEntity.class);
        pageTypes.put(HandbookAPI.prefix("quest"), PageQuest.class);
    }

    @Override
    public @NonNull String getName() {
        return "Handbook Client Book Registry";
    }

    @Override
    public @NonNull CompletableFuture<Void> reload(@NonNull SharedState sharedState,
            @NonNull Executor prepareExecutor, PreparationBarrier preparationBarrier, @NonNull Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> null, prepareExecutor)
            .thenCompose(preparationBarrier::wait).thenAcceptAsync((v) -> {
                // Only reload if resource packs changed after initial load
                if (Minecraft.getInstance().level != null) {
                    HandbookAPI.LOGGER.info("Reloading resource pack-based books");
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
    public void displayBookGui(Identifier bookStr, @Nullable Identifier entryId, int page) {
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
                string = HandbookAPI.MODID + ":" + string;
            }

            Identifier type = Identifier.tryParse(string);
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
            Identifier type = Identifier.tryParse(prim.getAsString());
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
