package guidebook.data.test;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;

import guidebook.api.GuidebookAPI;

public class GuidebookTestLanguageProvider extends FabricLanguageProvider {

    public GuidebookTestLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder builder) {
        addBookTranslation(builder, "guide_book", "Guide Book");
        addBookTranslation(builder, "guide_book.undefined", "Invalid book: no ID defined");
        addBookTranslation(builder, "guide_book.invalid", "Invalid book: %s");
        addBookTranslation(builder, "comprehensive_test_book.name", "Comprehensive Test Book");
        addBookTranslation(builder, "comprehensive_test_book.landing", "Test book with all the features.");
        addBookTranslation(builder, "comprehensive_test_book.language_test.contents", "This page should be replaced with something else when you switch to zh_cn");
        addBookTranslation(builder, "test_book_1.name", "Test Book 1");
        addBookTranslation(builder, "test_book_1.landing", "Not as fun landing text but still here.");
        addBookTranslation(builder, "test_book_2.name", "Test Book 2");
        addBookTranslation(builder, "test_book_2.landing", "Landing text! It even supports $(2)colors$() and $(4)the $(bold)like$()!");
        addBookTranslation(builder, "test_completion.name", "Test Advancement-Driven Book");
        addBookTranslation(builder, "test_completion.landing", "This book changes colors when you unlock entries!");
        addBookTranslation(builder, "pamphlet.name", "Test Pamphlet");
        addBookTranslation(builder, "pamphlet.landing", "A test pamphlet, which is a book with only one category.");
    }

    private void addBookTranslation(TranslationBuilder builder, String id, String text) {
        add(builder, "book." + GuidebookAPI.MODID + "test." + id, text);
    }

    private void add(TranslationBuilder builder, String id, String text) {
        builder.add(id, text);
    }

}
