package guidebook.common.item;

import java.util.function.BiConsumer;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import guidebook.api.GuidebookAPI;

public class GuidebookItems {

    public static final ResourceLocation BOOK_ID = GuidebookAPI.prefix("guide_book");
    public static final ResourceLocation BOOK_BLUE_ID = GuidebookAPI.prefix("book_blue");
    public static final ResourceLocation BOOK_BROWN_ID = GuidebookAPI.prefix("book_brown");
    public static final ResourceLocation BOOK_CYAN_ID = GuidebookAPI.prefix("book_cyan");
    public static final ResourceLocation BOOK_GRAY_ID = GuidebookAPI.prefix("book_gray");
    public static final ResourceLocation BOOK_GREEN_ID = GuidebookAPI.prefix("book_green");
    public static final ResourceLocation BOOK_PURPLE_ID = GuidebookAPI.prefix("book_purple");
    public static final ResourceLocation BOOK_RED_ID = GuidebookAPI.prefix("book_red");
    public static final Item BOOK = new ItemModBook(
        new Item.Properties()
            .stacksTo(1)
            .setId(ResourceKey.create(Registries.ITEM, BOOK_ID))
    );

    public static void submitItemRegistrations(BiConsumer<ResourceLocation, Item> consumer) {
        consumer.accept(BOOK_ID, BOOK);
    }

}
