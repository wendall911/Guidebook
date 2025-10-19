package handbook.common.item;

import java.util.function.BiConsumer;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

import handbook.api.HandbookAPI;

public class HandbookDataComponents {

    public static final ResourceLocation COMPONENT_ID = ResourceLocation.fromNamespaceAndPath(HandbookAPI.MODID, "book");
    public static final DataComponentType<ResourceLocation> BOOK = DataComponentType.<ResourceLocation>builder()
        .persistent(ResourceLocation.CODEC)
        .networkSynchronized(ResourceLocation.STREAM_CODEC)
        .build();

    public static void submitDataComponentRegistrations(BiConsumer<ResourceLocation, DataComponentType<?>> consumer) {
        consumer.accept(COMPONENT_ID, BOOK);
    }

}
