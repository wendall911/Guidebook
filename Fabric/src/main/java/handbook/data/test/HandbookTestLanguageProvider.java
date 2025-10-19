package handbook.data.test;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;

import handbook.api.HandbookAPI;

public class HandbookTestLanguageProvider extends FabricLanguageProvider {

    public HandbookTestLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder builder) {
        addBookTranslation(builder, "handbook_book", "Handbook Book");
        addBookTranslation(builder, "comprehensive_test_book.name", "Comprehensive Test Book");
        addBookTranslation(builder, "comprehensive_test_book.landing", "Test book with all the features.");
        addBookTranslation(builder, "comprehensive_test_book.language_test.contents", "This page should be replaced with something else when you switch to zh_cn");
        addBookTranslation(builder, "pamphlet.name", "Test Pamphlet");
        addBookTranslation(builder, "pamphlet.landing", "A test pamphlet, which is a book with only one category.");
    }

    private void addBookTranslation(TranslationBuilder builder, String id, String text) {
        add(builder, "book." + HandbookAPI.MODID + "test." + id, text);
    }

    private void add(TranslationBuilder builder, String id, String text) {
        builder.add(id, text);
    }

}
