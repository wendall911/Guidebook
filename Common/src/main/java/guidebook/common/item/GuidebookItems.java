package guidebook.common.item;

import java.util.function.BiConsumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import guidebook.api.GuidebookAPI;

public class GuidebookItems {

    public static final ResourceLocation BOOK_ID = GuidebookAPI.prefix("guide_book");
    public static final Item BOOK = new ItemModBook();

    public static void submitItemRegistrations(BiConsumer<ResourceLocation, Item> consumer) {
        consumer.accept(BOOK_ID, BOOK);
    }

}
