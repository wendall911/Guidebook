package guidebook.data.test;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;

public class GuidebookTestLanguageProvider extends FabricLanguageProvider {

    public GuidebookTestLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder builder) {
        addItem(builder, "guide_book", "Guide Book");
        addItem(builder, "guide_book.undefined", "Invalid book: no ID defined");
        addItem(builder, "guide_book.invalid", "Invalid book: %s");
        addItem(builder, "comprehensive_test_book.name", "Comprehensive Test Book");
        addItem(builder, "comprehensive_test_book.landing", "Test book with all the features.");
        addItem(builder, "test_book_1.name", "Test Book 1");
        addItem(builder, "test_book_1.landing", "Not as fun landing text but still here.");
        addItem(builder, "test_book_2.name", "Test Book 2");
        addItem(builder, "test_book_2.landing", "Landing text! It even supports $(2)colors$() and $(4)the $(bold)like$()!");
        addItem(builder, "test_completion.name", "Test Advancement-Driven Book");
        addItem(builder, "test_completion.landing", "This book changes colors when you unlock entries!");
        addItem(builder, "pamphlet.name", "Test Pamphlet");
        addItem(builder, "pamphlet.landing", "A test pamphlet, which is a book with only one category.");
    }

    private void addItem(TranslationBuilder builder, String id, String text) {
        builder.add("item.guidebook:" + id, text);
    }

}
