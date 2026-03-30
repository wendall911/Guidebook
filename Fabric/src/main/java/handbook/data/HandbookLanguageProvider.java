package handbook.data;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;

import handbook.api.HandbookAPI;
import handbook.common.Translations;
import handbook.common.item.HandbookItems;

import static handbook.data.HandbookInternalBookProvider.INTRO_BOOK_TRANSLATION_KEY;

public class HandbookLanguageProvider extends FabricLanguageProvider {

    public HandbookLanguageProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        addItem(builder, HandbookItems.BOOK_ID.getPath(), "Handbook Book");
        addBook(builder, INTRO_BOOK_TRANSLATION_KEY, "Handbook");
        addBookTranslation(builder, INTRO_BOOK_TRANSLATION_KEY + ".undefined", "Invalid book: no ID defined");
        addBookTranslation(builder, INTRO_BOOK_TRANSLATION_KEY + ".invalid", "Invalid book: %s");
        addBookTranslation(builder, "subtitle", "Documentation for everyone");
        addBookTranslation(builder, "landing", "With $(item)Handbook$(), " +
            "you can make easy to read, advancement unlockable $(thing)books$() for mods and modpacks!");
        addBookTranslation(builder, "introduction.name", "Introduction");
        addBookTranslation(builder, "introduction.description", "A Guide for Handbook!");
        addBookTranslation(builder, "introduction.welcome.name", "Welcome to Handbook");
        addBookTranslation(builder, "introduction.welcome.intro", "This is a sample book made with Handbook.$(br)" +
            "$(p)Handbook is a documentation mod for Minecraft that allows mod and modpack " +
            "authors to easily create in-game documentation for their mods.$(br)" +
            "$(p)This book is just a demonstration of what Handbook can do. " +
            "Feel free to explore and see the various features it has to offer.$(br)" +
            "$(p)If you're an author looking to create your own books, " +
            "please refer to the wiki for documentation and tutorials.");
        addSubtitle(builder, "book_open", "Book Opens");
        addSubtitle(builder, "book_flip", "Page Turns");
        addLexicon(builder, "landing_info", "(Placeholder landing text, define your own in your book json file!)");
        addLexicon(builder, "progress_meter", "Unlock Progress:");
        addLexicon(builder, "progress_tooltip", "%d/%d Unlocked");
        addLexicon(builder, "progress_tooltip.secret1", "Plus 1 Secret");
        addLexicon(builder, "progress_tooltip.secret", "Plus %d Secrets");
        addLexicon(builder, "progress_tooltip.info", "Complete Advancements to unlock more!");
        addLexicon(builder, "categories", "Categories");
        addLexicon(builder, "chapters", "Chapters");
        addLexicon(builder, "editor", "Book Editor");
        addLexicon(builder, "editor.mock_header", "Mock Header");
        addLexicon(builder, "editor.info", "This is an editor for Handbook book entries. " +
            "It's meant to be used for development or translation. There's no use for it if you're a player." +
            "$(br2)Please reference the $(l:https://github.com/Vazkii/Patchouli/wiki/Text-Formatting-101)" +
            "Patchouli wiki$() for usable control codes.");
        addLexicon(builder, "shift_for_recipe", "Shift-Click for Recipe");
        addLexicon(builder, "index", "Entry Index");
        addLexicon(builder, "index.info", "An index of every chapter available in this book.$(br2)" +
            "You may search through the index, or any other category, by simply typing in your query. " +
            "A search bar will then appear to assist you.");
        addLexicon(builder, "shapeless", "Shapeless");
        addLexicon(builder, "no_results", "No Results");
        addLexicon(builder, "sad", ":(");
        addLexicon(builder, "no_entries", "No Entries");
        addLexicon(builder, "locked", "(Locked)");
        addLexicon(builder, "reloaded", "Books reloaded in %d ms.");
        addLexicon(builder, "add_bookmark", "Add Bookmark");
        addLexicon(builder, "remove_bookmark", "(Shift-Click to Remove)");
        addLexicon(builder, "relations", "Related Chapters");
        addLexicon(builder, "not_anchored", "Right-Click a Block to anchor the Structure");
        addLexicon(builder, "structure_complete", "Complete!");
        addLexicon(builder, "needs_air", "(Clear blocks marked in red)");
        addLexicon(builder, "visualize_letter", "V");
        addLexicon(builder, "seconds", "%ss");
        addLexicon(builder, "history", "Reading History");
        addLexicon(builder, "history.info", "This section stores the previous few chapters you've visited." +
            "$(br2)Chapters are automatically added and removed as you browse through the book. " +
            "Should you wish to keep them referenced for longer, you may bookmark them." +
            "$(br2)$(o)Tip: Try shift-clicking a chapter button!$()");
        addLexicon(builder, "toast", "New Chapters");
        addLexicon(builder, "toast.info", "New Chapters Unlocked");
        addLexicon(builder, "external_link", "(External Link)");
        addLexicon(builder, "loading_error", "Loading error!");
        addLexicon(builder, "loading_error_hover", "(Hover for info)");
        addLexicon(builder, "loading_error_log", "Check your log for more");
        addLexicon(builder, "dev_edition", "Writer's");
        addLexicon(builder, "edition_str", "%s Edition");
        addLexicon(builder, "added_by", "Added by %s");
        addLexicon(builder, "sneak", "Sneak to view");
        addLexicon(builder, "view", "View entry");
        addLexicon(builder, "keybind", "Keybind: %s");
        addLexicon(builder, "keybind_missing", "No such keybind: %s");
        addLexicon(builder, "objective", "Objective");
        addLexicon(builder, "incomplete", "Incomplete");
        addLexicon(builder, "complete", "Complete!");
        addLexicon(builder, "mark_complete", "Mark Complete");
        addLexicon(builder, "mark_incomplete", "Mark Incomplete");
        addLexicon(builder, "button.prev_page", "Previous");
        addLexicon(builder, "button.next_page", "Next");
        addLexicon(builder, "button.back", "Back");
        addLexicon(builder, "button.back.info", "Shift-Click to return to Home");
        addLexicon(builder, "button.resize", "Resize UI");
        addLexicon(builder, "button.toggle_mock_header", "Toggle Mock Header");
        addLexicon(builder, "button.resize.size0", "Default");
        addLexicon(builder, "button.resize.size1", "Small");
        addLexicon(builder, "button.resize.size2", "Medium");
        addLexicon(builder, "button.resize.size3", "Medium-Large");
        addLexicon(builder, "button.resize.size4", "Large");
        addLexicon(builder, "button.resize.size5", "Huge");
        addLexicon(builder, "button.resize.verybig.message", "How large is your monitor even");
        addLexicon(builder, "button.resize.verybig.container", "%s?");
        addLexicon(builder, "button.editor", "Editor");
        addLexicon(builder, "button.editor.info", "(Shift-Click to Reload)");
        addLexicon(builder, "button.advancements", "Advancements");
        addLexicon(builder, "button.config", "Configuration");
        addLexicon(builder, "button.visualize", "Visualize");
        addLexicon(builder, "button.visualize.info", "(Click again to clear)");
        addLexicon(builder, "button.history", "History");
        addLexicon(builder, "button.mark_all_read", "Mark all as read");
        addLexicon(builder, "button.mark_category_read", "Mark this category as read");
        addNetworking(builder, "open_book.failed", "Failed to open book %s");
        addNetworking(builder, "reload_contents.failed", "Failed to reload contents %S");
        addTranslationTitle(builder, "Handbook Configuration");
        addTranslation(builder, "disableadvancementlocking");
        addTranslation(builder, "noadvancementbooks");
        addTranslation(builder, "testingmode");
        addTranslation(builder, "inventorybuttonbook");
        addTranslation(builder, "useshiftforquicklookup");
        addTranslation(builder, "textoverflowmode");
        addTranslation(builder, "quicklookuptime");
    }

    private void addBook(TranslationBuilder builder, String id, String text) {
        add(builder, id, text);
    }

    private void addBookTranslation(TranslationBuilder builder, String id, String text) {
        add(builder, INTRO_BOOK_TRANSLATION_KEY + "." + id, text);
    }

    private void addSubtitle(TranslationBuilder builder, String id, String text) {
        add(builder, "handbook.subtitle." + id, text);
    }

    private void addLexicon(TranslationBuilder builder, String id, String text) {
        add(builder, "handbook.gui.lexicon." + id, text);
    }

    private void addNetworking(TranslationBuilder builder, String id, String text) {
        add(builder, "handbook.networking." + id, text);
    }

    private void add(TranslationBuilder builder, String id, String text) {
        builder.add(id, text);
    }

    private void addTranslationTitle(TranslationBuilder builder, String title) {
        builder.add(HandbookAPI.MODID + ".configuration.title", title);
    }

    private void addTranslationName(TranslationBuilder builder, String id, String name) {
        builder.add(HandbookAPI.MODID + ".configuration." + id + ".name", name);
    }

    private void addTranslationDescription(TranslationBuilder builder, String id) {
        builder.add(HandbookAPI.MODID + ".configuration." + id + ".description", Translations.get(id));
    }

    private void addTranslation(TranslationBuilder buildder, String id) {
        addTranslationName(buildder, id, Translations.get(id + ".title"));
        addTranslationDescription(buildder, id);
    }

    private void addTranslationDescription(TranslationBuilder builder, String id, String key) {
        builder.add(HandbookAPI.MODID + ".configuration." + id + ".description", Translations.get(key));
    }

    private void addItem(TranslationBuilder translationBuilder, String name, String text) {
        translationBuilder.add("item." + HandbookAPI.MODID + "." + name, text);
    }

}
