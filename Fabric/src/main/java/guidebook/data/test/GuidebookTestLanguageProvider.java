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
        addGuidebookItem(builder, "guide_book", "Guide Book");
        addGuidebookItem(builder, "guide_book.undefined", "Invalid book: no ID defined");
        addGuidebookItem(builder, "guide_book.invalid", "Invalid book: %s");
        addGuidebookItem(builder, "comprehensive_test_book.name", "Comprehensive Test Book");
        addGuidebookItem(builder, "comprehensive_test_book.landing", "Test book with all the features.");
        addGuidebookItem(builder, "test_book_1.name", "Test Book 1");
        addGuidebookItem(builder, "test_book_1.landing", "Not as fun landing text but still here.");
        addGuidebookItem(builder, "test_book_2.name", "Test Book 2");
        addGuidebookItem(builder, "test_book_2.landing", "Landing text! It even supports $(2)colors$() and $(4)the $(bold)like$()!");
        addGuidebookItem(builder, "test_completion.name", "Test Advancement-Driven Book");
        addGuidebookItem(builder, "test_completion.landing", "This book changes colors when you unlock entries!");
        addGuidebookItem(builder, "pamphlet.name", "Test Pamphlet");
        addGuidebookItem(builder, "pamphlet.landing", "A test pamphlet, which is a book with only one category.");
    }

    private void addGuidebookItem(TranslationBuilder builder, String id, String text) {
        add(builder, "item." + GuidebookAPI.MODID + ":" + id, text);
    }

    private void add(TranslationBuilder builder, String id, String text) {
        builder.add(id, text);
    }

}
