package guidebook.data.test;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import guidebook.api.GuidebookAPI;
import guidebook.api.data.BookBuilder;
import guidebook.api.data.CategoryBuilder;
import guidebook.api.data.EntryBuilder;
import guidebook.api.data.GuidebookBookProvider;
import guidebook.config.GuidebookConfig.TextOverflowMode;

public class GuidebookTestingBooksProvider extends GuidebookBookProvider {

    public static final String COMPREHENSIVE_BOOK_TRANSLATION_KEY = "book." + GuidebookAPI.MODID + "test.comprehensive_test_book";

    public GuidebookTestingBooksProvider(PackOutput packOutput, CompletableFuture<Provider> registries) {
        super(packOutput, GuidebookAPI.MODID + "test", "en_us", registries);
    }

    @Override
    protected void addBooks(Consumer<BookBuilder> consumer, Provider provider) {
        createComprehensiveTestBook(this, provider).build(consumer);
    }

    public static BookBuilder createComprehensiveTestBook(GuidebookTestingBooksProvider builder, Provider provider) {
        BookBuilder comprehensiveTestBook = builder.createBookBuilder(
            "comprehensive_test_book",
            COMPREHENSIVE_BOOK_TRANSLATION_KEY + ".name",
            COMPREHENSIVE_BOOK_TRANSLATION_KEY + ".landing",
            provider
        )
        .setVersion("50")
        .setSubtitle("Comprehensive Test Book Subtitle")
        // Don't set creative tab if you want book to not appear in creative inventory or JEI/REI
        .setCreativeTab("minecraft:tools_and_utilities")
        .setTextOverflowMode(TextOverflowMode.RESIZE)
        .setI18n(true)
        .setPauseGame(true);

        comprehensiveTestBook = addConfigFlags(comprehensiveTestBook);
        comprehensiveTestBook = addDeserializationTest(comprehensiveTestBook);
        comprehensiveTestBook = addLanguageTest(comprehensiveTestBook);
        comprehensiveTestBook = addPageTypes(comprehensiveTestBook);
        comprehensiveTestBook = addRecipeMapping(comprehensiveTestBook);
        comprehensiveTestBook = addSubcategories(comprehensiveTestBook);
        comprehensiveTestBook = addTemplates(comprehensiveTestBook);

        return comprehensiveTestBook;
    }

    private static BookBuilder addConfigFlags(BookBuilder bookBuilder) {
        CategoryBuilder category = bookBuilder
            .addCategory(
                "config_flags",
                "Config Flag Test",
                "Test pages for config flags and their usage",
                new ItemStack(Items.COMPARATOR)
            );

        EntryBuilder gogMissing = category.addEntry(
            "config_flags/appears_gog_missing",
            "Appears if GOG is missing",
            new ItemStack(Items.OAK_SAPLING)
        ).setFlag("!mod:gardenofglass");

        gogMissing.addTextPage("This entry appears if the Garden of Glass mod is not installed.");

        return category.build();
    }

    private static BookBuilder addDeserializationTest(BookBuilder bookBuilder) {
        JsonElement keybindUse = new JsonObject();
        JsonElement shears = new JsonObject();
        JsonElement sheep = new JsonObject();
        JsonElement wool = new JsonObject();

        keybindUse.getAsJsonObject().addProperty("keybind", "key.use");
        shears.getAsJsonObject().addProperty("translate", "item.minecraft.shears");
        shears.getAsJsonObject().addProperty("italic", true);
        sheep.getAsJsonObject().addProperty("translate", "entity.minecraft.sheep");
        wool.getAsJsonObject().addProperty("translate", "item.minecraft.white_wool");

        CategoryBuilder category = bookBuilder
            .addCategory(
                "deserialization",
                "Deserialization Test",
                "Test pages for ~fancy~ deserialization and rendering",
                new ItemStack(Items.HOPPER)
            );

        EntryBuilder namesTest = category.addEntry(
            "deserialization/names_test",
            "Names Test",
            new ItemStack(Items.NAME_TAG)
        ).setFlag("!mod:gardenofglass");

        namesTest
            .addTextPage("The following page should read \"<playername> pressed <usebinding> " +
                "with their <shears name> on a <sheep name> and got some <wool name>\"").build()
            .addFormattedTextPage(
                "$(l)%s$() pressed %s with their $(6)%s on a %s$(0) and got some $(8)%s$(0)",
                "$(playername)",
                keybindUse,
                shears,
                sheep,
                wool
            );

        EntryBuilder overflowRender = category.addEntry(
            "deserialization/overflow_render",
            "Overflow Render Test",
            new ItemStack(Items.WATER_BUCKET)
        ).setFlag("!mod:gardenofglass");

        overflowRender
            .addTextPage("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do " +
                "eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                "$(l:https://www.lipsum.com/)Source.$(/l) " +
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat " +
                "nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia " +
                "deserunt mollit anim id est laborum."
            ).build()
            .addTextPage(
                "PsiDust is now completely uncraftable, player is stuck at level " +
                "0 permanently No bypass commands work.$(br)Tried every available " +
                "suggestion, and still nothing. Also noticed that apparently every time " +
                "someone as else posted a similar problem on here, its closed immediately, " +
                "which makes me believe that this mod should be removed from Stoneblock in " +
                "the future.$(br)I will be forwarding the recommendation to all Modpack " +
                "developers. Not even going to ask for help from the staff here, As I have " +
                "had it with most of the Mod Developers who are github these days and " +
                "their inability to act with any form of decorum or actually attempting to " +
                "resolve issues. Do not bother responding, as I am going to take an " +
                "immediate screenshot for my records, and will spend my now limitless " +
                "amount of available time to getting certain mod developers (if you can " +
                "call them that) removed."
            );

        EntryBuilder basicTest = category.addEntry(
            "deserialization/test",
            "Basic Test",
            new ItemStack(Items.OAK_SAPLING)
        ).setFlag("!mod:gardenofglass");

        basicTest
            .addTextPage("Error-safety: a stray /l command renders as an error: $(/l)").build()
            .addTextPage(
                "Old-style $(l)formatting codes$() should be supported, but the " +
                "text on the following page should be localized!"
            ).build()
            .addFormattedTextPage(
                "guidebook.gui.lexicon.edition_str",
                "Using the edition translation string with $(4)cool text that should " +
                    "propagate its formatting to the rest of the string!"
            ).build()
            .addTextPage(
                "This link should lead to the recipe_mapping category: " +
                " $(l:guidebook:recipe_mapping)click me$(/l)"
            );

        return category.build();
    }

    private static BookBuilder addLanguageTest(BookBuilder bookBuilder) {
        CategoryBuilder category = bookBuilder
            .addCategory(
                "language",
                "Language Test",
                "Test pages for multi-language support",
                new ItemStack(Items.GLOBE_BANNER_PATTERN)
            );

        EntryBuilder languageTest = category.addEntry(
                "deserialization/language_test",
                "Language Test",
                new ItemStack(Items.GLOBE_BANNER_PATTERN)
        );

        languageTest.addTextPage(COMPREHENSIVE_BOOK_TRANSLATION_KEY + ".language_test.contents");

        return category.build();
    }

    private static BookBuilder addPageTypes(BookBuilder bookBuilder) {
        CategoryBuilder category = bookBuilder
            .addCategory(
                "page_types",
                "Built-in Page Types Test",
                "Tests for builtin page types. Recipes, and templates have their own category.",
                new ItemStack(Items.DIAMOND)
            );

        EntryBuilder advancementQuest = category.addEntry(
            "page_types/advancement_quest",
            "Advancement Quest",
            new ItemStack(Items.VINDICATOR_SPAWN_EGG)
        );
        advancementQuest.addQuestPage(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/create_beacon"))
            .setText(
                "This quest is completed when the vanilla beacon advancement is completed. " +
                "Click $(c:/advancement grant @s only minecraft:nether/create_beacon)here$(/c) to grant it. " +
                "Click $(c:/advancement revoke @s only minecraft:nether/create_beacon)here$(/c) to revoke it. " +
                "(You may need to unpause the game to see changes take effect)"
            );

        EntryBuilder entity = category.addEntry(
            "page_types/entity",
            "Entity Page",
            new ItemStack(Items.CREEPER_SPAWN_EGG)
        );
        entity.addEntityPage("minecraft:creeper")
            .setText("This is a creeper. It explodes.");
        entity.addEntityPage("minecraft:creeper{powered:1}")
            .setText("This is a creeper. It is exploding.");
        entity.addEntityPage("minecraft:snow_golem")
            .setText("This is a snow golem. It throws snowballs.");
        entity.addEntityPage("minecraft:armor_stand")
            .setText("This is an armor stand. It holds and displays armor and other items.");
        entity.addEntityPage("minecraft:slime{Size:2}")
            .setText("This is a slime. It bounces around and splits into smaller slimes when killed.");

        EntryBuilder link = category.addEntry(
            "page_types/link",
            "Link",
            new ItemStack(Items.CHAIN)
        );
        link.addLinkPage("https://example.com", "Link to example.com")
            .setText("Button below opens example.com");

        EntryBuilder manualQuest = category.addEntry(
            "page_types/manual_quest",
            "Manual Quest",
            new ItemStack(Items.BOOK)
        );
        manualQuest.addQuestPage()
            .setText("This is a manual quest. Click the button below to complete it.");

        EntryBuilder relations = category.addEntry(
            "page_types/relations",
            "Relations",
            new ItemStack(Items.HEART_OF_THE_SEA)
        );
        relations.addRelationsPage()
            .setTitle("Check out these relations!")
            .setText("This is a relations page. It shows the relationships between entries in this book.")
            .addEntry(ResourceLocation.fromNamespaceAndPath("guidebooktest", "page_types/link"));

        EntryBuilder spotlight = category.addEntry(
            "page_types/spotlight",
            "Spotlight",
            new ItemStack(Items.DEAD_BRAIN_CORAL)
        );
        spotlight.addSpotlightPage(new ItemStack(Items.OAK_SAPLING)).setLinkRecipe(true);
        spotlight.addSpotlightPage(new ItemStack(Items.DEAD_BUSH), new ItemStack(Items.SPRUCE_SAPLING))
            .setTitle("Custom title")
            .setText("Custom text $(l)with formatting$().");
        spotlight.addSpotlightPage(ItemTags.PIGLIN_LOVED)
            .setTitle("Tag Spotlight")
            .setText("This spotlight shows all items in the piglin loved tag.");
        spotlight.addSpotlightPage(ItemTags.PIGLIN_REPELLENTS);
        spotlight.addSpotlightPage(ItemTags.WOOL)
            .addItem(new ItemStack(Items.BONE))
            .setTitle("Wool")
            .setText("But also bone...?");
        spotlight.addSpotlightPage(new ItemStack(Items.NAME_TAG))
            .setTitle("Name Tag")
            .setText("But also beds...?")
            .addTag(ItemTags.BEDS);

        return category.build();
    }

    private static BookBuilder addRecipeMapping(BookBuilder bookBuilder) {
        CategoryBuilder category = bookBuilder
            .addCategory(
                "recipe_mapping",
                "Recipe Mapping",
                "Test pages for recipe mapping, quick lookup, and world lookup",
                new ItemStack(Items.CRAFTING_TABLE)
            );

        EntryBuilder extra = category.addEntry(
            "recipe_mapping/extra",
            "Extra Recipe Mapping",
            new ItemStack(Items.STONE)
        )
        .addExtraRecipeMapping(new ItemStack(Items.GRASS_BLOCK), 0)
        .addExtraRecipeMapping(ItemTags.LOGS, 2);

        extra.addFormattedTextPage("guidebook.gui.lexicon.reloaded", 69D);
        extra.addEmptyPage();
        extra.addTextPage("Logs should be mapped here");

        EntryBuilder recipe = category.addEntry(
            "recipe_mapping/recipe",
            "Recipe Page",
            new ItemStack(Items.COOKED_SALMON)
        );

        recipe.addCraftingPage(ResourceLocation.fromNamespaceAndPath("minecraft", "diamond_sword"))
            .setRecipe2(ResourceLocation.fromNamespaceAndPath("minecraft", "flint_and_steel"));

        recipe.addCraftingPage(ResourceLocation.fromNamespaceAndPath("minecraft", "fletching_table"))
            .setRecipe2(ResourceLocation.fromNamespaceAndPath("minecraft", "snow"))
            .setLinkRecipe(false);
        recipe.addSmeltingPage(ResourceLocation.fromNamespaceAndPath("minecraft", "cooked_salmon"));
        recipe.addBlastingPage(
            ResourceLocation.fromNamespaceAndPath("minecraft", "iron_ingot_from_blasting_iron_ore"));
        recipe.addSmokingPage(
            ResourceLocation.fromNamespaceAndPath("minecraft", "cooked_cod_from_smoking"));
        recipe.addCampfirePage(
            ResourceLocation.fromNamespaceAndPath("minecraft", "cooked_cod_from_campfire_cooking"));
        recipe.addStonecuttingPage(
            ResourceLocation.fromNamespaceAndPath("minecraft", "andesite_slab_from_andesite_stonecutting"));
        recipe.addSmithingPage(ResourceLocation.fromNamespaceAndPath("minecraft", "netherite_axe_smithing"))
            .setText("Smithing table recipes.")
            .setRecipe2(ResourceLocation.fromNamespaceAndPath("minecraft", "netherite_pickaxe_smithing"));

        return category.build();
    }

    private static BookBuilder addSubcategories(BookBuilder bookBuilder) {
        CategoryBuilder category = bookBuilder
            .addCategory(
                "subcategories",
                "Subcategories",
                "Tests for subcategories. No entries here, so the subcategory buttons take their place.",
                new ItemStack(Items.FILLED_MAP)
            );

        CategoryBuilder normalSubcategory = category
            .build().addCategory(
                "subcategories/normal_subcategory",
                "Normal Subcategory",
                "Normal subcategory with some entries and some empty " +
                    "subcategories to observe the behavior of category button wrapping.",
                new ItemStack(Items.COOKED_CHICKEN)
            ).setParent("subcategories");

        EntryBuilder normalSubcategoryEntry = category.addEntry(
            "subcategories/normal_subcategory_entry",
            "Normal Subcategory Entry",
            new ItemStack(Items.CARROT_ON_A_STICK)
        );
        normalSubcategoryEntry.addTextPage("This entry is in the main category, not in a subcategory.");

        category.build().addCategory(
            "subcategories/empty_subcategory",
            "Empty Subcategory",
            "Empty subcategory with no entries",
            new ItemStack(Items.CHICKEN)
        ).setParent("subcategories").build()
        .addCategory(
            "subcategories/empty_subcategory_1",
            "Empty Subcategory 2",
            "This subcategory is empty.",
            new ItemStack(Items.BAKED_POTATO)
        ).setParent("subcategories").build()
        .addCategory(
            "subcategories/empty_subcategory_2",
            "Empty Subcategory 3",
            "This subcategory is empty.",
            new ItemStack(Items.POTATO)
        ).setParent("subcategories").build()
        .addCategory(
            "subcategories/empty_subcategory_3",
            "Empty Subcategory 4",
            "This subcategory is empty.",
            new ItemStack(Items.STONE)
        ).setParent("subcategories").build()
        .addCategory(
            "subcategories/empty_subsubcategory",
            "Empty Sub-sub-category",
            "Empty sub-sub-category with no entries",
            new ItemStack(Items.IRON_INGOT)
        ).setParent("subcategories/normal_subcategory").build()
        .addCategory(
            "subcategories/empty_subsubcategory_1",
            "Empty Sub-sub-category 2",
            "Empty sub-sub-category with no entries",
            new ItemStack(Items.GOLD_INGOT)
        ).setParent("subcategories/normal_subcategory").build()
        .addCategory(
            "subcategories/empty_subsubcategory_2",
            "Empty Sub-sub-category 3",
            "Empty sub-sub-category with no entries",
            new ItemStack(Items.IRON_PICKAXE)
        ).setParent("subcategories/normal_subcategory").build()
        .addCategory(
            "subcategories/empty_subsubcategory_3",
            "Empty Sub-sub-category 4",
            "Empty sub-sub-category with no entries",
            new ItemStack(Items.IRON_AXE)
        ).setParent("subcategories/normal_subcategory").build()
        .addCategory(
            "subcategories/empty_subsubcategory_4",
            "Empty Sub-sub-category 5",
            "Empty sub-sub-category with no entries",
            new ItemStack(Items.IRON_SWORD)
        ).setParent("subcategories/normal_subcategory");

        return category.build();
    }

    private static BookBuilder addTemplates(BookBuilder bookBuilder) {
        CategoryBuilder category = bookBuilder
            .addCategory(
                "templates",
                "Template Test",
                "Test pages for templates and their usage",
                new ItemStack(Items.WRITABLE_BOOK)
            );

        EntryBuilder builtinComponents = category.addEntry(
            "templates/builtin_components",
            "Basic Template Components",
            new ItemStack(Items.WRITTEN_BOOK)
        );

        //builtinComponents. add something with guidebooktest:builtin_compnents_1 ?? // I think this is custom json

        return category.build();
    }

}
