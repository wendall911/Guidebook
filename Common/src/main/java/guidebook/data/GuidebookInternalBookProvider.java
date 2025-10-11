package guidebook.data;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import guidebook.api.GuidebookAPI;
import guidebook.api.data.BookBuilder;
import guidebook.common.item.GuidebookItems;

public class GuidebookInternalBookProvider extends guidebook.api.data.GuidebookBookProvider {

    public static final String INTRO_BOOK_TRANSLATION_KEY = "book." + GuidebookAPI.MODID + ".intro_book";
    private int categorySortNum = -1;
    private int entrySortNum = -1;

    public GuidebookInternalBookProvider(PackOutput packOutput, CompletableFuture<Provider> registries) {
        super(packOutput, GuidebookAPI.MODID, "en_us", registries);
    }

    @Override
    protected void addBooks(Consumer<BookBuilder> consumer, Provider provider) {
        ItemStack book = new ItemStack(GuidebookItems.BOOK);
        ItemStack writeableBook = new ItemStack(Items.WRITABLE_BOOK);

        BookBuilder bookBuilder = createBookBuilder(GuidebookItems.BOOK, INTRO_BOOK_TRANSLATION_KEY, prefix("landing"), provider)
            .setSubtitle(prefix("subtitle"))
            .setCreativeTab("minecraft:tools_and_utilities")
            .setShowProgress(true)
            .setI18n(true)
            .addCategory(
                "introduction",
                prefix("introduction.name"),
                prefix("introduction.description"),
                book
            )
            .setSortnum(categorySortNum++)
            .addEntry(
                "introduction/welcome",
                prefix("introduction.welcome.name"),
                writeableBook
            ).setSortnum(entrySortNum++)
            .addTextPage(prefix("introduction.welcome.intro"))
            .build().build().build();

        bookBuilder.build(consumer);
    }

    private String prefix(String name) {
        return INTRO_BOOK_TRANSLATION_KEY + "." + name;
    }

}
