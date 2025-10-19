package handbook.data.test;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import handbook.api.HandbookAPI;
import handbook.api.data.BookBuilder;
import handbook.api.data.CategoryBuilder;
import handbook.api.data.EntryBuilder;
import handbook.api.data.HandbookBookProvider;
import handbook.api.data.TemplateBuilder;
import handbook.common.book.Book.BookLayoutTexture;
import handbook.config.HandbookConfig.TextOverflowMode;

public class HandbookTestingBooksProvider extends HandbookBookProvider {

    public static final String COMPREHENSIVE_BOOK_TRANSLATION_KEY = "book." + HandbookAPI.MODID + "test.comprehensive_test_book";
    public static final String PAMPHLET_TRANSLATION_KEY = "book." + HandbookAPI.MODID + "test.pamphlet";

    public HandbookTestingBooksProvider(PackOutput packOutput, CompletableFuture<Provider> registries) {
        super(packOutput, HandbookAPI.MODID + "test", "en_us", registries);
    }

    @Override
    protected void addBooks(Consumer<BookBuilder> consumer, Provider provider) {
        createPamphlet(this, provider).build(consumer);
        createSmeltingErrorBook(this, provider).build(consumer);
        createCraftingErrorBook(this, provider).build(consumer);
        createComprehensiveTestBook(this, provider).build(consumer);
    }

    private BookBuilder createPamphlet(HandbookTestingBooksProvider builder, Provider provider) {
        BookBuilder pamphlet = builder.createBookBuilder(
                "pamphlet",
                PAMPHLET_TRANSLATION_KEY + ".name",
                PAMPHLET_TRANSLATION_KEY + ".landing",
                provider
            )
            .setBookTexture(BookLayoutTexture.CYAN)
            .setPamphlet(true)
            .setCreativeTab("minecraft:tools_and_utilities")
            .setAdvancementsTab("handbooktest");

        CategoryBuilder category = pamphlet
            .addCategory(
                "root",
                "Root Category",
                "None of this is displayed.",
                new ItemStack(Items.BARRIER)
            );

        EntryBuilder pamphletEntry = category.addEntry(
            "pamphlet",
            "What is a Pamphlet?",
            new ItemStack(Items.CRAFTING_TABLE)
        ).setPriority(true);
        pamphletEntry
            .addTextPage(
                "A pamphlet is a book with only one category. " +
                "This means that when you open the book, you go straight to the category view, " +
                "skipping the landing page and the category selection page. " +
                "This is useful for small books that don't need multiple categories."
            ).build()
            .addTextPage(
                "You can still add subcategories, but they will be displayed as buttons at the bottom of the category view. " +
                "This is useful for grouping related entries together without adding too much complexity."
            );

        EntryBuilder anotherEntry = category.addEntry(
            "another_entry",
            "Another Entry",
            new ItemStack(Items.STICK)
        ).setSortnum(1);
        anotherEntry.addTextPage("Just another entry to show that entries still work fine in a pamphlet.");

        EntryBuilder finalEntry = category.addEntry(
            "final_entry",
            "Final Entry",
            new ItemStack(Items.WOODEN_SWORD)
        ).setSortnum(2);
        finalEntry
            .addCraftingPage(ResourceLocation.fromNamespaceAndPath("minecraft", "diamond_sword"))
                .setText("All normal page types, including custom pages work in a pamphlet.").build()
            .addTextPage("This is the final entry in the pamphlet. Thanks for reading!");

        return pamphlet;
    }

    private BookBuilder createSmeltingErrorBook(HandbookTestingBooksProvider builder, Provider provider) {
        BookBuilder smeltingErrorBook = builder.createBookBuilder(
                "i_am_smelting_error",
                "handbooktest.smelting.error",
                "DO NOT OPEN",
                provider
            )
            .setBookTexture(BookLayoutTexture.RED)
            .setCreativeTab("minecraft:tools_and_utilities")
            .setSubtitle("DO NOT OPEN")
            .setModel("minecraft:barrier")
            .setVersion("GRADLE:VERSION");

        CategoryBuilder category = smeltingErrorBook
            .addCategory(
                "burning_dab",
                "Invalid smelting recipe test",
                "What'll happen if I add an invalid smelting recipe?",
                new ItemStack(Items.END_CRYSTAL)
            );

        EntryBuilder entry = category.addEntry(
            "kraft_dab/burning_dab_entry",
            "Smelting page with an invalid recipe",
            new ItemStack(Items.FURNACE)
        );
        entry.addSmeltingPage(ResourceLocation.fromNamespaceAndPath("minecraft", "charcoal"))
            .setText("This is supposed to have a second recipe that does not exist.")
            .setRecipe2(ResourceLocation.fromNamespaceAndPath("minecraft", "carrot"));

        return smeltingErrorBook;
    }

    private BookBuilder createCraftingErrorBook(HandbookTestingBooksProvider builder, Provider provider) {
        BookBuilder craftingErrorBook = builder.createBookBuilder(
            "i_am_crafting_error",
            "handbooktest.crafting.error",
            "DO NOT OPEN",
            provider
        )
        .setBookTexture(BookLayoutTexture.RED)
        .setCreativeTab("minecraft:tools_and_utilities")
        .setSubtitle("DO NOT OPEN")
        .setModel("minecraft:barrier")
        .setVersion("GRADLE:VERSION");

        CategoryBuilder category = craftingErrorBook
            .addCategory(
                "kraft_dab",
                "Invalid crafting recipe test",
                "What'll happen if I add an invalid crafting recipe?",
                new ItemStack(Items.END_CRYSTAL)
            );

        EntryBuilder entry = category.addEntry(
            "kraft_dab/kraft_dab_entry",
            "Crafting page with an invalid recipe",
            new ItemStack(Items.CRAFTING_TABLE)
        );
        entry.addCraftingPage(ResourceLocation.fromNamespaceAndPath("minecraft", "furnace"))
            .setText("This is supposed to have a second recipe that does not exist.")
            .setRecipe2(ResourceLocation.fromNamespaceAndPath("minecraft", "does_not_exist"));

        return craftingErrorBook;
    }

    public static BookBuilder createComprehensiveTestBook(HandbookTestingBooksProvider builder, Provider provider) {
        BookBuilder comprehensiveTestBook = builder.createBookBuilder(
            "comprehensive_test_book",
            COMPREHENSIVE_BOOK_TRANSLATION_KEY + ".name",
            COMPREHENSIVE_BOOK_TRANSLATION_KEY + ".landing",
            provider
        )
        .setBookTexture(BookLayoutTexture.GREEN)
        .setVersion("50")
        .setSubtitle("Comprehensive Test Book Subtitle")
        // Don't set creative tab if you want book to not appear in creative inventory or JEI/REI
        .setCreativeTab("minecraft:tools_and_utilities")
        .setTextOverflowMode(TextOverflowMode.RESIZE)
        .setI18n(true)
        .setPauseGame(true);

        comprehensiveTestBook = addBuiltinComponentOne(comprehensiveTestBook);
        comprehensiveTestBook = addBuiltinComponentTwo(comprehensiveTestBook);
        comprehensiveTestBook = addDeriveIntegerToStack(comprehensiveTestBook);
        comprehensiveTestBook = addNesting(comprehensiveTestBook);
        comprehensiveTestBook = addCustomComponent(comprehensiveTestBook);
        comprehensiveTestBook = addConfigFlags(comprehensiveTestBook);
        comprehensiveTestBook = addDeserializationTest(comprehensiveTestBook);
        comprehensiveTestBook = addLanguageTest(comprehensiveTestBook);
        comprehensiveTestBook = addPageTypes(comprehensiveTestBook);
        comprehensiveTestBook = addRecipeMapping(comprehensiveTestBook);
        comprehensiveTestBook = addSubcategories(comprehensiveTestBook);
        comprehensiveTestBook = addTemplates(comprehensiveTestBook);

        return comprehensiveTestBook;
    }

    private static BookBuilder addBuiltinComponentOne(BookBuilder bookBuilder) {
        JsonElement header = new JsonObject();
        JsonElement separator = new JsonObject();
        JsonElement text = new JsonObject();
        JsonElement item = new JsonObject();
        JsonElement image = new JsonObject();

        // builtin_components_1
        header.getAsJsonObject().addProperty("type", "handbook:header");
        header.getAsJsonObject().addProperty("text", "#headertext");
        header.getAsJsonObject().addProperty("x", -1);
        header.getAsJsonObject().addProperty("y", -1);
        separator.getAsJsonObject().addProperty("type", "handbook:separator");
        separator.getAsJsonObject().addProperty("x", -1);
        separator.getAsJsonObject().addProperty("y", -1);
        text.getAsJsonObject().addProperty("type", "handbook:text");
        text.getAsJsonObject().addProperty("text", "#texttext");
        text.getAsJsonObject().addProperty("x", 20);
        text.getAsJsonObject().addProperty("y", 30);
        item.getAsJsonObject().addProperty("type", "handbook:item");
        item.getAsJsonObject().addProperty("item", "#item");
        item.getAsJsonObject().addProperty("x", 20);
        item.getAsJsonObject().addProperty("y", 80);
        item.getAsJsonObject().addProperty("framed", true);
        image.getAsJsonObject().addProperty("type", "handbook:image");
        image.getAsJsonObject().addProperty("image", "#image");
        image.getAsJsonObject().addProperty("x", 20);
        image.getAsJsonObject().addProperty("y", 50);
        image.getAsJsonObject().addProperty("width", 16);
        image.getAsJsonObject().addProperty("height", 16);
        image.getAsJsonObject().addProperty("texture_width", "16");
        image.getAsJsonObject().addProperty("texture_height", "16");

        TemplateBuilder template = bookBuilder.addTemplate("builtin_components_1")
            .addComponent(header)
            .addComponent(separator)
            .addComponent(text)
            .addComponent(item)
            .addComponent(image);

        return template.build();
    }

    private static BookBuilder addBuiltinComponentTwo(BookBuilder bookBuilder) {
        JsonElement entity = new JsonObject();
        JsonElement frame = new JsonObject();
        JsonElement tooltip = new JsonObject();
        JsonArray tooltips = new JsonArray();

        // builtin_components_2
        entity.getAsJsonObject().addProperty("type", "handbook:entity");
        entity.getAsJsonObject().addProperty("entity", "#entity");
        entity.getAsJsonObject().addProperty("x", 50);
        entity.getAsJsonObject().addProperty("y", 50);
        frame.getAsJsonObject().addProperty("type", "handbook:frame");
        frame.getAsJsonObject().addProperty("x", -1);
        frame.getAsJsonObject().addProperty("y", -1);
        tooltip.getAsJsonObject().addProperty("type", "handbook:tooltip");
        tooltips.add("#tip1");
        tooltips.add("#tip2");
        tooltip.getAsJsonObject().add("tooltip", tooltips);
        tooltip.getAsJsonObject().addProperty("width", 100);
        tooltip.getAsJsonObject().addProperty("height", 100);

        TemplateBuilder template = bookBuilder.addTemplate("builtin_components_2")
            .addComponent(entity)
            .addComponent(frame)
            .addComponent(tooltip);

        return template.build();
    }

    private static BookBuilder addDeriveIntegerToStack(BookBuilder bookBuilder) {
        JsonElement item = new JsonObject();

        // derive_ingr_to_stack
        item.getAsJsonObject().addProperty("type", "handbook:item");
        item.getAsJsonObject().addProperty("item", "#input->stacks");
        item.getAsJsonObject().addProperty("x", 10);
        item.getAsJsonObject().addProperty("y", 40);

        TemplateBuilder template = bookBuilder.addTemplate("derive_ingr_to_stack")
            .addComponent(item);

        return template.build();
    }

    private static BookBuilder addNesting(BookBuilder bookBuilder) {
        JsonElement templateInclude = new JsonObject();
        JsonElement using = new JsonObject();

        // nesting
        using.getAsJsonObject().addProperty("headertext", "Header text from using block");
        using.getAsJsonObject().addProperty("texttext", "#texttext");
        templateInclude.getAsJsonObject().addProperty("template", "handbooktest:builtin_components_1");
        templateInclude.getAsJsonObject().addProperty("as", "child1");
        templateInclude.getAsJsonObject().add("using", using);

        TemplateBuilder template = bookBuilder.addTemplate("nesting")
            .addInclude(templateInclude);

        return template.build();
    }

    private static BookBuilder addCustomComponent(BookBuilder bookBuilder) {
        JsonElement custom = new JsonObject();

        // custom_component
        custom.getAsJsonObject().addProperty("type", "handbook:custom");
        custom.getAsJsonObject().addProperty("class", "handbook.client.book.template.test.ComponentCustomTest");
        custom.getAsJsonObject().addProperty("x", 10);
        custom.getAsJsonObject().addProperty("y", 40);

        TemplateBuilder template = bookBuilder.addTemplate("custom_component")
            .addComponent(custom);

        return template.build();
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
        JsonElement shears = new JsonObject();

        shears.getAsJsonObject().addProperty("translate", "item.minecraft.shears");
        shears.getAsJsonObject().addProperty("italic", true);

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
                "$(l)%s$() pressed %s with their $(6)%s on a %s$(0) and got some $(8)%s$(0)"
            )
            .with("$(playername)")
            .with("keybind", "key.use")
            .with(shears)
            .with("translate", "entity.minecraft.sheep")
            .with("translate", "item.minecraft.white_wool");

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
                "handbook.gui.lexicon.edition_str"
            )
            .with("Using the edition translation string with $(4)cool text that should " +
                "propagate its formatting to the rest of the string!").build()
            .addTextPage(
                "This link should lead to the recipe_mapping category: " +
                " $(l:handbooktest:recipe_mapping)click me$(/l)"
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

        EntryBuilder image = category.addEntry(
            "page_types/image",
            "Image Page",
            new ItemStack(Items.PAINTING)
        );
        image.addImagePage(bookImage("cat"))
            .setTitle("Example Image")
            .setText("This is an example image. It is 256x256 pixels with a transparent background.");

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
            .addEntry(ResourceLocation.fromNamespaceAndPath("handbooktest", "page_types/link"));

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

        extra.addFormattedTextPage("handbook.gui.lexicon.reloaded").with(69);
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

        EntryBuilder normalSubcategoryEntry = normalSubcategory.addEntry(
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
        JsonElement input = new JsonObject();

        input.getAsJsonObject().addProperty("tag", ItemTags.CREEPER_DROP_MUSIC_DISCS.location().toString());

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
        builtinComponents.addCustomPage("handbooktest:builtin_components_1")
            .set("headertext", "Header component")
            .set("texttext", "Text component section")
            .set("item", "minecraft:poisonous_potato")
            .set("image", "minecraft:textures/item/end_crystal.png").build()
        .addCustomPage("handbooktest:builtin_components_2")
            .set("entity", "minecraft:drowned")
            .set("tip1", "The Drowned is a special zombie that spawns in oceans.")
            .set("tip2", "It can also be created by manually drowning standard zombies.");

        EntryBuilder customComponents = category.addEntry(
            "templates/custom_components",
            "Custom Template Components",
            new ItemStack(Items.WRITTEN_BOOK)
        );
        customComponents.addCustomPage("handbooktest:custom_component")
            .set("spaget", "pasta")
            .set("pop", "beer");

        EntryBuilder deriveFunctions = category.addEntry(
            "templates/derive_functions",
            "Derive Functions in Templates",
            new ItemStack(Items.WRITTEN_BOOK)
        );
        deriveFunctions.addCustomPage("handbooktest:derive_ingr_to_stack")
            .set("input", input);

        EntryBuilder templateNesting = category.addEntry(
            "templates/template_nesting",
            "Template Nesting",
            new ItemStack(Items.WRITTEN_BOOK)
        );
        templateNesting.addCustomPage("handbooktest:nesting")
            .set("texttext", "Text component text defined in entry json")
            .set("child1.item", "minecraft:barrier")
            .set("child1.image", "minecraft:textures/item/end_crystal.png");

        return category.build();
    }

    private static ResourceLocation bookImage(String id) {
        return ResourceLocation.fromNamespaceAndPath(HandbookAPI.MODID + "test", "textures/gui/book/" + id + ".png");
    }

}
