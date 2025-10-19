package handbook.common.item;

import java.util.function.BiConsumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import handbook.api.HandbookAPI;

public class HandbookItems {

    public static final ResourceLocation BOOK_ID = HandbookAPI.prefix("handbook_book");
    public static final ResourceLocation BOOK_BLUE_ID = HandbookAPI.prefix("book_blue");
    public static final ResourceLocation BOOK_BROWN_ID = HandbookAPI.prefix("book_brown");
    public static final ResourceLocation BOOK_CYAN_ID = HandbookAPI.prefix("book_cyan");
    public static final ResourceLocation BOOK_GRAY_ID = HandbookAPI.prefix("book_gray");
    public static final ResourceLocation BOOK_GREEN_ID = HandbookAPI.prefix("book_green");
    public static final ResourceLocation BOOK_PURPLE_ID = HandbookAPI.prefix("book_purple");
    public static final ResourceLocation BOOK_RED_ID = HandbookAPI.prefix("book_red");
    public static final Item BOOK = new HandbookBook();

    public static void submitItemRegistrations(BiConsumer<ResourceLocation, Item> consumer) {
        consumer.accept(BOOK_ID, BOOK);
    }

}
