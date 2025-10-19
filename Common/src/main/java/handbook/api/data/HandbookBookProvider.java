package handbook.api.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import technology.roughness.whitenoise.util.ResourceLocationHelper;

public abstract class HandbookBookProvider implements DataProvider {

    protected final PackOutput.PathProvider datapackProvider;
    protected final PackOutput.PathProvider assetsProvider;

    private final String locale;
    private final String modid;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public HandbookBookProvider(PackOutput packOutput, String modid, String locale, CompletableFuture<HolderLookup.Provider> registries) {
        this.datapackProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "handbook_books");
        this.assetsProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "handbook_books");
        this.modid = modid;
        this.locale = locale;
        this.registries = registries;
    }

    /**
     * Performs this provider's action.
     *
     * @param cache the cache
     * @return the completable future
     */
    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        return this.registries.thenCompose(provider -> {
            List<CompletableFuture<?>> list = new ArrayList<>();

            this.addBooks(book -> {
                list.add(saveBook(cache, book.toJson(), book.getId()));

                for (CategoryBuilder category : book.getCategories()) {
                    list.add(saveCategory(cache, category.toJson(), book.getId(), category.getId()));

                    for (EntryBuilder entry : category.getEntries()) {
                        list.add(saveEntry(cache, entry.toJson(), book.getId(), entry.getId()));
                    }
                }
                for (TemplateBuilder template : book.getTemplates()) {
                    list.add(saveTemplate(cache, template.toJson(), book.getId(), template.getId()));
                }
            }, provider);

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected abstract void addBooks(Consumer<BookBuilder> consumer, HolderLookup.Provider provider);

    private CompletableFuture<?> saveEntry(CachedOutput cache, JsonObject json, ResourceLocation bookId, ResourceLocation id) {
        String pathSuffix = bookId.getPath() + "/" + locale + "/entries/" + id.getPath();

        return DataProvider.saveStable(cache, json, assetsProvider.json(ResourceLocation.fromNamespaceAndPath(bookId.getNamespace(), pathSuffix)));
    }

    private CompletableFuture<?> saveCategory(CachedOutput cache, JsonObject json, ResourceLocation bookId, ResourceLocation id) {
        String pathSuffix = bookId.getPath() + "/" + locale + "/categories/" + id.getPath();

        return DataProvider.saveStable(cache, json, assetsProvider.json(ResourceLocation.fromNamespaceAndPath(bookId.getNamespace(), pathSuffix)));
    }

    private CompletableFuture<?> saveTemplate(CachedOutput cache, JsonObject json, ResourceLocation bookId, ResourceLocation id) {
        String pathSuffix = bookId.getPath() + "/" + locale + "/templates/" + id.getPath();

        return DataProvider.saveStable(cache, json, assetsProvider.json(ResourceLocation.fromNamespaceAndPath(bookId.getNamespace(), pathSuffix)));
    }

    private CompletableFuture<?> saveBook(CachedOutput cache, JsonObject json, ResourceLocation bookId) {
        String pathSuffix = bookId.getPath() + "/book";

        return DataProvider.saveStable(cache, json, datapackProvider.json(ResourceLocation.fromNamespaceAndPath(bookId.getNamespace(), pathSuffix)));
    }

    public BookBuilder createBookBuilder(String id, String name, String landingText, HolderLookup.Provider provider) {
        return new BookBuilder(modid, id, name, landingText, provider);
    }

    /**
     * Creates a BookBuilder for a book with the given item.
     * The item is used both as the book item and to derive the book ID and model.
     */
    public BookBuilder createBookBuilder(Item item, String name, String landingText, HolderLookup.Provider provider) {
        ItemStack bookItem = new ItemStack(item);
        ResourceLocation itemId = ResourceLocationHelper.getItemStackId(bookItem);

        return new BookBuilder(itemId, name, landingText, provider)
            .setModel(itemId)
            .setCustomBookItem(bookItem);
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @NotNull
    @Override
    public String getName() {
        return "Handbook Book Provider";
    }

}
