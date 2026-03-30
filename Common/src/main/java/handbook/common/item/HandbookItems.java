package handbook.common.item;

import java.util.function.BiConsumer;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import handbook.api.HandbookAPI;

public class HandbookItems {

    public static final Identifier BOOK_ID = HandbookAPI.prefix("handbook_book");
    public static final Identifier BOOK_BLUE_ID = HandbookAPI.prefix("book_blue");
    public static final Identifier BOOK_BROWN_ID = HandbookAPI.prefix("book_brown");
    public static final Identifier BOOK_CYAN_ID = HandbookAPI.prefix("book_cyan");
    public static final Identifier BOOK_GRAY_ID = HandbookAPI.prefix("book_gray");
    public static final Identifier BOOK_GREEN_ID = HandbookAPI.prefix("book_green");
    public static final Identifier BOOK_PURPLE_ID = HandbookAPI.prefix("book_purple");
    public static final Identifier BOOK_RED_ID = HandbookAPI.prefix("book_red");
    public static final Item BOOK = new HandbookBook(
        new Item.Properties()
            .stacksTo(1)
            .setId(ResourceKey.create(Registries.ITEM, BOOK_ID))
    );
    public static final Item BOOK_BLUE = new HandbookBook(
        new Item.Properties()
            .stacksTo(1)
            .setId(ResourceKey.create(Registries.ITEM, BOOK_BLUE_ID))
    );
    public static final Item BOOK_BROWN = new HandbookBook(
        new Item.Properties()
            .stacksTo(1)
            .setId(ResourceKey.create(Registries.ITEM, BOOK_BROWN_ID))
    );
    public static final Item BOOK_CYAN = new HandbookBook(
        new Item.Properties()
            .stacksTo(1)
            .setId(ResourceKey.create(Registries.ITEM, BOOK_CYAN_ID))
    );
    public static final Item BOOK_GRAY = new HandbookBook(
        new Item.Properties()
            .stacksTo(1)
            .setId(ResourceKey.create(Registries.ITEM, BOOK_GRAY_ID))
    );
    public static final Item BOOK_GREEN = new HandbookBook(
        new Item.Properties()
            .stacksTo(1)
            .setId(ResourceKey.create(Registries.ITEM, BOOK_GREEN_ID))
    );
    public static final Item BOOK_PURPLE = new HandbookBook(
        new Item.Properties()
            .stacksTo(1)
            .setId(ResourceKey.create(Registries.ITEM, BOOK_PURPLE_ID))
    );
    public static final Item BOOK_RED = new HandbookBook(
        new Item.Properties()
            .stacksTo(1)
            .setId(ResourceKey.create(Registries.ITEM, BOOK_RED_ID))
    );

    public static void submitItemRegistrations(BiConsumer<Identifier, Item> consumer) {
        consumer.accept(BOOK_ID, BOOK);
        consumer.accept(BOOK_BLUE_ID, BOOK_BLUE);
        consumer.accept(BOOK_BROWN_ID, BOOK_BROWN);
        consumer.accept(BOOK_CYAN_ID, BOOK_CYAN);
        consumer.accept(BOOK_GRAY_ID, BOOK_GRAY);
        consumer.accept(BOOK_GREEN_ID, BOOK_GREEN);
        consumer.accept(BOOK_PURPLE_ID, BOOK_PURPLE);
        consumer.accept(BOOK_RED_ID, BOOK_RED);
    }

}
